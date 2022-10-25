package com.nextlabs.nxljco.sap;

public interface IAttributeExtractor {
	/**
	 * Extract attribute in map
	 * 
	 * @param attribute
	 *            The attribute or key to search in the Map object.
	 * 
	 * @return value of attribute in Object class. You will need to cast it to
	 *         its specific class to make it meaningful. Null will be returned
	 *         if the attribute is not found in the Map object.
	 */
	public Object extract(String attribute);
}
