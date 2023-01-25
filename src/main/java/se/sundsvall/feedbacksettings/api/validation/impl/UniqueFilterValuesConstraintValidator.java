package se.sundsvall.feedbacksettings.api.validation.impl;

import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import se.sundsvall.feedbacksettings.api.validation.UniqueFilterValues;

public class UniqueFilterValuesConstraintValidator implements ConstraintValidator<UniqueFilterValues, Collection<String>> {

	@Override
	public boolean isValid(final Collection<String> value, final ConstraintValidatorContext context) {
		Collection<String> nonNullValues = ofNullable(value).orElse(Collections.emptyList())
			.stream()
			.filter(Objects::nonNull)
			.toList();

		return nonNullValues.stream()
			.map(String::trim)
			.map(String::toLowerCase)
			.distinct()
			.count() == nonNullValues.size();
	}
}
