package com.nextlabs.nxljco.sap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.cache.CacheEngine;
import com.nextlabs.common.Constants;
import com.nextlabs.common.Profile;
import com.nextlabs.common.Provider;
import com.nextlabs.common.SAPObject;
import com.nextlabs.common.UserObject;
import com.nextlabs.exception.InvalidProfileException;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;	

public class SAPProvider implements Provider {
	private static final Log LOG = LogFactory.getLog(SAPProvider.class);
	private static SAPProvider provider;
	private SAPProfile singleProfile;
	private boolean isSingleProfile;
	private Map<String, String> idToObjectTypeMap;	
	private int numberOfRetries;		
	private int intervalBetweenRetries;
	private Boolean isRefreshing;
	private static Properties commonProp;
	
	private IJCoFunctionHandler functionHandler;

	public SAPProvider() {
		idToObjectTypeMap = new ConcurrentHashMap<String, String>();
		isRefreshing = false;
	}

	public static SAPProvider getInstance() {
		if (provider == null) {
			provider = new SAPProvider();
		}
		return provider;
	}
	
	private Map<String, Object> setupInput() {

		Map<String, Object> importsMap = new HashMap<String, Object>();
		
		importsMap.put(Constants.IMPORT_AOR_GET_ALL, Constants.IMPORT_AOR_GET_ALL_YES);

		return importsMap;
	}

	
	private Map<String, Object> setupInput(List<String> aor) {

		Map<String, Object> importsMap = new HashMap<String, Object>();
		
		importsMap.put(Constants.IMPORT_AOR_ID, aor);
		importsMap.put(Constants.IMPORT_AOR_GET_ALL, Constants.IMPORT_AOR_GET_ALL_NO);

		return importsMap;
	}

	@Override
	public void setCommonProperties(Properties props) {
		commonProp = props;
	}
	
	@Override
	public List<SAPObject> getSAPObject(List<String> aorId, String attributeToSearch) {

		List<SAPObject> object = null;
			
		object = queryForAOR(singleProfile, aorId);
	
		if (object == null) {
			LOG.error(String.format("Object [%s] cannot be queried from SAP", aorId));
		}

		return object;
	}

	private List<SAPObject> queryForAOR(SAPProfile sapProfile, List<String> aor) {
		
		// Setup Input
		Map<String, Object> importsMap = setupInput(aor);
		
		long startTime = System.nanoTime();
		
		// Call function
		Map<String, Object> outputValues = callFunction(importsMap, sapProfile);
		
		
		LOG.info(String.format("Query for AOR [%s] took [%s]ms", aor, computeTimeTaken(startTime,System.nanoTime())));
		
		// Extract values
		List<SAPObject> sapObjectList = extractData(outputValues, aor);		

		if (sapObjectList == null || sapObjectList.size() == 0) {
			LOG.error(String.format("Unable to get result from SAP [%s]", sapProfile.getName()));
		}

		return sapObjectList;
	}
	
	private String computeTimeTaken(long start, long end){
		double differenceInMilli = (end - start) / 1000000.00;
		return Double.toString(differenceInMilli);
	}

	
	private Map<String, Object> callFunction(Map<String, Object> importsMap, SAPProfile sapProfile) {
		Map<String, Object> outputValues = new HashMap<>();

		try {
			String serverPrefix = sapProfile.getServerPrefix();
			String functionName = sapProfile.getSAPHandler();
			
			LOG.info(String.format("Calling SAP function with server prefix [%s] and handler [%s]", serverPrefix, functionName));

			IJCoFunctionHandler handler = createFunctionHandler(serverPrefix);
			
			if(handler==null) {
				LOG.error(String.format("Server handler [%s] is unrecognized, returning empty data",serverPrefix));
				return new HashMap<String, Object>();
			}
			
			if(handler.getFunction(functionName)==null) {
				LOG.error(String.format("Function name [%s] is unrecognized, returning empty data",functionName));
				return new HashMap<String, Object>();
			}
						
			@SuppressWarnings("unchecked")
			List<String> sAORList = (List<String>)importsMap.get(Constants.IMPORT_AOR_ID);
			
			if(sAORList!=null && sAORList.size()>0) {
			
				JCoTable aorTable = handler.getFunction(functionName).getImportParameterList().getTable(Constants.IMPORT_AOR_ID);
				aorTable.clear();
				
				for (String sAOR : sAORList){
					LOG.debug(String.format("Adding AOR [%s] to JCOTable input", sAOR));
					aorTable.appendRow();
					aorTable.setValue(0, sAOR);
		        }
				//Remove the old entry
				importsMap.remove(importsMap.get(Constants.IMPORT_AOR_ID));
				//Add in new entry as JCOtable
				importsMap.put(Constants.IMPORT_AOR_ID, aorTable);
			}
			else {
				//Add handling for clear the old AOR table;
				JCoTable aorTable = handler.getFunction(functionName).getImportParameterList().getTable(Constants.IMPORT_AOR_ID);
				aorTable.clear();
			}
			
			
			handler.callFunction(functionName, importsMap);
			
			outputValues = handler.getRfcExport();
			
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage());
		} catch (IOException e) {
			LOG.error(e.getMessage());
		} catch (JCoException e) {
			LOG.error(e.getMessage());
		}
		return outputValues;
	}
	
	
	private IJCoFunctionHandler createFunctionHandler(String serverPrefix) {
		if (this.functionHandler == null) {
			this.functionHandler = new JCoFunctionHandler(serverPrefix);
		}

		return this.functionHandler;
	}
	
 
	private List<SAPObject> extractData(Map<String, Object> outputValues, List<String> queryList) {

		// Check response existence
		if (outputValues.containsKey(Constants.EXPORT_BAPI_RETURN)) {
			
			Object responseValues = outputValues.get(Constants.EXPORT_BAPI_RETURN);
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseValues;

			if (responseList != null && responseList.size() > 0) {
				LOG.warn("SAP return response code with error");
				
				for (Map<String, Object> rowResponse : responseList) {
					
					LOG.warn(String.format(" SAP response with Type [%s], ID [%s], Number [%s], Message [%s]", rowResponse.get(Constants.EXPORT_RESPONSE_TYPE), 
							rowResponse.get(Constants.EXPORT_RESPONSE_ID),rowResponse.get(Constants.EXPORT_RESPONSE_NUMBER),rowResponse.get(Constants.EXPORT_RESPONSE_MESSAGE)));

				}
				
				//Adding empty value SAPObject to cache for performance purpose
				for (String sAORID : queryList) {
					
		        		SAPObject sapObject = new SAPObject(sAORID);
		        		
		        		Map<String, List<String>> attributes = new HashMap<String, List<String>>();
		        		
		        		attributes.put(Constants.EXPORT_AOR_COLUMN_STORAGE_LOCATION, null);
		            	attributes.put(Constants.EXPORT_AOR_COLUMN_WAREHOUSE_NUMBER, null);
		            	attributes.put(Constants.EXPORT_AOR_COLUMN_BUSINESS_PARTNER, null);
		            	attributes.put(Constants.EXPORT_AOR_COLUMN_SHIPPING_POINT, null);
		            	attributes.put(Constants.EXPORT_AOR_COLUMN_FORCE_ELEMENT, null);
		            	
		            	sapObject.setAttributes(attributes);
		            			            	
		            	//Store the information into cache
		            	CacheEngine.getInstance().writeObjectToAORCache(sapObject);
		        }

			} else {
				
				Hashtable<String, Set<String>> sLocTable = new Hashtable<String, Set<String>>();
				Hashtable<String, Set<String>> sWhNumTable = new Hashtable<String, Set<String>>();
				Hashtable<String, Set<String>> sBizPartnerTable = new Hashtable<String, Set<String>>();
				Hashtable<String, Set<String>> sShippingPointTable = new Hashtable<String, Set<String>>();
				Hashtable<String, Set<String>> sFeTable = new Hashtable<String, Set<String>>();
				Set<String> sAorIDList = new HashSet<String>();

				// Extract
				Object extractedValues = outputValues.get(Constants.EXPORT_AOR_DETAILS);

				if (extractedValues != null && extractedValues instanceof List) {

					@SuppressWarnings("unchecked")
					List<Map<String, Object>> valuesList = (List<Map<String, Object>>) extractedValues;

					for (Map<String, Object> row : valuesList) {

						String sAorID = (String) row.get(Constants.EXPORT_AOR_ID);
						// Always store in lower case for key
						String sKey = ((String) row.get(Constants.EXPORT_AOR_KEY)).toLowerCase();
						String sValue = (String) row.get(Constants.EXPORT_AOR_VALUE);

						if (sKey.equals(Constants.EXPORT_AOR_COLUMN_STORAGE_LOCATION)) {

							Set<String> sData = new HashSet<String>();

							if (sLocTable.containsKey(sAorID)) {

								sData = sLocTable.get(sAorID);

								sData.add(sValue);
								
							} else {

								sData.add(sValue);
							}
							sLocTable.put(sAorID, sData);
							LOG.debug(String.format("SAP have value %s for %s",sData, Constants.EXPORT_AOR_COLUMN_STORAGE_LOCATION));
							sAorIDList.add(sAorID);

						} else if (sKey.equals(Constants.EXPORT_AOR_COLUMN_WAREHOUSE_NUMBER)) {

							Set<String> sData = new HashSet<String>();

							if (sWhNumTable.containsKey(sAorID)) {

								sData = sWhNumTable.get(sAorID);

								sData.add(sValue);
							} else {

								sData.add(sValue);
							}
							sWhNumTable.put(sAorID, sData);
							LOG.debug(String.format("SAP have value %s for %s",sData, Constants.EXPORT_AOR_COLUMN_WAREHOUSE_NUMBER));
							sAorIDList.add(sAorID);

						} else if (sKey.equals(Constants.EXPORT_AOR_COLUMN_BUSINESS_PARTNER)) {

							Set<String> sData = new HashSet<String>();

							if (sBizPartnerTable.containsKey(sAorID)) {

								sData = sBizPartnerTable.get(sAorID);

								sData.add(sValue);
							} else {

								sData.add(sValue);
							}
							sBizPartnerTable.put(sAorID, sData);
							LOG.debug(String.format("SAP have value %s for %s",sData, Constants.EXPORT_AOR_COLUMN_BUSINESS_PARTNER));
							sAorIDList.add(sAorID);

						} else if (sKey.equals(Constants.EXPORT_AOR_COLUMN_SHIPPING_POINT)) {

							Set<String> sData = new HashSet<String>();

							if (sShippingPointTable.containsKey(sAorID)) {

								sData = sShippingPointTable.get(sAorID);

								sData.add(sValue);
							} else {

								sData.add(sValue);
							}
							sShippingPointTable.put(sAorID, sData);
							LOG.debug(String.format("SAP have value %s for %s",sData, Constants.EXPORT_AOR_COLUMN_SHIPPING_POINT));
							sAorIDList.add(sAorID);

						} else if (sKey.equals(Constants.EXPORT_AOR_COLUMN_FORCE_ELEMENT)) {

							Set<String> sData = new HashSet<String>();

							if (sFeTable.containsKey(sAorID)) {

								sData = sFeTable.get(sAorID);

								sData.add(sValue);
							} else {

								sData.add(sValue);
							}
							sFeTable.put(sAorID, sData);
							LOG.debug(String.format("SAP have value %s for %s",sData, Constants.EXPORT_AOR_COLUMN_FORCE_ELEMENT));
							sAorIDList.add(sAorID);

						}

					}

					return processData2Cache(sLocTable, sWhNumTable, sBizPartnerTable, sShippingPointTable, sFeTable,
							sAorIDList, queryList);

				} else {
					LOG.warn("SAP response contain empty data");
				}
			}
		}
		else {
			LOG.error("SAP function return empty response, will return empty list");
		}

		return new ArrayList<SAPObject>();
	}
	
	private List<SAPObject> processData2Cache(Hashtable<String, Set<String>> sLocTable, Hashtable<String, Set<String>> sWhNumTable,
			Hashtable<String, Set<String>> sBizPartnerTable, Hashtable<String, Set<String>> sShippingPointTable, Hashtable<String, Set<String>> sFeTable, Set<String> sAorIDList, List<String> queryList) {
			
		Iterator<String> setIterator = sAorIDList.iterator();
		List<SAPObject> sapObjectList = new ArrayList<SAPObject>();
		
        while(setIterator.hasNext()){
            
        	String sAorID = setIterator.next();
        	
        	Map<String, List<String>> attributes = new HashMap<String, List<String>>();
        	
        	SAPObject sapObject = new SAPObject(sAorID);
        	        	
        	attributes.put(Constants.EXPORT_AOR_COLUMN_STORAGE_LOCATION, convert2List(sLocTable.get(sAorID)));
        	attributes.put(Constants.EXPORT_AOR_COLUMN_WAREHOUSE_NUMBER, convert2List(sWhNumTable.get(sAorID)));
        	attributes.put(Constants.EXPORT_AOR_COLUMN_BUSINESS_PARTNER, convert2List(sBizPartnerTable.get(sAorID)));
        	attributes.put(Constants.EXPORT_AOR_COLUMN_SHIPPING_POINT, convert2List(sShippingPointTable.get(sAorID)));
        	attributes.put(Constants.EXPORT_AOR_COLUMN_FORCE_ELEMENT, convert2List(sFeTable.get(sAorID)));
        	
        	sapObject.setAttributes(attributes);
        	
        	sapObjectList.add(sapObject);
        	
        	//Store the information into cache
        	CacheEngine.getInstance().writeObjectToAORCache(sapObject);
        	
        }
        
        
      //Adding empty value SAPObject to cache for performance purpose
        for (String sAORID : queryList) {
        	
        	if (!sAorIDList.contains(sAORID)) {
        		
        		SAPObject sapObject = new SAPObject(sAORID);
        		
        		Map<String, List<String>> attributes = new HashMap<String, List<String>>();
        		
        		attributes.put(Constants.EXPORT_AOR_COLUMN_STORAGE_LOCATION, null);
            	attributes.put(Constants.EXPORT_AOR_COLUMN_WAREHOUSE_NUMBER, null);
            	attributes.put(Constants.EXPORT_AOR_COLUMN_BUSINESS_PARTNER, null);
            	attributes.put(Constants.EXPORT_AOR_COLUMN_SHIPPING_POINT, null);
            	attributes.put(Constants.EXPORT_AOR_COLUMN_FORCE_ELEMENT, null);
            	
            	sapObject.setAttributes(attributes);
            	            	
            	//Store the information into cache
            	CacheEngine.getInstance().writeObjectToAORCache(sapObject);
        	}
        }
		return sapObjectList;
		
	}
	
	
	private List<String> convert2List(Set<String> attributeValueSet) {
		
		if(attributeValueSet!=null)
			return (new ArrayList<String>(attributeValueSet));
		else
			return (new ArrayList<String>());
	}
	

	@Override
	public synchronized void refreshCache() {
	
		//Clear all data in AOR Cache
		CacheEngine.getInstance().purgeAORCache();
		
		if (commonProp.getProperty("aor_refresh","true").equalsIgnoreCase("true")) {

			isRefreshing = true;

			long startTime = System.nanoTime();

			int count = 0;

			while (true) {
				try {
					if (isSingleProfile) {
						refreshProfile(singleProfile);
					} 
					break;

				} catch (Exception e) {

					LOG.error("AOR Cache refresh encountered an exception.", e);

					if (count++ == numberOfRetries) {
						LOG.error(String.format("Attempted [%d] retries without success.", numberOfRetries));
						break;
					} else {
						LOG.debug(String.format("Retrying refreshing cache in [%d] seconds..", intervalBetweenRetries));
						try {
							Thread.sleep(intervalBetweenRetries * 1000);
						} catch (InterruptedException ie) {
							// IGNORE
						}
					}
				}
			}

			long endTime = System.nanoTime();

			isRefreshing = false;

			LOG.info("AOR Cache refresh completed");
			LOG.info("Time Taken: " + computeTimeTaken(startTime, endTime) + "ms");

		}
		else{
			LOG.info("Skip reload cache since the aor_refresh is NOT true");
		}
	}

	private void refreshProfile(SAPProfile sapProfile) throws NamingException {
		
		LOG.info(String.format("Started refreshing server [%s]", sapProfile.getName()));

		if (!sapProfile.getIsValid()) {
			LOG.error(String.format("Profile [%s] is invalid. Skip refreshing.", sapProfile.getName()));
			return;
		}
				
		// Setup Input
		Map<String, Object> importsMap = setupInput();
				
		long startTime = System.nanoTime();
				
		// Call function
		Map<String, Object> outputValues = callFunction(importsMap, sapProfile);
				
		long endTime = System.nanoTime();
				
		LOG.info(String.format("Query for ALL AOR took [%s]ms", computeTimeTaken(startTime, endTime)));
				
		// Extract values
		List<SAPObject> sapObjectList = extractData(outputValues, new ArrayList<String>());		

		if (sapObjectList == null || sapObjectList.size() == 0) {
			LOG.error(String.format("Unable to get result from SAP [%s]", sapProfile.getName()));
		}

	}
	

	@Override
	public void loadSingleProfile(Properties props) {
		
		String name = props.getProperty("sap_server_prefix");

		LOG.info(String.format("Loading profile of SAP [%s]", name));
		
		SAPProfile profile = new SAPProfile(name);
		try {
			profile.parseProfile(props);
			singleProfile = profile;
		} catch (InvalidProfileException ipe) {
			LOG.error(String.format("Invalid profile for domain [%s]", name), ipe);
		}

	}

	public boolean getIsSingleProfile() {
		return isSingleProfile;
	}

	@Override
	public void setIsSingleProfile(Boolean isSingleProfile) {
		this.isSingleProfile = isSingleProfile;
	}

	@Override
	public String getPDPObjectType(String id) {
		return idToObjectTypeMap.get(id);
	}

	@Override
	public Boolean isRefreshing() {
		return isRefreshing;
	}

	@Override
	public void loadProfiles(Properties props) {
		//Skip implementation
	}

	@Override
	public UserObject getUserObject(String id, String attributeToSearch) throws Exception {
		//skip implementation
		return null;
	}

	@Override
	public Profile getProfile() {
		return singleProfile;
	}
}
