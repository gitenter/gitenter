package enterovirus.enzymark.propertiesfile;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import enterovirus.enzymark.propertiesfile.PropertiesFileFormatException;
import enterovirus.enzymark.propertiesfile.PropertiesFileParser;
import enterovirus.gitar.wrap.CommitSha;

public class PropertiesFileParserTest {
	
	@Rule
	public TemporaryFolder testFolder = new TemporaryFolder();

	@Test
	public void test1() throws Exception {
		
		File confFile = testFolder.newFile("gitenter.properties");
		FileUtils.writeStringToFile(confFile,
				 "# lalala\n" // comment is okay
				+"\n" // blank line is okay
				+"enable_systemwide = \t on \t \t\n" // white space around the properties values will be trimed
				+"include_paths = folder1, folder2, \tfolder3 \n",
				"UTF-8");
				
		PropertiesFileParser parser = new PropertiesFileParser(confFile);
		assertTrue(parser.isEnabledSystemwide());
		assertArrayEquals(parser.getIncludePaths(), new String[] {"folder1", "folder2", "folder3"});
	}

	@Test
	public void test2() throws Exception {
		
		File confFile = testFolder.newFile("gitenter.properties");
		FileUtils.writeStringToFile(confFile,
				 "# lalala\n" // comment is okay
				+"\n" // blank line is okay
				+"enable_systemwide = \t on \t \t\n" // white space around the properties values will be trimed
				+"include_paths = folder1\n"
				+"include_paths = folder2\n"
				+"include_paths = folder3\n",
				"UTF-8");
				
		PropertiesFileParser parser = new PropertiesFileParser(confFile);
		assertTrue(parser.isEnabledSystemwide());
		assertArrayEquals(parser.getIncludePaths(), new String[] {"folder1", "folder2", "folder3"});
	}

	@Test
	public void testNoIncludePath() throws Exception {
		
		File confFile = testFolder.newFile("gitenter.properties");
		FileUtils.writeStringToFile(confFile,
				"enable_systemwide = on",
				"UTF-8");
				
		PropertiesFileParser parser = new PropertiesFileParser(confFile);
		assertTrue(parser.isEnabledSystemwide());
		assertArrayEquals(parser.getIncludePaths(), new String[]{});
	}
	
	@Test
	public void testConfFileNotExist() throws Exception {
		
		PropertiesFileParser parser = new PropertiesFileParser(new File("/properties/file/not/exist"));
		assertFalse(parser.isEnabledSystemwide());
	}
	
	@Test(expected = PropertiesFileFormatException.class)
	public void testWrongPropName() throws Exception {
		
		File confFile = testFolder.newFile("gitenter.properties");
		FileUtils.writeStringToFile(confFile,
				"enable_systemwideeeeee = on",
				"UTF-8");
				
		@SuppressWarnings("unused")
		PropertiesFileParser parser = new PropertiesFileParser(confFile);
	}
	
	@Test(expected = PropertiesFileFormatException.class)
	public void testWrongPropValue() throws Exception {
		
		File confFile = testFolder.newFile("gitenter.properties");
		FileUtils.writeStringToFile(confFile,
				"enable_systemwide = onnnnnnn",
				"UTF-8");
				
		@SuppressWarnings("unused")
		PropertiesFileParser parser = new PropertiesFileParser(confFile);
	}
	
	@Test
	public void testFromGit() throws Exception {
		
		File repositoryDirectory = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_commit_traceability/org/repo.git");
		String relativeFilepath = "gitenter.properties";
		
		File commitRecordFile = new File(System.getProperty("user.home"), "Workspace/enterovirus-test/one_commit_traceability/commit-sha-list.txt");
		CommitSha commitSha = new CommitSha(commitRecordFile, 1);
	
		PropertiesFileParser parser = new PropertiesFileParser(repositoryDirectory, commitSha, relativeFilepath);
		assertTrue(parser.isEnabledSystemwide());
		assertArrayEquals(parser.getIncludePaths(), new String[] {"requirement"});
	}
}