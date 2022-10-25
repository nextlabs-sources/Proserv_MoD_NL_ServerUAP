package com.nextlabs.nxljco.sap.exporter;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.nxljco.sap.AttributeExtractor;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoParameterList;

public class ParameterExporter implements IParameterExporter {

	private IFieldExporter fieldExporter;
	private IExportIterator exportIterator;
	private static final Log LOG = LogFactory.getLog(AttributeExtractor.class);

	@Override
	public Map<String, Object> exportParameter(JCoParameterList jCoParameters) {
		// Check Parameters
		if (jCoParameters == null) {
			return new HashMap<>();
		}

		// Debug log
		LOG.debug(jCoParameters.toXML());
		
		// Export
		JCoFieldIterator iterator = jCoParameters.getFieldIterator();
		Map<String, Object> exportMap = iterateStructureSubFields(iterator);

		return exportMap;
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
