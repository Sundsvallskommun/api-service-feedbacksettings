package se.sundsvall.feedbacksettings.service.util;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import se.sundsvall.feedbacksettings.api.model.FeedbackFilter;
import se.sundsvall.feedbacksettings.api.model.WeightedFeedbackSetting;

@ExtendWith(MockitoExtension.class)
class CalculationUtilsTest {
	
	private static final String KEY_1 = "key-1";
	private static final String KEY_2 = "key-2";
	private static final String KEY_3 = "key-3";
	private static final String VALUE = "value";
	private static final String NO_MATCH_VALUE = "no-value-match";
	
	@Mock
	private WeightedFeedbackSetting settingMock;
	
	@Mock
	private FeedbackFilter feedbackFilterMock;
	
	/**
	 * Test should return without executing matching logic (leaving set percent value untouched)
	 */
	@Test
	void testNullFilterList() {
		CalculationUtils.calculateMatchPercentage(null, settingMock);
		
		verify(settingMock, never()).setMatchingPercent(anyInt());
		verify(settingMock, never()).withMatchingPercent(anyInt());
	}
	
	/**
	 * Test should match on mandatory orgId/persId-filter which leads to calculation of percentage as:
	 * 1 match of (0 incoming filter + 1 mandatory orgId/persId-filter + 0 unmatched filters on setting)
	 * = 100%
	 */
	@Test
	void testEmptyFilterList() {
		CalculationUtils.calculateMatchPercentage(Collections.emptyList(), settingMock);

		verify(settingMock, never()).getFilters();
		verify(settingMock).setMatchingPercent(100);
	}
	
	/**
	 * Test should match on incoming filter (as setting doesn't have any filter with equal key) 
	 * plus mandatory orgId/persId-filter which leads to calculation of percentage as:
	 * 2 matches of (1 incoming filter + 1 mandatory orgId/persId-filter)
	 * = 100%
	 */
	@Test
	void testFilterListContainsFilterButSettingsContainsNoFilters() {
		when(settingMock.getFilters()).thenReturn(Collections.emptyList());
		
		List<FeedbackFilter> filters = List.of(FeedbackFilter.create()
				.withKey(KEY_1)
				.withValues(List.of(VALUE)));
		
		CalculationUtils.calculateMatchPercentage(filters, settingMock);
		
		verify(settingMock, times(1)).getFilters();
		verify(settingMock).setMatchingPercent(100);
	}
	
	/**
	 * Test should match on first incoming filter plus mandatory orgId/persId-filter which leads to 
	 * calculation of percentage as:
	 * 2 matches of (2 incoming-filter + 1 mandatory orgId/persId-filter + 0 unmatched filters on setting)
	 * = 67%
	 */
	@Test
	void testFilterListContainsTwoFiltersMatchedByOneSettingsFilter() {
		List<FeedbackFilter> settingsFilters = List.of(
				FeedbackFilter.create().withKey(KEY_1).withValues(List.of(VALUE)),
				FeedbackFilter.create().withKey(KEY_2).withValues(List.of(NO_MATCH_VALUE)));
		
		when(settingMock.getFilters()).thenReturn(settingsFilters);
		
		List<FeedbackFilter> filters = List.of(
				FeedbackFilter.create().withKey(KEY_1).withValues(List.of(VALUE)),
				FeedbackFilter.create().withKey(KEY_2).withValues(List.of(VALUE)));
		
		CalculationUtils.calculateMatchPercentage(filters, settingMock);
		
		verify(settingMock).setMatchingPercent(67);
	}

	/**
	 * Test should match on first incoming filter, but not second (and disregarding the third
	 * filter on setting), plus mandatory orgId/persId-filter which leads to calculation of 
	 * percentage as:
	 * 2 matches of (2 incoming-filter + 1 mandatory orgId/persId-filter)
	 * = 67%
	 */
	@Test
	void testFilterListContainsTwoFiltersMatchedByOneSettingsFilterAndOneNotPresentInIncomingFilter() {
		List<FeedbackFilter> settingsFilters = List.of(
				FeedbackFilter.create().withKey(KEY_1).withValues(List.of(VALUE)),
				FeedbackFilter.create().withKey(KEY_2).withValues(List.of(NO_MATCH_VALUE)),
				FeedbackFilter.create().withKey(KEY_3).withValues(List.of(NO_MATCH_VALUE)));
		
		when(settingMock.getFilters()).thenReturn(settingsFilters);
		
		List<FeedbackFilter> filters = List.of(
				FeedbackFilter.create().withKey(KEY_1).withValues(List.of(VALUE)),
				FeedbackFilter.create().withKey(KEY_2).withValues(List.of(VALUE)));
		
		CalculationUtils.calculateMatchPercentage(filters, settingMock);
		
		verify(settingMock).setMatchingPercent(67);
	}
	
	/**
	 * Test should match on incoming filter plus mandatory orgId/persId-filter and having 
	 * two unmatched filters on the setting which leads to calculation of percentage as: 
	 * 2 matches of (1 incoming-filter + 1 mandatory orgId/persId-filter) 
	 * = 100%
	 */
	@Test
	void testFilterListContainsOneFilterMatchedByOneSettingsFilter() {
		List<FeedbackFilter> settingsFilters = List.of(
				FeedbackFilter.create().withKey(KEY_1).withValues(List.of(VALUE)),
				FeedbackFilter.create().withKey(KEY_2).withValues(List.of(NO_MATCH_VALUE)),
				FeedbackFilter.create().withKey(KEY_3).withValues(List.of(NO_MATCH_VALUE)));
		
		when(settingMock.getFilters()).thenReturn(settingsFilters);
		
		List<FeedbackFilter> filters = List.of(
				FeedbackFilter.create().withKey(KEY_1).withValues(List.of(VALUE)));
		
		CalculationUtils.calculateMatchPercentage(filters, settingMock);
		
		verify(settingMock).setMatchingPercent(100);
	}

	/**
	 * Test should match on first incoming filter, but not second plus mandatory orgId/persId-filter 
	 * which leads to calculation of percentage as:
	 * 2 matches of (2 incoming filters + 1 mandatory orgId/persId-filter)
	 * = 67%
	 */
	@Test
	void testFilterListContainsTwoFiltersWhereOneIsMatchedBySettingsFilter() {
		List<FeedbackFilter> settingsFilters = List.of(
				FeedbackFilter.create().withKey(KEY_1).withValues(List.of(VALUE)),
				FeedbackFilter.create().withKey(KEY_2).withValues(List.of(NO_MATCH_VALUE)));
		
		when(settingMock.getFilters()).thenReturn(settingsFilters);
		
		List<FeedbackFilter> filters = List.of(
				FeedbackFilter.create().withKey(KEY_1).withValues(List.of(VALUE)),
				FeedbackFilter.create().withKey(KEY_2).withValues(Collections.emptyList()));
		
		CalculationUtils.calculateMatchPercentage(filters, settingMock);
		
		verify(settingMock).setMatchingPercent(67);
	}
}
