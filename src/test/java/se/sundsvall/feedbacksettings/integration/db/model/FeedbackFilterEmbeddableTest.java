package se.sundsvall.feedbacksettings.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import org.junit.jupiter.api.Test;

class FeedbackFilterEmbeddableTest {
	
	private static final String KEY = "key";
	private static final String VALUE = "value";
	
	@Test
	void testBean() {
		assertThat(FeedbackFilterEmbeddable.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		FeedbackFilterEmbeddable bean = FeedbackFilterEmbeddable.create()
				.withKey(KEY)
				.withValue(VALUE);
		
		assertThat(bean.getKey()).isEqualTo(KEY);
		assertThat(bean.getValue()).isEqualTo(VALUE);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FeedbackFilterEmbeddable.create()).hasAllNullFieldsOrProperties();
		assertThat(new FeedbackFilterEmbeddable()).hasAllNullFieldsOrProperties();
	}
}
