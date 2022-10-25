package com.nextlabs.nxljco.sap.exporter;

import com.sap.conn.jco.JCoField;

public class ValueExporter implements IValueExporter {

	@Override
	public Object export(JCoField field) {
		if (field == null) {
			return null;
		}

		Object value = field.getValue();
		return value;
	}

}
