package se.sundsvall.feedbacksettings.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.groups.Tuple.tuple;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher.NullHandler;
import org.springframework.http.HttpHeaders;

import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.FeedbackFilter;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackFilter;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackChannelEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackFilterEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

class FeedbackSettingsMapperTest {
	
	private static final String FEEDBACK_SETTING_ID = "settingId";
	private static final String ALIAS_FOR_EMAIL_ADDRESS = "aliasForEmailAddress";
	private static final String EMAIL_ADDRESS = "emailAddress";
	private static final String ALIAS_FOR_MOBILE_NUMBER = "aliasForMobileNumber";
	private static final String MOBILE_NUMBER = "mobileNumber";
	private static final String PERSON_ID = "personId";
	private static final String ORGANIZATION_ID = "organizationId";
	private static final Boolean SEND_FEEDBACK = Boolean.TRUE;
	private static final OffsetDateTime CREATED = OffsetDateTime.now().minusDays(5L);
	private static final OffsetDateTime MODIFIED = OffsetDateTime.now().minusDays(2L);
	private static final String KEY = "key";
	private static final String VALUE = "value";
	private static final String HEADER_X_UNKNOWMN = "x-unknown-blargh";
	private static final String HEADER_X_FILTER = "x-filter-";
	private static final String[] IGNORED_PATHS = new String[]{"id", "feedbackFilters", "feedbackChannels", "created", "modified"};

	@Test
	void toFeedbackSettingsEntityFromCreateRequestWithAlias() {
		final var postRequest = CreateFeedbackSettingRequest.create()
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withAlias(ALIAS_FOR_EMAIL_ADDRESS)
								.withDestination(EMAIL_ADDRESS)
								.withSendFeedback(SEND_FEEDBACK),
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withAlias(ALIAS_FOR_MOBILE_NUMBER)
								.withDestination(MOBILE_NUMBER)
								.withSendFeedback(SEND_FEEDBACK)))
				.withFilters(List.of(RequestedFeedbackFilter.create()
						.withKey(KEY)
						.withValues(List.of(VALUE))));
		
		final var entity = FeedbackSettingsMapper.toFeedbackSettingEntity(postRequest);
		
		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isNull();
		assertThat(entity.getCreated()).isNull();
		assertThat(entity.getModified()).isNull();
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		
		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, ALIAS_FOR_MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK), 
				tuple(ContactMethod.EMAIL, ALIAS_FOR_EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));
		
		assertThat(entity.getFeedbackFilters())
		.hasSize(1)
		.extracting(FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactlyInAnyOrder(
				tuple(KEY.toUpperCase(), VALUE));
	}

	@Test
	void toFeedbackSettingsEntityFromCreateRequestWithoutAlias() {
		final var postRequest = CreateFeedbackSettingRequest.create()
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withDestination(EMAIL_ADDRESS)
								.withSendFeedback(SEND_FEEDBACK),
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withDestination(MOBILE_NUMBER)
								.withSendFeedback(SEND_FEEDBACK)))
				.withFilters(List.of(RequestedFeedbackFilter.create()
						.withKey(KEY)
						.withValues(List.of(VALUE))));
		
		final var entity = FeedbackSettingsMapper.toFeedbackSettingEntity(postRequest);
		
		assertThat(entity).isNotNull();
		assertThat(entity.getId()).isNull();
		assertThat(entity.getCreated()).isNull();
		assertThat(entity.getModified()).isNull();
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		
		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK), 
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));
		
		assertThat(entity.getFeedbackFilters())
		.hasSize(1)
		.extracting(FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactlyInAnyOrder(
				tuple(KEY.toUpperCase(), VALUE));
	}

	@Test
	void toFeedbackSettingsEntityFromNullPostRequest() {
		final var entity = FeedbackSettingsMapper.toFeedbackSettingEntity(null);
		
		assertThat(entity).isNull();
	}

	@Test
	void toFeedbackSettingsFromEntity() {
		final var entity = generateEntity();
		final var settings = FeedbackSettingsMapper.toFeedbackSetting(entity);
		
		assertThat(settings).isNotNull();
		assertThat(settings.getId()).isEqualTo(FEEDBACK_SETTING_ID);
		assertThat(settings.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(settings.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		
		assertThat(settings.getChannels())
		.hasSize(2)
		.extracting(FeedbackChannel::getContactMethod, FeedbackChannel::getAlias, FeedbackChannel::getDestination, FeedbackChannel::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, ALIAS_FOR_MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK), 
				tuple(ContactMethod.EMAIL, ALIAS_FOR_EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));
		assertThat(settings.getCreated()).isEqualTo(CREATED);
		assertThat(settings.getModified()).isEqualTo(MODIFIED);
	}

	@Test
	void toFeedbackSettingsFromNullEntity() {
		final var settings = FeedbackSettingsMapper.toFeedbackSetting((FeedbackSettingEntity)null);
		
		assertThat(settings).isNull();
	}

	@Test
	void toFeedbackSettingsFromEntities() {
		final var entities = List.of(generateEntity(), generateEntity(), generateEntity());
		final var settings = FeedbackSettingsMapper.toFeedbackSettings(entities);
		
		assertThat(settings).isNotNull().hasSize(3);
		
		for (int i=0; i<settings.size(); i++) {
			assertThat(settings.get(i).getId()).isEqualTo(FEEDBACK_SETTING_ID);
			assertThat(settings.get(i).getPersonId()).isEqualTo(PERSON_ID);
			assertThat(settings.get(i).getOrganizationId()).isEqualTo(ORGANIZATION_ID);
			assertThat(settings.get(i).getChannels())
			.hasSize(2)
			.extracting(FeedbackChannel::getContactMethod, FeedbackChannel::getAlias, FeedbackChannel::getDestination, FeedbackChannel::isSendFeedback)
			.containsExactlyInAnyOrder(
					tuple(ContactMethod.SMS, ALIAS_FOR_MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK), 
					tuple(ContactMethod.EMAIL, ALIAS_FOR_EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));
			assertThat(settings.get(i).getCreated()).isEqualTo(CREATED);
			assertThat(settings.get(i).getModified()).isEqualTo(MODIFIED);
		}
	}
	
	@Test
	void toFeedbackSettingsFromNullEntities() {
		final var settings = FeedbackSettingsMapper.toFeedbackSettings((List<FeedbackSettingEntity>)null);
		
		assertThat(settings).isNotNull().isEmpty();
	}

	@Test
	void toFeedbackSettingsFromEmptyList() {
		final var settings = FeedbackSettingsMapper.toFeedbackSettings(Collections.emptyList());
		
		assertThat(settings).isNotNull().isEmpty();
	}
	
	@Test
	void toWeightedFeedbackSettingsFromEntity() {
		final var entity = generateEntity();
		final var settings = FeedbackSettingsMapper.toWeightedFeedbackSetting(entity);
		
		assertThat(settings).isNotNull();
		assertThat(settings.getId()).isEqualTo(FEEDBACK_SETTING_ID);
		assertThat(settings.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(settings.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		
		assertThat(settings.getChannels())
		.hasSize(2)
		.extracting(FeedbackChannel::getContactMethod, FeedbackChannel::getAlias, FeedbackChannel::getDestination, FeedbackChannel::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, ALIAS_FOR_MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK), 
				tuple(ContactMethod.EMAIL, ALIAS_FOR_EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));
		
		assertThat(settings.getFilters())
		.hasSize(1)
		.extracting(FeedbackFilter::getKey, FeedbackFilter::getValues)
		.containsExactlyInAnyOrder(
				tuple(KEY, List.of(VALUE)));
		
		assertThat(settings.getCreated()).isEqualTo(CREATED);
		assertThat(settings.getModified()).isEqualTo(MODIFIED);
		assertThat(settings.getMatchingPercent()).isEqualTo(100);
	}

	@Test
	void toWeightedFeedbackSettingsFromNullEntity() {
		final var settings = FeedbackSettingsMapper.toWeightedFeedbackSetting((FeedbackSettingEntity)null);
		
		assertThat(settings).isNull();
	}

	@Test
	void toWeightedFeedbackSettingsFromEntities() {
		final var entities = List.of(generateEntity(), generateEntity(), generateEntity());
		final var settings = FeedbackSettingsMapper.toWeightedFeedbackSettings(entities);
		
		assertThat(settings).isNotNull().hasSize(3);
		
		for (int i=0; i<settings.size(); i++) {
			assertThat(settings.get(i).getId()).isEqualTo(FEEDBACK_SETTING_ID);
			assertThat(settings.get(i).getPersonId()).isEqualTo(PERSON_ID);
			assertThat(settings.get(i).getOrganizationId()).isEqualTo(ORGANIZATION_ID);

			assertThat(settings.get(i).getChannels())
			.hasSize(2)
			.extracting(FeedbackChannel::getContactMethod, FeedbackChannel::getAlias, FeedbackChannel::getDestination, FeedbackChannel::isSendFeedback)
			.containsExactlyInAnyOrder(
					tuple(ContactMethod.SMS, ALIAS_FOR_MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK), 
					tuple(ContactMethod.EMAIL, ALIAS_FOR_EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));

			assertThat(settings.get(i).getFilters())
			.hasSize(1)
			.extracting(FeedbackFilter::getKey, FeedbackFilter::getValues)
			.containsExactlyInAnyOrder(
					tuple(KEY, List.of(VALUE)));

			assertThat(settings.get(i).getCreated()).isEqualTo(CREATED);
			assertThat(settings.get(i).getModified()).isEqualTo(MODIFIED);
			assertThat(settings.get(i).getMatchingPercent()).isEqualTo(100);
		}
	}
	
	@Test
	void toWeightedFeedbackSettingsFromNullEntities() {
		final var settings = FeedbackSettingsMapper.toWeightedFeedbackSettings((List<FeedbackSettingEntity>)null);
		
		assertThat(settings).isNotNull().isEmpty();
	}

	@Test
	void toWeightedFeedbackSettingsFromEmptyList() {
		final var settings = FeedbackSettingsMapper.toWeightedFeedbackSettings(Collections.emptyList());
		
		assertThat(settings).isNotNull().isEmpty();
	}
	
	@Test
	void mergeFeedbackSettingsWithChangedChannelAndFilter() {
		final var entity = generateEntity();

		final var request = UpdateFeedbackSettingRequest.create()
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withAlias(ALIAS_FOR_EMAIL_ADDRESS.concat("updated"))
								.withDestination(EMAIL_ADDRESS.concat("updated"))
								.withSendFeedback(!SEND_FEEDBACK),
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withDestination(MOBILE_NUMBER.concat("updated"))
								.withSendFeedback(!SEND_FEEDBACK)))
				.withFilters(List.of(RequestedFeedbackFilter.create()
						.withKey(KEY.concat("updated"))
						.withValues(List.of(VALUE.concat("updated")))));
		
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, request) ;
		
		//Only feedback attributes in Entity should be changed (plus modified date)
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isNotEqualTo(MODIFIED);
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));

		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, MOBILE_NUMBER.concat("updated"), MOBILE_NUMBER.concat("updated"), !SEND_FEEDBACK), 
				tuple(ContactMethod.EMAIL, ALIAS_FOR_EMAIL_ADDRESS.concat("updated"), EMAIL_ADDRESS.concat("updated"), !SEND_FEEDBACK));

		assertThat(entity.getFeedbackFilters())
		.hasSize(1)
		.extracting(FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactlyInAnyOrder(
				tuple(KEY.concat("updated").toUpperCase(), VALUE.concat("updated")));
	}

	@Test
	void mergeFeedbackSettingsWithRemovedChannelAndFilter() {
		final var entity = generateEntity();

		final var request = UpdateFeedbackSettingRequest.create()
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withDestination(EMAIL_ADDRESS)
								.withSendFeedback(SEND_FEEDBACK)))
				.withFilters(Collections.emptyList());
		
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, request) ;
		
		//Only feedback attributes in Entity should be changed (plus modified date)
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isNotEqualTo(MODIFIED);
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));
		
		assertThat(entity.getFeedbackChannels())
		.hasSize(1)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactly(
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));

		assertThat(entity.getFeedbackFilters()).isEmpty();
	}

	@Test
	void mergeFeedbackSettingsWithAddedChannelAndFilter() {
		final var entity = generateEntity();

		final var request = UpdateFeedbackSettingRequest.create()
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withDestination(EMAIL_ADDRESS)
								.withSendFeedback(SEND_FEEDBACK),
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withDestination(MOBILE_NUMBER)
								.withSendFeedback(SEND_FEEDBACK),
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withDestination(MOBILE_NUMBER.concat("2"))
								.withSendFeedback(SEND_FEEDBACK)))
				.withFilters(List.of(
						RequestedFeedbackFilter.create()
								.withKey(KEY)
								.withValues(List.of(VALUE)),
						RequestedFeedbackFilter.create()
								.withKey(KEY.concat("_other"))
								.withValues(List.of(VALUE.concat("_other")))
						));
		
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, request) ;
		
		//Only feedback attributes in Entity should be changed (plus modified date)
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isNotEqualTo(MODIFIED);
		assertThat(entity.getModified()).isCloseTo(OffsetDateTime.now(), within(2, ChronoUnit.SECONDS));

		assertThat(entity.getFeedbackChannels())
		.hasSize(3)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactly(
				tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK),
				tuple(ContactMethod.SMS, MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK),
				tuple(ContactMethod.SMS, MOBILE_NUMBER.concat("2"), MOBILE_NUMBER.concat("2"), SEND_FEEDBACK));

		assertThat(entity.getFeedbackFilters())
		.hasSize(2)
		.extracting(FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactlyInAnyOrder(
				tuple(KEY, VALUE),
				tuple(KEY.concat("_other").toUpperCase(), VALUE.concat("_other")));
	}

	@Test
	void mergeFeedbackSettingsWithNoChanges() {
		final var entity = generateEntity().withModified(null);

		final var request = UpdateFeedbackSettingRequest.create()
				.withChannels(List.of(
						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.SMS)
								.withAlias(ALIAS_FOR_MOBILE_NUMBER)
								.withDestination(MOBILE_NUMBER)
								.withSendFeedback(SEND_FEEDBACK),
 						RequestedFeedbackChannel.create()
								.withContactMethod(ContactMethod.EMAIL)
								.withAlias(ALIAS_FOR_EMAIL_ADDRESS)
								.withDestination(EMAIL_ADDRESS)
								.withSendFeedback(SEND_FEEDBACK)))
				.withFilters(List.of(
						RequestedFeedbackFilter.create()
								.withKey(KEY)
								.withValues(List.of(VALUE))));
		
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, request) ;
		
		//Entity should be untouched
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isNull();

		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, ALIAS_FOR_MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK),
				tuple(ContactMethod.EMAIL, ALIAS_FOR_EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));
		
		assertThat(entity.getFeedbackFilters())
		.hasSize(1)
		.extracting(FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactly(
				tuple(KEY, VALUE));
	}

	@Test
	void mergeFeedbackSettingsFromNullSettings() {
		final var entity = generateEntity();
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, null) ;

		//Entity should be untouched
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isEqualTo(MODIFIED);

		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, ALIAS_FOR_MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK),
				tuple(ContactMethod.EMAIL, ALIAS_FOR_EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));
		
		assertThat(entity.getFeedbackFilters())
		.hasSize(1)
		.extracting(FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactly(
				tuple(KEY, VALUE));
	}

	@Test
	void mergeFeedbackSettingsFromNullList() {
		final var entity = generateEntity();
		FeedbackSettingsMapper.mergeFeedbackSettings(entity, UpdateFeedbackSettingRequest.create()) ;

		//Entity should be untouched
		assertThat(entity.getId()).isEqualTo(FEEDBACK_SETTING_ID); 
		assertThat(entity.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entity.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(entity.getCreated()).isEqualTo(CREATED); 
		assertThat(entity.getModified()).isEqualTo(MODIFIED);

		assertThat(entity.getFeedbackChannels())
		.hasSize(2)
		.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getAlias, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
		.containsExactlyInAnyOrder(
				tuple(ContactMethod.SMS, ALIAS_FOR_MOBILE_NUMBER, MOBILE_NUMBER, SEND_FEEDBACK),
				tuple(ContactMethod.EMAIL, ALIAS_FOR_EMAIL_ADDRESS, EMAIL_ADDRESS, SEND_FEEDBACK));
		
		assertThat(entity.getFeedbackFilters())
		.hasSize(1)
		.extracting(FeedbackFilterEmbeddable::getKey, FeedbackFilterEmbeddable::getValue)
		.containsExactly(
				tuple(KEY, VALUE));
	}

	@Test
	void toFeedbackFiltersFromNull() {
		assertThat(FeedbackSettingsMapper.toFeedbackFilters(null)).isEmpty();
	}

	@Test
	void toFeedbackFilters() {
		HttpHeaders headers = new HttpHeaders();
		headers.add(HEADER_X_UNKNOWMN, "any-string");
		headers.add(HEADER_X_FILTER.concat(KEY), "value-1");
		headers.add(HEADER_X_FILTER.concat(KEY), "VaLuE-1");
		headers.add(HEADER_X_FILTER.concat(KEY), "value-2");
		headers.add(HEADER_X_FILTER.concat(KEY).concat("-2"), "VALUE-3");
		
		assertThat(FeedbackSettingsMapper.toFeedbackFilters(headers))
		.hasSize(2)
		.extracting(FeedbackFilter::getKey, FeedbackFilter::getValues)
		.containsExactlyInAnyOrder(
				tuple(KEY, List.of("value-1", "value-2")),
				tuple(KEY.concat("-2"), List.of("VALUE-3")));
	}

	@Test
	void toExampleWithAllIdsSet() {
		Example<FeedbackSettingEntity> example = FeedbackSettingsMapper.toExample(PERSON_ID, ORGANIZATION_ID, true);

		assertThat(example.getProbe())
				.hasAllNullFieldsOrPropertiesExcept("personId", "organizationId")
				.extracting(FeedbackSettingEntity::getPersonId, FeedbackSettingEntity::getOrganizationId)
				.containsExactlyInAnyOrder(PERSON_ID, ORGANIZATION_ID);
		assertThat(example.getMatcher().getNullHandler()).isEqualTo(NullHandler.INCLUDE);
		assertThat(example.getMatcher().getIgnoredPaths())
				.containsExactlyInAnyOrder(IGNORED_PATHS);
	}
	
	@Test
	void toExampleWithPersonIdSet() {
		Example<FeedbackSettingEntity> example = FeedbackSettingsMapper.toExample(PERSON_ID, null, false);
		
		assertThat(example.getProbe())
				.hasAllNullFieldsOrPropertiesExcept("personId")
				.extracting(FeedbackSettingEntity::getPersonId)
				.isEqualTo(PERSON_ID);
		assertThat(example.getMatcher().getNullHandler()).isEqualTo(NullHandler.IGNORE);
		assertThat(example.getMatcher().getIgnoredPaths())
				.containsExactlyInAnyOrder(IGNORED_PATHS);
	}

	@Test
	void toExampleWithOrganizationIdSet() {
		Example<FeedbackSettingEntity> example = FeedbackSettingsMapper.toExample(null, ORGANIZATION_ID, true);

		assertThat(example.getProbe())
				.hasAllNullFieldsOrPropertiesExcept("organizationId")
				.extracting(FeedbackSettingEntity::getOrganizationId)
				.isEqualTo(ORGANIZATION_ID);
		assertThat(example.getMatcher().getNullHandler()).isEqualTo(NullHandler.INCLUDE);
		assertThat(example.getMatcher().getIgnoredPaths())
				.containsExactlyInAnyOrder(IGNORED_PATHS);
	}

	private FeedbackSettingEntity generateEntity() {
		return FeedbackSettingEntity.create()
				.withId(FEEDBACK_SETTING_ID)
				.withCreated(CREATED)
				.withModified(MODIFIED)
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withFeedbackChannels(new ArrayList<>(List.of(
					FeedbackChannelEmbeddable.create()
							.withContactMethod(ContactMethod.EMAIL)
							.withAlias(ALIAS_FOR_EMAIL_ADDRESS)
							.withDestination(EMAIL_ADDRESS)
							.withSendFeedback(SEND_FEEDBACK),
					FeedbackChannelEmbeddable.create()
							.withContactMethod(ContactMethod.SMS)
							.withAlias(ALIAS_FOR_MOBILE_NUMBER)
							.withDestination(MOBILE_NUMBER)
							.withSendFeedback(SEND_FEEDBACK)
				)))
				.withFeedbackFilters(new ArrayList<>(List.of(
					FeedbackFilterEmbeddable.create()
							.withKey(KEY)
							.withValue(VALUE)
				)));
	}

}