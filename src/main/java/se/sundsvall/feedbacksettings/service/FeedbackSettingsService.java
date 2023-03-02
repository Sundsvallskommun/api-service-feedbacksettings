package se.sundsvall.feedbacksettings.service;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.NOT_FOUND;
import static se.sundsvall.feedbacksettings.service.ServiceConstants.SETTINGS_ALREADY_EXISTS_FOR_ORGANIZATION_REPRESENTATIVE;
import static se.sundsvall.feedbacksettings.service.ServiceConstants.SETTINGS_ALREADY_EXISTS_FOR_PERSONID;
import static se.sundsvall.feedbacksettings.service.ServiceConstants.SETTINGS_NOT_FOUND_FOR_ID;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.mergeFeedbackSettings;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toExample;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toFeedbackFilters;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toFeedbackSetting;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toFeedbackSettingEntity;
import static se.sundsvall.feedbacksettings.service.mapper.FeedbackSettingsMapper.toWeightedFeedbackSettings;
import static se.sundsvall.feedbacksettings.service.util.CalculationUtils.calculateMatchPercentage;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import org.zalando.problem.ThrowableProblem;

import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackFilter;
import se.sundsvall.feedbacksettings.api.model.FeedbackSetting;
import se.sundsvall.feedbacksettings.api.model.MetaData;
import se.sundsvall.feedbacksettings.api.model.SearchResult;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.api.model.WeightedFeedbackSetting;
import se.sundsvall.feedbacksettings.integration.db.FeedbackSettingsRepository;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

@Service
public class FeedbackSettingsService {
	
    @Autowired
    private FeedbackSettingsRepository feedbackSettingsRepository;

	public FeedbackSetting createFeedbackSetting(CreateFeedbackSettingRequest feedbackSetting) {
		verifyNonExistingSettings(feedbackSetting.getPersonId(), feedbackSetting.getOrganizationId());

		FeedbackSettingEntity entity = toFeedbackSettingEntity(feedbackSetting);
		feedbackSettingsRepository.save(entity);
		
		return toFeedbackSetting(entity);
	}

	public FeedbackSetting updateFeedbackSetting(String id, UpdateFeedbackSettingRequest feedbackSetting) {
		FeedbackSettingEntity entity = feedbackSettingsRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(SETTINGS_NOT_FOUND_FOR_ID, id)));

		//Merge and persist incoming changes to existing entity 
		mergeFeedbackSettings(entity, feedbackSetting);
		feedbackSettingsRepository.save(entity);

		return toFeedbackSetting(entity);
	}

	public FeedbackSetting getFeedbackSettingById(String id) {
		FeedbackSettingEntity entity = feedbackSettingsRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(SETTINGS_NOT_FOUND_FOR_ID, id)));

		return toFeedbackSetting(entity);
	}

	public SearchResult getFeedbackSettings(HttpHeaders headers, String personId, String organizationId, int page, int limit) {
		// Paging in SpringData starts with 0, API paging starts with 1 - hence the subtraction of 1
		Page<FeedbackSettingEntity> matches =
			feedbackSettingsRepository.findAll(toExample(personId, organizationId, false), PageRequest.of(page - 1, limit));
		
		// If page larger than last page is requested, a empty list is returned otherwise the current page
		List<WeightedFeedbackSetting> settings = matches.getTotalPages() < page ? Collections.emptyList() : toWeightedFeedbackSettings(matches.getContent());

		// Convert headers to searchFilters and calculate match percentage for fetched feedback settings 
		List<FeedbackFilter> searchFilters = toFeedbackFilters(headers);
		settings.stream().forEach(setting -> calculateMatchPercentage(searchFilters, setting));

		// Return result sorted ascending by matching percent
		return SearchResult.create()
			.withMetaData(MetaData.create()
				.withPage(page)
				.withTotalPages(matches.getTotalPages())
				.withTotalRecords(matches.getTotalElements())
				.withCount(settings.size())
				.withLimit(limit))
			.withFeedbackSettings(settings.stream().sorted((o1, o2) -> o2.getMatchingPercent() - o1.getMatchingPercent()).toList());
	}
	
	public void deleteFeedbackSetting(String id) {
		// Check that setting for sent in id exists
		if (!feedbackSettingsRepository.existsById(id)) {
			throw Problem.valueOf(NOT_FOUND, format(SETTINGS_NOT_FOUND_FOR_ID, id));
		}

		feedbackSettingsRepository.deleteById(id);
	}

	/**
	 * Method checks for existing settings for combination of sent in personId and organizationId
	 * 
	 * @param personId       personId
	 * @param organizationId organizationId
	 * @throws ThrowableProblem if an existing match for the sent in parameters is found in the database
	 */
	private void verifyNonExistingSettings(String personId, String organizationId) {
		if (feedbackSettingsRepository.exists(toExample(personId, organizationId, true))) {
			throw isNull(organizationId) ? 
				Problem.valueOf(BAD_REQUEST, String.format(SETTINGS_ALREADY_EXISTS_FOR_PERSONID, personId)) : 
				Problem.valueOf(BAD_REQUEST, String.format(SETTINGS_ALREADY_EXISTS_FOR_ORGANIZATION_REPRESENTATIVE, personId, organizationId));
		}
	}
}
