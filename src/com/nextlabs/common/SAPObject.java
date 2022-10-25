package com.nextlabs.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SAPObject {
	private String id;
	private Map<String, List<String>> attributes;

	public SAPObject(String id) {
		this.id = id;
		attributes = new HashMap<String, List<String>>();
	}

	public void addAttribute(String key, List<String> value) {
		attributes.put(key, value);
	}

		
	public List<String> getAttributeAsList(String key) {
		return attributes.get(key);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Map<String, List<String>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, List<String>> attributes) {
		this.attributes = attributes;
	}

}
