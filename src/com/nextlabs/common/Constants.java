package com.nextlabs.common;

public class Constants {
	// Plugin Properties
	public static final String PROPERTIES_SERVER_PREFIX = "sap_server_prefix";
	public static final String PROPERTIES_HANDLER = "sap_handler";
	public static final String PROPERTIES_AMMO_LEVEL_ATTR_KEY = "ammo_level_attribute_key";
	public static final String PROPERTIES_AMMO_FULL_VALUE = "ammo_full_value";
	public static final String PROPERTIES_AMMO_AOR_VALUE = "ammo_aor_value";
	
	// Default Properties
	public static final String DEFAULT_SERVER_PREFIX = "SERV3_";
	public static final String DEFAULT_HANDLER = "ZFM_NXL_GET_AOR_ATTRS";
	
	//SAP Request
	public static final String REQUEST_KEY_EMPLOYEE_NO= "employeenumber";

	// SAP RFC Imports
	public static final String IMPORT_AOR_ID = "IT_AOR_ID";
	public static final String IMPORT_AOR_GET_ALL = "IV_GET_ALL";
	public static final String IMPORT_AOR_GET_ALL_YES = "X";
	public static final String IMPORT_AOR_GET_ALL_NO = "";

	// SAP RFC Export
	public static final String EXPORT_AOR_DETAILS = "ET_AOR_DETAILS";
	public static final String EXPORT_BAPI_RETURN = "ET_RETURN";
	
	//SAP AOR_DETAILS Table Column Name
	public static final String EXPORT_AOR_ID = "AOR_ID";
	public static final String EXPORT_AOR_KEY = "KEY";
	public static final String EXPORT_AOR_VALUE = "VALUE";
	
	public static final String EXPORT_AOR_COLUMN_STORAGE_LOCATION = "sloc";
	public static final String EXPORT_AOR_COLUMN_WAREHOUSE_NUMBER = "whnum";
	public static final String EXPORT_AOR_COLUMN_BUSINESS_PARTNER = "bizpn";
	public static final String EXPORT_AOR_COLUMN_SHIPPING_POINT = "shippt";
	public static final String EXPORT_AOR_COLUMN_FORCE_ELEMENT= "fe";
	
	public static final String EXPORT_RESPONSE_TYPE = "TYPE";
	public static final String EXPORT_RESPONSE_ID = "ID";
	public static final String EXPORT_RESPONSE_NUMBER = "NUMBER";
	public static final String EXPORT_RESPONSE_MESSAGE = "MESSAGE";
	
	//LDAP to SAP Mapping
	public static final String LDAP_ATTRIBUTE_AOR_KEY = "ammoarea";
	
	
	// General
	public static final String YES = "YES";
	public static final String NO = "NO";

}
