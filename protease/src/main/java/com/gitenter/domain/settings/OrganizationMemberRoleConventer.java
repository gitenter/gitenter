package com.gitenter.domain.settings;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class OrganizationMemberRoleConventer implements AttributeConverter<OrganizationMemberRole,Character> {

	@Override
	public Character convertToDatabaseColumn (OrganizationMemberRole role) {
		return role.getShortName();
	}
	
	@Override
	public OrganizationMemberRole convertToEntityAttribute (Character dbData) {
		return OrganizationMemberRole.fromShortName(dbData);
	}
}
