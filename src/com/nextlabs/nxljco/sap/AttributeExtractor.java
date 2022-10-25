package com.nextlabs.nxljco.sap;

import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AttributeExtractor implements IAttributeExtractor {
	private final String LOG_VALUE_IS_NULL = "Value is null.";
	private final String LOG_ATTRIBUTE_VALUE_FOUND = "Attribute value found.";
	private final String LOG_ATTRIBUTE_IS_EMPTY = "Attribute is empty.";
	private final String LOG_ATTRIBUTE_IS_NULL = "Attribute is null.";
	private final String LOG_MAP_IS_NULL = "Map is null.";
	private static final Log LOG = LogFactory.getLog(AttributeExtractor.class);

	private Map<String, Object> map;
	private String attribute;

	/**
	 * Constructor for MapValueFinder.	
	 * 
	 * @param map
	 *            Map object which an attribute is to be search from.
	 */
	public AttributeExtractor(Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public Object extract(String attribute) {
		if (this.map == null) {
			LOG.error(LOG_MAP_IS_NULL);
			return null;
		}

		if (attribute == null) {
			LOG.error(LOG_ATTRIBUTE_IS_NULL);
			return null;
		}

		if (attribute.isEmpty()) {
			LOG.error(LOG_ATTRIBUTE_IS_EMPTY);
			return null;
		}

		this.attribute = attribute;

		return getAttributeValueFromMap(map, attribute);
	}

	private Object getAttributeValueFromMap(Map<String, Object> map, String attribute) {
		Object value = null;

		if (map.containsKey(attribute)) {
			value = getValue(map, attribute);
		} else {
			value = findValue(map, attribute);
		}

		return value;
	}

	private Object findInChildMap(Map<String, Object> map, String key) {
		Object value = null;

		// Check for Map (Structure)
		Object object = map.get(key);
		if (object instanceof Map<?, ?>) {
			@SuppressWarnings("unchecked")
			Map<String, Object> childMap = (Map<String, Object>) object;
			value = getAttributeValueFromMap(childMap, attribute);
		}

		return value;
	}

	private Object findValue(Map<String, Object> map, String attribute) {
		Object value = null;
		Set<String> keys = map.keySet();
		for (String key : keys) {
			// Find in Child Map
			value = findInChildMap(map, key);

			// Check if attribute value is found
			if (value != null) {
				LOG.debug(LOG_ATTRIBUTE_VALUE_FOUND);
				break;
			}
		}
		return value;
	}

	private Object getValue(Map<String, Object> map, String attribute) {
		Object value = null;
		value = map.get(attribute);

		// Check for Object
		if (value == null) {
			LOG.warn(LOG_VALUE_IS_NULL);
		}

		return value;
	}
}
