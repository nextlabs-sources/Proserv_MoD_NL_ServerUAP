package com.nextlabs.provider;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bluejungle.framework.expressions.EvalValue;
import com.bluejungle.framework.expressions.IEvalValue;
import com.bluejungle.framework.expressions.IMultivalue;
import com.bluejungle.framework.expressions.Multivalue;
import com.bluejungle.pf.domain.destiny.serviceprovider.IHeartbeatServiceProvider;
import com.bluejungle.pf.domain.destiny.serviceprovider.ISubjectAttributeProvider;
import com.bluejungle.pf.domain.destiny.serviceprovider.ServiceProviderException;
import com.bluejungle.pf.domain.destiny.subject.IDSubject;
import com.bluejungle.pf.domain.destiny.subject.Subject;
import com.bluejungle.pf.domain.destiny.subject.SubjectType;
import com.nextlabs.cache.CacheEngine;
import com.nextlabs.common.Constants;
import com.nextlabs.common.PropertyLoader;
import com.nextlabs.common.Provider;
import com.nextlabs.common.SAPObject;
import com.nextlabs.common.UserObject;
import com.nextlabs.common.Util;
import com.nextlabs.ldap.LdapProvider;
import com.nextlabs.nxljco.sap.SAPProvider;
import com.nextlabs.task.RefreshTask;

public class UserAttributeProvider implements IHeartbeatServiceProvider,
		ISubjectAttributeProvider {
	private static final Log LOG = LogFactory.getLog(UserAttributeProvider.class);
	private Properties PLUGIN_PROPS;
	private final String CLIENT_PROPS_FILE = "jservice/config/ServerUserAttributeProvider.properties";
	private IEvalValue nullReturn;
	CacheEngine engine;
	Provider ldapProvider;
	Provider sapProvider;
	private static String LOG_EMPTY_EMPLOYEE_NUMBER = "Incoming request for [%s] without employee ID, ignore and return null";
	private static String LOG_INCOMING_REUQEST = "Incoming request from SAP with sapID [%s] and employeeNumber [%s]";
	private static String LOG_USER_CACHE_MISSED = "Cache missed for USER [%s]. Attempt to query...";
	private static String LOG_USER_ATTRIBUTE_NEEDED = "Attribute [%s] is needed from IAM-LDAP";
	private static String LOG_SAP_ATTRIBUTE_NEEDED = "Attribute [%s] is needed from SAP AOR master";
	private static String LOG_AOR_CACHE_MISSED = "Cache missed for user [%s] with AOR/AmmoArea id [%s], Added to query list for SAP...";
	private static String LOG_AOR_CACHE_HIT = "Able to resolve from cache for user [%s] with AOR id [%s]";
	private static String LOG_GET_AOR_ATTRIBUTE = "Getting attribute AmmonArea as AOR [%s] for USER [%s]";
	private static String LOG_TIME_TAKEN = "Time Taken: %sms";


	/*
	 * public UserAttributeProvider(Properties props, CacheEngine engine,
	 * Provider provider, int refreshPeriod) { PLUGIN_PROPS = props; this.engine
	 * = engine; this.provider = provider; this.refreshPeriod = 3; }
	 */

	public void init() {
		long startTime = System.nanoTime();
		LOG.debug("init() started");
		PLUGIN_PROPS = PropertyLoader.loadPropertiesInPDP(CLIENT_PROPS_FILE);
	
		// Set null return
		String nullString = PLUGIN_PROPS.getProperty("null_string");
		if (nullString == null) {
			nullReturn = EvalValue.NULL;
		} else {
			nullReturn = EvalValue.build(nullString);
		}

		// Initialize Cache
		engine = CacheEngine.getInstance();
		engine.initializeCache(PLUGIN_PROPS);

		ldapProvider = LdapProvider.getInstance();
		ldapProvider.setCommonProperties(PLUGIN_PROPS);
		
		if (PLUGIN_PROPS.getProperty("profile_names") == null|| PLUGIN_PROPS.getProperty("profile_names").length() == 0) {
				ldapProvider.setIsSingleProfile(true);
				ldapProvider.loadSingleProfile(PLUGIN_PROPS);
		} else {
			ldapProvider.setIsSingleProfile(false);
			ldapProvider.loadProfiles(PLUGIN_PROPS);
		}
		
		sapProvider = SAPProvider.getInstance();
		sapProvider.setCommonProperties(PLUGIN_PROPS);
		sapProvider.setIsSingleProfile(true);
		sapProvider.loadSingleProfile(PLUGIN_PROPS);
				
		try {
			
			if(PLUGIN_PROPS.getProperty("aor_expired_mode","purge").equals("purge")) {
				LOG.info("Schedule timer for purging AOR cache");
				sapProvider.refreshCache();
				scheduleTimer();
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		LOG.info("init() completed successfully");
		LOG.info(String.format(LOG_TIME_TAKEN,computeTimeTaken(startTime, System.nanoTime())));
	}

	private void scheduleTimer() {

		String pattern = "HH:mm";

		String formattedDate = PLUGIN_PROPS.getProperty("aor_purge_time");

		Date scheduleTime = null;
		
		Calendar scheduleCal = Calendar.getInstance();

		if (formattedDate == null) {
			LOG.warn("aor_purge_time is not set. Cache refresh process will be started immediately");
		} else {	
			try {
				scheduleTime = new SimpleDateFormat(pattern).parse(formattedDate);
				scheduleCal.setTime(scheduleTime);
			} catch (Exception e) {
				LOG.error("Cannot parse aor_purge_time. Cache refresh process will be started immediately", e);
			}
		}

		String refreshPeriodString = "1_DAYS";

		TimeUnit unit = TimeUnit.MILLISECONDS;
		int refreshPeriod;

		String[] temp = refreshPeriodString.split("_");

		try {
			refreshPeriod = Integer.parseInt(temp[0]);
		} catch (IllegalArgumentException e) {
			LOG.error("Invalid cache_refresh_period value(s), resetting to 1_DAYS", e);
			refreshPeriod = 1000 * 60 * 60 * 24;
		}

		try {
			switch (temp[1]) {
			case "SECS":
				refreshPeriod = refreshPeriod * 1000;	
				break;
			case "MINS":
				refreshPeriod = refreshPeriod * 1000 * 60;
				break;
			case "HRS":
				refreshPeriod = refreshPeriod * 1000 * 60 * 60;
				break;
			case "DAYS":
				refreshPeriod = refreshPeriod * 1000 * 60 * 60 * 24;
				break;
			default:
			}
		} catch (Exception ex) {
			LOG.error("Invalid cache_refresh_period unit, resetting to DAYS", ex);
			refreshPeriod = refreshPeriod * 1000 * 60 * 60 * 24;
		}

		if (scheduleTime != null) {
			LOG.info(String.format("Cache refresh period is set as [%s]", Util.getDurationBreakdown(refreshPeriod)));
		}
		
		Long startTime = 0L;
		
		if (LocalDateTime.now().getHour() <= scheduleCal.get(Calendar.HOUR_OF_DAY)) {
			startTime=LocalDateTime.now().until(LocalDate.now().plusDays(0).atTime(scheduleCal.get(Calendar.HOUR_OF_DAY), scheduleCal.get(Calendar.MINUTE)), ChronoUnit.MILLIS) + System.currentTimeMillis();
		}
		else {
			startTime=LocalDateTime.now().until(LocalDate.now().plusDays(1).atTime(scheduleCal.get(Calendar.HOUR_OF_DAY), scheduleCal.get(Calendar.MINUTE)), ChronoUnit.MILLIS) + System.currentTimeMillis();;
		}
		
		LOG.info(String.format("Cache refresh process should start after [%s]",(startTime == null || startTime - System.currentTimeMillis() < 0) ? (0 + " minute")
						: Util.getDurationBreakdown(startTime - System.currentTimeMillis())));

		ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
		service.scheduleAtFixedRate(RefreshTask.getInstance(),(startTime == null) ? 0 : (startTime - System.currentTimeMillis()), refreshPeriod, unit);
		LOG.info("Cache refresh process has been scheduled");
	}

	@Override
	public Serializable prepareRequest(String id) {
		return null;
	}

	@Override
	public void processResponse(String id, String data) {
		return;
	}
	
	

	public synchronized IEvalValue getAttribute(IDSubject subj, String attribute) throws ServiceProviderException {
		
		try {
		
			long startTime = System.nanoTime();
			String sapID = subj.getUid();
			
			if (subj.getAttribute(Constants.REQUEST_KEY_EMPLOYEE_NO)==null) {
				LOG.error(String.format(LOG_EMPTY_EMPLOYEE_NUMBER, sapID));
				return EvalValue.NULL;
			}
					
			String employeeNumber = (String) subj.getAttribute(Constants.REQUEST_KEY_EMPLOYEE_NO).getValue();
	
			LOG.info(String.format(LOG_INCOMING_REUQEST, sapID, employeeNumber));
				
			LOG.debug(String.format("Getting attribute [%s] for [%s]", attribute.toLowerCase(), employeeNumber));
	
			UserObject userObj = engine.getUserObjectFromCache(employeeNumber);
	
			// try again with case insensitive
			if (userObj == null) {
				userObj = engine.getUserObjectFromCache(employeeNumber.toLowerCase());
			}
	
			// cache doesn't contain the user, query from AD
			if (userObj == null) {
				LOG.info(String.format(LOG_USER_CACHE_MISSED, employeeNumber));
	
				try {
					userObj = ldapProvider.getUserObject(employeeNumber, attribute.toLowerCase());
				} catch (Exception e) {
					LOG.error(String.format("Unable to query for USER [%s]", employeeNumber));
					LOG.error(e.getMessage(), e);
					return nullReturn;
				}
			}
	
			if (userObj == null) {
				LOG.warn(String.format("Cannot resolve attribute [%s] for [%s] after query IAM-LDAP", attribute, employeeNumber));
				LOG.info(String.format(LOG_TIME_TAKEN, computeTimeTaken(startTime, System.nanoTime())));
				return nullReturn;
			}
	
			//  Determine attribute from where
			if (ldapProvider.getProfile().getUserAttributesToPull().contains(attribute)) {
	
				LOG.info(String.format(LOG_USER_ATTRIBUTE_NEEDED, attribute));
	
				IEvalValue val = userObj.getAttribute(attribute.toLowerCase());
	
				if (val == null || val.getValue() == null) {
					LOG.info(String.format("Attribute [%s] is null for user [%s]", attribute, employeeNumber));
					val = nullReturn;
				}
	
				if (val.getValue() instanceof IMultivalue) {
					StringBuilder sb = new StringBuilder("[").append(employeeNumber).append("] has attribute [").append(attribute).append("] with value = ");
	
					boolean first = true;
					for (IEvalValue v : (IMultivalue) val.getValue()) {
						if (!first) {
							sb.append(", ");
						}
						first = false;
						if (v == null) {
							sb.append("null");
						} else {
							sb.append(v.getValue());
						}
					}
					LOG.debug(sb.toString());
				} else {
					LOG.debug(String.format("user [%s] has attribute [%s] with value = [%s]", employeeNumber, attribute, val.getValue()));
				}
				LOG.info(String.format(LOG_TIME_TAKEN, computeTimeTaken(startTime, System.nanoTime())));
				
				return val;
	
			} else {
	
				if (sapProvider.getProfile().getAORAttributesToPull().contains(attribute)) {
	
					LOG.info(String.format(LOG_SAP_ATTRIBUTE_NEEDED, attribute));
					
					//Handling for Provider refreshing cache.
					while (sapProvider.isRefreshing()) {
						
						LOG.info("SAP Provider is flusing AOR cache, sleep for 20ms then re-try");
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
							LOG.error(e.getMessage(), e);
						}
						
					}
	
					IEvalValue val = userObj.getAttribute(Constants.LDAP_ATTRIBUTE_AOR_KEY);
	
					if (val == null || val.getValue() == null) {
						LOG.warn(String.format("Attribute AmmoArea is null for user [%s]",employeeNumber));
						val = nullReturn;
					}
					
					//Reading  AOR List from user cache
					List<String> sAORList = new ArrayList<String>();
	
					if (val.getValue() instanceof IMultivalue) {
						StringBuilder sb = new StringBuilder("[" + employeeNumber + "] has attribute [" + Constants.LDAP_ATTRIBUTE_AOR_KEY + "] with value = ");
	
						boolean first = true;
						for (IEvalValue v : (IMultivalue) val.getValue()) {
							if (!first) {
								sb.append(", ");
							}
							first = false;
							if (v == null) {
								sb.append("null");
							} else {
								sb.append(v.getValue());
								sAORList.add((String)v.getValue());
							}
	
						}
						
						LOG.debug(sb.toString());
						
						return getSAPData(sAORList, employeeNumber, attribute, startTime);
	
					}
					LOG.info(String.format(LOG_TIME_TAKEN, computeTimeTaken(startTime, System.nanoTime())));
					return nullReturn;
				} else {
					LOG.info(String.format("Unknow attribute [%s] request from PEP, will return an empty value", attribute));
					LOG.info(String.format(LOG_TIME_TAKEN, computeTimeTaken(startTime, System.nanoTime())));
					return nullReturn;
				}
	
			}
		
		} catch (Exception e) {
			LOG.error("Fatal exception occured, returning null value");
			LOG.error(e.getMessage(), e);
			return nullReturn;
		}

	}
	
	private synchronized IEvalValue getSAPData(List<String> sAORList, String employeeNumber, String attribute2Search, long startTime) {

		List<String> returnList = new ArrayList<String>();
		List<String> queryList = new ArrayList<String>();

		for (String sAOR : sAORList) {

			LOG.debug(String.format(LOG_GET_AOR_ATTRIBUTE, sAOR, employeeNumber));

			SAPObject sapObj = engine.getAorObjectFromCache(sAOR);

			// try again with case insensitive
			if (sapObj == null) {
				sapObj = engine.getAorObjectFromCache(sAOR.toLowerCase());
			}

			// cache doesn't contain the AOR, query from SAP
			if (sapObj == null) {
				
				LOG.info(String.format(LOG_AOR_CACHE_MISSED, employeeNumber, sAOR));

				queryList.add(sAOR);
			} else {
				
				LOG.info(String.format(LOG_AOR_CACHE_HIT, employeeNumber, sAOR));
				
				List<String> result = sapObj.getAttributeAsList(attribute2Search);
				
				if (result == null || result.size() <1) {
					LOG.info(String.format("Attribute [%s] for user [%s] from cache for AOR [%s] is empty value", attribute2Search, employeeNumber, sAOR));
				}
				else {
					returnList.addAll(result);
				}
			}
		}//end for
		
		//Calling to SAP to get data
		if (queryList.size() > 0) {
			
			LOG.info(String.format("Calling SAP to get the AOR Data with AOR list [%s]",queryList.toString()));
			
			List<SAPObject> sapObjList = sapProvider.getSAPObject(queryList, attribute2Search);
			
			for (SAPObject sapObj1: sapObjList) {
				
				List<String> result = sapObj1.getAttributeAsList(attribute2Search);
				
				if (result == null || result.size() <1) {
					LOG.info(String.format("Cannot resolve attribute [%s] for [%s] from cache for AOR [%s]", attribute2Search, employeeNumber, sapObj1.getId()));
				}
				
				if (result != null)
					returnList.addAll(result);
			}
		}
		
		LOG.debug("SAP function return values: " + returnList.toString());
		LOG.info(String.format(LOG_TIME_TAKEN, computeTimeTaken(startTime, System.nanoTime())));
		
		if (returnList.isEmpty()) {
			return nullReturn;
		}
		// Compose to multivalue IEvalValue
		IEvalValue value = EvalValue.EMPTY;
		IMultivalue multiValue = Multivalue.create(returnList);
		value = EvalValue.build(multiValue);
		return value;
	}

	private String computeTimeTaken(long start, long end){
		double differenceInMilli = (end - start) / 1000000.00;
		return Double.toString(differenceInMilli);
	}

	public static void main(String args[]) {
				
		UserAttributeProvider uap = new UserAttributeProvider();
		uap.init();
	
		Subject subject=new Subject("AHMADNU3","AHMADNU3","AHMADNU3",null, SubjectType.USER);
		
		System.out.println(subject.getUid());
		try {
			Thread.sleep(1000);
			System.out.println(uap.getAttribute(subject, "SITE_CODE"));
		} catch (ServiceProviderException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}
}
