package enterovirus.enzymark;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import lombok.Getter;

/*
 * The config file is read using the Properties file:
 * https://docs.oracle.com/javase/tutorial/essential/environment/properties.html
 * https://docs.oracle.com/javase/8/docs/api/java/util/Properties.html
 * http://www.mkyong.com/java/java-properties-file-examples/
 * 
 * Example how to write a conf file:
 * /etc/postgresql/9.5/main/postgresql.conf
 */
public class ConfFileParser {
	
	private boolean enableSystemwide;

	public ConfFileParser(File confFile) throws IOException {
		
		Properties prop = new Properties();
		
		try (InputStream input = new FileInputStream(confFile)) {
			
			prop.load(input);
			
			String enableSystemValue = prop.getProperty("enable_systemwide");
			if (enableSystemValue.equals("on")) {
				enableSystemwide = true;
			}
			else if (enableSystemValue.equals("off")) {
				enableSystemwide = false;
			} 
			
			/*
			 * TODO: 
			 * what if the conf file has wrong format?
			 * (prop.getProperty will give "null") 
			 */
		}
		catch (IOException e) {
			/*
			 * If the conf file does not exist, then this entire system
			 * is turned off.
			 */
			enableSystemwide = false;
		}
	}
	
	public boolean isEnabledSystemwide () {
		return enableSystemwide;
	}
}
