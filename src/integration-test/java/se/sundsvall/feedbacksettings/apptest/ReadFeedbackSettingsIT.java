package se.sundsvall.feedbacksettings.apptest;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.feedbacksettings.Application;

/**
 * Read feedback settings application tests
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/ReadFeedbackSettings/", classes = Application.class)
@Sql(scripts = {
        "/db/scripts/truncate.sql",
        "/db/scripts/testdata.sql"
})
class ReadFeedbackSettingsIT extends AbstractAppTest {
	private static final String PATH = "/settings";
	private static final String RESPONSE_FILE = "response.json";
	private static final String X_FILTER_CATEGORIES = "x-filter-categories";
	private static final String X_FILTER_MESSAGETYPES = "x-filter-messagetypes";
	private static final String CATEGORY_BROADBAND = "Broadband";
	private static final String CATEGORY_ELECTRICITY = "Electricity";
	private static final String MESSAGE_TYPE_DISTURBANCE = "Disturbance";
	private static final String MESSAGE_TYPE_INFORMATION = "Information";
	
	@Test
	void test01_readById() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e2";

		setupCall()
			.withServicePath(PATH.concat("/").concat(id))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test02_readByNonExistingId() throws Exception { //NOSONAR
		final var id = "9a24743c-5c19-4774-954e-a3ad67a734e0";

		setupCall()
			.withServicePath(PATH.concat("/").concat(id))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(NOT_FOUND)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test03_queryByPersonId() throws Exception { //NOSONAR
		final var personId = "49a974ea-9137-419b-bcb9-ad74c81a1d3f";

		setupCall()
			.withServicePath(PATH
					.concat("?personId=").concat(personId))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
	
	@Test
	void test04_queryByPersonAndOrganizationId() throws Exception { //NOSONAR
		final var personId = "49a974ea-9137-419b-bcb9-ad74c81a1d3f";
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?personId=").concat(personId)
					.concat("&organizationId=").concat(organizationId))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test05_queryByOrganizationIdAndNoFilters() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test06_queryByOrganizationIdFilterOnCategoryBroadband() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(GET)
			.withHeader(X_FILTER_CATEGORIES, CATEGORY_BROADBAND)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test07_queryByOrganizationIdFilterOnCategoryBroadbandMessageTypeDisturbance() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(GET)
			.withHeader(X_FILTER_CATEGORIES, CATEGORY_BROADBAND)
			.withHeader(X_FILTER_MESSAGETYPES, MESSAGE_TYPE_DISTURBANCE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test08_queryByOrganizationIdFilterOnCategoryBroadbandMessageTypeBlank() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(GET)
			.withHeader(X_FILTER_CATEGORIES, CATEGORY_BROADBAND)
			.withHeader(X_FILTER_MESSAGETYPES, "")
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test09_queryByOrganizationIdFilterOnCategoryElectricity() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(GET)
			.withHeader(X_FILTER_CATEGORIES, CATEGORY_ELECTRICITY)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test10_queryByOrganizationIdFilterOnCategoryElectricityMessageTypeInformation() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(GET)
			.withHeader(X_FILTER_CATEGORIES, CATEGORY_ELECTRICITY)
			.withHeader(X_FILTER_MESSAGETYPES, MESSAGE_TYPE_INFORMATION)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
	
	@Test
	void test11_queryByOrganizationIdFilterOnMessageTypeDisturbance() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84a";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(GET)
			.withHeader(X_FILTER_MESSAGETYPES, MESSAGE_TYPE_DISTURBANCE)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}

	@Test
	void test12_queryWithNonDefaultPagingParams() throws Exception { //NOSONAR
		setupCall()
			.withServicePath(PATH
					.concat("?page=1")
					.concat("&limit=3"))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
	
	@Test
	void test13_queryWithNoMatches() throws Exception { //NOSONAR
		final var organizationId = "15aee472-46ab-4f03-9605-68bd64ebc84f";

		setupCall()
			.withServicePath(PATH
					.concat("?organizationId=").concat(organizationId))
			.withHttpMethod(GET)
			.withExpectedResponseStatus(OK)
			.withExpectedResponse(RESPONSE_FILE)
			.sendRequestAndVerifyResponse();
	}
}
