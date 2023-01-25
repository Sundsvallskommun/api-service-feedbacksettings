package se.sundsvall.feedbacksettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.OffsetDateTime;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.ContactMethod;

class RequestedFeedbackChannelTest {
	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(RequestedFeedbackChannel.class, allOf(
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

		RequestedFeedbackChannel channel = RequestedFeedbackChannel.create()
				.withAlias(alias)
				.withContactMethod(contactMethod)
				.withDestination(destination)
				.withSendFeedback(sendFeedback);
		
		assertThat(channel.getAlias()).isEqualTo(alias);
		assertThat(channel.getContactMethod()).isEqualTo(contactMethod);
		assertThat(channel.getDestination()).isEqualTo(destination);
		assertThat(channel.getSendFeedback()).isEqualTo(sendFeedback);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(RequestedFeedbackChannel.create())
			.hasAllNullFieldsOrProperties();

		assertThat(new RequestedFeedbackChannel())
			.hasAllNullFieldsOrProperties();
	}
}
