package com.nextlabs.common;

import java.util.List;
import java.util.Properties;

public interface Provider {

	public void setCommonProperties(Properties props);

	public UserObject getUserObject(String id, String attributeToSearch) throws Exception;
	
	public List<SAPObject> getSAPObject(List<String> aorId, String attributeToSearch);

	public void refreshCache();

	public void loadProfiles(Properties props);
	
	public void loadSingleProfile(Properties props);
	
	public void setIsSingleProfile(Boolean isSingleProfile);	
	
	public String getPDPObjectType(String id);
	
	public Boolean isRefreshing();
	
	public Profile getProfile();
}
	