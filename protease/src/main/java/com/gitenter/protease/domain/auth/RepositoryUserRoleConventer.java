package com.gitenter.protease.domain.auth;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RepositoryUserRoleConventer implements AttributeConverter<RepositoryUserRole,Character> {

	@Override
	public Character convertToDatabaseColumn(RepositoryUserRole role) {
		return role.getShortName();
	}
	
	@Override
	public RepositoryUserRole convertToEntityAttribute(Character dbData) {
		return RepositoryUserRole.fromShortName(dbData);
	}
}
