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
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class WeightedFeedbackSettingTest {
	private static final List<FeedbackChannel> CHANNELS = List.of(FeedbackChannel.create());
	private static final String ID = "uuid";
	private static final String ORGANIZATION_ID = "organizationId";
	private static final String PERSON_ID = "personId";
	private static final OffsetDateTime CREATED = OffsetDateTime.now().minusDays(1);
	private static final OffsetDateTime MODIFIED = OffsetDateTime.now();
	private static final int MATCHING_PERCENT = RandomUtils.nextInt();

	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(WeightedFeedbackSetting.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		WeightedFeedbackSetting settings = WeightedFeedbackSetting.create()
				.withChannels(CHANNELS)
				.withId(ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withPersonId(PERSON_ID)
				.withCreated(CREATED)
				.withMatchingPercent(MATCHING_PERCENT)
				.withModified(MODIFIED);
		
		assertThat(settings.getChannels()).isEqualTo(CHANNELS);
		assertThat(settings.getId()).isEqualTo(ID);
		assertThat(settings.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(settings.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(settings.getCreated()).isEqualTo(CREATED);
		assertThat(settings.getMatchingPercent()).isEqualTo(MATCHING_PERCENT);
		assertThat(settings.getModified()).isEqualTo(MODIFIED);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(WeightedFeedbackSetting.create()).hasAllNullFieldsOrPropertiesExcept("matchingPercent").hasFieldOrPropertyWithValue("matchingPercent", 0);
		assertThat(new WeightedFeedbackSetting()).hasAllNullFieldsOrPropertiesExcept("matchingPercent").hasFieldOrPropertyWithValue("matchingPercent", 0);
	}
}
