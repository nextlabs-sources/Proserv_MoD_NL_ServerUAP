package com.nextlabs.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import com.nextlabs.nxljco.sap.exporter.IValueExporter;
import com.nextlabs.nxljco.sap.exporter.ValueExporter;
import com.sap.conn.jco.JCoField;

public class TestValueExporter {

	@Test
	public void testExportingInteger() {
		JCoField mockField = mock(JCoField.class);
		when(mockField.getValue()).thenReturn(123);

		IValueExporter exporter = new ValueExporter();
		int value = (int) exporter.export(mockField);

		assertEquals(123, value);
	}

	@Test
	public void testExportingDouble() {
		JCoField mockField = mock(JCoField.class);
		when(mockField.getValue()).thenReturn(123.456);

		IValueExporter exporter = new ValueExporter();
		double value = (double) exporter.export(mockField);

		double difference = Math.abs(value - 123.456);

		assertTrue("Values not similar enough to be equal", difference < 0.000001);
	}

	@Test
	public void testExportingString() {
		JCoField mockField = mock(JCoField.class);
		when(mockField.getValue()).thenReturn("test string");

		IValueExporter exporter = new ValueExporter();
		String value = (String) exporter.export(mockField);

		assertEquals("test string", value);
	}

	@Test
	public void testExportingDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(0);
		calendar.set(1957, 8, 31, 12, 34, 56);

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTimeInMillis(0);
		calendar2.set(1957, 8, 31, 12, 34, 56);

		Date date = calendar.getTime();
		Date date2 = calendar2.getTime();

		JCoField mockField = mock(JCoField.class);
		when(mockField.getValue()).thenReturn(date);

		IValueExporter exporter = new ValueExporter();
		Date value = (Date) exporter.export(mockField);

		assertEquals(date2, value);
	}

	@Test
	public void testExportingNullValue() {
		JCoField mockField = mock(JCoField.class);
		when(mockField.getValue()).thenReturn(null);

		IValueExporter exporter = new ValueExporter();
		Object value = exporter.export(mockField);

		assertNull(value);
	}

	@Test
	public void testExportingNullField() {
		IValueExporter exporter = new ValueExporter();
		Object value = exporter.export(null);

		assertNull(value);
	}

}
