package se.sundsvall.feedbacksettings.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

class UniqueFilterValuesConstraintValidatorTest {

	private static final String VALUE_1 = "value1";
	private static final String VALUE_2 = "value2";

	private UniqueFilterValuesConstraintValidator validator = new UniqueFilterValuesConstraintValidator();

	@Test
	void differentValues() {
		assertThat(validator.isValid(List.of(VALUE_1, VALUE_2), null)).isTrue();
	}

	@Test
	void sameValues() {
		assertThat(validator.isValid(List.of(VALUE_1, VALUE_1), null)).isFalse();
	}

	@Test
	void sameValueDifferentCapitalization() {
		assertThat(validator.isValid(List.of(VALUE_1.toLowerCase(), VALUE_1.toUpperCase()), null)).isFalse();
	}

	@Test
	void sameValueWithLeadingAndTrailingSpaces() {
		assertThat(validator.isValid(List.of(VALUE_1.concat(" "), " ".concat(VALUE_1)), null)).isFalse();
	}
	
	@Test
	void oneIsNull() {
		List<String> values = new ArrayList<>();
		values.add(null);
		values.add(VALUE_1);
		
		assertThat(validator.isValid(values, null)).isTrue();
	}

	@Test
	void emptyList() {
		assertThat(validator.isValid(Collections.emptyList(), null)).isTrue();
	}

	@Test
	void nullValue() {
		assertThat(validator.isValid(null, null)).isTrue();
	}
}
