package com.nextlabs.nxljco.sap.exporter;

import java.util.Map;

import com.sap.conn.jco.JCoField;

public interface IStructureExporter {
	/**
	 * Extract and export the JCoField item into a physical Java Map Object.
	 * 
	 * @param field
	 *            JCoField item to be exported.
	 * @return Map object representation of the JCoField.
	 */
	public Map<String, Object> export(JCoField field);
}
