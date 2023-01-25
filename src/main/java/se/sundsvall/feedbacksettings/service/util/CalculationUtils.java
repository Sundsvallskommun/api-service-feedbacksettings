package se.sundsvall.feedbacksettings.service.util;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import se.sundsvall.feedbacksettings.api.model.FeedbackFilter;
import se.sundsvall.feedbacksettings.api.model.WeightedFeedbackSetting;

public class CalculationUtils {

	private static final int PERCENTS_100 = 100;

	private CalculationUtils() {}

	/**
	 * Utility method for calculating matching percentage for a feedback setting, based on the search filters that has been
	 * sent
	 * in as header values. Each filter must be named x-filter-[filter_key] to be evaluated. For example x-filter-categories
	 * will
	 * be matched against feedback filter with key categories.
	 * 
	 * The calculation calculates percentage accordingly:
	 * 
	 * <pre>
	 * - Each feedback setting is considered to have 1 matching filter from start, as query always are based on a combination of organizationId/personId
	 * - The total filter count is calculated as size of incoming x-filter-[filter_key]s plus 1 (see previous row) plus any filters stored on
	 *   the feedback setting that is not matched by any incoming search filters.
	 * - For each incoming search filter the following logic is executed:
	 *   1 It is considered to be a match if feedback setting doesn't have any filter key matching the key of the incoming search filter (i.e the setting 
	 *     has no active filtering for for the key)
	 *   2 It is considered to be a match if feedback setting matches key of incoming search filter and has a filter value matching at least one of the 
	 *     incoming filter values
	 *   3 If not 1 or 2 has been triggered the feedback setting is considered to not match the incoming search filter
	 * - Depending on result, the matching filter counter is increased or left untouched
	 * - After all incoming search filters have been processed, the feedback setting is updated with the calculated matching percent
	 * </pre>
	 * 
	 * @param searchFilters containing filters, to base match percent on, sent in the request
	 * @param setting       matching the orgainizataionId/personId parameters sent in the request
	 */
	public static void calculateMatchPercentage(List<FeedbackFilter> searchFilters, WeightedFeedbackSetting setting) {
		if (isNull(searchFilters)) {
			return;
		}

		// Match always starts with 1 as there is always a match on combination of sent in personId/organizationId
		AtomicInteger matchingFilters = new AtomicInteger(1);

		// Check each search filter against filters of the setting and increase filterMatches counter when matching
		searchFilters.stream().forEach(searchFilter -> matchingFilters.addAndGet(matchesFilterForSetting(searchFilter, setting) ? 1 : 0));

		// Calculate matching percent
		setting.setMatchingPercent(Math.round(matchingFilters.floatValue() / (searchFilters.size() + 1) * PERCENTS_100));
	}

	private static boolean matchesFilterForSetting(FeedbackFilter incomingFilter, WeightedFeedbackSetting setting) {
		boolean settingLacksFilter = ofNullable(setting.getFilters()).orElse(emptyList()).stream()
			.noneMatch(filter -> incomingFilter.getKey().equalsIgnoreCase(filter.getKey()));

		return settingLacksFilter || ofNullable(setting.getFilters()).orElse(emptyList()).stream()
			.filter(filter -> incomingFilter.getKey().equalsIgnoreCase(filter.getKey()))
			.anyMatch(filter -> matchesFilterValue(incomingFilter.getValues(), filter.getValues()));
	}

	private static boolean matchesFilterValue(List<String> settingsFilterValues, List<String> requestedFilterValues) {
		return settingsFilterValues.stream()
			.anyMatch(settingsFilterValue -> requestedFilterValues.stream().anyMatch(settingsFilterValue::equalsIgnoreCase));
	}
}
