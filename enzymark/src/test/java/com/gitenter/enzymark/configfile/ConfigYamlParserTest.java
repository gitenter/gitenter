package com.gitenter.enzymark.configfile;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.gitenter.enzymark.configfile.bean.GitEnterConfigBean;

public class ConfigYamlParserTest {
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();
	
	@Test
	public void testParseWithValidInput() throws Exception {
		
		String yamlContent = "version: 1\n" + 
				"\n" + 
				"documents:\n" + 
				"    - requirements/*.md\n" + 
				"    - design_doc\n" + 
				"    - meeting_notes\n" + 
				"\n" + 
				"traceability:\n" + 
				"    markdown:\n" + 
				"        - requirements/*.md\n" + 
				"        - design_doc\n" + 
				"    gherkin:\n" + 
				"        - integration_test/\n";
		
		GitEnterConfigBean gitEnterConfig = ConfigYamlParser.parse(yamlContent);
		assertEquals(gitEnterConfig.getVersion(), new Integer(1));
		assertArrayEquals(gitEnterConfig.getDocumentScanPaths().toArray(), new String[] {"requirements/*.md", "design_doc", "meeting_notes"});
		assertTrue(gitEnterConfig.isTraceablityScanEnabled());
		assertArrayEquals(gitEnterConfig.getTraceabilityScanPaths("markdown").toArray(), new String[] {"requirements/*.md", "design_doc"});
		assertArrayEquals(gitEnterConfig.getTraceabilityScanPaths("gherkin").toArray(), new String[] {"integration_test/"});
		assertNull(gitEnterConfig.getTraceabilityScanPaths("java"));
	}

	@Test
	public void testParseWithMissingTraceabilitySetup() throws Exception {
		
		String yamlContent = "version: 1\n" + 
				"\n" + 
				"documents:\n" + 
				"    - requirements/*.md\n" + 
				"    - design_doc\n" + 
				"    - meeting_notes\n";
		
		GitEnterConfigBean gitEnterConfig = ConfigYamlParser.parse(yamlContent);
		assertEquals(gitEnterConfig.getVersion(), new Integer(1));
		assertFalse(gitEnterConfig.isTraceablityScanEnabled());
	}
	
	@Test(expected = ConfigFileFormatException.class)
	public void testParseWithUnexpectedRootAttribute() throws Exception {
		
		String yamlContent = "version: 1\n" + 
				"\n" + 
				"documents:\n" + 
				"    - requirements/*.md\n" + 
				"    - design_doc\n" + 
				"    - meeting_notes\n" +
				"does-not-exist: value\n";

		ConfigYamlParser.parse(yamlContent);
	}
	
	@Test(expected = ConfigFileFormatException.class)
	public void testParseWithUnexpectedTraceabilityAttribute() throws Exception {
		
		String yamlContent = "version: 1\n" + 
				"\n" + 
				"documents:\n" + 
				"    - requirements/*.md\n" + 
				"    - design_doc\n" + 
				"    - meeting_notes\n" + 
				"\n" + 
				"traceability:\n" + 
				"    does-not-exist:\n" + 
				"        - file_path/\n";

		ConfigYamlParser.parse(yamlContent);
	}
	
	@Test(expected = ConfigFileFormatException.class)
	public void testParseWithWrongValueType() throws Exception {
		
		String yamlContent = "version: not an integer\n";
		ConfigYamlParser.parse(yamlContent);
	}
	
	@Test
	public void testParseFromFile() throws Exception {
		
		File configFile = testFolder.newFile("gitenter.yml");
		
		String yamlContent = "version: 1";
		FileUtils.writeStringToFile(configFile, yamlContent, Charset.forName("UTF-8"));
		
		GitEnterConfigBean gitEnterConfig = ConfigYamlParser.parse(configFile);
		assertEquals(gitEnterConfig.getVersion(), new Integer(1));
	}
	
	/*
	 * TODO:
	 * Test read from git.
	 */
}