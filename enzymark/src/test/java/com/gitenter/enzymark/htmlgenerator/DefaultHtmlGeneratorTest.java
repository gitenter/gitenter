package com.gitenter.enzymark.htmlgenerator;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DefaultHtmlGeneratorTest {
	
	@Rule
    public TemporaryFolder folder = new TemporaryFolder();
	
	@Test
	public void testBold() throws IOException, GitAPIException {
		
		File file = folder.newFile("markdown.md");
		
		String content = "**Bold**\n";
		String expectedOutput = "<p><strong>Bold</strong></p>\n";
		Files.write(file.toPath(), content.getBytes());
		
		DefaultHtmlGenerator parser = new DefaultHtmlGenerator(file);
		assertEquals(parser.getHtml(), expectedOutput);
	}
	
	@Test
	public void testItalic() throws IOException, GitAPIException {
		
		File file = folder.newFile("markdown.md");
		
		String content = "*italic*\n";
		String expectedOutput = "<p><em>italic</em></p>\n";
		Files.write(file.toPath(), content.getBytes());
		
		DefaultHtmlGenerator parser = new DefaultHtmlGenerator(file);
		assertEquals(parser.getHtml(), expectedOutput);
	}
}