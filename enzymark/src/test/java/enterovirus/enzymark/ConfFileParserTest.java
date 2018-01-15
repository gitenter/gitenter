package enterovirus.enzymark;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ConfFileParserTest {
	
	@Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

	@Test
	public void test1() throws Exception {
		
		File confFile = testFolder.newFile("enterovirus.conf");
		FileUtils.writeStringToFile(confFile,
				 "# ------------------------------\n"
				+"# Enterovirus configuration file\n" 
				+"# ------------------------------\n"
				+"\n"
				+"enable_systemwide = on", "UTF-8");
				
		ConfFileParser parser = new ConfFileParser(confFile);
		
		System.out.println(parser.isEnabledSystemwide());
	}
}