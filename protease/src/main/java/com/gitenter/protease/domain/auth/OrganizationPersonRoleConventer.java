package com.gitenter.protease.domain.auth;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OrganizationPersonRoleConventer implements AttributeConverter<OrganizationPersonRole,Character> {

	@Override
	public Character convertToDatabaseColumn(OrganizationPersonRole role) {
		return role.getShortName();
	}
	
	@Override
	public OrganizationPersonRole convertToEntityAttribute(Character dbData) {
		return OrganizationPersonRole.fromShortName(dbData);
	}
}
