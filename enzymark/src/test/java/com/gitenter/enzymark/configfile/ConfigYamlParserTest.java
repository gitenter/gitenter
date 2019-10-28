package com.gitenter.enzymark.configfile;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gitenter.enzymark.configfile.bean.GitEnterConfigBean;

public class ConfigYamlParserTest {
	
	@TempDir
	public File testFolder;
	
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
	
	@Test
	public void testParseWithUnexpectedRootAttribute() throws Exception {
		
		String yamlContent = "version: 1\n" + 
				"\n" + 
				"documents:\n" + 
				"    - requirements/*.md\n" + 
				"    - design_doc\n" + 
				"    - meeting_notes\n" +
				"does-not-exist: value\n";

		Assertions.assertThrows(ConfigFileFormatException.class, () -> {
			ConfigYamlParser.parse(yamlContent);
		});
	}
	
	@Test
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

		Assertions.assertThrows(ConfigFileFormatException.class, () -> {
			ConfigYamlParser.parse(yamlContent);
		});
	}
	
	@Test
	public void testParseWithWrongValueType() throws Exception {
		
		String yamlContent = "version: not an integer\n";
		
		Assertions.assertThrows(ConfigFileFormatException.class, () -> {
			ConfigYamlParser.parse(yamlContent);
		});
	}
	
	@Test
	public void testParseFromFile(@TempDir File configFile) throws Exception {
		
		String yamlContent = "version: 1";
		FileUtils.writeStringToFile(configFile, yamlContent, Charset.forName("UTF-8"));
		
		GitEnterConfigBean gitEnterConfig = ConfigYamlParser.parse(configFile);
		assertEquals(gitEnterConfig.getVersion(), new Integer(1));
		assertNull(gitEnterConfig.getDocumentScanPaths());
		assertFalse(gitEnterConfig.isTraceablityScanEnabled());
	}
	
	/*
	 * TODO:
	 * Test read from git.
	 */
}