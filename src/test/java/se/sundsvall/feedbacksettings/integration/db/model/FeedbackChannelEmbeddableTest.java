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

import se.sundsvall.feedbacksettings.ContactMethod;

class FeedbackChannelEmbeddableTest {

	@Test
	void testBean() {
		assertThat(FeedbackChannelEmbeddable.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		final var alias = "alias";
		final var contactMethod = ContactMethod.EMAIL;
		final var destination = "destination";
		final var sendFeedback = true;

		FeedbackChannelEmbeddable bean = FeedbackChannelEmbeddable.create()
			.withAlias(alias)
			.withContactMethod(contactMethod)
			.withDestination(destination)
			.withSendFeedback(sendFeedback);

		assertThat(bean.getAlias()).isEqualTo(alias);
		assertThat(bean.getContactMethod()).isEqualTo(contactMethod);
		assertThat(bean.getDestination()).isEqualTo(destination);
		assertThat(bean.isSendFeedback()).isEqualTo(sendFeedback);
	}

	@Test
	void testNoDirtOnCreatedBeans() {
		assertThat(FeedbackChannelEmbeddable.create()).hasAllNullFieldsOrPropertiesExcept("sendFeedback").hasFieldOrPropertyWithValue("sendFeedback", false);
		assertThat(new FeedbackChannelEmbeddable()).hasAllNullFieldsOrPropertiesExcept("sendFeedback").hasFieldOrPropertyWithValue("sendFeedback", false);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FeedbackChannelEmbeddable.create())
			.hasAllNullFieldsOrPropertiesExcept("sendFeedback")
			.hasFieldOrPropertyWithValue("sendFeedback", false);

		assertThat(new FeedbackChannelEmbeddable())
			.hasAllNullFieldsOrPropertiesExcept("sendFeedback")
			.hasFieldOrPropertyWithValue("sendFeedback", false);
	}
}
