package se.sundsvall.feedbacksettings.integration.db.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static com.google.code.beanmatchers.BeanMatchers.registerValueGenerator;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FeedbackSettingEntityTest {

	private static final String ID = "id";
	private static final String PERSON_ID = "personId";
	private static final String ORGANIZATION_ID = "organizationId";
	private static final List<FeedbackChannelEmbeddable> FEEDBACK_CHANNELS = List.of(FeedbackChannelEmbeddable.create());
	
	@BeforeAll
	static void setup() {
		registerValueGenerator(() -> now().plusDays(new Random().nextInt()), OffsetDateTime.class);
	}

	@Test
	void testBean() {
		assertThat(FeedbackSettingEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}

	@Test
	void testCreatePattern() {
		FeedbackSettingEntity entity = FeedbackSettingEntity.create()
				.withId(ID)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(FEEDBACK_CHANNELS)
				.withCreated(OffsetDateTime.now())
				.withModified(OffsetDateTime.now());

		assertThat(entity.getId()).isEqualTo(ID);
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getFeedbackChannels()).isEqualTo(FEEDBACK_CHANNELS);
		assertThat(entity.getCreated()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(1, ChronoUnit.SECONDS));
	}

	@Test
	void testPrePersist() {
		FeedbackSettingEntity entity = FeedbackSettingEntity.create();
		entity.prePersist();

		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("created");
		assertThat(entity.getCreated()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));
	}

	@Test
	void testUpdate() {
		FeedbackSettingEntity entity = FeedbackSettingEntity.create();
		entity.preUpdate();

		assertThat(entity).hasAllNullFieldsOrPropertiesExcept("modified");
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(new FeedbackSettingEntity()).hasAllNullFieldsOrProperties();
		assertThat(FeedbackSettingEntity.create()).hasAllNullFieldsOrProperties();
	}
}
