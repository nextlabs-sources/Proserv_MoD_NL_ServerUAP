package com.nextlabs.common;

import java.util.Set;

public abstract class Profile {
	protected String name;
	
	public Profile(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Set<String> getUserAttributesToPull() {
		return null;
	}
	
	public Set<String> getAORAttributesToPull() {	
		return null;
	}

}
