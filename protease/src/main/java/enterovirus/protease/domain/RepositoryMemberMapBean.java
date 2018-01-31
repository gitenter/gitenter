package enterovirus.protease.domain;

import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.*;

@Getter
@Setter
@Entity
@Table(schema = "config", name = "repository_member_map")
public class RepositoryMemberMapBean {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id", updatable=false)
	private Integer id;

	@ManyToOne
	@JoinColumn(name="repository_id")
	private RepositoryBean repository;
	
	@ManyToOne
	@JoinColumn(name="member_id")
	private MemberBean member;
	
	@Column(name="role")
	@Convert(converter = RepositoryMemberRoleConventer.class)
	private RepositoryMemberRole role;
	
//	public enum Role {
//		
//		READER,
//		REVIEWER,
//		EDITOR,
//		PROJECT_LEADER;
//	}
//	
//	/*
//	 * Refer to:
//	 * https://stackoverflow.com/questions/2751733/map-enum-in-jpa-with-fixed-values
//	 * https://www.thoughts-on-java.org/jpa-21-how-to-implement-type-converter/
//	 * https://www.thoughts-on-java.org/jpa-21-type-converter-better-way-to/
//	 * https://dzone.com/articles/mapping-enums-done-right
//	 */
//	@Converter(autoApply = true)
//	public class RoleConverter implements AttributeConverter<Role,Integer>{
//		
//		@Override
//		public Integer convertToDatabaseColumn (Role attribute) {
//			switch (attribute) {
//			case READER:
//				return 1;
//			case REVIEWER:
//				return 2;
//			case EDITOR:
//				return 3;
//			case PROJECT_LEADER:
//				return 4;
//			default:
//				throw new IllegalArgumentException("Unknown RepositoryMemberMapBean.Role: "+attribute);
//			}
//		}
//		
//		@Override
//		public Role convertToEntityAttribute (Integer dbData) {
//			switch (dbData) {
//			case 1:
//				return Role.READER;
//			case 2:
//				return Role.REVIEWER;
//			case 3:
//				return Role.EDITOR;
//			case 4:
//				return Role.PROJECT_LEADER;
//			default:
//				throw new IllegalArgumentException("Unknown RepositoryMemberMapBean.Role database column value: "+dbData);
//			}
//		}
//	}
}
