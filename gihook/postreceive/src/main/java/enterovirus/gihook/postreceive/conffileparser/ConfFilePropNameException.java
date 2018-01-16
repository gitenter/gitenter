package enterovirus.gihook.postreceive.conffileparser;

public class ConfFilePropNameException extends ConfFileFormatException {

	private static final long serialVersionUID = 1L;
	
	public ConfFilePropNameException(String property) {
		super("Cannot find value of \""+property+"\" in conf file.");
	}
}
