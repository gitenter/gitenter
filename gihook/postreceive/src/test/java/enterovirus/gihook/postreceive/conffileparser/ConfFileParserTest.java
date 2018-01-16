package enterovirus.gihook.postreceive.conffileparser;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import enterovirus.gihook.postreceive.conffileparser.ConfFileParser;

public class ConfFileParserTest {
	
	@Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

	@Test
	public void test() throws Exception {
		
		File confFile = testFolder.newFile("enterovirus.conf");
		FileUtils.writeStringToFile(confFile,
				 "# comment is okay\n"
				+"# blank line is okay\n"
				+"\n"
				+"\n"
				+"enable_systemwide = on \t # comment after and white space around are okay",
				"UTF-8");
				
		ConfFileParser parser = new ConfFileParser(confFile);
		assertTrue(parser.isEnabledSystemwide());
	}
	
	@Test
	public void testConfFileNotExist() throws Exception {
		
		ConfFileParser parser = new ConfFileParser(new File("enterovirus.conf"));
		assertFalse(parser.isEnabledSystemwide());
	}
	
	@Test(expected = ConfFileFormatException.class)
	public void testWrongPropName() throws Exception {
		
		File confFile = testFolder.newFile("enterovirus.conf");
		FileUtils.writeStringToFile(confFile,
				"enable_systemwideeeeee = on",
				"UTF-8");
				
		@SuppressWarnings("unused")
		ConfFileParser parser = new ConfFileParser(confFile);
	}
	
	@Test(expected = ConfFileFormatException.class)
	public void testWrongPropValue() throws Exception {
		
		File confFile = testFolder.newFile("enterovirus.conf");
		FileUtils.writeStringToFile(confFile,
				"enable_systemwide = onnnnnnn",
				"UTF-8");
				
		@SuppressWarnings("unused")
		ConfFileParser parser = new ConfFileParser(confFile);
	}
}