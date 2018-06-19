package com.gitenter.domain.auth;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RepositoryMemberRoleConventer implements AttributeConverter<RepositoryMemberRole,Character> {

	@Override
	public Character convertToDatabaseColumn (RepositoryMemberRole role) {
		return role.getShortName();
	}
	
	@Override
	public RepositoryMemberRole convertToEntityAttribute (Character dbData) {
		return RepositoryMemberRole.fromShortName(dbData);
	}
}
