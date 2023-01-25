package se.sundsvall.feedbacksettings.apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.feedbacksettings.Application;
import se.sundsvall.feedbacksettings.api.model.FeedbackSetting;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;

/**
 * Create feedback settings application tests
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/CreateFeedbackSettings/", classes = Application.class)
@Sql(scripts = {
        "/db/scripts/truncate.sql",
        "/db/scripts/testdata.sql"
})
class CreateFeedbackSettingsIT extends AbstractAppTest {
	private static final String PATH = "/settings";
	private static final String REQUEST_FILE = "request.json";
	private static final String RESPONSE_FILE = "response.json";
	private static final String HEADER_LOCATION = "location";
	
	// Regexp matching 'http[s]://[any string larger than 1 char]/settings/[valid UUID format]'
	private static final String UUID_REGEXP = "https?:\\/\\/\\S+\\/settings\\/[0-9a-f]{8}-[0-9a-f]{4}-[0-5][0-9a-f]{3}-[089ab][0-9a-f]{3}-[0-9a-f]{12}";

	@Autowired
	private FeedbackSettingsRepository repository;
	
	@Test
	void test01_createPersonalSetting() throws Exception { //NOSONAR
		FeedbackSetting response = setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(HEADER_LOCATION, List.of(UUID_REGEXP))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse()
			.andReturnBody(FeedbackSetting.class);
		
		assertThat(repository.findById(response.getId())).isPresent();
	}

	@Test
	void test02_createRepresentativeSetting() throws Exception { //NOSONAR
		FeedbackSetting response = setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(HEADER_LOCATION, List.of(UUID_REGEXP))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse()
			.andReturnBody(FeedbackSetting.class);
		
		assertThat(repository.findById(response.getId())).isPresent();
	}

	@Test
	void test03_createWithoutChannels() throws Exception { //NOSONAR
		FeedbackSetting response =  setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(CREATED)
			.withExpectedResponseHeader(HEADER_LOCATION, List.of(UUID_REGEXP))
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse()
			.andReturnBody(FeedbackSetting.class);
		
		assertThat(repository.findById(response.getId())).isPresent();
	}
	
	@Test
	void test04_createForExistingId() throws Exception { //NOSONAR
		setupCall()
			.withServicePath(PATH)
			.withHttpMethod(POST)
			.withRequest(REQUEST_FILE)
			.withExpectedResponseStatus(BAD_REQUEST)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
