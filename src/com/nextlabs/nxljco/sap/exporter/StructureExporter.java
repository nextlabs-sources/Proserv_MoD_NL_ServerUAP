package com.nextlabs.nxljco.sap.exporter;

import java.util.HashMap;
import java.util.Map;

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoStructure;

public class StructureExporter implements IStructureExporter {

	private IFieldExporter fieldExporter;
	private IExportIterator exportIterator;

	@Override
	public Map<String, Object> export(JCoField field) {
		if (field == null) {
			return new HashMap<String, Object>();
		}

		Map<String, Object> valueMap = new HashMap<String, Object>();

		JCoStructure structure = field.getStructure();
		if (structure == null) {
			return new HashMap<String, Object>();
		}

		// Extract Structure
		JCoFieldIterator iterator = structure.getFieldIterator();
		valueMap = iterateStructureSubFields(iterator);

		return valueMap;
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

	private Map<String, Object> iterateStructureSubFields(JCoFieldIterator iterator) {
		Map<String, Object> exportMap = new HashMap<>();
		IExportIterator exportIterator = getExportIterator(iterator);
		JCoField field = exportIterator.next();
		while (field != null) {
			// Name
			String key = field.getName();

			// Value
			IFieldExporter fieldExporter = getFieldExporter();
			Object value = fieldExporter.export(field);
			exportMap.put(key, value);

			field = exportIterator.next();
		}

		return exportMap;
	}
}
