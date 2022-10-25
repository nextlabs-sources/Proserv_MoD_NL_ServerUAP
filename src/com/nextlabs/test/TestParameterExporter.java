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
import com.nextlabs.nxljco.sap.exporter.IParameterExporter;
import com.nextlabs.nxljco.sap.exporter.ParameterExporter;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoParameterList;

public class TestParameterExporter {

	@Test
	public void testExportingNull() {
		IParameterExporter exporter = new ParameterExporter();

		Map<String, Object> value = exporter.exportParameter(null);
		assertTrue(value.isEmpty());
	}

	@Test
	public void testExportingNullStructure() {
		IParameterExporter exporter = new ParameterExporter();
		JCoParameterList mockParameter = mock(JCoParameterList.class);

		Map<String, Object> value = exporter.exportParameter(mockParameter);
		assertTrue(value.isEmpty());
	}

	@Test
	public void testExportingSingleValue() {
		Map<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("key1", "value1");

		IFieldExporter mockFieldExporter = mock(IFieldExporter.class);
		IExportIterator mockExportIterator = mock(IExportIterator.class);
		JCoField mockSubField = mock(JCoField.class);
		JCoParameterList mockParameter = mock(JCoParameterList.class);

		when(mockSubField.getName()).thenReturn("key1");
		when(mockFieldExporter.export(mockSubField)).thenReturn("value1");
		when(mockExportIterator.next()).thenReturn(mockSubField).thenReturn(null);

		IParameterExporter exporter = new ParameterExporter();
		((ParameterExporter) exporter).setFieldExporter(mockFieldExporter);
		((ParameterExporter) exporter).setExportIterator(mockExportIterator);

		Map<String, Object> value = exporter.exportParameter(mockParameter);
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
		JCoParameterList mockParameter = mock(JCoParameterList.class);
		JCoField mockSubField1 = mock(JCoField.class);
		JCoField mockSubField2 = mock(JCoField.class);
		JCoField mockSubField3 = mock(JCoField.class);

		when(mockSubField1.getName()).thenReturn("key1");
		when(mockFieldExporter.export(mockSubField1)).thenReturn("value1");
		when(mockSubField2.getName()).thenReturn("key2");
		when(mockFieldExporter.export(mockSubField2)).thenReturn("value2");
		when(mockSubField3.getName()).thenReturn("key3");
		when(mockFieldExporter.export(mockSubField3)).thenReturn("value3");

		when(mockExportIterator.next()).thenReturn(mockSubField1).thenReturn(mockSubField2).thenReturn(mockSubField3)
				.thenReturn(null);

		IParameterExporter exporter = new ParameterExporter();
		((ParameterExporter) exporter).setFieldExporter(mockFieldExporter);
		((ParameterExporter) exporter).setExportIterator(mockExportIterator);

		Map<String, Object> value = exporter.exportParameter(mockParameter);
		assertEquals(expectedMap, value);
	}

	@Test
	public void testUpdateIterator() {
		JCoField mockField1 = mock(JCoField.class);
		JCoField mockField2 = mock(JCoField.class);
		JCoFieldIterator mockFieldIterator1 = mock(JCoFieldIterator.class);
		JCoFieldIterator mockFieldIterator2 = mock(JCoFieldIterator.class);
		JCoParameterList mockParameterList1 = mock(JCoParameterList.class);
		JCoParameterList mockParameterList2 = mock(JCoParameterList.class);

		when(mockField1.getName()).thenReturn("test1");
		when(mockField2.getName()).thenReturn("test2");
		when(mockField1.getValue()).thenReturn("value1");
		when(mockField2.getValue()).thenReturn("value2");

		when(mockFieldIterator1.hasNextField()).thenReturn(true).thenReturn(false);
		when(mockFieldIterator2.hasNextField()).thenReturn(true).thenReturn(false);
		when(mockFieldIterator1.nextField()).thenReturn(mockField1);
		when(mockFieldIterator2.nextField()).thenReturn(mockField2);

		when(mockParameterList1.getFieldIterator()).thenReturn(mockFieldIterator1);
		when(mockParameterList2.getFieldIterator()).thenReturn(mockFieldIterator2);

		IParameterExporter exporter = new ParameterExporter();
		Map<String, Object> export1 = exporter.exportParameter(mockParameterList1);
		Map<String, Object> export2 = exporter.exportParameter(mockParameterList2);

		// Expected
		Map<String, Object> expectedExport1 = new HashMap<String, Object>();
		Map<String, Object> expectedExport2 = new HashMap<String, Object>();
		expectedExport1.put("test1", "value1");
		expectedExport2.put("test2", "value2");

		assertEquals(expectedExport1, export1);
		assertEquals(expectedExport2, export2);
	}
}
