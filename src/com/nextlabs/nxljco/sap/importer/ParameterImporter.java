package com.nextlabs.nxljco.sap.importer;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sap.conn.jco.JCoParameterList;

public class ParameterImporter implements IParameterImporter {

	public void importParameter(Map<String, Object> imports, JCoParameterList jCoParameters)
			throws IllegalArgumentException {
		// Check parameters
		if (!isParameterValid(imports, jCoParameters)) {
			throw new IllegalArgumentException();
		}

		// Import
		insertParameters(imports, jCoParameters);
	}

	private void insertParameters(Map<String, Object> imports, JCoParameterList jCoParameters) {
		Set<String> keys = imports.keySet();
		Iterator<String> iterator = keys.iterator();

		while (iterator.hasNext()) {
			String key = iterator.next();
			Object value = imports.get(key);

			insertValue(jCoParameters, key, value);

			// Insert into Parameters
			jCoParameters.setValue(key, value);
		}
	}

	private void insertValue(JCoParameterList jCoParameters, String key, Object value) {
		jCoParameters.setValue(key, value);
	}

	private boolean isParameterValid(Map<String, Object> imports, JCoParameterList jCoParameters) {
		boolean isOK = true;

		// Check for null imports
		if (imports == null) {
			isOK = false;
		}

		// Check for null parameter
		if (jCoParameters == null) {
			isOK = false;
		}

		return isOK;
	}

}
