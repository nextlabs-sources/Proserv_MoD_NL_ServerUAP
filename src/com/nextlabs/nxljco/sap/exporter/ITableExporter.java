package com.nextlabs.nxljco.sap.exporter;

import java.util.List;

import com.sap.conn.jco.JCoField;

public interface ITableExporter {
	/**
	 * Extract and export the JCoField item into a physical Java List Object.
	 * 
	 * @param field
	 *            JCoField item to be exported.
	 * @return List object representation of the JCoField.
	 */
	public List<Object> export(JCoField field);
}
