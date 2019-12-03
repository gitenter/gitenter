package com.gitenter.protease.domain.auth;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OrganizationUserRoleConventer implements AttributeConverter<OrganizationUserRole,Character> {

	@Override
	public Character convertToDatabaseColumn(OrganizationUserRole role) {
		return role.getShortName();
	}
	
	@Override
	public OrganizationUserRole convertToEntityAttribute(Character dbData) {
		return OrganizationUserRole.fromShortName(dbData);
	}
}
