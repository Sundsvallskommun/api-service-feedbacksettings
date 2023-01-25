package se.sundsvall.feedbacksettings.api;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import se.sundsvall.feedbacksettings.Application;
import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackFilter;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.service.FeedbackSettingsService;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class FeedbackSettingsResourceFailuresTest {
	private static final String MOBILE_NBR = "0701234567";
	private static final String PERSON_ID = UUID.randomUUID().toString();
	private static final Boolean SEND_FEEDBACK = Boolean.TRUE;
	private static final String ID = UUID.randomUUID().toString();

	private static final String SETTINGS_ALREADY_EXISTS_FOR_PERSONID = "Settings already exist for personId '%s'";
	private static final String SETTINGS_NOT_FOUND_FOR_ID = "No settings matching id '%s' were found";

	@Autowired
	private WebTestClient webTestClient;

	@MockBean
	private FeedbackSettingsService feedbackSettingsServiceMock;

	// POST failure tests
	@Test
	void createMissingBody() {
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();
		
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo("Required request body is missing: public org.springframework.http.ResponseEntity<se.sundsvall.feedbacksettings.api.model.FeedbackSetting> "
			+ "se.sundsvall.feedbacksettings.api.FeedbackSettingsResource.createFeedbackSetting(org.springframework.web.util.UriComponentsBuilder,"
			+ "se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingRequest)");

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void createEmptyBody() {
		final var body = CreateFeedbackSettingRequest.create(); // Empty body

		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("personId", "not a valid UUID"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void createNullSettings() {
		final var body = generateCreateRequest( null, null, null, null, null);
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("channels[0].contactMethod", "must not be null"),
			tuple("channels[0].sendFeedback", "must not be null"),
			tuple("channels[0]", "format for destination is not compliable with provided contact method"),
			tuple("personId", "not a valid UUID"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void createInvalidSettings() {
		final var body = generateCreateRequest("not-valid", "not-valid", null, "not-valid", null);
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("channels[0].contactMethod", "must not be null"),
			tuple("channels[0].sendFeedback", "must not be null"),
			tuple("channels[0]", "format for destination is not compliable with provided contact method"),
			tuple("organizationId", "not a valid UUID"),
			tuple("personId", "not a valid UUID"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@ParameterizedTest
	@MethodSource("keyProvider")
	void createWithDuplicateFilterKeys(String key1, String key2) {
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK, 
			List.of(RequestedFeedbackFilter.create().withKey(key1).withValues(List.of("value")), 
				RequestedFeedbackFilter.create().withKey(key2).withValues(List.of("value"))));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();
		
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters", "keys in the collection must be unique"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void createWithNullAsFilterKey() {
		final var value = "value1";
		
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK, 
			List.of(RequestedFeedbackFilter.create().withValues(List.of(value))));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].key", "must not be blank"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void createWithEmptyStringAsFilterKey() {
		final var key = " ";
		final var value = "value1";
		
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK, 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(List.of(value))));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].key", "must not be blank"));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void createWithNullAsFilterValues() {
		final var key = "key1";
		
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK, 
			List.of(RequestedFeedbackFilter.create().withKey(key)));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values", "must not be empty"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void createWithEmptyFilterValues() {
		final var key = "key1";
		
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK, 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(Collections.emptyList())));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values", "must not be empty"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void createWithDuplicateFilterValues() {
		final var key = "key1";
		final var value = "value1";
		
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK, 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(List.of(value, value))));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values", "values in the collection must be unique"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void createWithNullFilterValues() {
		final var key = "key1";
		final var values = new ArrayList<String>();
		values.add(null);
		values.add("value1");
		
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK, 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(values)));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values[0]", "must not be blank"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void createWithDuplicateFilterValuesDifferentCapitalization() {
		final var key = "key1";
		final var value = "value1";
		
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK, 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(List.of(value.toLowerCase(), value.toUpperCase()))));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values", "values in the collection must be unique"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "070-1234567", "070123456", "07012345678", "0711234567", "0741234567", "0751234567", "0771234567", "0781234567", "46701234567"}) 
	void createInvalidMobileNumbers(String invalidMobileNumber) {
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, invalidMobileNumber, SEND_FEEDBACK);
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("channels[0]", "destination must match pattern 07[02369]nnnnnnn when provided contact method is SMS"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " ", "invalid", "invalid@value.", ".invalid@value", "invalid @value"}) 
	void createInvalidEmails(String invalidEmail) {
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.EMAIL, invalidEmail, SEND_FEEDBACK);
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("channels[0]", "destination must be a well-formed email address when provided contact method is EMAIL"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void createNonUniqueMembers() throws Exception {
		final var body = CreateFeedbackSettingRequest.create()
			.withPersonId(PERSON_ID)
			.withChannels(List.of(
				RequestedFeedbackChannel.create()
					.withContactMethod(ContactMethod.SMS)
					.withDestination(MOBILE_NBR)
					.withSendFeedback(true),
				RequestedFeedbackChannel.create()
					.withContactMethod(ContactMethod.SMS)
					.withDestination(MOBILE_NBR)
					.withSendFeedback(false)));
		
		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("channels", "the collection contains two or more elements with equal contactMethod and destination, these values must be unique"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void createForExistingEntity() throws Exception {
		final var body = generateCreateRequest(PERSON_ID, null, ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK);
		
		when(feedbackSettingsServiceMock.createFeedbackSetting(body)).thenThrow(Problem.valueOf(Status.BAD_REQUEST, format(SETTINGS_ALREADY_EXISTS_FOR_PERSONID, PERSON_ID)));

		final var response = webTestClient.post().uri("/settings").contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo(format(SETTINGS_ALREADY_EXISTS_FOR_PERSONID, PERSON_ID));
		
		verify(feedbackSettingsServiceMock).createFeedbackSetting(body);
	}

	// PATCH failure tests
	@Test
	void updateMissingBody() {
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Bad Request");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getDetail()).isEqualTo("Required request body is missing: public org.springframework.http.ResponseEntity<se.sundsvall.feedbacksettings.api.model.FeedbackSetting> "
			+ "se.sundsvall.feedbacksettings.api.FeedbackSettingsResource.updateFeedbackSetting(java.lang.String,"
			+ "se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingRequest)");

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void updateNullSettings() {
		final var body = generateUpdateRequest(null, null, null);
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("channels[0].contactMethod", "must not be null"),
			tuple("channels[0].sendFeedback", "must not be null"),
			tuple("channels[0]", "format for destination is not compliable with provided contact method"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void updateInvalidSettings() {
		final var body = generateUpdateRequest(null, "not-valid", null);
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("channels[0].contactMethod", "must not be null"),
			tuple("channels[0].sendFeedback", "must not be null"),
			tuple("channels[0]", "format for destination is not compliable with provided contact method"));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@ParameterizedTest
	@MethodSource("keyProvider")
	void updateWithDuplicateFilterKeys(String key1, String key2) {
		final var value = "value1";
		
		final var body = generateUpdateRequest( 
			List.of(RequestedFeedbackFilter.create().withKey(key1).withValues(List.of(value)), 
				RequestedFeedbackFilter.create().withKey(key2).withValues(List.of(value))));
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters", "keys in the collection must be unique"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void updateWithNullAsFilterKey() {
		final var value = "value1";
		
		final var body = generateUpdateRequest( 
			List.of(RequestedFeedbackFilter.create().withValues(List.of(value))));
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].key", "must not be blank"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void updateWithEmptyStringAsFilterKey() {
		final var key = " ";
		final var value = "value1";
		
		final var body = generateUpdateRequest( 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(List.of(value))));
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].key", "must not be blank"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void updateWithNullAsFilterValues() {
		final var key = "key1";
		
		final var body = generateUpdateRequest( 
			List.of(RequestedFeedbackFilter.create().withKey(key)));
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values", "must not be empty"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void updateWithEmptyFilterValues() {
		final var key = "key1";
		
		final var body = generateUpdateRequest( 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(Collections.emptyList())));
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values", "must not be empty"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void updateWithDuplicateFilterValues() {
		final var key = "key1";
		final var value = "value1";
		
		final var body = generateUpdateRequest( 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(List.of(value, value))));
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values", "values in the collection must be unique"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void updateWithNullFilterValues() {
		final var key = "key1";
		final var values = new ArrayList<String>();
		values.add(null);
		values.add("value1");
		
		final var body = generateUpdateRequest( 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(values)));
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values[0]", "must not be blank"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void updateWithDuplicateFilterValuesDifferentCapitalization() {
		final var key = "key1";
		final var value = "value1";
		
		final var body = generateUpdateRequest( 
			List.of(RequestedFeedbackFilter.create().withKey(key).withValues(List.of(value.toLowerCase(), value.toUpperCase()))));
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("filters[0].values", "values in the collection must be unique"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "070-1234567", "070123456", "07012345678", "0711234567", "0741234567", "0751234567", "0771234567", "0781234567", "46701234567"}) 
	void updateInvalidMobileNumbers(String invalidMobileNumber) {
		final var body = generateUpdateRequest(ContactMethod.SMS, invalidMobileNumber, SEND_FEEDBACK);
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("channels[0]", "destination must match pattern 07[02369]nnnnnnn when provided contact method is SMS"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@ParameterizedTest
	@ValueSource(strings = {"", " ", "invalid", "invalid@value.", ".invalid@value", "invalid @value"}) 
	void updateInvalidEmails(String invalidEmail) {
		final var body = generateUpdateRequest(ContactMethod.EMAIL, invalidEmail, SEND_FEEDBACK);
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("channels[0]", "destination must be a well-formed email address when provided contact method is EMAIL"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void updateNonUniqueMembers() throws Exception {
		final var body = UpdateFeedbackSettingRequest.create()
			.withChannels(List.of(
				RequestedFeedbackChannel.create()
					.withContactMethod(ContactMethod.SMS)
					.withDestination(MOBILE_NBR)
					.withSendFeedback(true),
				RequestedFeedbackChannel.create()
					.withContactMethod(ContactMethod.SMS)
					.withDestination(MOBILE_NBR)
					.withSendFeedback(false)));
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("channels", "the collection contains two or more elements with equal contactMethod and destination, these values must be unique"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void updateWithNonValidUUID() throws Exception {
		final var body = generateUpdateRequest(ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK);
		
		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", "non-valid")))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("updateFeedbackSetting.id", "not a valid UUID"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void updateForNonExistingEntity() throws Exception {
		final var body = generateUpdateRequest(ContactMethod.SMS, MOBILE_NBR, SEND_FEEDBACK);
		
		when(feedbackSettingsServiceMock.updateFeedbackSetting(ID, body)).thenThrow(Problem.valueOf(Status.NOT_FOUND, format(SETTINGS_NOT_FOUND_FOR_ID, ID)));

		final var response = webTestClient.patch().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.contentType(APPLICATION_JSON)
			.bodyValue(body)
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Not Found");
		assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(response.getDetail()).isEqualTo(format(SETTINGS_NOT_FOUND_FOR_ID, ID));

		verify(feedbackSettingsServiceMock).updateFeedbackSetting(ID, body);
	}
	
	// DELETE failure tests
	@Test
	void deleteWithNonValidUUID() throws Exception {
		final var response = webTestClient.delete().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", "not-valid")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("deleteFeedbackSetting.id", "not a valid UUID"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void deleteForNonExistingEntity() throws Exception {
		doThrow(Problem.valueOf(Status.NOT_FOUND, format(SETTINGS_NOT_FOUND_FOR_ID, ID))).when(feedbackSettingsServiceMock).deleteFeedbackSetting(ID);

		final var response = webTestClient.delete().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Not Found");
		assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(response.getDetail()).isEqualTo(format(SETTINGS_NOT_FOUND_FOR_ID, ID));

		verify(feedbackSettingsServiceMock).deleteFeedbackSetting(ID);
	}

	// GET failure tests
	@Test
	void getByIdForNonValidUUID() throws Exception {
		final var response = webTestClient.get().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", "not-valid")))
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();
		
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("getFeedbackSettingById.id", "not a valid UUID"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void getByIdForNonExistingEntity() throws Exception {
		when(feedbackSettingsServiceMock.getFeedbackSettingById(ID)).thenThrow(Problem.valueOf(Status.NOT_FOUND, format(SETTINGS_NOT_FOUND_FOR_ID, ID)));

		final var response = webTestClient.get().uri(builder -> builder.path("/settings/{id}").build(Map.of("id", ID)))
			.exchange()
			.expectStatus().isNotFound()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(Problem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Not Found");
		assertThat(response.getStatus()).isEqualTo(NOT_FOUND);
		assertThat(response.getDetail()).isEqualTo(format(SETTINGS_NOT_FOUND_FOR_ID, ID));
		
		verify(feedbackSettingsServiceMock).getFeedbackSettingById(ID);
	}
	
	@Test
	void getByQueryWithNonValidUUID() {
		final var response = webTestClient.get().uri(builder -> builder.path("/settings").queryParams(createParameterMap(null, null, "non-valid", "non-valid")).build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();
		
		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactlyInAnyOrder(
			tuple("getFeedbackSettingsByQuery.organizationId", "not a valid UUID"),
			tuple("getFeedbackSettingsByQuery.personId", "not a valid UUID"));
		
		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void getByQueryWithPageLessThanOne() {
		final var response = webTestClient.get().uri(builder -> builder.path("/settings").queryParams(createParameterMap(0, null, PERSON_ID, null)).build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("getFeedbackSettingsByQuery.page", "must be greater than or equal to 1"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	@Test
	void getByQueryWithPageSizeLessThanOne() {
		final var response = webTestClient.get().uri(builder -> builder.path("/settings").queryParams(createParameterMap(null, 0, PERSON_ID, null)).build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("getFeedbackSettingsByQuery.limit", "must be greater than or equal to 1"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}

	@Test
	void getByQueryWithPageSizeMoreThanOneHundred() {
		final var response = webTestClient.get().uri(builder -> builder.path("/settings").queryParams(createParameterMap(null, 101, PERSON_ID, null)).build())
			.exchange()
			.expectStatus().isBadRequest()
			.expectHeader().contentType(APPLICATION_PROBLEM_JSON)
			.expectBody(ConstraintViolationProblem.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response.getTitle()).isEqualTo("Constraint Violation");
		assertThat(response.getStatus()).isEqualTo(BAD_REQUEST);
		assertThat(response.getViolations()).extracting(Violation::getField, Violation::getMessage).containsExactly(
			tuple("getFeedbackSettingsByQuery.limit", "must be less than or equal to 100"));

		verifyNoInteractions(feedbackSettingsServiceMock);	
	}
	
	private CreateFeedbackSettingRequest generateCreateRequest(String personId, String organizationId, ContactMethod contactMethod, String destination, Boolean sendFeedback) {
		return generateCreateRequest(personId, organizationId, contactMethod, destination, sendFeedback, null);
	}

	private CreateFeedbackSettingRequest generateCreateRequest(String personId, String organizationId, ContactMethod contactMethod, String destination, Boolean sendFeedback, List<RequestedFeedbackFilter> filters) {
		return CreateFeedbackSettingRequest.create()
			.withPersonId(personId)
			.withOrganizationId(organizationId)
			.withChannels(List.of(RequestedFeedbackChannel.create()
				.withContactMethod(contactMethod)
				.withDestination(destination)
				.withSendFeedback(sendFeedback)))
			.withFilters(filters);
	}

	private UpdateFeedbackSettingRequest generateUpdateRequest(ContactMethod contactMethod, String destination, Boolean sendFeedback) {
		return UpdateFeedbackSettingRequest.create()
			.withChannels(List.of(RequestedFeedbackChannel.create()
				.withContactMethod(contactMethod)
				.withDestination(destination)
				.withSendFeedback(sendFeedback)));
	}

	private UpdateFeedbackSettingRequest generateUpdateRequest(List<RequestedFeedbackFilter> filters) {
		return UpdateFeedbackSettingRequest.create().withFilters(filters);
	}

	private static Stream<Arguments> keyProvider() {
		return Stream.of(
			Arguments.of("key1", "key1"),
			Arguments.of("kEy1", "KeY1"),
			Arguments.of("key1", " key1 "),
			Arguments.of("key1 ", " key1"));
	}

	private MultiValueMap<String, String> createParameterMap(Integer page, Integer limit, String personId, String organizationId) {
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		
		ofNullable(page).ifPresent(p -> parameters.add("page", p.toString()));
		ofNullable(limit).ifPresent(p -> parameters.add("limit", p.toString()));
		ofNullable(personId).ifPresent(p -> parameters.add("personId", p));
		ofNullable(organizationId).ifPresent(p -> parameters.add("organizationId", p));
		
		return parameters;
	}
}
