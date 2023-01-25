package se.sundsvall.feedbacksettings.service.mapper;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static se.sundsvall.feedbacksettings.service.util.MappingUtils.getAddedFeedbackChannels;
import static se.sundsvall.feedbacksettings.service.util.MappingUtils.getAddedFeedbackFilters;
import static se.sundsvall.feedbacksettings.service.util.MappingUtils.getRemovedFeedbackChannels;
import static se.sundsvall.feedbacksettings.service.util.MappingUtils.getRemovedFeedbackFilters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpHeaders;

import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.FeedbackFilter;
import se.sundsvall.feedbacksettings.api.model.FeedbackSetting;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackFilter;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.api.model.WeightedFeedbackSetting;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackChannelEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackFilterEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

public class FeedbackSettingsMapper {

	private static final String X_FILTER_PREFIX = "x-filter-";
	private static final String[] IGNORED_PATHS = { "id", "feedbackFilters", "feedbackChannels", "created", "modified" };
	private static final int DEFAULT_MATCHING_PERCENT = 100;

	private FeedbackSettingsMapper() {}

	public static FeedbackSettingEntity toFeedbackSettingEntity(CreateFeedbackSettingRequest feedbackSetting) {
		if (isNull(feedbackSetting)) {
			return null;
		}

		return FeedbackSettingEntity.create()
			.withPersonId(feedbackSetting.getPersonId())
			.withOrganizationId(feedbackSetting.getOrganizationId())
			.withFeedbackFilters(toFeedbackFilterEmbeddables(feedbackSetting.getFilters()))
			.withFeedbackChannels(toFeedbackChannelEmbeddables(feedbackSetting.getChannels()));
	}

	private static List<FeedbackFilterEmbeddable> toFeedbackFilterEmbeddables(List<RequestedFeedbackFilter> filters) {
		return ofNullable(filters).orElse(emptyList()).stream()
			.filter(Objects::nonNull)
			.flatMap(filter -> toFeedbackFilterEmbeddables(filter).stream())
			.toList();
	}

	private static List<FeedbackFilterEmbeddable> toFeedbackFilterEmbeddables(RequestedFeedbackFilter filter) {
		return filter.getValues().stream()
			.map(value -> FeedbackFilterEmbeddable.create()
				.withKey(filter.getKey().toUpperCase())
				.withValue(value))
			.toList();
	}

	private static List<FeedbackChannelEmbeddable> toFeedbackChannelEmbeddables(List<RequestedFeedbackChannel> feedbackChannels) {
		return ofNullable(feedbackChannels).orElse(emptyList()).stream()
			.filter(Objects::nonNull)
			.map(channel -> FeedbackChannelEmbeddable.create()
				.withContactMethod(channel.getContactMethod())
				.withAlias(isBlank(channel.getAlias()) ? channel.getDestination() : channel.getAlias()) // Default to destination if incoming alias is blank
				.withDestination(channel.getDestination())
				.withSendFeedback(channel.getSendFeedback()))
			.toList();
	}

	public static FeedbackSetting toFeedbackSetting(FeedbackSettingEntity entity) {
		if (isNull(entity)) {
			return null;
		}

		return FeedbackSetting.create()
			.withId(entity.getId())
			.withPersonId(entity.getPersonId())
			.withOrganizationId(entity.getOrganizationId())
			.withFilters(toFeedbackFilters(entity.getFeedbackFilters()))
			.withChannels(toFeedbackChannels(entity.getFeedbackChannels()))
			.withCreated(entity.getCreated())
			.withModified(entity.getModified());
	}

	private static List<FeedbackFilter> toFeedbackFilters(List<FeedbackFilterEmbeddable> feedbackSettingsFiltersEmbeddables) {
		return ofNullable(feedbackSettingsFiltersEmbeddables).orElse(emptyList()).stream()
			.filter(Objects::nonNull)
			.collect(groupingBy(FeedbackFilterEmbeddable::getKey,
				mapping(FeedbackFilterEmbeddable::getValue, toList())))
			.entrySet()
			.stream()
			.map(entry -> FeedbackFilter.create()
				.withKey(entry.getKey())
				.withValues(entry.getValue()))
			.toList();
	}

	public static WeightedFeedbackSetting toWeightedFeedbackSetting(FeedbackSettingEntity entity) {
		if (isNull(entity)) {
			return null;
		}

		return WeightedFeedbackSetting.create()
			.withId(entity.getId())
			.withPersonId(entity.getPersonId())
			.withOrganizationId(entity.getOrganizationId())
			.withFilters(toFeedbackFilters(entity.getFeedbackFilters()))
			.withChannels(toFeedbackChannels(entity.getFeedbackChannels()))
			.withCreated(entity.getCreated())
			.withModified(entity.getModified())
			.withMatchingPercent(DEFAULT_MATCHING_PERCENT);
	}

	private static List<FeedbackChannel> toFeedbackChannels(List<FeedbackChannelEmbeddable> feedbackChannelEmbeddables) {
		return ofNullable(feedbackChannelEmbeddables).orElse(emptyList()).stream()
			.filter(Objects::nonNull)
			.map(entity -> FeedbackChannel.create()
				.withContactMethod(entity.getContactMethod())
				.withAlias(entity.getAlias())
				.withDestination(entity.getDestination())
				.withSendFeedback(entity.isSendFeedback()))
			.toList();
	}

	public static List<FeedbackSetting> toFeedbackSettings(List<FeedbackSettingEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(FeedbackSettingsMapper::toFeedbackSetting)
			.toList();
	}

	public static List<WeightedFeedbackSetting> toWeightedFeedbackSettings(List<FeedbackSettingEntity> entities) {
		return ofNullable(entities).orElse(emptyList()).stream()
			.map(FeedbackSettingsMapper::toWeightedFeedbackSetting)
			.toList();
	}

	public static void mergeFeedbackSettings(FeedbackSettingEntity entity, UpdateFeedbackSettingRequest feedbackSetting) {
		if (nonNull(feedbackSetting)) {
			mergeFeedbackChannels(entity, feedbackSetting.getChannels());
			mergeFeedbackFilters(entity, feedbackSetting.getFilters());
		}
	}

	private static void mergeFeedbackChannels(FeedbackSettingEntity entity, List<RequestedFeedbackChannel> feedbackChannels) {
		if (nonNull(feedbackChannels)) {
			List<FeedbackChannelEmbeddable> requestedChannelEntities = toFeedbackChannelEmbeddables(feedbackChannels);

			// Save current channels before modification
			List<FeedbackChannelEmbeddable> oldChannelEntities = new ArrayList<>(entity.getFeedbackChannels());

			entity.getFeedbackChannels().addAll(getAddedFeedbackChannels(entity, requestedChannelEntities));
			entity.getFeedbackChannels().removeAll(getRemovedFeedbackChannels(entity, requestedChannelEntities));

			// Compare old and new list and if they differ, call preUpdate as Hibernate has an open bug regarding @preUpdate doesn't
			// get triggered when collections are updated
			if (!oldChannelEntities.equals(entity.getFeedbackChannels())) {
				entity.preUpdate();
			}
		}
	}

	private static void mergeFeedbackFilters(FeedbackSettingEntity entity, List<RequestedFeedbackFilter> filters) {
		if (nonNull(filters)) {
			List<FeedbackFilterEmbeddable> requestedFilterEntities = toFeedbackFilterEmbeddables(filters);

			// Save current filters before modification
			List<FeedbackFilterEmbeddable> oldFilterEntities = new ArrayList<>(entity.getFeedbackFilters());

			entity.getFeedbackFilters().addAll(getAddedFeedbackFilters(entity, requestedFilterEntities));
			entity.getFeedbackFilters().removeAll(getRemovedFeedbackFilters(entity, requestedFilterEntities));

			// Compare old and new list and if they differ, call preUpdate as Hibernate has an open bug regarding @preUpdate doesn't
			// get triggered when collections are updated
			if (!oldFilterEntities.equals(entity.getFeedbackFilters())) {
				entity.preUpdate();
			}
		}
	}

	public static List<FeedbackFilter> toFeedbackFilters(HttpHeaders headers) {
		if (isNull(headers)) {
			return Collections.emptyList();
		}

		return headers.keySet().stream()
			.filter(key -> StringUtils.startsWithIgnoreCase(key, X_FILTER_PREFIX))
			.map(key -> FeedbackFilter.create()
				.withKey(key.substring(X_FILTER_PREFIX.length()))
				.withValues(getDistictValues(headers.get(key))))
			.toList();
	}

	private static List<String> getDistictValues(List<String> original) {
		Set<String> distinct = new TreeSet<>(CASE_INSENSITIVE_ORDER);
		distinct.addAll(original);
		return distinct.stream().toList();
	}

	/**
	 * Method for building a query by example object based on the feedback setting entity. The example only takes into
	 * consideration the fields personId and organizationId when matching and if includeNullValues is set, it also checks
	 * that null is matched for non set fields in the example. Otherwise only fields containing a real value is matched
	 * and fields with null values are ignored.
	 * 
	 * @param personId          id of person to match (or null)
	 * @param organizationId    id of organization to match (or null)
	 * @param includeNullValues set to true to include check of field equal to null, set to false if null values should
	 *                          be ignored when matching.
	 * @return example to match against when performing database query
	 */
	public static Example<FeedbackSettingEntity> toExample(String personId, String organizationId, boolean includeNullValues) {
		ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths(IGNORED_PATHS);
		return Example.of(FeedbackSettingEntity.create()
			.withPersonId(personId)
			.withOrganizationId(organizationId), includeNullValues ? matcher.withIncludeNullValues() : matcher);
	}
}
