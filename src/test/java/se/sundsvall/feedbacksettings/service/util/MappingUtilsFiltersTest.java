package se.sundsvall.feedbacksettings.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import se.sundsvall.feedbacksettings.integration.db.model.FeedbackFilterEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

class MappingUtilsFiltersTest {

	private static final String KEY_1 = "key-1";
	private static final String KEY_2 = "KeY-2";
	private static final String KEY_3 = "KEY-3";
	private static final String VALUE_1 = "value-1";
	private static final String VALUE_2 = "VaLuE-2";
	private static final String VALUE_3 = "VALUE-3";
	
	private static final FeedbackFilterEmbeddable FILTER_1 = FeedbackFilterEmbeddable.create().withKey(KEY_1).withValue(VALUE_1);
	private static final FeedbackFilterEmbeddable FILTER_2 = FeedbackFilterEmbeddable.create().withKey(KEY_1).withValue(VALUE_2);
	private static final FeedbackFilterEmbeddable FILTER_3 = FeedbackFilterEmbeddable.create().withKey(KEY_2).withValue(VALUE_1);
	private static final FeedbackFilterEmbeddable FILTER_4 = FeedbackFilterEmbeddable.create().withKey(KEY_2).withValue(VALUE_2);
	private static final FeedbackFilterEmbeddable FILTER_5 = FeedbackFilterEmbeddable.create().withKey(KEY_3).withValue(VALUE_3);
	
	/**
	 * Entity contains:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 * Request contains: [FILTER_1, FILTER_3]
	 * Expected result:  [FILTER_2, FILTER_4]
	 */
	@Test
	void getRemovedFeedbackFiltersOneRemoved() {
		final var requestedFilters = List.of(FILTER_1, FILTER_3);
		
		assertThat(MappingUtils.getRemovedFeedbackFilters(generateEntity(), requestedFilters))
		.hasSize(2)
		.extracting( FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactlyInAnyOrder(
				tuple(FILTER_2.getKey(), FILTER_2.getValue()),
				tuple(FILTER_4.getKey(), FILTER_4.getValue()));
	}

	/**
	 * Entity contains:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 * Request contains: [FILTER_5]
	 * Expected result:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 */
	@Test
	void getRemovedFeedbackFiltersAllRemoved() {
		final var requestedFilters = List.of(FILTER_5);
		
		assertThat(MappingUtils.getRemovedFeedbackFilters(generateEntity(), requestedFilters))
		.hasSize(4)
		.extracting( FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactlyInAnyOrder(
				tuple(FILTER_1.getKey(), FILTER_1.getValue()),
				tuple(FILTER_2.getKey(), FILTER_2.getValue()),
				tuple(FILTER_3.getKey(), FILTER_3.getValue()),
				tuple(FILTER_4.getKey(), FILTER_4.getValue()));
	}

	/**
	 * Entity contains:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 * Request contains: [FILTER_1, FILTER_2, FILTER_3, FILTER_4] (with different capitalizations)
	 * Expected result:  []
	 */
	@Test
	void getRemovedFeedbackChannelsListUnchanged() {
		final var requestedFilters = List.of(
			FeedbackFilterEmbeddable.create()
				.withKey(FILTER_1.getKey().toLowerCase())
				.withValue(FILTER_1.getValue().toUpperCase()),
			FeedbackFilterEmbeddable.create()
				.withKey(FILTER_2.getKey().toLowerCase())
				.withValue(FILTER_2.getValue()),
			FeedbackFilterEmbeddable.create()
				.withKey(FILTER_3.getKey())
				.withValue(FILTER_3.getValue().toUpperCase()),
			FeedbackFilterEmbeddable.create()
				.withKey(FILTER_4.getKey().toLowerCase())
				.withValue(FILTER_4.getValue().toUpperCase()));
		
		assertThat(MappingUtils.getRemovedFeedbackFilters(generateEntity(), requestedFilters)).isEmpty();
	}
	
	/**
	 * Entity contains:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 * Request contains: [FILTER_1, FILTER_2, FILTER_3, FILTER_4, FILTER_5]
	 * Expected result:  []
	 */
	@Test
	void getRemovedFeedbackFiltersOneAdded() {
		final var requestedFilters = List.of(FILTER_1, FILTER_2, FILTER_3, FILTER_4, FILTER_5);
			
		assertThat(MappingUtils.getRemovedFeedbackFilters(generateEntity(), requestedFilters)).isEmpty();		
	}

	/**
	 * Entity contains:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 * Request contains: null
	 * Expected result:  []
	 */
	@Test
	void getRemovedFeedbackChannelsWhenListNull() {
		assertThat(MappingUtils.getRemovedFeedbackFilters(generateEntity(), null)).isEmpty();
	}

	/**
	 * Entity contains:  List in entity is null
	 * Request contains: [FILTER_1, FILTER_2, FILTER_3]
	 * Expected result:  []
	 */
	@Test
	void getRemovedFeedbackChannelsWhenOldEntityListNull() {
		final var requestedFilters = List.of(FILTER_1, FILTER_2, FILTER_3);
			
		assertThat(MappingUtils.getRemovedFeedbackFilters(FeedbackSettingEntity.create(), requestedFilters)).isEmpty();
	}
	
	
	/**
	 * Entity contains:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 * Request contains: [FILTER_1, FILTER_2, FILTER_3, FILTER_4, FILTER_5]
	 * Expected result:  [FILTER_5]
	 */
	@Test
	void getAddedFeedbackChannelsOneAdded() {
		final var requestedFilters = List.of(FILTER_1, FILTER_2, FILTER_3, FILTER_4, FILTER_5);
				
		assertThat(MappingUtils.getAddedFeedbackFilters(generateEntity(), requestedFilters))	
		.hasSize(1)
		.extracting( FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactlyInAnyOrder(
				tuple(FILTER_5.getKey(), FILTER_5.getValue()));
	}

	/**
	 * Entity contains:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 * Request contains: [FILTER_1, FILTER_2, FILTER_3]
	 * Expected result:  []
	 */
	@Test
	void getAddedFeedbackChannelsOneRemoved() {
		final var requestedFilters = List.of(FILTER_1, FILTER_2, FILTER_3);
				
		assertThat(MappingUtils.getAddedFeedbackFilters(generateEntity(), requestedFilters)).isEmpty();
	}

	/**
	 * Entity contains:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 * Request contains: [FILTER_1, FILTER_2, FILTER_3, FILTER_4] (with different capitalization)
	 * Expected result:  []
	 */
	@Test
	void getAddedFeedbackChannelsListUnchanged() {
		final var requestedFilters = List.of(
				FeedbackFilterEmbeddable.create()
					.withKey(FILTER_1.getKey().toLowerCase())
					.withValue(FILTER_1.getValue().toUpperCase()),
				FeedbackFilterEmbeddable.create()
					.withKey(FILTER_2.getKey().toLowerCase())
					.withValue(FILTER_2.getValue()),
				FeedbackFilterEmbeddable.create()
					.withKey(FILTER_3.getKey())
					.withValue(FILTER_3.getValue().toUpperCase()),
				FeedbackFilterEmbeddable.create()
					.withKey(FILTER_4.getKey().toLowerCase())
					.withValue(FILTER_4.getValue().toUpperCase()));

		assertThat(MappingUtils.getAddedFeedbackFilters(generateEntity(), requestedFilters)).isEmpty();
	}
	
	/**
	 * Entity contains:  Old entity null
	 * Request contains: [FILTER_1, FILTER_2, FILTER_3]
	 * Expected result:  [FILTER_1, FILTER_2, FILTER_3]
	 */
	@Test
	void getAddedFeedbackOldEntityNull() {
		final var requestedFilters = List.of(FILTER_1, FILTER_2, FILTER_3);

		assertThat(MappingUtils.getAddedFeedbackFilters(null, requestedFilters))
		.hasSize(3)
		.extracting( FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactlyInAnyOrder(
				tuple(FILTER_1.getKey(), FILTER_1.getValue()),
				tuple(FILTER_2.getKey(), FILTER_2.getValue()),
				tuple(FILTER_3.getKey(), FILTER_3.getValue()));
	}

	/**
	 * Entity contains:  List in old entity is null
	 * Request contains: [FILTER_1, FILTER_2, FILTER_3]
	 * Expected result:  [FILTER_1, FILTER_2, FILTER_3]
	 */
	@Test
	void getAddedFeedbackListInOldEntityNull() {
		final var requestedFilters = List.of(FILTER_1, FILTER_2, FILTER_3);

		assertThat(MappingUtils.getAddedFeedbackFilters(FeedbackSettingEntity.create(), requestedFilters))
		.hasSize(3)
		.extracting( FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactlyInAnyOrder(
				tuple(FILTER_1.getKey(), FILTER_1.getValue()),
				tuple(FILTER_2.getKey(), FILTER_2.getValue()),
				tuple(FILTER_3.getKey(), FILTER_3.getValue()));
	}

	/**
	 * Entity contains:  [FILTER_1, FILTER_2, FILTER_3, FILTER_4]
	 * Request contains: null
	 * Expected result:  []
	 */
	@Test
	void getAddedFeedbackRequestListNull() {
		assertThat(MappingUtils.getAddedFeedbackFilters(generateEntity(), null)).isEmpty();
	}

	private FeedbackSettingEntity generateEntity() {
		return FeedbackSettingEntity.create()
				.withFeedbackFilters(new ArrayList<>(List.of(
						FeedbackFilterEmbeddable.create()
							.withKey(FILTER_1.getKey())
							.withValue(FILTER_1.getValue()),
						FeedbackFilterEmbeddable.create()
							.withKey(FILTER_2.getKey())
							.withValue(FILTER_2.getValue()),
						FeedbackFilterEmbeddable.create()
							.withKey(FILTER_3.getKey())
							.withValue(FILTER_3.getValue()),
						FeedbackFilterEmbeddable.create()
							.withKey(FILTER_4.getKey())
							.withValue(FILTER_4.getValue())
				)));
	}
}
