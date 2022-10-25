package com.nextlabs.nxljco.sap.exporter;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;

public class ExportIterator implements IExportIterator {
	private JCoFieldIterator iterator;

	@Override
	public void setIterator(JCoFieldIterator iterator) {
		this.iterator = iterator;
	}

	@Override
	public JCoField next() {
		if (iterator == null) {
			return null;
		}

		if (iterator.hasNextField()) {
			return iterator.nextField();
		}

		return null;
	}
}
