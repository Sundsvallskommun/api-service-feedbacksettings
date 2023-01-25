package se.sundsvall.feedbacksettings.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackChannelEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

class MappingUtilsChannelsTest {

	private static final String ALIAS_1 = "alias-1";
	private static final String ALIAS_2 = "alias-2";
	private static final String ALIAS_3 = "alias-3";
	private static final String ALIAS_4 = "alias-4";
	
	private static final String DESTINATION_1 = "destination-1";
	private static final String DESTINATION_2 = "destination-2";
	private static final String DESTINATION_3 = "destination-3";
	private static final String DESTINATION_4 = "destination-4";
	
	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: [DESTINATION_1, DESTINATION_3]
	 * Expected result:  [DESTINATION_2]
	 */
	@Test
	void getRemovedFeedbackChannelsOneRemoved() {
		final var requestedChannels = List.of(
			FeedbackChannelEmbeddable.create()
				.withContactMethod(ContactMethod.SMS)
				.withAlias(ALIAS_1)
				.withDestination(DESTINATION_1)
				.withSendFeedback(true),
			FeedbackChannelEmbeddable.create()
				.withContactMethod(ContactMethod.SMS)
				.withAlias(ALIAS_3)
				.withDestination(DESTINATION_3)
				.withSendFeedback(true));
		
		assertThat(MappingUtils.getRemovedFeedbackChannels(generateEntity(), requestedChannels))
		.hasSize(1)
		.extracting( FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.EMAIL, ALIAS_2, DESTINATION_2, false));
	}

	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: [DESTINATION_4]
	 * Expected result:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 */
	@Test
	void getRemovedFeedbackChannelsAllRemoved() {
		final var requestedChannels = List.of(
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_4)
					.withDestination(DESTINATION_4)
					.withSendFeedback(true));

		assertThat(MappingUtils.getRemovedFeedbackChannels(generateEntity(), requestedChannels))
		.hasSize(3)
		.extracting( FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, ALIAS_1, DESTINATION_1, true),
				tuple(ContactMethod.EMAIL, ALIAS_2, DESTINATION_2, false),
				tuple(ContactMethod.SMS, ALIAS_3, DESTINATION_3, true));
	}

	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Expected result:  []
	 */
	@Test
	void getRemovedFeedbackChannelsListUnchanged() {
		final var requestedChannels = List.of(
			FeedbackChannelEmbeddable.create()
				.withContactMethod(ContactMethod.SMS)
				.withAlias(ALIAS_1)
				.withDestination(DESTINATION_1)
				.withSendFeedback(true),
			FeedbackChannelEmbeddable.create()
				.withContactMethod(ContactMethod.EMAIL)
				.withAlias(ALIAS_2)
				.withDestination(DESTINATION_2)
				.withSendFeedback(false),
			FeedbackChannelEmbeddable.create()
				.withContactMethod(ContactMethod.SMS)
				.withAlias(ALIAS_3)
				.withDestination(DESTINATION_3)
				.withSendFeedback(true));
		
		assertThat(MappingUtils.getRemovedFeedbackChannels(generateEntity(), requestedChannels)).isEmpty();
	}
	
	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Expected result:  []
	 */
	@Test
	void getRemovedFeedbackChannelsListUnchangedAllUpperCase() {
		final var requestedChannels = List.of(
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_1.toUpperCase())
					.withDestination(DESTINATION_1.toUpperCase())
					.withSendFeedback(true),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_2.toUpperCase())
					.withDestination(DESTINATION_2.toUpperCase())
					.withSendFeedback(false),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_3.toUpperCase())
					.withDestination(DESTINATION_3.toUpperCase())
					.withSendFeedback(true));
			
		assertThat(MappingUtils.getRemovedFeedbackChannels(generateEntity(), requestedChannels)).isEmpty();		
	}

	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: [DESTINATION_1, DESTINATION_2, DESTINATION_3, DESTINATION_4]
	 * Expected result:  []
	 */
	@Test
	void getRemovedFeedbackChannelsOneAdded() {
		final var requestedChannels = List.of(
			FeedbackChannelEmbeddable.create()
				.withContactMethod(ContactMethod.SMS)
				.withAlias(ALIAS_1)
				.withDestination(DESTINATION_1)
				.withSendFeedback(true),
			FeedbackChannelEmbeddable.create()
				.withContactMethod(ContactMethod.EMAIL)
				.withAlias(ALIAS_2)
				.withDestination(DESTINATION_2)
				.withSendFeedback(false),
			FeedbackChannelEmbeddable.create()
				.withContactMethod(ContactMethod.SMS)
				.withAlias(ALIAS_3)
				.withDestination(DESTINATION_3)
				.withSendFeedback(true),
			FeedbackChannelEmbeddable.create()
				.withContactMethod(ContactMethod.EMAIL)
				.withAlias(ALIAS_4)
				.withDestination(DESTINATION_4)
				.withSendFeedback(true));
			
		assertThat(MappingUtils.getRemovedFeedbackChannels(generateEntity(), requestedChannels)).isEmpty();		
	}

	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: null
	 * Expected result:  []
	 */
	@Test
	void getRemovedFeedbackChannelsWhenListNull() {
		assertThat(MappingUtils.getRemovedFeedbackChannels(generateEntity(), null)).isEmpty();
	}

	/**
	 * Entity contains:  List in entity is null
	 * Request contains: [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Expected result:  []
	 */
	@Test
	void getRemovedFeedbackChannelsWhenOldEntityListNull() {
		final var requestedChannels = List.of(
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_1)
					.withDestination(DESTINATION_1.toUpperCase())
					.withSendFeedback(true),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_2)
					.withDestination(DESTINATION_2.toUpperCase())
					.withSendFeedback(false),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_3)
					.withDestination(DESTINATION_3.toUpperCase())
					.withSendFeedback(true));
			
		assertThat(MappingUtils.getRemovedFeedbackChannels(FeedbackSettingEntity.create(), requestedChannels)).isEmpty();
	}
	
	
	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: [DESTINATION_1, DESTINATION_2, DESTINATION_3, DESTINATION_4]
	 * Expected result:  [DESTINATION_4]
	 */
	@Test
	void getAddedFeedbackChannelsOneAdded() {
		final var requestedChannels = List.of(
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_1)
					.withDestination(DESTINATION_1)
					.withSendFeedback(true),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_2)
					.withDestination(DESTINATION_2)
					.withSendFeedback(false),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_3)
					.withDestination(DESTINATION_3)
					.withSendFeedback(true),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_4)
					.withDestination(DESTINATION_4)
					.withSendFeedback(true));
				
		assertThat(MappingUtils.getAddedFeedbackChannels(generateEntity(), requestedChannels))	
		.hasSize(1)
		.extracting( FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.EMAIL, ALIAS_4, DESTINATION_4, true));
	}

	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: [DESTINATION_1, DESTINATION_2]
	 * Expected result:  []
	 */
	@Test
	void getAddedFeedbackChannelsOneRemoved() {
		final var requestedChannels = List.of(
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_1)
					.withDestination(DESTINATION_1)
					.withSendFeedback(true),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_2)
					.withDestination(DESTINATION_2)
					.withSendFeedback(false));
				
		assertThat(MappingUtils.getAddedFeedbackChannels(generateEntity(), requestedChannels)).isEmpty();
	}

	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Expected result:  []
	 */
	@Test
	void getAddedFeedbackChannelsListUnchanged() {
		final var requestedChannels = List.of(
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_1)
					.withDestination(DESTINATION_1)
					.withSendFeedback(true),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_2)
					.withDestination(DESTINATION_2)
					.withSendFeedback(false),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_3)
					.withDestination(DESTINATION_3)
					.withSendFeedback(true));

		assertThat(MappingUtils.getAddedFeedbackChannels(generateEntity(), requestedChannels)).isEmpty();
	}
	
	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Expected result:  []
	 */
	@Test
	void getAddedFeedbackChannelsListUnchangedAllUpperCase() {
		final var requestedChannels = List.of(
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_1)
					.withDestination(DESTINATION_1.toUpperCase())
					.withSendFeedback(true),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_2)
					.withDestination(DESTINATION_2.toUpperCase())
					.withSendFeedback(false),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_3)
					.withDestination(DESTINATION_3.toUpperCase())
					.withSendFeedback(true));

		assertThat(MappingUtils.getAddedFeedbackChannels(generateEntity(), requestedChannels)).isEmpty();
	}

	/**
	 * Entity contains:  Old entity null
	 * Request contains: [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Expected result:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 */
	@Test
	void getAddedFeedbackOldEntityNull() {
		final var requestedChannels = List.of(
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_1)
					.withDestination(DESTINATION_1)
					.withSendFeedback(true),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_2)
					.withDestination(DESTINATION_2)
					.withSendFeedback(false),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_3)
					.withDestination(DESTINATION_3)
					.withSendFeedback(true));

		assertThat(MappingUtils.getAddedFeedbackChannels(null, requestedChannels))
		.hasSize(3)
		.extracting( FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, ALIAS_1, DESTINATION_1, true),
				tuple(ContactMethod.EMAIL, ALIAS_2, DESTINATION_2, false),
				tuple(ContactMethod.SMS, ALIAS_3, DESTINATION_3, true));
	}

	/**
	 * Entity contains:  List in old entity is null
	 * Request contains: [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Expected result:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 */
	@Test
	void getAddedFeedbackListInOldEntityNull() {
		final var requestedChannels = List.of(
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_1)
					.withDestination(DESTINATION_1)
					.withSendFeedback(true),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.EMAIL)
					.withAlias(ALIAS_2)
					.withDestination(DESTINATION_2)
					.withSendFeedback(false),
				FeedbackChannelEmbeddable.create()
					.withContactMethod(ContactMethod.SMS)
					.withAlias(ALIAS_3)
					.withDestination(DESTINATION_3)
					.withSendFeedback(true));

		assertThat(MappingUtils.getAddedFeedbackChannels(FeedbackSettingEntity.create(), requestedChannels))
		.hasSize(3)
		.extracting( FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, ALIAS_1, DESTINATION_1, true),
				tuple(ContactMethod.EMAIL, ALIAS_2, DESTINATION_2, false),
				tuple(ContactMethod.SMS, ALIAS_3, DESTINATION_3, true));
	}

	/**
	 * Entity contains:  [DESTINATION_1, DESTINATION_2, DESTINATION_3]
	 * Request contains: null
	 * Expected result:  []
	 */
	@Test
	void getAddedFeedbackRequestListNull() {
		assertThat(MappingUtils.getAddedFeedbackChannels(generateEntity(), null)).isEmpty();
	}

	private FeedbackSettingEntity generateEntity() {
		return FeedbackSettingEntity.create()
				.withFeedbackChannels(new ArrayList<>(List.of(
						FeedbackChannelEmbeddable.create()
							.withContactMethod(ContactMethod.SMS)
							.withAlias(ALIAS_1)
							.withDestination(DESTINATION_1)
							.withSendFeedback(true),
						FeedbackChannelEmbeddable.create()
							.withContactMethod(ContactMethod.EMAIL)
							.withAlias(ALIAS_2)
							.withDestination(DESTINATION_2)
							.withSendFeedback(false),
						FeedbackChannelEmbeddable.create()
							.withContactMethod(ContactMethod.SMS)
							.withAlias(ALIAS_3)
							.withDestination(DESTINATION_3)
							.withSendFeedback(true)
				)));
	}
}
