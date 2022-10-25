package com.nextlabs.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.nextlabs.nxljco.sap.exporter.IExportIterator;
import com.nextlabs.nxljco.sap.exporter.IFieldExporter;
import com.nextlabs.nxljco.sap.exporter.ITableExporter;
import com.nextlabs.nxljco.sap.exporter.TableExporter;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoTable;

public class TestTableExporter {

	@Test
	public void testExportingNull() {
		ITableExporter exporter = new TableExporter();

		List<Object> value = exporter.export(null);
		assertTrue(value.isEmpty());
	}

	@Test
	public void testExportingNullTable() {
		ITableExporter exporter = new TableExporter();
		JCoField mockField = mock(JCoField.class);
		when(mockField.isTable()).thenReturn(true);
		when(mockField.getTable()).thenReturn(null);

		List<Object> value = exporter.export(mockField);
		assertTrue(value.isEmpty());
	}

	@Test
	public void testExportingEmptyTable() {
		IExportIterator mockExportIterator = mock(IExportIterator.class);
		JCoField mockField = mock(JCoField.class);
		JCoField mockSubField = mock(JCoField.class);
		JCoTable mockTable = mock(JCoTable.class);

		when(mockSubField.getName()).thenReturn("");
		when(mockField.isTable()).thenReturn(true);
		when(mockField.getTable()).thenReturn(mockTable);
		when(mockTable.isEmpty()).thenReturn(true);

		when(mockExportIterator.next()).thenReturn(null);

		ITableExporter exporter = new TableExporter();
		((TableExporter) exporter).setExportIterator(mockExportIterator);

		List<Object> value = exporter.export(mockField);
		assertTrue(value.isEmpty());
	}

	@Test
	public void testExportingUnindicatedEmptyTable() {
		IExportIterator mockExportIterator = mock(IExportIterator.class);
		JCoField mockField = mock(JCoField.class);
		JCoField mockSubField = mock(JCoField.class);
		JCoTable mockTable = mock(JCoTable.class);

		IFieldExporter mockFieldExporter = mock(IFieldExporter.class);

		when(mockSubField.getName()).thenReturn("");
		when(mockFieldExporter.export(mockSubField)).thenReturn("value1");
		when(mockField.isTable()).thenReturn(true);
		when(mockField.getTable()).thenReturn(mockTable);
		when(mockTable.isLastRow()).thenReturn(true);
		when(mockTable.isEmpty()).thenReturn(false);

		when(mockExportIterator.next()).thenReturn(null);

		ITableExporter exporter = new TableExporter();
		((TableExporter) exporter).setExportIterator(mockExportIterator);

		List<Object> value = exporter.export(mockField);
		assertTrue(value.isEmpty());
	}

	@Test
	public void testExportingSingleValue() {
		List<Object> expectedList = new ArrayList<Object>();
		expectedList.add("value1");

		IFieldExporter mockFieldExporter = mock(IFieldExporter.class);
		IExportIterator mockExportIterator = mock(IExportIterator.class);
		JCoField mockField = mock(JCoField.class);
		JCoField mockSubField = mock(JCoField.class);
		JCoTable mockTable = mock(JCoTable.class);

		when(mockSubField.getName()).thenReturn("");
		when(mockFieldExporter.export(mockSubField)).thenReturn("value1");
		when(mockField.isTable()).thenReturn(true);
		when(mockField.getTable()).thenReturn(mockTable);
		when(mockTable.isLastRow()).thenReturn(true);
		when(mockTable.isEmpty()).thenReturn(false);

		when(mockExportIterator.next()).thenReturn(mockSubField).thenReturn(null);

		ITableExporter exporter = new TableExporter();
		((TableExporter) exporter).setFieldExporter(mockFieldExporter);
		((TableExporter) exporter).setExportIterator(mockExportIterator);

		List<Object> value = exporter.export(mockField);
		assertEquals(expectedList, value);
	}

	@Test
	public void testExportingMultiValue() {
		List<Object> expectedList = new ArrayList<Object>();
		expectedList.add("value1");
		expectedList.add("value2");
		expectedList.add("value3");

		IFieldExporter mockFieldExporter = mock(IFieldExporter.class);
		IExportIterator mockExportIterator = mock(IExportIterator.class);
		JCoField mockField = mock(JCoField.class);
		JCoField mockSubField = mock(JCoField.class);
		JCoTable mockTable = mock(JCoTable.class);

		when(mockSubField.getName()).thenReturn("");
		when(mockFieldExporter.export(mockSubField)).thenReturn("value1", "value2", "value3");
		when(mockField.isTable()).thenReturn(true);
		when(mockField.getTable()).thenReturn(mockTable);
		when(mockTable.isLastRow()).thenReturn(false, false, true);
		when(mockTable.isEmpty()).thenReturn(false);

		when(mockExportIterator.next()).thenReturn(mockSubField).thenReturn(null).thenReturn(mockSubField)
				.thenReturn(null).thenReturn(mockSubField).thenReturn(null);

		ITableExporter exporter = new TableExporter();
		((TableExporter) exporter).setFieldExporter(mockFieldExporter);
		((TableExporter) exporter).setExportIterator(mockExportIterator);

		List<Object> value = exporter.export(mockField);
		assertEquals(expectedList, value);
	}

	@Test
	public void testExportingSingleValueMap() {
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("key1", "value1");
		List<Object> expectedList = new ArrayList<Object>();
		expectedList.add(map1);

		IFieldExporter mockFieldExporter = mock(IFieldExporter.class);
		IExportIterator mockExportIterator = mock(IExportIterator.class);
		JCoField mockField = mock(JCoField.class);
		JCoField mockSubField = mock(JCoField.class);
		JCoTable mockTable = mock(JCoTable.class);

		when(mockSubField.getName()).thenReturn("key1");
		when(mockFieldExporter.export(mockSubField)).thenReturn("value1");
		when(mockField.isTable()).thenReturn(true);
		when(mockField.getTable()).thenReturn(mockTable);
		when(mockTable.isLastRow()).thenReturn(true);
		when(mockTable.isEmpty()).thenReturn(false);

		when(mockExportIterator.next()).thenReturn(mockSubField).thenReturn(null);

		ITableExporter exporter = new TableExporter();
		((TableExporter) exporter).setFieldExporter(mockFieldExporter);
		((TableExporter) exporter).setExportIterator(mockExportIterator);

		List<Object> value = exporter.export(mockField);
		assertEquals(expectedList, value);
	}

	@Test
	public void testExportingMultiValueMap() {
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("key1", "value1");
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("key2", "value2");
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("key3", "value3");
		List<Object> expectedList = new ArrayList<Object>();
		expectedList.add(map1);
		expectedList.add(map2);
		expectedList.add(map3);

		IFieldExporter mockFieldExporter = mock(IFieldExporter.class);
		IExportIterator mockExportIterator = mock(IExportIterator.class);
		JCoField mockField = mock(JCoField.class);
		JCoField mockSubField = mock(JCoField.class);
		JCoTable mockTable = mock(JCoTable.class);

		when(mockSubField.getName()).thenReturn("key1", "key2", "key3");
		when(mockFieldExporter.export(mockSubField)).thenReturn("value1", "value2", "value3");
		when(mockField.isTable()).thenReturn(true);
		when(mockField.getTable()).thenReturn(mockTable);
		when(mockTable.isLastRow()).thenReturn(false, false, true);
		when(mockTable.isEmpty()).thenReturn(false);

		when(mockExportIterator.next()).thenReturn(mockSubField).thenReturn(null).thenReturn(mockSubField)
				.thenReturn(null).thenReturn(mockSubField).thenReturn(null);

		ITableExporter exporter = new TableExporter();
		((TableExporter) exporter).setFieldExporter(mockFieldExporter);
		((TableExporter) exporter).setExportIterator(mockExportIterator);

		List<Object> value = exporter.export(mockField);
		assertEquals(expectedList, value);
	}

	@Test
	public void testUpdateIterator() {
		JCoField mockField1 = mock(JCoField.class);
		JCoField mockField2 = mock(JCoField.class);
		JCoFieldIterator mockFieldIterator1 = mock(JCoFieldIterator.class);
		JCoFieldIterator mockFieldIterator2 = mock(JCoFieldIterator.class);
		JCoTable mockTable1 = mock(JCoTable.class);
		JCoTable mockTable2 = mock(JCoTable.class);
		JCoField mockMainField1 = mock(JCoField.class);
		JCoField mockMainField2 = mock(JCoField.class);

		when(mockField1.getName()).thenReturn("");
		when(mockField2.getName()).thenReturn("");
		when(mockField1.getValue()).thenReturn("value1");
		when(mockField2.getValue()).thenReturn("value2");

		when(mockFieldIterator1.hasNextField()).thenReturn(true).thenReturn(false);
		when(mockFieldIterator2.hasNextField()).thenReturn(true).thenReturn(false);
		when(mockFieldIterator1.nextField()).thenReturn(mockField1);
		when(mockFieldIterator2.nextField()).thenReturn(mockField2);

		when(mockTable1.getFieldIterator()).thenReturn(mockFieldIterator1);
		when(mockTable2.getFieldIterator()).thenReturn(mockFieldIterator2);
		when(mockTable1.isLastRow()).thenReturn(true);
		when(mockTable2.isLastRow()).thenReturn(true);

		when(mockMainField1.getTable()).thenReturn(mockTable1);
		when(mockMainField2.getTable()).thenReturn(mockTable2);

		ITableExporter exporter = new TableExporter();
		List<Object> export1 = exporter.export(mockMainField1);
		List<Object> export2 = exporter.export(mockMainField2);

		// Expected
		List<Object> expectedExport1 = new ArrayList<Object>();
		List<Object> expectedExport2 = new ArrayList<Object>();
		expectedExport1.add("value1");
		expectedExport2.add("value2");

		assertEquals(expectedExport1, export1);
		assertEquals(expectedExport2, export2);
	}
}
