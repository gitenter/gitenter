package com.gitenter.enzymark.htmlgenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class DefaultHtmlGeneratorTest {
	
	@Test
	public void testBold(@TempDir File tmpFolder) throws IOException, GitAPIException {
		File file = new File(tmpFolder, "file");
		file.createNewFile();
		
		String content = "**Bold**\n";
		String expectedOutput = "<p><strong>Bold</strong></p>\n";
		Files.write(file.toPath(), content.getBytes());
		
		DefaultHtmlGenerator parser = new DefaultHtmlGenerator(file);
		assertEquals(parser.getHtml(), expectedOutput);
	}
	
	@Test
	public void testItalic(@TempDir File tmpFolder) throws IOException, GitAPIException {
		File file = new File(tmpFolder, "file");
		file.createNewFile();
		
		String content = "*italic*\n";
		String expectedOutput = "<p><em>italic</em></p>\n";
		Files.write(file.toPath(), content.getBytes());
		
		DefaultHtmlGenerator parser = new DefaultHtmlGenerator(file);
		assertEquals(parser.getHtml(), expectedOutput);
	}
}