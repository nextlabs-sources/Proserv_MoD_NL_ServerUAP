package com.nextlabs.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.nextlabs.nxljco.sap.AttributeExtractor;
import com.nextlabs.nxljco.sap.IJCoFunctionHandler;
import com.nextlabs.nxljco.sap.JCoFunctionHandler;
import com.nextlabs.nxljco.sap.exporter.IParameterExporter;
import com.nextlabs.nxljco.sap.importer.IParameterImporter;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;

public class TestJCoFunctionHandler {
	@org.junit.Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void improperNullPrefixInitializationShouldFail() {
		thrown.expect(NullPointerException.class);
		new JCoFunctionHandler(null);

		// Should not reach this line
		fail("Expected NullPointerException");
	}

	@Test
	public void improperEmptyPrefixInitializationShouldFail() {
		thrown.expect(NullPointerException.class);
		new JCoFunctionHandler("");

		// Should not reach this line
		fail("Expected NullPointerException");
	}

	@Test
	public void properInitializationShouldPass() {
		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		assertNotNull(functionHandler);
	}

	@Test
	public void improperArgumentNullHandlerShouldFail() throws IllegalArgumentException, IOException {
		String handler = null;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");

		thrown.expect(IllegalArgumentException.class);
		functionHandler.callFunction(handler, map);

		fail("Expect IllegalArgumentException");
	}

	@Test
	public void improperArgumentNullMapShouldFail() throws IllegalArgumentException, IOException {
		String handler = "test";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");

		thrown.expect(IllegalArgumentException.class);
		functionHandler.callFunction(handler, null);

		fail("Expect IllegalArgumentException");
	}

	@Test
	public void improperArgumentEmptyHandlerShouldFail() throws IllegalArgumentException, IOException {
		String handler = "";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");

		thrown.expect(IllegalArgumentException.class);
		functionHandler.callFunction(handler, map);

		fail("Expect IllegalArgumentException");
	}

	@Test
	public void improperArgumentNullAttributeShouldFail() throws IllegalArgumentException, IOException {
		String handler = "test";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");

		thrown.expect(IllegalArgumentException.class);
		functionHandler.callFunctionForAttribute(handler, map, null);

		fail("Expect IllegalArgumentException");
	}

	@Test
	public void improperArgumentEmptyAttributeShouldFail() throws IllegalArgumentException, IOException {
		String handler = "test";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");

		thrown.expect(IllegalArgumentException.class);
		functionHandler.callFunctionForAttribute(handler, map, "");

		fail("Expect IllegalArgumentException");
	}

	@Test
	public void testNullImport() throws IllegalArgumentException, IOException {
		JCoFunction function = mock(JCoFunction.class);
		JCoParameterList exportParameters = mock(JCoParameterList.class);
		when(function.getImportParameterList()).thenReturn(null);
		when(function.getExportParameterList()).thenReturn(exportParameters);

		String handler = "test";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		((JCoFunctionHandler) functionHandler).setFunction(function);

		Map<String, Object> returnMap = functionHandler.callFunction(handler, map);
		assertNotNull(returnMap);
		assertTrue(returnMap.isEmpty());
	}

	@Test
	public void testNullExport() throws IllegalArgumentException, IOException {
		JCoFunction function = mock(JCoFunction.class);
		JCoParameterList parameters = mock(JCoParameterList.class);
		when(function.getImportParameterList()).thenReturn(parameters);
		when(function.getExportParameterList()).thenReturn(null);

		String handler = "test";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		((JCoFunctionHandler) functionHandler).setFunction(function);

		Map<String, Object> returnMap = functionHandler.callFunction(handler, map);
		assertNotNull(returnMap);
		assertTrue(returnMap.isEmpty());
	}

	@Test
	public void testNullTable() throws IllegalArgumentException, IOException {
		JCoFunction function = mock(JCoFunction.class);
		JCoParameterList parameters = mock(JCoParameterList.class);
		when(function.getTableParameterList()).thenReturn(null);
		when(function.getImportParameterList()).thenReturn(parameters);
		when(function.getExportParameterList()).thenReturn(parameters);

		String handler = "test";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		((JCoFunctionHandler) functionHandler).setFunction(function);

		Map<String, Object> returnMap = functionHandler.callFunction(handler, map);
		assertNotNull(returnMap);
		assertTrue(returnMap.isEmpty());
	}

	@Test
	public void testNullChanging() throws IllegalArgumentException, IOException {
		JCoFunction function = mock(JCoFunction.class);
		JCoParameterList parameters = mock(JCoParameterList.class);
		when(function.getImportParameterList()).thenReturn(parameters);
		when(function.getExportParameterList()).thenReturn(parameters);
		when(function.getTableParameterList()).thenReturn(parameters);
		when(function.getChangingParameterList()).thenReturn(null);

		String handler = "test";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		((JCoFunctionHandler) functionHandler).setFunction(function);

		Map<String, Object> returnMap = functionHandler.callFunction(handler, map);
		assertNotNull(returnMap);
		assertTrue(returnMap.isEmpty());
	}

	@Test
	public void testCallFunction() throws IllegalArgumentException, IOException {
		Map<String, Object> expectedMap = new HashMap<String, Object>();
		expectedMap.put("outTest", "outValue");

		JCoFunction function = mock(JCoFunction.class);
		JCoParameterList parameters = mock(JCoParameterList.class);
		IParameterImporter mockImporter = mock(IParameterImporter.class);
		IParameterExporter mockExporter = mock(IParameterExporter.class);
		when(function.getImportParameterList()).thenReturn(parameters);
		when(function.getExportParameterList()).thenReturn(parameters);
		when(function.getTableParameterList()).thenReturn(parameters);
		when(function.getChangingParameterList()).thenReturn(parameters);
		when(mockExporter.exportParameter(any(JCoParameterList.class))).thenReturn(expectedMap);

		String handler = "test";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		((JCoFunctionHandler) functionHandler).setFunction(function);
		((JCoFunctionHandler) functionHandler).setImporter(mockImporter);
		((JCoFunctionHandler) functionHandler).setExporter(mockExporter);

		Map<String, Object> returnMap = functionHandler.callFunction(handler, map);
		assertNotNull(returnMap);
		assertEquals(expectedMap, returnMap);
	}

	@Test
	public void testCallFunctionForAttribute() throws IllegalArgumentException, IOException {
		JCoFunction function = mock(JCoFunction.class);
		JCoParameterList parameters = mock(JCoParameterList.class);
		AttributeExtractor attributeExtractor = mock(AttributeExtractor.class);
		when(function.getImportParameterList()).thenReturn(parameters);
		when(function.getExportParameterList()).thenReturn(parameters);
		when(function.getTableParameterList()).thenReturn(parameters);
		when(function.getChangingParameterList()).thenReturn(parameters);
		when(attributeExtractor.extract("test")).thenReturn("testValue");

		String handler = "test";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("test", "value");

		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		((JCoFunctionHandler) functionHandler).setFunction(function);
		((JCoFunctionHandler) functionHandler).setAttributeExtractor(attributeExtractor);

		Object value = functionHandler.callFunctionForAttribute(handler, map, "test");
		assertEquals("testValue", value);
	}

	@Test
	public void testGetImport() {
		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		assertTrue(functionHandler.getRfcExport().isEmpty());
	}

	@Test
	public void testGetExport() {
		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		assertTrue(functionHandler.getRfcImport().isEmpty());
	}

	@Test
	public void testGetChanging() {
		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		assertTrue(functionHandler.getRfcChanging().isEmpty());
	}

	@Test
	public void testGetTable() {
		IJCoFunctionHandler functionHandler = new JCoFunctionHandler("test");
		assertTrue(functionHandler.getRfcTable().isEmpty());
	}

}
