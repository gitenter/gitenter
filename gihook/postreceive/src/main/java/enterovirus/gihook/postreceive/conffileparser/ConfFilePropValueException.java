package enterovirus.gihook.postreceive.conffileparser;

public class ConfFilePropValueException extends ConfFileFormatException {

	private static final long serialVersionUID = 1L;
	
	public ConfFilePropValueException(String property) {
		super("The value of \""+property+"\" is not allowed in conf file.");
	}
}
