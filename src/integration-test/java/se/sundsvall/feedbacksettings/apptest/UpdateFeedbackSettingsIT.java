package se.sundsvall.feedbacksettings.apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpStatus.OK;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.feedbacksettings.Application;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;

/**
 * Update feedback settings application tests
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/UpdateFeedbackSettings/", classes = Application.class)
@Sql(scripts = {
        "/db/scripts/truncate.sql",
        "/db/scripts/testdata.sql"
})
class UpdateFeedbackSettingsIT extends AbstractAppTest {
	private static final String PATH = "/settings/";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";

	@Autowired
	private FeedbackSettingsRepository repository;

	@Test
	void test01_updatePersonalSetting() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e2";
		
		setupCall()
			.withServicePath(PATH.concat(id))
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
		
		assertThat(repository.findById(id).get().getModified()).isCloseTo(OffsetDateTime.now(), within(2,  ChronoUnit.SECONDS));
	}

	@Test
	void test02_updateRepresentativeSetting() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e5";

		setupCall()
			.withServicePath(PATH.concat(id))
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
		
		assertThat(repository.findById(id).get(). getModified()).isCloseTo(OffsetDateTime.now(), within(2,  ChronoUnit.SECONDS));
	}
	
	@Test
	void test03_addContactChannel() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e2";
		
		setupCall()
			.withServicePath(PATH.concat(id))
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
		
		assertThat(repository.findById(id).get().getModified()).isCloseTo(OffsetDateTime.now(), within(2,  ChronoUnit.SECONDS));
	}
	
	@Test
	void test04_removeContactChannel() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e2";

		setupCall()
		.withServicePath(PATH.concat(id))
			.withHttpMethod(PATCH)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
		
		assertThat(repository.findById(id).get().getModified()).isCloseTo(OffsetDateTime.now(), within(2,  ChronoUnit.SECONDS));
	}
}
