package com.nextlabs.nxljco.sap.exporter;

import java.util.Map;

import com.sap.conn.jco.JCoParameterList;

public interface IParameterExporter {

	/**
	 * Extracts return values of the JCo Function as a Map object.
	 * 
	 * @param jCoParameters
	 *            Export JCo Parameters extracted from the JCo Function.
	 * @return Extracted return values in the form of a Map object. The Map
	 *         object can be several levels deep. Please check with you SAP
	 *         administrator/developer for the Export structure.
	 */
	public Map<String, Object> exportParameter(JCoParameterList jCoParameters);

}
