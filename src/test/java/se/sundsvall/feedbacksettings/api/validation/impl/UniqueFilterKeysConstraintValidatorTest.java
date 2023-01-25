package se.sundsvall.feedbacksettings.api.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackFilter;

class UniqueFilterKeysConstraintValidatorTest {

	private static final String KEY_1 = "key1";
	private static final String KEY_2 = "key2";

	private UniqueFilterKeysConstraintValidator validator = new UniqueFilterKeysConstraintValidator();

	@Test
	void sameKey() {
		RequestedFeedbackFilter filter_1 = RequestedFeedbackFilter.create().withKey(KEY_1);
		RequestedFeedbackFilter filter_2 = RequestedFeedbackFilter.create().withKey(KEY_1);
		assertThat(validator.isValid(List.of(filter_1, filter_2), null)).isFalse();
	}

	@Test
	void sameKeyDifferentCapitalization() {
		RequestedFeedbackFilter filter_1 = RequestedFeedbackFilter.create().withKey(KEY_1.toUpperCase());
		RequestedFeedbackFilter filter_2 = RequestedFeedbackFilter.create().withKey(KEY_1.toLowerCase());
		assertThat(validator.isValid(List.of(filter_1, filter_2), null)).isFalse();
	}
	
	@Test
	void sameKeyWithLeadingAndTrailingSpaces() {
		RequestedFeedbackFilter filter_1 = RequestedFeedbackFilter.create().withKey(KEY_1.concat(" "));
		RequestedFeedbackFilter filter_2 = RequestedFeedbackFilter.create().withKey(" ".concat(KEY_1));
		assertThat(validator.isValid(List.of(filter_1, filter_2), null)).isFalse();
	}
	
	@Test
	void differentKeys() {
		RequestedFeedbackFilter filter_1 = RequestedFeedbackFilter.create().withKey(KEY_1);
		RequestedFeedbackFilter filter_2 = RequestedFeedbackFilter.create().withKey(KEY_2);
		assertThat(validator.isValid(List.of(filter_1, filter_2), null)).isTrue();
	}
	
	@Test
	void oneIsNull() {
		List<RequestedFeedbackFilter> filters = new ArrayList<>();
		filters.add(null);
		filters.add(RequestedFeedbackFilter.create().withKey(KEY_1));
		
		assertThat(validator.isValid(filters, null)).isTrue();
	}

	@Test
	void oneHasNullKey() {
		RequestedFeedbackFilter filter_1 = RequestedFeedbackFilter.create();
		RequestedFeedbackFilter filter_2 = RequestedFeedbackFilter.create().withKey(KEY_2);
		assertThat(validator.isValid(List.of(filter_1, filter_2), null)).isTrue();
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
