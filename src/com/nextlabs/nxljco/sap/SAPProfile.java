package com.nextlabs.nxljco.sap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.common.Profile;
import com.nextlabs.exception.InvalidProfileException;

public class SAPProfile extends Profile {
	private String serverPrefix;
	private String sapHandler;
	private List<String> aorKeyAttributes;
	private Set<String> aorAttributesToPull;
	private Map<String, Boolean> aorCardinalityMap;
	private Map<String, Boolean> aorKeyCaseSensitiveMap;
	private Boolean isValid;

	private static final Log LOG = LogFactory.getLog(SAPProfile.class);

	public SAPProfile(String name) {
		super(name);
		aorKeyAttributes = new ArrayList<String>();
		aorAttributesToPull = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		aorCardinalityMap = new HashMap<String, Boolean>();
		aorKeyCaseSensitiveMap = new HashMap<String, Boolean>();
	}

	public void parseProfile(Properties props) throws InvalidProfileException {

		LOG.info(String.format("Started parsing profile for SAP [%s]", this.name));

		if (props == null) {
			isValid = false;
			throw new InvalidProfileException("Properties is undefined");
		}

		serverPrefix = getProperty("sap_server_prefix", props);

		if (serverPrefix == null || serverPrefix.length() == 0) {
			isValid = false;
			throw new InvalidProfileException("SAP Server Prefix is undefined");
		}


		sapHandler = getProperty("sap_handler", props);

		if (sapHandler == null || sapHandler.length() == 0) {
			isValid = false;
			throw new InvalidProfileException(
					"SAP Handler filter is undefined. The plugin cannot search for everything.");
		}

//		String aor_key_attributes = getProperty("aor_key_attributes", props);
//
//		if (aor_key_attributes == null || aor_key_attributes.length() == 0) {
//			isValid = false;
//			throw new InvalidProfileException("User key attribute is undefined");
//		}
//
//		for (String attr : aor_key_attributes.split(",")) {
//
//			String[] pAttr = attr.trim().split(":");
//
//			if (pAttr.length == 2) {
//				aorKeyAttributes.add(pAttr[1].trim());
//				aorKeyCaseSensitiveMap.put(pAttr[1], (pAttr[0].equals("cs") ? true : false));
//			} else {
//				LOG.error(String.format("Key attribute [%s] is invalid", attr));
//			}
//		}
//
//		LOG.info(String.format("AOR key attributes for SAP [%s] are [%s] ", name, aorKeyAttributes));

		String aorAttr2Pull = getProperty("aor_attributes_to_pull", props);

		if (aorAttr2Pull == null || aorAttr2Pull.length() == 0) {
			isValid = false;
			throw new InvalidProfileException("No AOR attribute to pull");
		}

		for (String attr : aorAttr2Pull.split(",")) {
			String[] pAttr = attr.trim().split(":");

			if (pAttr.length == 2) {
				aorAttributesToPull.add(pAttr[1].trim());
				aorCardinalityMap.put(pAttr[1], (pAttr[0].equals("multi") ? true : false));
			} else {
				LOG.error(String.format("Attribute [%s] is invalid", attr));
			}
		}

		LOG.info(String.format("AOR attributes to pull for domain [%s] are [%s] ", name, aorAttributesToPull));


		isValid = true;

		LOG.info(String.format("Finished parsing profile for domain [%s]", this.name));
	}

	public Boolean isUserMultiAttribute(String attributeName) {
		return (aorCardinalityMap.get(attributeName));
	}

	
	public Boolean isAORKeyCaseSensitive(String key) {
		return (aorKeyCaseSensitiveMap.get(key));
	}

	private String getProperty(String name, Properties props) {
		return props.getProperty(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServerPrefix() {
		return serverPrefix;
	}

	public void setServerPrefix(String host) {
		this.serverPrefix = host;
	}


	public String getSAPHandler() {
		return sapHandler;
	}

	public void setSAPHandler(String sapHandler) {
		this.sapHandler = sapHandler;
	}


	public List<String> getAORKeyAttributes() {
		return aorKeyAttributes;
	}

	public void setAORKeyAttributes(List<String> aorKeyAttributes) {
		this.aorKeyAttributes = aorKeyAttributes;
	}
	
	@Override
	public Set<String> getAORAttributesToPull() {
		return aorAttributesToPull;
	}

	public void setAORAttributesToPull(Set<String> aorAttributesToPull) {
		this.aorAttributesToPull = aorAttributesToPull;
	}


	public Map<String, Boolean> getAORCardinalityMap() {
		return aorCardinalityMap;
	}

	public void setAORCardinalityMap(Map<String, Boolean> aorCardinalityMap) {
		this.aorCardinalityMap = aorCardinalityMap;
	}


	public Boolean getIsValid() {
		return isValid;
	}

	public void setIsValid(Boolean isValid) {
		this.isValid = isValid;
	}

}
