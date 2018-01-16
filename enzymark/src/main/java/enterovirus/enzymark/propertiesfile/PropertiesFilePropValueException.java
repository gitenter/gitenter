package enterovirus.enzymark.propertiesfile;

public class PropertiesFilePropValueException extends PropertiesFileFormatException {

	private static final long serialVersionUID = 1L;
	
	public PropertiesFilePropValueException(String property, String value) {
		super("The value \""+value+"\" of \""+property+"\" is not allowed in the properties file.");
	}
}
