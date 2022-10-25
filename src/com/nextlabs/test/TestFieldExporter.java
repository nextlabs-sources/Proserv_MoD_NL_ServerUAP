package com.nextlabs.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.nextlabs.nxljco.sap.exporter.FieldExporter;
import com.nextlabs.nxljco.sap.exporter.IFieldExporter;
import com.nextlabs.nxljco.sap.exporter.IStructureExporter;
import com.nextlabs.nxljco.sap.exporter.ITableExporter;
import com.nextlabs.nxljco.sap.exporter.IValueExporter;
import com.sap.conn.jco.JCoField;

public class TestFieldExporter {

	@Test
	public void testExportingNull() {
		JCoField mockField = mock(JCoField.class);
		IStructureExporter mockStructureExporter = mock(IStructureExporter.class);
		ITableExporter mockTableExporter = mock(ITableExporter.class);
		IValueExporter mockValueExporter = mock(IValueExporter.class);
		when(mockStructureExporter.export(mockField)).thenReturn(new HashMap<String, Object>());
		when(mockTableExporter.export(mockField)).thenReturn(new ArrayList<Object>());
		when(mockValueExporter.export(mockField)).thenReturn("value");

		IFieldExporter exporter = new FieldExporter();
		((FieldExporter) exporter).setStructureExporter(mockStructureExporter);
		((FieldExporter) exporter).setTableExporter(mockTableExporter);
		((FieldExporter) exporter).setValueExporter(mockValueExporter);

		Object value = exporter.export(null);
		assertNull(value);
	}

	@Test
	public void testExportingStructure() {
		JCoField mockField = mock(JCoField.class);
		IStructureExporter mockStructureExporter = mock(IStructureExporter.class);
		when(mockField.isTable()).thenReturn(false);
		when(mockField.isStructure()).thenReturn(true);
		when(mockField.isInitialized()).thenReturn(false);
		when(mockStructureExporter.export(mockField)).thenReturn(new HashMap<String, Object>());

		IFieldExporter exporter = new FieldExporter();
		((FieldExporter) exporter).setStructureExporter(mockStructureExporter);

		Object value = exporter.export(mockField);
		assertTrue(value instanceof Map);
	}

	@Test
	public void testExportingTable() {
		JCoField mockField = mock(JCoField.class);
		ITableExporter mockTableExporter = mock(ITableExporter.class);
		when(mockField.isTable()).thenReturn(true);
		when(mockField.isStructure()).thenReturn(false);
		when(mockField.isInitialized()).thenReturn(false);
		when(mockTableExporter.export(mockField)).thenReturn(new ArrayList<Object>());

		IFieldExporter exporter = new FieldExporter();
		((FieldExporter) exporter).setTableExporter(mockTableExporter);

		Object value = exporter.export(mockField);
		assertTrue(value instanceof List);
	}

	@Test
	public void testExportingValue() {
		JCoField mockField = mock(JCoField.class);
		IValueExporter mockValueExporter = mock(IValueExporter.class);
		when(mockField.isTable()).thenReturn(false);
		when(mockField.isStructure()).thenReturn(false);
		when(mockField.isInitialized()).thenReturn(false);
		when(mockValueExporter.export(mockField)).thenReturn("value");

		IFieldExporter exporter = new FieldExporter();
		((FieldExporter) exporter).setValueExporter(mockValueExporter);

		Object value = exporter.export(mockField);
		assertEquals(value, "value");
	}
}
