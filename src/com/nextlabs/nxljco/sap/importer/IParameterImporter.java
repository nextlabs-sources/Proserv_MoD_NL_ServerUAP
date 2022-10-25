package com.nextlabs.nxljco.sap.importer;

import java.util.Map;

import com.sap.conn.jco.JCoParameterList;

public interface IParameterImporter {
	/**
	 * Imports the parameters as a Map object into the JCo Parameter object
	 * instance.
	 * 
	 * @param imports
	 *            Map object of the parameters to be imported. (Currently
	 *            supports a single layer only, meaning only String, Date and
	 *            Number is allowed)
	 * @param jCoParameters
	 *            Import JCo Parameter extracted from the JCo Function.
	 * 
	 * @exception IllegalArgumentException
	 */
	public void importParameter(Map<String, Object> imports, JCoParameterList jCoParameters)
			throws IllegalArgumentException;
}
