package com.nextlabs.test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestJCoFunctionHandler.class,
	TestAttributeExtractor.class,
	TestExportIterator.class,
	TestFieldExporter.class,
	TestTableExporter.class,
	TestStructureExporter.class,
	TestValueExporter.class,
	TestParameterExporter.class,
	TestParameterImporter.class,
})

public class TestSuite {
}
