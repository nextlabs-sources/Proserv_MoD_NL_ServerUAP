package com.nextlabs.nxljco.sap.exporter;

import com.sap.conn.jco.JCoField;

public interface IFieldExporter {
	/**
	 * Extract and export the JCoField item into a physical Java Object.
	 * 
	 * @param field
	 *            JCoField item to be exported.
	 * @return Value of the JCoField.
	 */
	public Object export(JCoField field);
}
