package com.gitenter.protease.domain.auth;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RepositoryPersonRoleConventer implements AttributeConverter<RepositoryPersonRole,Character> {

	@Override
	public Character convertToDatabaseColumn(RepositoryPersonRole role) {
		return role.getShortName();
	}
	
	@Override
	public RepositoryPersonRole convertToEntityAttribute(Character dbData) {
		return RepositoryPersonRole.fromShortName(dbData);
	}
}
