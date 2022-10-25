package com.nextlabs.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.nextlabs.nxljco.sap.exporter.ExportIterator;
import com.nextlabs.nxljco.sap.exporter.IExportIterator;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;

public class TestExportIterator {

	@Test
	public void testNullIterator() {
		IExportIterator iterator = new ExportIterator();
		JCoField field = iterator.next();
		assertNull(field);
	}

	@Test
	public void testSingleIterator() {
		JCoFieldIterator mockFieldIterator = mock(JCoFieldIterator.class);
		JCoField mockField = mock(JCoField.class);
		when(mockFieldIterator.hasNextField()).thenReturn(true, false);
		when(mockFieldIterator.nextField()).thenReturn(mockField);

		IExportIterator iterator = new ExportIterator();
		((ExportIterator) iterator).setIterator(mockFieldIterator);
		JCoField field1 = iterator.next();
		assertNotNull(field1);

		JCoField field2 = iterator.next();
		assertNull(field2);
	}

	@Test
	public void testDoubleIterator() {
		JCoFieldIterator mockFieldIterator = mock(JCoFieldIterator.class);
		JCoField mockField = mock(JCoField.class);
		when(mockFieldIterator.hasNextField()).thenReturn(true, true, false);
		when(mockFieldIterator.nextField()).thenReturn(mockField);

		IExportIterator iterator = new ExportIterator();
		((ExportIterator) iterator).setIterator(mockFieldIterator);
		JCoField field1 = iterator.next();
		assertNotNull(field1);

		JCoField field2 = iterator.next();
		assertNotNull(field2);

		JCoField field3 = iterator.next();
		assertNull(field3);
	}

}
