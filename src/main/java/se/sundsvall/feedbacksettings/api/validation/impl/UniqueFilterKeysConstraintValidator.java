package se.sundsvall.feedbacksettings.api.validation.impl;

import static java.util.Optional.ofNullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackFilter;
import se.sundsvall.feedbacksettings.api.validation.UniqueFilterKeys;

public class UniqueFilterKeysConstraintValidator implements ConstraintValidator<UniqueFilterKeys, Collection<RequestedFeedbackFilter>> {

	@Override
	public boolean isValid(final Collection<RequestedFeedbackFilter> value, final ConstraintValidatorContext context) {
		Collection<String> nonNullKeys = ofNullable(value).orElse(Collections.emptyList())
			.stream()
			.filter(Objects::nonNull)
			.map(RequestedFeedbackFilter::getKey)
			.filter(Objects::nonNull)
			.map(String::trim)
			.map(String::toLowerCase)
			.toList();

		return nonNullKeys.stream()
			.distinct()
			.count() == nonNullKeys.size();
	}
}
