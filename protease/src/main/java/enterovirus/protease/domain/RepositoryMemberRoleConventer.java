package enterovirus.protease.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RepositoryMemberRoleConventer implements AttributeConverter<RepositoryMemberRole,String> {

	@Override
	public String convertToDatabaseColumn (RepositoryMemberRole role) {
		return role.getShortName();
	}
	
	@Override
	public RepositoryMemberRole convertToEntityAttribute (String dbData) {
		System.out.println("dbData: "+dbData);
		return RepositoryMemberRole.fromShortName(dbData);
	}
}
