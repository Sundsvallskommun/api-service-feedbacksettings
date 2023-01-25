package se.sundsvall.feedbacksettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.ContactMethod;

class FeedbackChannelTest {
	@Test
	void testBean() {
		assertThat(FeedbackChannel.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		final var alias = "alias";
		final var destination = "destination";
		final var contactMethod = ContactMethod.EMAIL;
		final var sendFeedback = true;
		
		FeedbackChannel channel = FeedbackChannel.create()
				.withAlias(alias)
				.withContactMethod(contactMethod)
				.withDestination(destination)
				.withSendFeedback(sendFeedback);
		
		assertThat(channel.getAlias()).isEqualTo(alias);
		assertThat(channel.getContactMethod()).isEqualTo(contactMethod);
		assertThat(channel.getDestination()).isEqualTo(destination);
		assertThat(channel.isSendFeedback()).isEqualTo(sendFeedback);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(FeedbackChannel.create())
			.hasAllNullFieldsOrPropertiesExcept("sendFeedback")
			.hasFieldOrPropertyWithValue("sendFeedback", false);

		assertThat(new FeedbackChannel())
			.hasAllNullFieldsOrPropertiesExcept("sendFeedback")
			.hasFieldOrPropertyWithValue("sendFeedback", false);
	}
}
