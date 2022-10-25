package com.nextlabs.nxljco.sap.exporter;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;

public interface IExportIterator {
	/**
	 * Gets the next JCoField item
	 * 
	 * @return the referenced JCoField item
	 */
	public JCoField next();

	/**
	 * Sets or Update the iterator to be used
	 * 
	 * @param iterator
	 *            Iterator to be used
	 */
	public void setIterator(JCoFieldIterator iterator);

}
