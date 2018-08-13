package com.gitenter.enzymark.propertiesfile;

public class PropertiesFilePropNameException extends PropertiesFileFormatException {

	private static final long serialVersionUID = 1L;
	
	public PropertiesFilePropNameException(String property) {
		super("Cannot find value of \""+property+"\" in properties file.");
	}
}
