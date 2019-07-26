package com.gitenter.protease.domain.review;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class ReviewStatusConventer implements AttributeConverter<ReviewStatus,Character> {

	@Override
	public Character convertToDatabaseColumn(ReviewStatus status) {
		return status.getShortName();
	}
	
	@Override
	public ReviewStatus convertToEntityAttribute(Character dbData) {
		return ReviewStatus.fromShortName(dbData);
	}
}
