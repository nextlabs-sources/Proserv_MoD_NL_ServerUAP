package com.nextlabs.nxljco.sap.exporter;

import com.sap.conn.jco.JCoField;

public class FieldExporter implements IFieldExporter {

	private IStructureExporter structureExporter;
	private ITableExporter tableExporter;
	private IValueExporter valueExporter;

	@Override
	public Object export(JCoField field) {
		if (field == null) {
			return null;
		}

		Object value = null;
		if (field.isStructure()) {
			// Extract Structure
			value = getStructureExporter().export(field);
		} else if (field.isTable()) {
			// Extract Table
			value = getTableExporter().export(field);
		} else {
			// Extract Value (Number, String, Date)
			value = getValueExporter().export(field);
		}

		return value;
	}

	public void setStructureExporter(IStructureExporter structureExporter) {
		this.structureExporter = structureExporter;
	}

	public void setTableExporter(ITableExporter tableExporter) {
		this.tableExporter = tableExporter;
	}

	public void setValueExporter(IValueExporter valueExporter) {
		this.valueExporter = valueExporter;
	}

	private IStructureExporter getStructureExporter() {
		if (this.structureExporter == null) {
			// Create instance
			this.structureExporter = new StructureExporter();
		}
		return this.structureExporter;
	}

	private ITableExporter getTableExporter() {
		if (this.tableExporter == null) {
			// Create instance
			this.tableExporter = new TableExporter();
		}
		return this.tableExporter;
	}

	private IValueExporter getValueExporter() {
		if (this.valueExporter == null) {
			// Create instance
			this.valueExporter = new ValueExporter();
		}
		return this.valueExporter;
	}

}
