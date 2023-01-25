package se.sundsvall.feedbacksettings.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.sundsvall.feedbacksettings.Application;
import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackSetting;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.SearchResult;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.service.FeedbackSettingsService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class FeedbackSettingsResourceTest {

	private static final String MOBILE_NBR = "0701234567";
	private static final String EMAIL_ADDRESS = "valid.email@host.org";
	private static final String PERSON_ID = UUID.randomUUID().toString();
	private static final String ORGANIZATION_ID = UUID.randomUUID().toString();
	private static final Boolean SEND_FEEDBACK = Boolean.TRUE;
	private static final String ID = UUID.randomUUID().toString();
	private static final HttpHeaders HEADERS = new HttpHeaders();
	private static final String HEADER_FILTER_KEY = "x-filter-keyname";
	private static final String HEADER_FILTER_VALUE = "x-filter-value";
	
	@MockBean
	private FeedbackSettingsService feedbackSettingsServiceMock;
	
	@Captor
	private ArgumentCaptor<HttpHeaders> headersCaptor;
	
	@Autowired
	private WebTestClient webTestClient;

	@LocalServerPort
	private int port;
	
	@BeforeAll
	static void setupHeaderMap() {
		HEADERS.add(HEADER_FILTER_KEY, HEADER_FILTER_VALUE);
	}

	@Test
	void testCreateForPerson() throws Exception {
		CreateFeedbackSettingRequest request = CreateFeedbackSettingRequest.create()
			.withPersonId(PERSON_ID)
			.withOrganizationId(ORGANIZATION_ID)
			.withChannels(List.of(RequestedFeedbackChannel.create()
				.withContactMethod(ContactMethod.EMAIL)
				.withDestination(EMAIL_ADDRESS)
				.withSendFeedback(SEND_FEEDBACK)));
		
		when(feedbackSettingsServiceMock.createFeedbackSetting(request)).thenReturn(FeedbackSetting.create().withId(String.valueOf(ID)));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectHeader().location("http://localhost:".concat(String.valueOf(port)).concat("/settings/").concat(ID))
			.expectBody(FeedbackSetting.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).createFeedbackSetting(request);
	}

	@Test
	void testCreateForOrganizationalRepresentative() throws Exception {
		CreateFeedbackSettingRequest request = CreateFeedbackSettingRequest.create()
			.withPersonId(PERSON_ID)
			.withOrganizationId(ORGANIZATION_ID)
			.withChannels(List.of(RequestedFeedbackChannel.create()
				.withContactMethod(ContactMethod.SMS)
				.withDestination(MOBILE_NBR)
				.withSendFeedback(SEND_FEEDBACK)));
		
		when(feedbackSettingsServiceMock.createFeedbackSetting(request)).thenReturn(FeedbackSetting.create().withId(String.valueOf(ID)));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectHeader().location("http://localhost:".concat(String.valueOf(port)).concat("/settings/").concat(ID))
			.expectBody(FeedbackSetting.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).createFeedbackSetting(request);
	}
	
	@Test
	void testUpdate() throws Exception {
		UpdateFeedbackSettingRequest request = UpdateFeedbackSettingRequest.create()
			.withChannels(List.of(RequestedFeedbackChannel.create()
				.withContactMethod(ContactMethod.SMS)
				.withDestination(MOBILE_NBR)
				.withSendFeedback(SEND_FEEDBACK)));

		when(feedbackSettingsServiceMock.updateFeedbackSetting(ID, request)).thenReturn(FeedbackSetting.create().withId(String.valueOf(ID)));

		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID))).contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(FeedbackSetting.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).updateFeedbackSetting(ID, request);
	}

	@Test
	void testUpdateWithEmptyBody() throws Exception { // To verify that parameters can be null 
		UpdateFeedbackSettingRequest request = UpdateFeedbackSettingRequest.create();

		when(feedbackSettingsServiceMock.updateFeedbackSetting(ID, request)).thenReturn(FeedbackSetting.create().withId(String.valueOf(ID)));

		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID))).contentType(APPLICATION_JSON)
			.bodyValue(request)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(FeedbackSetting.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).updateFeedbackSetting(ID, request);
	}
	
	@Test
	void testDelete() throws Exception {
		webTestClient.delete().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.exchange()
			.expectStatus().isNoContent();
		
		verify(feedbackSettingsServiceMock).deleteFeedbackSetting(ID);
	}
	
	@Test
	void testGetById() throws Exception {
		when(feedbackSettingsServiceMock.getFeedbackSettingById(ID)).thenReturn(FeedbackSetting.create().withId(String.valueOf(ID)));

		final var response = webTestClient.get().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(FeedbackSetting.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		verify(feedbackSettingsServiceMock).getFeedbackSettingById(ID);
	}

	@Test
	void testGetByQueryWithDefaultPageSettings() {
		when(feedbackSettingsServiceMock.getFeedbackSettings(any(), eq(PERSON_ID), isNull(), eq(1), eq(20))).thenReturn(SearchResult.create());

		final var response = webTestClient.get().uri(builder -> builder.path("/settings").queryParam("personId", PERSON_ID).build())
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody(SearchResult.class)
			.returnResult()
			.getResponseBody();

		verify(feedbackSettingsServiceMock).getFeedbackSettings(headersCaptor.capture(), eq(PERSON_ID), isNull(), eq(1), eq(20));
		assertThat(response).isNotNull();
		assertThat(headersCaptor.getValue().containsKey(HEADER_FILTER_KEY)).isFalse();
	}
	
	@Test
	void testGetByQueryWithSpecificPageSettingsAndHeaders() {
		when(feedbackSettingsServiceMock.getFeedbackSettings(any(), eq(PERSON_ID), eq(ORGANIZATION_ID), eq(1), eq(10))).thenReturn(SearchResult.create());

		final var response = webTestClient.get().uri(builder -> builder.path("/settings")
					.queryParam("personId", PERSON_ID)
					.queryParam("organizationId", ORGANIZATION_ID)
					.queryParam("page", 1)
					.queryParam("limit", 10)
					.build())
				.header(HEADER_FILTER_KEY, HEADER_FILTER_VALUE)
				.exchange()
				.expectStatus().isOk()
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody(SearchResult.class)
				.returnResult()
				.getResponseBody();

		verify(feedbackSettingsServiceMock).getFeedbackSettings(headersCaptor.capture(), eq(PERSON_ID), eq(ORGANIZATION_ID), eq(1), eq(10));
		assertThat(response).isNotNull();
		assertThat(headersCaptor.getValue().get(HEADER_FILTER_KEY)).hasSize(1).contains(HEADER_FILTER_VALUE);
	}
}
