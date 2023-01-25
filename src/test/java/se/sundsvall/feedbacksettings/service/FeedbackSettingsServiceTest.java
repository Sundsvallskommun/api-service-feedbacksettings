package se.sundsvall.feedbacksettings.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toFeedbackSetting;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toWeightedFeedbackSetting;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.FeedbackSetting;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.SearchResult;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackChannelEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

@ExtendWith(MockitoExtension.class)
class FeedbackSettingsServiceTest {

	private static final String FEEDBACK_SETTINGS_ID = "settingsId";
	private static final String MOBILE_NBR = "mobileNbr";
	private static final String EMAIL_ADDRESS = "emailAddress";
	private static final String PERSON_ID = "personId";
	private static final String ORGANIZATION_ID = "organizationId";
	private static final boolean SEND_FEEDBACK = true;
	private static final OffsetDateTime CREATED = OffsetDateTime.now().minusDays(1);
	private static final OffsetDateTime MODIFIED = OffsetDateTime.now();
	private static final HttpHeaders HEADERS = new HttpHeaders();

	@Mock
	private FeedbackSettingsRepository repositoryMock;
	
	@Mock
	private FeedbackSettingEntity entityMock;
	
	@Mock
	private Page<FeedbackSettingEntity> pageMock;
	
	@InjectMocks
	private FeedbackSettingsService service;
	
	@Captor
	private ArgumentCaptor<FeedbackSettingEntity> entityCaptor;
	
	@Captor
	private ArgumentCaptor<Example<FeedbackSettingEntity>> exampleCaptor;
	
	@Test
	void createFeedbackSettings() {
		List<RequestedFeedbackChannel> channels = generateChannels();
		CreateFeedbackSettingRequest request = CreateFeedbackSettingRequest.create()
				.withPersonId(PERSON_ID)
				.withOrganizationId(ORGANIZATION_ID)
				.withChannels(channels);
				
		FeedbackSetting response = service.createFeedbackSetting(request);
		
		verify(repositoryMock).exists(exampleCaptor.capture());
		verify(repositoryMock).save(entityCaptor.capture());
		verifyNoMoreInteractions(repositoryMock);
		
		assertThat(entityCaptor.getValue().getFeedbackChannels())
			.hasSize(2)
			.extracting(FeedbackChannelEmbeddable::getContactMethod, FeedbackChannelEmbeddable::getDestination, FeedbackChannelEmbeddable::isSendFeedback)
			.containsExactlyInAnyOrder(
					tuple(ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK), 
					tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
		assertThat(entityCaptor.getValue().getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entityCaptor.getValue().getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(exampleCaptor.getValue().getProbe())
			.hasFieldOrPropertyWithValue("personId", PERSON_ID)
			.hasFieldOrPropertyWithValue("organizationId", ORGANIZATION_ID);
		
		assertThat(response.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(response.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
		assertThat(response.getChannels())
			.hasSize(2)
			.extracting(FeedbackChannel::getContactMethod, FeedbackChannel::getDestination, FeedbackChannel::isSendFeedback)
			.containsExactlyInAnyOrder(
					tuple(ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK), 
					tuple(ContactMethod.EMAIL, EMAIL_ADDRESS, SEND_FEEDBACK));
	}

	@Test
	void createFeedbackSettingsForExistingPrivatePerson() {
		when(repositoryMock.exists(ArgumentMatchers.<Example<FeedbackSettingEntity>>any())).thenReturn(true);
		
		CreateFeedbackSettingRequest request = CreateFeedbackSettingRequest.create().withPersonId(PERSON_ID);
		
		final var exception = assertThrows(ThrowableProblem.class, 
				() -> service.createFeedbackSetting(request));

		verify(repositoryMock).exists(exampleCaptor.capture());
		verifyNoMoreInteractions(repositoryMock);
		
		assertThat(exception.getMessage()).isEqualTo("Bad Request: Settings already exist for personId 'personId'");
		assertThat(exampleCaptor.getValue().getProbe())
			.hasFieldOrPropertyWithValue("personId", PERSON_ID)
			.hasFieldOrPropertyWithValue("organizationId", null);
	}
	
	@Test
	void createFeedbackSettingsForExistingOrganizationRepresentative() {
		when(repositoryMock.exists(ArgumentMatchers.<Example<FeedbackSettingEntity>>any())).thenReturn(true);
		
		CreateFeedbackSettingRequest request = CreateFeedbackSettingRequest.create().withPersonId(PERSON_ID).withOrganizationId(ORGANIZATION_ID);
		
		final var exception = assertThrows(ThrowableProblem.class, 
				() -> service.createFeedbackSetting(request));

		verify(repositoryMock).exists(exampleCaptor.capture());
		verifyNoMoreInteractions(repositoryMock);

		assertThat(exception.getMessage()).isEqualTo("Bad Request: Settings already exist for personId 'personId' representing organizationId 'organizationId'");
		assertThat(exampleCaptor.getValue().getProbe())
			.hasFieldOrPropertyWithValue("personId", PERSON_ID)
			.hasFieldOrPropertyWithValue("organizationId", ORGANIZATION_ID);
	}

	@Test
	void updateFeedbackSettings() {
		when(repositoryMock.findById(FEEDBACK_SETTINGS_ID)).thenReturn(Optional.of(entityMock));
		when(entityMock.getId()).thenReturn(FEEDBACK_SETTINGS_ID);
		when(entityMock.getPersonId()).thenReturn(PERSON_ID);
		when(entityMock.getOrganizationId()).thenReturn(ORGANIZATION_ID);

		UpdateFeedbackSettingRequest request = UpdateFeedbackSettingRequest.create().withChannels(generateChannels());
		FeedbackSetting response = service.updateFeedbackSetting(FEEDBACK_SETTINGS_ID, request);
		
		verify(repositoryMock).findById(FEEDBACK_SETTINGS_ID);
		verify(repositoryMock).save(entityCaptor.capture());
		verifyNoMoreInteractions(repositoryMock);
		
		assertThat(entityCaptor.getValue().getId()).isEqualTo(FEEDBACK_SETTINGS_ID);
		assertThat(entityCaptor.getValue().getPersonId()).isEqualTo(PERSON_ID);
		assertThat(entityCaptor.getValue().getOrganizationId()).isEqualTo(ORGANIZATION_ID);

		assertThat(response.getId()).isEqualTo(FEEDBACK_SETTINGS_ID);
		assertThat(response.getPersonId()).isEqualTo(PERSON_ID);
		assertThat(response.getOrganizationId()).isEqualTo(ORGANIZATION_ID);
	}

	@Test
	void updateFeedbackSettingsForNonExistingId() {
		UpdateFeedbackSettingRequest request = UpdateFeedbackSettingRequest.create();
		
		final var exception = assertThrows(ThrowableProblem.class, 
				() -> service.updateFeedbackSetting(FEEDBACK_SETTINGS_ID, request));
		
		verify(repositoryMock).findById(FEEDBACK_SETTINGS_ID);
		verifyNoMoreInteractions(repositoryMock);

		assertThat(exception.getMessage()).isEqualTo("Not Found: No settings matching id 'settingsId' were found");
	}
	
	@Test
	void getFeedbackSettingsById() {
		when(repositoryMock.findById(FEEDBACK_SETTINGS_ID)).thenReturn(Optional.of(entityMock));
		when(entityMock.getId()).thenReturn(FEEDBACK_SETTINGS_ID);
		when(entityMock.getPersonId()).thenReturn(PERSON_ID);
		when(entityMock.getOrganizationId()).thenReturn(ORGANIZATION_ID);
		when(entityMock.getCreated()).thenReturn(CREATED);
		when(entityMock.getModified()).thenReturn(MODIFIED);

		FeedbackSetting response = service.getFeedbackSettingById(FEEDBACK_SETTINGS_ID);
		
		verify(repositoryMock).findById(FEEDBACK_SETTINGS_ID);
		verifyNoMoreInteractions(repositoryMock);
		
		assertThat(response).isEqualTo(toFeedbackSetting(entityMock));
	}

	@Test
	void getFeedbackSettingsByIdForNonExistingId() {
		when(repositoryMock.findById(FEEDBACK_SETTINGS_ID)).thenReturn(Optional.empty());
		
		final var exception = assertThrows(ThrowableProblem.class, 
				() -> service.getFeedbackSettingById(FEEDBACK_SETTINGS_ID));

		verify(repositoryMock).findById(FEEDBACK_SETTINGS_ID);
		verifyNoMoreInteractions(repositoryMock);

		assertThat(exception.getMessage()).isEqualTo("Not Found: No settings matching id 'settingsId' were found");
	}

	@Test
	void getFeedbackSettingsForPersonId() {
		when(repositoryMock.findAll(ArgumentMatchers.<Example<FeedbackSettingEntity>>any(), ArgumentMatchers.<Pageable>any())).thenReturn(pageMock);
		when(pageMock.getContent()).thenReturn(List.of(entityMock));
		when(pageMock.getTotalPages()).thenReturn(1);
		when(pageMock.getTotalElements()).thenReturn(1L);
		
		SearchResult response = service.getFeedbackSettings(HEADERS, PERSON_ID, null, 1, 10);
		
		verify(repositoryMock).findAll(exampleCaptor.capture(), any(Pageable.class));
		verifyNoMoreInteractions(repositoryMock);

		assertThat(response.getMetaData().getCount()).isEqualTo(1L);
		assertThat(response.getMetaData().getLimit()).isEqualTo(10);
		assertThat(response.getMetaData().getPage()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalPages()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1L);
		assertThat(response.getFeedbackSettings()).containsExactly(toWeightedFeedbackSetting(entityMock));
		assertThat(exampleCaptor.getValue().getProbe())
			.hasFieldOrPropertyWithValue("personId", PERSON_ID)
			.hasFieldOrPropertyWithValue("organizationId", null);
	}

	@Test
	void getFeedbackSettingsForOrganizationId() {
		when(repositoryMock.findAll(ArgumentMatchers.<Example<FeedbackSettingEntity>>any(), ArgumentMatchers.<Pageable>any())).thenReturn(pageMock);
		when(pageMock.getContent()).thenReturn(List.of(entityMock));
		when(pageMock.getTotalPages()).thenReturn(1);
		when(pageMock.getTotalElements()).thenReturn(1L);
		
		SearchResult response = service.getFeedbackSettings(HEADERS, null, ORGANIZATION_ID, 1, 10);
		
		verify(repositoryMock).findAll(exampleCaptor.capture(), any(Pageable.class));
		verifyNoMoreInteractions(repositoryMock);

		assertThat(response.getMetaData().getCount()).isEqualTo(1L);
		assertThat(response.getMetaData().getLimit()).isEqualTo(10);
		assertThat(response.getMetaData().getPage()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalPages()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1L);
		assertThat(response.getFeedbackSettings()).containsExactly(toWeightedFeedbackSetting(entityMock));
		assertThat(exampleCaptor.getValue().getProbe())
			.hasFieldOrPropertyWithValue("personId", null)
			.hasFieldOrPropertyWithValue("organizationId", ORGANIZATION_ID);
	}

	@Test
	void getFeedbackSettingsForPersonIdAndOrganizationId() {
		when(repositoryMock.findAll(ArgumentMatchers.<Example<FeedbackSettingEntity>>any(), ArgumentMatchers.<Pageable>any())).thenReturn(pageMock);
		when(pageMock.getContent()).thenReturn(List.of(entityMock));
		when(pageMock.getTotalPages()).thenReturn(1);
		when(pageMock.getTotalElements()).thenReturn(1L);
		
		SearchResult response = service.getFeedbackSettings(HEADERS, PERSON_ID, ORGANIZATION_ID, 1, 10);
		
		verify(repositoryMock).findAll(exampleCaptor.capture(), any(Pageable.class));
		verifyNoMoreInteractions(repositoryMock);

		assertThat(response.getMetaData().getCount()).isEqualTo(1L);
		assertThat(response.getMetaData().getLimit()).isEqualTo(10);
		assertThat(response.getMetaData().getPage()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalPages()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1L);
		assertThat(response.getFeedbackSettings()).containsExactly(toWeightedFeedbackSetting(entityMock));
		assertThat(exampleCaptor.getValue().getProbe())
			.hasFieldOrPropertyWithValue("personId", PERSON_ID)
			.hasFieldOrPropertyWithValue("organizationId", ORGANIZATION_ID);
	}

	@Test
	void getFeedbackSettingsWithNoFilters() {
		when(repositoryMock.findAll(ArgumentMatchers.<Example<FeedbackSettingEntity>>any(), ArgumentMatchers.<Pageable>any())).thenReturn(pageMock);
		when(pageMock.getContent()).thenReturn(List.of(entityMock));
		when(pageMock.getTotalPages()).thenReturn(2);
		when(pageMock.getTotalElements()).thenReturn(15L);
		
		SearchResult response = service.getFeedbackSettings(HEADERS, null, null, 1, 10);
		
		verify(repositoryMock).findAll(exampleCaptor.capture(), any(Pageable.class));
		verifyNoMoreInteractions(repositoryMock);

		assertThat(response.getMetaData().getCount()).isEqualTo(1L);
		assertThat(response.getMetaData().getLimit()).isEqualTo(10);
		assertThat(response.getMetaData().getPage()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalPages()).isEqualTo(2);
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(15L);
		assertThat(response.getFeedbackSettings()).containsExactly(toWeightedFeedbackSetting(entityMock));
		assertThat(exampleCaptor.getValue().getProbe())
			.hasFieldOrPropertyWithValue("personId", null)
			.hasFieldOrPropertyWithValue("organizationId", null);
	}

	@Test
	void getFeedbackSettingsForPageLargerThanResultsMaxPage() {
		when(repositoryMock.findAll(ArgumentMatchers.<Example<FeedbackSettingEntity>>any(), ArgumentMatchers.<Pageable>any())).thenReturn(pageMock);
		when(pageMock.getTotalPages()).thenReturn(1);
		when(pageMock.getTotalElements()).thenReturn(1L);
		
		SearchResult response = service.getFeedbackSettings(HEADERS, null, null, 100, 10);
		
		verify(repositoryMock).findAll(exampleCaptor.capture(), any(Pageable.class));
		verifyNoMoreInteractions(repositoryMock);

		assertThat(response.getMetaData().getCount()).isZero();
		assertThat(response.getMetaData().getLimit()).isEqualTo(10);
		assertThat(response.getMetaData().getPage()).isEqualTo(100);
		assertThat(response.getMetaData().getTotalPages()).isEqualTo(1);
		assertThat(response.getMetaData().getTotalRecords()).isEqualTo(1L);
		assertThat(response.getFeedbackSettings()).isEmpty();
		assertThat(exampleCaptor.getValue().getProbe())
			.hasFieldOrPropertyWithValue("personId", null)
			.hasFieldOrPropertyWithValue("organizationId", null);
	}

	@Test
	void deleteFeedbackSettings() {
		when(repositoryMock.existsById(FEEDBACK_SETTINGS_ID)).thenReturn(true);
		service.deleteFeedbackSetting(FEEDBACK_SETTINGS_ID);
		
		verify(repositoryMock).existsById(FEEDBACK_SETTINGS_ID);
		verify(repositoryMock).deleteById(FEEDBACK_SETTINGS_ID);
		
		verifyNoMoreInteractions(repositoryMock);
	}

	@Test
	void deleteFeedbackSettingsForNonExistingId() {
		final var exception = assertThrows(ThrowableProblem.class, 
				() -> service.deleteFeedbackSetting(FEEDBACK_SETTINGS_ID));

		verify(repositoryMock).existsById(FEEDBACK_SETTINGS_ID);
		verifyNoMoreInteractions(repositoryMock);

		assertThat(exception.getMessage()).isEqualTo("Not Found: No settings matching id 'settingsId' were found");
		
	}
	
	private List<RequestedFeedbackChannel> generateChannels() {
		return List.of(
				RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.SMS)
						.withDestination(MOBILE_NBR)
						.withSendFeedback(SEND_FEEDBACK),
				RequestedFeedbackChannel.create()
						.withContactMethod(ContactMethod.EMAIL)
						.withDestination(EMAIL_ADDRESS)
						.withSendFeedback(SEND_FEEDBACK));
	}
}
