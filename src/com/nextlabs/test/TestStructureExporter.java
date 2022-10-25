package com.nextlabs.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.nextlabs.nxljco.sap.exporter.IExportIterator;
import com.nextlabs.nxljco.sap.exporter.IFieldExporter;
import com.nextlabs.nxljco.sap.exporter.IStructureExporter;
import com.nextlabs.nxljco.sap.exporter.StructureExporter;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoStructure;

public class TestStructureExporter {

	@Test
	public void testExportingNull() {
		IStructureExporter exporter = new StructureExporter();

		Map<String, Object> value = exporter.export(null);
		assertTrue(value.isEmpty());
	}

	@Test
	public void testExportingNullStructure() {
		IStructureExporter exporter = new StructureExporter();
		JCoField mockField = mock(JCoField.class);
		when(mockField.isStructure()).thenReturn(true);
		when(mockField.getStructure()).thenReturn(null);

		Map<String, Object> value = exporter.export(mockField);
		assertTrue(value.isEmpty());
	}

	@Test
	public void testExportingSingleValue() {
		Map<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("key1", "value1");

		IFieldExporter mockFieldExporter = mock(IFieldExporter.class);
		IExportIterator mockExportIterator = mock(IExportIterator.class);
		JCoField mockField = mock(JCoField.class);
		JCoField mockSubField = mock(JCoField.class);
		JCoStructure mockStructure = mock(JCoStructure.class);

		when(mockSubField.getName()).thenReturn("key1");
		when(mockFieldExporter.export(mockSubField)).thenReturn("value1");
		when(mockField.isStructure()).thenReturn(true);
		when(mockField.getStructure()).thenReturn(mockStructure);
		when(mockExportIterator.next()).thenReturn(mockSubField).thenReturn(null);

		IStructureExporter exporter = new StructureExporter();
		((StructureExporter) exporter).setFieldExporter(mockFieldExporter);
		((StructureExporter) exporter).setExportIterator(mockExportIterator);

		Map<String, Object> value = exporter.export(mockField);
		assertEquals(expectedMap, value);
	}

	@Test
	public void testExportingMultiValue() {
		Map<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("key1", "value1");
		expectedMap.put("key2", "value2");
		expectedMap.put("key3", "value3");

		IFieldExporter mockFieldExporter = mock(IFieldExporter.class);
		IExportIterator mockExportIterator = mock(IExportIterator.class);
		JCoField mockField = mock(JCoField.class);
		JCoField mockSubField1 = mock(JCoField.class);
		JCoField mockSubField2 = mock(JCoField.class);
		JCoField mockSubField3 = mock(JCoField.class);
		JCoStructure mockStructure = mock(JCoStructure.class);

		when(mockSubField1.getName()).thenReturn("key1");
		when(mockFieldExporter.export(mockSubField1)).thenReturn("value1");
		when(mockSubField2.getName()).thenReturn("key2");
		when(mockFieldExporter.export(mockSubField2)).thenReturn("value2");
		when(mockSubField3.getName()).thenReturn("key3");
		when(mockFieldExporter.export(mockSubField3)).thenReturn("value3");

		when(mockField.isStructure()).thenReturn(true);
		when(mockField.getStructure()).thenReturn(mockStructure);
		when(mockExportIterator.next()).thenReturn(mockSubField1).thenReturn(mockSubField2).thenReturn(mockSubField3)
				.thenReturn(null);

		IStructureExporter exporter = new StructureExporter();
		((StructureExporter) exporter).setFieldExporter(mockFieldExporter);
		((StructureExporter) exporter).setExportIterator(mockExportIterator);

		Map<String, Object> value = exporter.export(mockField);
		assertEquals(expectedMap, value);
	}

	@Test
	public void testUpdateIterator() {
		JCoField mockField1 = mock(JCoField.class);
		JCoField mockField2 = mock(JCoField.class);
		JCoFieldIterator mockFieldIterator1 = mock(JCoFieldIterator.class);
		JCoFieldIterator mockFieldIterator2 = mock(JCoFieldIterator.class);
		JCoStructure mockStructure1 = mock(JCoStructure.class);
		JCoStructure mockStructure2 = mock(JCoStructure.class);
		JCoField mockMainField1 = mock(JCoField.class);
		JCoField mockMainField2 = mock(JCoField.class);

		when(mockField1.getName()).thenReturn("test1");
		when(mockField2.getName()).thenReturn("test2");
		when(mockField1.getValue()).thenReturn("value1");
		when(mockField2.getValue()).thenReturn("value2");

		when(mockFieldIterator1.hasNextField()).thenReturn(true).thenReturn(false);
		when(mockFieldIterator2.hasNextField()).thenReturn(true).thenReturn(false);
		when(mockFieldIterator1.nextField()).thenReturn(mockField1);
		when(mockFieldIterator2.nextField()).thenReturn(mockField2);

		when(mockStructure1.getFieldIterator()).thenReturn(mockFieldIterator1);
		when(mockStructure2.getFieldIterator()).thenReturn(mockFieldIterator2);

		when(mockMainField1.getStructure()).thenReturn(mockStructure1);
		when(mockMainField2.getStructure()).thenReturn(mockStructure2);

		IStructureExporter exporter = new StructureExporter();
		Map<String, Object> export1 = exporter.export(mockMainField1);
		Map<String, Object> export2 = exporter.export(mockMainField2);

		// Expected
		Map<String, Object> expectedExport1 = new HashMap<String, Object>();
		Map<String, Object> expectedExport2 = new HashMap<String, Object>();
		expectedExport1.put("test1", "value1");
		expectedExport2.put("test2", "value2");

		assertEquals(expectedExport1, export1);
		assertEquals(expectedExport2, export2);
	}
}
