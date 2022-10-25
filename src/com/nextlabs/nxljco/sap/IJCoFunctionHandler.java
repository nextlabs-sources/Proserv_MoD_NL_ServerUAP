package com.nextlabs.nxljco.sap;

import java.io.IOException;
import java.util.Map;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;

public interface IJCoFunctionHandler {
	/**
	 * Performs Remote Function Call (RFC) via JCo on the SAP server using the
	 * provided handler (function name) and parameters (function imports). This
	 * method throws IOException if the function calling fails and the
	 * IllegalArgumentException if the input arguments are not valid.
	 * 
	 * @param handler
	 *            RFC handler (function name).
	 * @param parameters
	 *            RFC parameters to be passed to the SAP server (function
	 *            imports). The parameters Map object can only be single level,
	 *            meaning only String, Number, and Date values are allowed.
	 * @return Map of Returned parameters from the SAP server (function
	 *         exports). The returned Map object may have several levels deep.
	 *         Please check with your SAP Administrator/Developer for the actual
	 *         structure of the Export. Note: Tables are mapped to List (row) of
	 *         Map objects (column)
	 * @throws IOException
	 *             Exception when calling the function fails. You should ensure
	 *             that the SAP server is reachable and the arguments provided
	 *             are valid.
	 * @throws IllegalArgumentException
	 *             Exception when the input arguments are null or invalid.
	 *             Handler should not be empty or null. Input parameters should
	 *             be a valid Map instance.
	 */
	public Map<String, Object> callFunction(String handler, Map<String, Object> parameters)
			throws IOException, IllegalArgumentException;

	/**
	 * Performs Remote Function Call (RFC) via JCo on the SAP server using the
	 * provided handler (function name) and parameters (function imports). This
	 * method throws IOException if the function calling fails and the
	 * IllegalArgumentException if the input arguments are not valid.
	 * 
	 * @param handler
	 *            RFC handler (function name).
	 * @param parameters
	 *            RFC parameters to be passed to the SAP server (function
	 *            imports). The parameters Map object can only be single level,
	 *            meaning only String, Number, and Date values are allowed.
	 * @param attribute
	 *            Desired attribute to be returned. This attribute should be
	 *            within one of the fields of the returned Map object.
	 * @return The attribute value from the Map object returned by the function.
	 *         The return value will be Null if the attribute is not found
	 *         within the Map object.
	 * @throws IOException
	 *             Exception when calling the function fails. You should ensure
	 *             that the SAP server is reachable and the arguments provided
	 *             are valid.
	 * @throws IllegalArgumentException
	 *             Exception when the input arguments are null or invalid.
	 *             Handler should not be empty or null. Input parameters should
	 *             be a valid Map instance.
	 */
	public Object callFunctionForAttribute(String handler, Map<String, Object> parameters, String attribute)
			throws IOException, IllegalArgumentException;

	/**
	 * Gets the import parameters of the function as a Map object.
	 * 
	 * @return RFC import parameters that is or will be passed to the SAP server
	 *         (funtion imports). An empty map will be returend if the import
	 *         parameters are not set.
	 */
	public Map<String, Object> getRfcImport();

	/**
	 * Gets the export parameters of the function as a Map object.
	 * 
	 * @return RFC export parameters that is returned by the SAP server. An
	 *         empty map will be returned if the function has not been called.
	 */
	public Map<String, Object> getRfcExport();

	/**
	 * Gets the changing parameters of the function as a Map object.
	 * 
	 * @return RFC changing parameters that is returned by the SAP server. An
	 *         empty map will be returned if the function has not been called.
	 */
	public Map<String, Object> getRfcChanging();

	/**
	 * Gets the table parameters of the function as a Map object.
	 * 
	 * @return RFC table parameters that is returned by the SAP server. An empty
	 *         map will be returned if the function has not been called.
	 */
	public Map<String, Object> getRfcTable();
	
	public JCoFunction getFunction(String handler) throws JCoException;
}
