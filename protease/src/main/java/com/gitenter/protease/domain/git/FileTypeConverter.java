package com.gitenter.protease.domain.git;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class FileTypeConverter implements AttributeConverter<FileType,String>{

	@Override
	public String convertToDatabaseColumn(FileType role) {
		return role.getName();
	}
	
	@Override
	public FileType convertToEntityAttribute(String dbData) {
		return FileType.fromName(dbData);
	}
}
