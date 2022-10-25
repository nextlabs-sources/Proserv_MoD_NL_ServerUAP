package com.nextlabs.test;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.nextlabs.nxljco.sap.AttributeExtractor;

public class TestAttributeExtractor {

	@Test
	public void nullMapShouldReturnNull() {
		Map<String, Object> map = null;
		String attributeKey = "testKey";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals(null, attributeValue);
	}

	@Test
	public void emptyMapShouldReturnNull() {
		Map<String, Object> map = new HashMap<>();
		String attributeKey = "testKey";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals(null, attributeValue);
	}

	@Test
	public void singleLevelMapWithKeyShouldReturnValue() {
		Map<String, Object> map = new HashMap<>();
		map.put("dummyKey1", "dummyValue1");
		map.put("dummyKey2", "dummyValue2");
		map.put("dummyKey3", "dummyValue3");
		map.put("dummyKey4", "dummyValue4");
		map.put("dummyKey5", "dummyValue5");
		map.put("testKey", "testValue");

		String attributeKey = "testKey";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals("testValue", attributeValue);
	}

	@Test
	public void singleLevelMapWithoutKeyShouldReturnNull() {
		Map<String, Object> map = new HashMap<>();
		map.put("dummyKey1", "dummyValue1");
		map.put("dummyKey2", "dummyValue2");
		map.put("dummyKey3", "dummyValue3");
		map.put("dummyKey4", "dummyValue4");
		map.put("dummyKey5", "dummyValue5");

		String attributeKey = "testKey";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals(null, attributeValue);
	}

	@Test
	public void doubleLevelMapWithKeyShouldReturnValue() {
		Map<String, Object> childMap1 = new HashMap<>();
		childMap1.put("dummyKey11", "dummyValue11");
		childMap1.put("dummyKey12", "dummyValue12");
		childMap1.put("dummyKey13", "dummyValue13");
		childMap1.put("dummyKey14", "dummyValue14");
		childMap1.put("dummyKey15", "dummyValue15");

		Map<String, Object> childMap2 = new HashMap<>();
		childMap2.put("dummyKey21", "dummyValue21");
		childMap2.put("dummyKey22", "dummyValue22");
		childMap2.put("dummyKey23", "dummyValue23");
		childMap2.put("dummyKey24", "dummyValue24");
		childMap2.put("dummyKey25", "dummyValue25");
		childMap2.put("testKey", "testValue");

		Map<String, Object> childMap3 = new HashMap<>();
		childMap3.put("dummyKey31", "dummyValue31");
		childMap3.put("dummyKey32", "dummyValue32");
		childMap3.put("dummyKey33", "dummyValue33");
		childMap3.put("dummyKey34", "dummyValue34");
		childMap3.put("dummyKey35", "dummyValue35");

		Map<String, Object> map = new HashMap<>();
		map.put("dummyKey1", childMap1);
		map.put("dummyKey2", childMap2);
		map.put("dummyKey3", childMap3);

		String attributeKey = "testKey";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals("testValue", attributeValue);
	}

	@Test
	public void doubleLevelMapWithoutKeyShouldReturnNull() {
		Map<String, Object> childMap1 = new HashMap<>();
		childMap1.put("dummyKey11", "dummyValue11");
		childMap1.put("dummyKey12", "dummyValue12");
		childMap1.put("dummyKey13", "dummyValue13");
		childMap1.put("dummyKey14", "dummyValue14");
		childMap1.put("dummyKey15", "dummyValue15");

		Map<String, Object> childMap2 = new HashMap<>();
		childMap2.put("dummyKey21", "dummyValue21");
		childMap2.put("dummyKey22", "dummyValue22");
		childMap2.put("dummyKey23", "dummyValue23");
		childMap2.put("dummyKey24", "dummyValue24");
		childMap2.put("dummyKey25", "dummyValue25");

		Map<String, Object> childMap3 = new HashMap<>();
		childMap3.put("dummyKey31", "dummyValue31");
		childMap3.put("dummyKey32", "dummyValue32");
		childMap3.put("dummyKey33", "dummyValue33");
		childMap3.put("dummyKey34", "dummyValue34");
		childMap3.put("dummyKey35", "dummyValue35");

		Map<String, Object> map = new HashMap<>();
		map.put("dummyKey1", childMap1);
		map.put("dummyKey2", childMap2);
		map.put("dummyKey3", childMap3);

		String attributeKey = "testKey";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals(null, attributeValue);
	}

	@Test
	public void doubleLevelMapWithTopLevelKeyShouldReturnValue() {
		Map<String, Object> childMap1 = new HashMap<>();
		childMap1.put("dummyKey11", "dummyValue11");
		childMap1.put("dummyKey12", "dummyValue12");
		childMap1.put("dummyKey13", "dummyValue13");
		childMap1.put("dummyKey14", "dummyValue14");
		childMap1.put("dummyKey15", "dummyValue15");

		Map<String, Object> childMap2 = new HashMap<>();
		childMap2.put("dummyKey21", "dummyValue21");
		childMap2.put("dummyKey22", "dummyValue22");
		childMap2.put("dummyKey23", "dummyValue23");
		childMap2.put("dummyKey24", "dummyValue24");
		childMap2.put("dummyKey25", "dummyValue25");

		Map<String, Object> childMap3 = new HashMap<>();
		childMap3.put("dummyKey31", "dummyValue31");
		childMap3.put("dummyKey32", "dummyValue32");
		childMap3.put("dummyKey33", "dummyValue33");
		childMap3.put("dummyKey34", "dummyValue34");
		childMap3.put("dummyKey35", "dummyValue35");

		Map<String, Object> map = new HashMap<>();
		map.put("dummyKey1", childMap1);
		map.put("dummyKey2", childMap2);
		map.put("dummyKey3", childMap3);
		map.put("testKey", "testValue");

		String attributeKey = "testKey";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals("testValue", attributeValue);
	}

	@Test
	public void threeLevelMapWithKeyShouldReturnValue() {
		Map<String, Object> childMap1 = new HashMap<>();
		childMap1.put("dummyKey11", "dummyValue11");
		childMap1.put("dummyKey12", "dummyValue12");
		childMap1.put("dummyKey13", "dummyValue13");
		childMap1.put("dummyKey14", "dummyValue14");
		childMap1.put("dummyKey15", "dummyValue15");

		Map<String, Object> childMap2 = new HashMap<>();
		childMap2.put("dummyKey21", "dummyValue21");
		childMap2.put("dummyKey22", "dummyValue22");
		childMap2.put("dummyKey23", "dummyValue23");
		childMap2.put("dummyKey24", "dummyValue24");
		childMap2.put("dummyKey25", "dummyValue25");

		Map<String, Object> childMap31 = new HashMap<>();
		childMap31.put("dummyKey31", "dummyValue31");
		childMap31.put("dummyKey32", "dummyValue32");
		childMap31.put("dummyKey33", "dummyValue33");
		childMap31.put("dummyKey34", "dummyValue34");
		childMap31.put("dummyKey35", "dummyValue35");
		childMap31.put("testKey", "testValue");

		Map<String, Object> childMap3 = new HashMap<>();
		childMap3.put("dummyKey31", childMap31);
		childMap3.put("dummyKey32", "dummyValue32");
		childMap3.put("dummyKey33", "dummyValue33");
		childMap3.put("dummyKey34", "dummyValue34");
		childMap3.put("dummyKey35", "dummyValue35");

		Map<String, Object> map = new HashMap<>();
		map.put("dummyKey1", childMap1);
		map.put("dummyKey2", childMap2);
		map.put("dummyKey3", childMap3);

		String attributeKey = "testKey";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals("testValue", attributeValue);
	}

	@Test
	public void nullAttributeValueShouldReturnNull() {
		Map<String, Object> map = new HashMap<>();
		map.put("dummyKey1", "dummyValue1");
		map.put("dummyKey2", "dummyValue2");
		map.put("dummyKey3", "dummyValue3");
		map.put("dummyKey4", "dummyValue4");
		map.put("dummyKey5", "dummyValue5");
		map.put("testKey", null);

		String attributeKey = "testKey";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals(null, attributeValue);
	}

	@Test
	public void nullAttributeShouldReturnNull() {
		Map<String, Object> map = new HashMap<>();
		map.put("dummyKey1", "dummyValue1");
		map.put("dummyKey2", "dummyValue2");
		map.put("dummyKey3", "dummyValue3");
		map.put("dummyKey4", "dummyValue4");
		map.put("dummyKey5", "dummyValue5");
		map.put("testKey", "testValue");

		String attributeKey = null;
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals(null, attributeValue);
	}

	@Test
	public void emptyAttributeShouldReturnNull() {
		Map<String, Object> map = new HashMap<>();
		map.put("dummyKey1", "dummyValue1");
		map.put("dummyKey2", "dummyValue2");
		map.put("dummyKey3", "dummyValue3");
		map.put("dummyKey4", "dummyValue4");
		map.put("dummyKey5", "dummyValue5");
		map.put("testKey", "testValue");

		String attributeKey = "";
		AttributeExtractor extractor = new AttributeExtractor(map);
		Object attributeValue = extractor.extract(attributeKey);

		assertEquals(null, attributeValue);
	}

}
