package com.nextlabs.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.nextlabs.nxljco.sap.importer.IParameterImporter;
import com.nextlabs.nxljco.sap.importer.ParameterImporter;
import com.sap.conn.jco.JCoParameterList;

public class TestParameterImporter {
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testNullParameters() {
		Map<String, Object> imports = new HashMap<String, Object>();
		IParameterImporter importer = new ParameterImporter();

		thrown.expect(IllegalArgumentException.class);
		importer.importParameter(imports, null);

		fail("Should not reach here");
	}

	@Test
	public void testNullImports() {
		JCoParameterList parameterList = mock(JCoParameterList.class);
		IParameterImporter importer = new ParameterImporter();

		thrown.expect(IllegalArgumentException.class);
		importer.importParameter(null, parameterList);

		fail("Should not reach here");
	}

	@Test
	public void testInsertSingleValue() {
		JCoParameterList parameterList = mock(JCoParameterList.class);
		Map<String, Object> imports = new HashMap<String, Object>();
		imports.put("key1", "value1");

		when(parameterList.getString("key1")).thenReturn("value1");

		IParameterImporter importer = new ParameterImporter();
		importer.importParameter(imports, parameterList);

		assertEquals("value1", parameterList.getString("key1"));
	}

	@Test
	public void testInsertMultiValue() {
		JCoParameterList parameterList = mock(JCoParameterList.class);
		Map<String, Object> imports = new HashMap<String, Object>();
		imports.put("key1", "value1");
		imports.put("key2", "value2");
		imports.put("key3", "value3");

		when(parameterList.getString("key1")).thenReturn("value1");
		when(parameterList.getString("key2")).thenReturn("value2");
		when(parameterList.getString("key3")).thenReturn("value3");

		IParameterImporter importer = new ParameterImporter();
		importer.importParameter(imports, parameterList);

		assertEquals("value1", parameterList.getString("key1"));
		assertEquals("value2", parameterList.getString("key2"));
		assertEquals("value3", parameterList.getString("key3"));
	}

}
