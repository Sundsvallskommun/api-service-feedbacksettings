package se.sundsvall.feedbacksettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class RequestedFeedbackFilterTest {
	private static final String KEY = "key1";
	private static final List<String> VALUES = List.of("value1", "value2");
	
	@Test
	void testBean() {
		assertThat(RequestedFeedbackFilter.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		RequestedFeedbackFilter filter = RequestedFeedbackFilter.create()
				.withKey(KEY)
				.withValues(VALUES);
		
		assertThat(filter.getKey()).isEqualTo(KEY);
		assertThat(filter.getValues()).isEqualTo(VALUES);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(RequestedFeedbackFilter.create()).hasAllNullFieldsOrProperties();
		assertThat(new RequestedFeedbackFilter()).hasAllNullFieldsOrProperties();
	}
}
