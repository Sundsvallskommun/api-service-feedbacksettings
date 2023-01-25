package se.sundsvall.feedbacksettings.apptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.feedbacksettings.Application;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;

/**
 * Delete feedback settings application tests
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/DeleteFeedbackSettings/", classes = Application.class)
@Sql(scripts = {
        "/db/scripts/truncate.sql",
        "/db/scripts/testdata.sql"
})
class DeleteFeedbackSettingsIT extends AbstractAppTest {
	private static final String PATH = "/settings/";
	private static final String RESPONSE_FILE = "response.json";

	@Autowired
	private FeedbackSettingsRepository repository;
	
	@Test
	void test01_delete() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e1";
		
		setupCall()
			.withServicePath(PATH.concat(id))
			.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NO_CONTENT)
			.sendRequestAndVerifyResponse();
			
			assertThat(repository.findById(id)).isEmpty();
	}

	@Test
	void test02_deleteNonExistingId() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e0";

		setupCall()
		.withServicePath(PATH.concat(id))
		.withHttpMethod(DELETE)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
