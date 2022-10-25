package com.nextlabs.nxljco.sap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nextlabs.nxljco.sap.exporter.IParameterExporter;
import com.nextlabs.nxljco.sap.exporter.ParameterExporter;
import com.nextlabs.nxljco.sap.importer.IParameterImporter;
import com.nextlabs.nxljco.sap.importer.ParameterImporter;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoRepository;

public class JCoFunctionHandler implements IJCoFunctionHandler {
	private final String NO_JCO_PARAMETERS = "No JcoParameters";
	private final String NO_FUNCTION = "No function";
	private final String LOG_NULL_SERVER_PREFIX = "Server Prefix should never be null or empty. Please check with your SAP developer or administrator for the Server Prefix value.";
	private String serverPrefix;

	private Map<String, Object> rfcImport;
	private Map<String, Object> rfcExport;
	private Map<String, Object> rfcChanging;
	private Map<String, Object> rfcTable;

	private JCoDestination destination;
	private JCoRepository repository;
	private JCoFunction function;
	private AttributeExtractor attributeExtractor;

	private IParameterImporter importer;
	private IParameterExporter exporter;
	
	private static final Log LOG = LogFactory.getLog(JCoFunctionHandler.class);

	private JCoDestination getDestination() throws JCoException {
		if (this.destination == null) {
			this.destination = JCoDestinationManager.getDestination(serverPrefix);
		}
		return destination;
	}

	private JCoRepository getRepository() throws JCoException {
		if (this.repository == null) {
			this.repository = getDestination().getRepository();
		}
		return repository;
	}

	public JCoFunction getFunction(String handler) throws JCoException {
		if (this.function == null) {
			this.function = getRepository().getFunction(handler);
		}
		return function;
	}

	private AttributeExtractor getAttributeExtractor(Map<String, Object> map) {
		if (this.attributeExtractor == null) {
			this.attributeExtractor = new AttributeExtractor(map);
		}
		return attributeExtractor;
	}

	private IParameterImporter getImporter() {
		if (this.importer == null) {
			this.importer = new ParameterImporter();
		}
		return importer;
	}

	private IParameterExporter getExporter() {
		if (this.exporter == null) {
			this.exporter = new ParameterExporter();
		}
		return exporter;
	}

	public void setDestination(JCoDestination destination) {
		this.destination = destination;
	}

	public void setRepository(JCoRepository repository) {
		this.repository = repository;
	}

	public void setFunction(JCoFunction function) {
		this.function = function;
	}

	public void setAttributeExtractor(AttributeExtractor attributeExtractor) {
		this.attributeExtractor = attributeExtractor;
	}

	public void setImporter(IParameterImporter importer) {
		this.importer = importer;
	}

	public void setExporter(IParameterExporter exporter) {
		this.exporter = exporter;
	}

	/**
	 * Constructor for JCo Function Handler. The constructor will throw
	 * IllegalArgumentException if the provided server prefix is null or empty.
	 * 
	 * @param serverPrefix
	 *            Server Prefix used in the Policy Controller. You can find it
	 *            in "SAPJavaSDKService.properties" in "Policy
	 *            Controller/jservice/config".
	 */
	public JCoFunctionHandler(String serverPrefix) {
		if (serverPrefix == null || serverPrefix.isEmpty()) {
			throw new NullPointerException(LOG_NULL_SERVER_PREFIX);
		}

		this.serverPrefix = serverPrefix;
		this.rfcImport = new HashMap<String, Object>();
		this.rfcExport = new HashMap<String, Object>();
		this.rfcChanging = new HashMap<String, Object>();
		this.rfcTable = new HashMap<String, Object>();
	}

	@Override
	public Map<String, Object> callFunction(String handler, Map<String, Object> parameters)
			throws IOException, IllegalArgumentException {
		if (!isArgumentValid(handler, parameters)) {
			throw new IllegalArgumentException();
		}

		rfcImport = parameters;
		rfcExport = new HashMap<String, Object>();

		try {
			// Load Function
			JCoFunction function = getFunction(handler);
			if (function == null) {
				this.repository = null;
				this.destination = null;
				LOG.error(NO_FUNCTION);
				return rfcExport;
			}

			// Insert Imports
			insertImports(parameters, function);

			// Execute
			function.execute(destination);

			// Extract Exports
			rfcExport = extractExports(function);
			rfcChanging = extractChanging(function);
			rfcTable = extractTable(function);

		} catch (JCoException e) {
			throw new IOException(e.getMessage());
		}

		return rfcExport;
	}

	@Override
	public Object callFunctionForAttribute(String handler, Map<String, Object> parameters, String attribute)
			throws IOException, IllegalArgumentException {

		// Check null attribute
		if (attribute == null) {
			throw new IllegalArgumentException();
		}

		// Check empty attribute
		if (attribute.isEmpty()) {
			throw new IllegalArgumentException();
		}

		// Call function
		Map<String, Object> returnedMap = callFunction(handler, parameters);

		// Extract
		IAttributeExtractor extractor = getAttributeExtractor(returnedMap);
		Object attributeValue = extractor.extract(attribute);

		return attributeValue;
	}

	private boolean isArgumentValid(String handler, Map<String, Object> parameters) {
		boolean isOK = true;

		if (handler == null) {
			isOK = false;
		}

		else if (handler.isEmpty()) {
			isOK = false;
		}

		else if (parameters == null) {
			isOK = false;
		}

		return isOK;
	}

	private void insertImports(Map<String, Object> imports, JCoFunction function) throws IllegalArgumentException {
		JCoParameterList jCoParameters = function.getImportParameterList();
		if (jCoParameters == null) {
			LOG.error(NO_JCO_PARAMETERS);
			return;
		}
		IParameterImporter importer = getImporter();
		importer.importParameter(imports, jCoParameters);
	}

	private Map<String, Object> extractExports(JCoFunction function) throws IllegalArgumentException {
		JCoParameterList jCoParameters = function.getExportParameterList();
		if (jCoParameters == null) {	
			return new HashMap<>();
		}

		IParameterExporter exporter = getExporter();
		Map<String, Object> exports = exporter.exportParameter(jCoParameters);
		return exports;
	}

	private Map<String, Object> extractChanging(JCoFunction function) throws IllegalArgumentException {
		JCoParameterList jCoParameters = function.getChangingParameterList();
		if (jCoParameters == null) {
			return new HashMap<>();
		}

		IParameterExporter exporter = getExporter();
		Map<String, Object> exports = exporter.exportParameter(jCoParameters);
		return exports;
	}

	private Map<String, Object> extractTable(JCoFunction function) throws IllegalArgumentException {
		JCoParameterList jCoParameters = function.getTableParameterList();
		if (jCoParameters == null) {
			return new HashMap<>();
		}

		IParameterExporter exporter = getExporter();
		Map<String, Object> exports = exporter.exportParameter(jCoParameters);
		return exports;
	}

	public Map<String, Object> getRfcImport() {
		return rfcImport;
	}

	public Map<String, Object> getRfcExport() {
		return rfcExport;
	}

	public Map<String, Object> getRfcChanging() {
		return rfcChanging;
	}

	public Map<String, Object> getRfcTable() {
		return rfcTable;
	}
}
