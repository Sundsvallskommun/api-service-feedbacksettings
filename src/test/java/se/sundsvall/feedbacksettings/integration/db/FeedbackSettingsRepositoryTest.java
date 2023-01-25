package se.sundsvall.feedbacksettings.integration.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toExample;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import se.sundsvall.feedbacksettings.Application;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

/**
 * Feedback repository tests.
 * 
 * @see src/test/resources/db/testdata.sql for data setup.
 */
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("junit")
@Sql(scripts = {
	"/db/scripts/truncate.sql",
	"/db/scripts/testdata.sql"
})
@Transactional
class FeedbackSettingsRepositoryTest {

	private static final String EXISTING_PRIVATE_PERSON_ID = "49a974ea-9137-419b-bcb9-ad74c81a1d2f";
	private static final String EXISTING_PRIVATE_AND_ORGANIZATION_REPRESENTATIVE_PERSON_ID = "49a974ea-9137-419b-bcb9-ad74c81a1d3f";
	private static final String EXISTING_ORGANIZATION_REPRESENTATIVE_PERSON_ID = "49a974ea-9137-419b-bcb9-ad74c81a1d4f";
	private static final String EXISTING_ORGANIZATION_ID = "15aee472-46ab-4f03-9605-68bd64ebc84a";
	private static final String NON_EXISTING_PERSON_ID = "49a974ea-9137-419b-bcb9-ad74c81d9f5a7";
	
	@Autowired
	private FeedbackSettingsRepository feedbackRepository;

	@Test
	void privateCustomerSettingsExists() {
		assertThat(feedbackRepository.exists(toExample(EXISTING_PRIVATE_PERSON_ID, null, true))).isTrue();
		assertThat(feedbackRepository.exists(toExample(EXISTING_PRIVATE_AND_ORGANIZATION_REPRESENTATIVE_PERSON_ID, null, true))).isTrue();
		assertThat(feedbackRepository.exists(toExample(EXISTING_ORGANIZATION_REPRESENTATIVE_PERSON_ID, null, true))).isFalse();
		assertThat(feedbackRepository.exists(toExample(NON_EXISTING_PERSON_ID, null, true))).isFalse();
	}

	@Test
	void organizationRepresentativeSettingsExists() {
		assertThat(feedbackRepository.exists(toExample(EXISTING_ORGANIZATION_REPRESENTATIVE_PERSON_ID, EXISTING_ORGANIZATION_ID, true))).isTrue();
		assertThat(feedbackRepository.exists(toExample(EXISTING_PRIVATE_AND_ORGANIZATION_REPRESENTATIVE_PERSON_ID, EXISTING_ORGANIZATION_ID, true))).isTrue();
		assertThat(feedbackRepository.exists(toExample(EXISTING_PRIVATE_PERSON_ID, EXISTING_ORGANIZATION_ID, true))).isFalse();
	}

	@Test
	void findAllFullResponse() {
		Page<FeedbackSettingEntity> query = feedbackRepository.findAll(PageRequest.of(0, 100));
		List<FeedbackSettingEntity> entities = query.getContent();

		assertThat(query.getNumberOfElements()).isEqualTo(8);
		assertThat(query.getNumber()).isZero();
		assertThat(query.getTotalElements()).isEqualTo(8);
		assertThat(query.getTotalPages()).isEqualTo(1);
		assertThat(entities)
			.hasSize(8)
			.extracting(FeedbackSettingEntity::getPersonId, FeedbackSettingEntity::getOrganizationId)
			.containsExactlyInAnyOrder(
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d1f", null),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d2f", null),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d3f", null),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d3f", "15aee472-46ab-4f03-9605-68bd64ebc84a"),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d4f", "15aee472-46ab-4f03-9605-68bd64ebc84a"),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d5f", "15aee472-46ab-4f03-9605-68bd64ebc84a"),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d6f", "15aee472-46ab-4f03-9605-68bd64ebc84a"),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d7f", "15aee472-46ab-4f03-9605-68bd64ebc84a"));
	}

	@Test
	void findAllFirstPageOfTwo() {
		Page<FeedbackSettingEntity> query = feedbackRepository.findAll(PageRequest.of(1, 5));
		List<FeedbackSettingEntity> entities = query.getContent();

		assertThat(query.getNumberOfElements()).isEqualTo(3);
		assertThat(query.getNumber()).isEqualTo(1);
		assertThat(query.getTotalElements()).isEqualTo(8);
		assertThat(query.getTotalPages()).isEqualTo(2);
		assertThat(entities)
			.hasSize(3)
			.extracting(FeedbackSettingEntity::getPersonId, FeedbackSettingEntity::getOrganizationId)
			.containsExactlyInAnyOrder(
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d5f", "15aee472-46ab-4f03-9605-68bd64ebc84a"),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d6f", "15aee472-46ab-4f03-9605-68bd64ebc84a"),
					tuple("49a974ea-9137-419b-bcb9-ad74c81a1d7f", "15aee472-46ab-4f03-9605-68bd64ebc84a"));
		}
}
