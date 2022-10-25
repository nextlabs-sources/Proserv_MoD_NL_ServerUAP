package com.nextlabs.nxljco.sap.exporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoTable;

public class TableExporter implements ITableExporter {

	private IFieldExporter fieldExporter;
	private IExportIterator exportIterator;

	@Override
	public List<Object> export(JCoField field) {
		if (field == null) {
			return new ArrayList<Object>();
		}

		JCoTable table = field.getTable();
		if (table == null || table.isEmpty()) {
			return new ArrayList<Object>();
		}

		List<Object> exportList = iterateRows(table);
		return exportList;
	}

	private IFieldExporter getFieldExporter() {
		if (this.fieldExporter == null) {
			// Create instance
			this.fieldExporter = new FieldExporter();
		}

		return this.fieldExporter;
	}

	public void setFieldExporter(IFieldExporter fieldExporter) {
		this.fieldExporter = fieldExporter;
	}

	private IExportIterator getExportIterator(JCoFieldIterator iterator) {
		if (this.exportIterator == null) {
			// Create instance
			this.exportIterator = new ExportIterator();
		}
		this.exportIterator.setIterator(iterator);
		return exportIterator;
	}

	public void setExportIterator(IExportIterator iterator) {
		this.exportIterator = iterator;
	}

	private List<Object> iterateRows(JCoTable table) {
		List<Object> rowsList = new ArrayList<>();

		boolean tableOutOfBound = false;
		table.firstRow();

		while (!tableOutOfBound) {
			// Extract row
			JCoFieldIterator iterator = table.getFieldIterator();
			Object rowValue = iterateColumns(iterator);
			if (rowValue != null) {
				rowsList.add(rowValue);
			}

			// Check last row
			if (table.isLastRow()) {
				tableOutOfBound = true;
			} else {
				table.nextRow();
			}
		}
		return rowsList;
	}

	private Object iterateColumns(JCoFieldIterator iterator) {
		Object rowValue = null;
		Map<String, Object> rowMap = new HashMap<String, Object>();

		boolean isMap = false;

		IExportIterator exportIterator = getExportIterator(iterator);
		JCoField field = exportIterator.next();

		while (field != null) {
			String key = field.getName();

			IFieldExporter fieldExporter = getFieldExporter();
			if (key.isEmpty()) {
				// Extract value
				rowValue = fieldExporter.export(field);
				isMap = false;
			} else {
				// Extract and insert into map
				Object value = fieldExporter.export(field);
				rowMap.put(key, value);
				isMap = true;
			}

			field = exportIterator.next();
		}

		// Check return type
		if (isMap) {
			return rowMap;
		} else {
			return rowValue;
		}
	}

}
