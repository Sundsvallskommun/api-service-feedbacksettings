package se.sundsvall.feedbacksettings.api.validation.impl;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PatternValidator<A extends Annotation> implements ConstraintValidator<A, CharSequence> {

	private String pattern;

	public PatternValidator(String pattern) {
		this.pattern = pattern;
	}
	
	@Override
	public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
		return String.valueOf(value).matches(pattern);
	}
}
