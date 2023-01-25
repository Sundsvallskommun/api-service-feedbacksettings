package se.sundsvall.feedbacksettings.service.util;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

import java.util.List;
import java.util.Optional;

import se.sundsvall.feedbacksettings.integration.db.model.FeedbackChannelEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackFilterEmbeddable;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

public class MappingUtils {
	
	private MappingUtils() {}
	
	/**
	 * Returns all removed FeedbackChannelEmbeddable elements from oldSettingsEntity.getFeedbackChannels(), when comparing with
	 * newFeedbackChannels.
	 * 
	 * E.g.
	 * 
	 * oldSettingsEntity.getFeedbackChannels() contains: <ELEMENT-1>, <ELEMENT-2>, <ELEMENT-3>
	 * newFeedbackChannels contains: <ELEMENT-1>, <ELEMENT-3>
	 * 
	 * Result: This method will return [<ELEMENT-2>]
	 * 
	 * @param oldSettingsEntity
	 * @param newFeedbackChannels
	 * @return Returns the difference (removed elements) from oldSettingsEntity.
	 */
	public static List<FeedbackChannelEmbeddable> getRemovedFeedbackChannels(FeedbackSettingEntity oldSettingsEntity, List<FeedbackChannelEmbeddable> newFeedbackChannels) {
		// If affectedEntities in newFeedbackChannels isn't set (i.e. is null), just return an empty list.
		if (isNull(newFeedbackChannels)) {
			return emptyList();
		}
		return ofNullable(oldSettingsEntity.getFeedbackChannels()).orElse(emptyList()).stream()
			.filter(oldEntity -> !existsInList(oldEntity, newFeedbackChannels))
			.toList();
	}

	/**
	 * Returns all added FeedbackChannelEmbeddable elements from newFeedbackChannels, when comparing with
	 * oldSettingsEntity.getFeedbackChannels().
	 * 
	 * E.g.
	 * 
	 * oldSettingsEntity.getFeedbackChannels() contains: <ELEMENT-1>, <ELEMENT-2>, <ELEMENT-3>
	 * newFeedbackChannels contains: <ELEMENT-1>, <ELEMENT-4>
	 * 
	 * Result: This method will return [<ELEMENT-4>]
	 * 
	 * @param oldSettingsEntity
	 * @param newFeedbackChannels
	 * @return Returns the added elements from newFeedbackChannels.
	 */
	public static List<FeedbackChannelEmbeddable> getAddedFeedbackChannels(FeedbackSettingEntity oldSettingsEntity, List<FeedbackChannelEmbeddable> newFeedbackChannels) {
		if (isNull(oldSettingsEntity) || isNull(oldSettingsEntity.getFeedbackChannels())) {
			return Optional.ofNullable(newFeedbackChannels).orElse(emptyList());
		}
		// If newFeedbackChannelEntities isn't set (i.e. is null), just return an empty list.
		if (isNull(newFeedbackChannels)) {
			return emptyList();
		}
		return newFeedbackChannels.stream()
			.filter(newEntity -> !existsInList(newEntity, oldSettingsEntity.getFeedbackChannels()))
			.toList();
	}

	private static boolean existsInList(FeedbackChannelEmbeddable objectToCheck, List<FeedbackChannelEmbeddable> list) {
		return ofNullable(list).orElse(emptyList()).stream()
			.anyMatch(entity -> 
					equalsIgnoreCase(entity.getAlias(), objectToCheck.getAlias()) &&
					equalsIgnoreCase(entity.getDestination(), objectToCheck.getDestination()) &&
					entity.getContactMethod() == objectToCheck.getContactMethod() && 
					entity.isSendFeedback() == objectToCheck.isSendFeedback());
	}
	
	/**
	 * Returns all removed FeedbackFilterEmbeddable elements from oldSettingsEntity.getFeedbackFilters(), when comparing with
	 * newFilters.
	 * 
	 * E.g.
	 * 
	 * oldSettingsEntity.getFeedbackFilters() contains: <ELEMENT-1>, <ELEMENT-2>, <ELEMENT-3>
	 * newFilters contains: <ELEMENT-1>, <ELEMENT-3>
	 * 
	 * Result: This method will return [<ELEMENT-2>]
	 * 
	 * @param oldSettingsEntity
	 * @param newFeedbackFilters
	 * @return Returns the difference (removed elements) from oldSettingsEntity.
	 */
	public static List<FeedbackFilterEmbeddable> getRemovedFeedbackFilters(FeedbackSettingEntity oldSettingsEntity, List<FeedbackFilterEmbeddable> newFeedbackFilters) {
		// If affectedEntities in newFeedbackChannels isn't set (i.e. is null), just return an empty list.
		if (isNull(newFeedbackFilters)) {
			return emptyList();
		}
		return ofNullable(oldSettingsEntity.getFeedbackFilters()).orElse(emptyList()).stream()
			.filter(oldEntity -> !existsInList(oldEntity, newFeedbackFilters))
			.toList();
	}

	/**
	 * Returns all added FeedbackFilterEmbeddable elements from newFilters, when comparing with
	 * oldSettingsEntity.getFeedbackFilters().
	 * 
	 * E.g.
	 * 
	 * oldSettingsEntity.getFeedbackFilters() contains: <ELEMENT-1>, <ELEMENT-2>, <ELEMENT-3>
	 * newFilters contains: <ELEMENT-1>, <ELEMENT-4>
	 * 
	 * Result: This method will return [<ELEMENT-4>]
	 * 
	 * @param oldSettingsEntity
	 * @param newFeedbackFilters
	 * @return Returns the added elements from newFilters.
	 */
	public static List<FeedbackFilterEmbeddable> getAddedFeedbackFilters(FeedbackSettingEntity oldSettingsEntity, List<FeedbackFilterEmbeddable> newFeedbackFilters) {
		if (isNull(oldSettingsEntity) || isNull(oldSettingsEntity.getFeedbackFilters())) {
			return Optional.ofNullable(newFeedbackFilters).orElse(emptyList());
		}
		// If newFeedbackFilters isn't set (i.e. is null), just return an empty list.
		if (isNull(newFeedbackFilters)) {
			return emptyList();
		}
		return newFeedbackFilters.stream()
			.filter(newEntity -> !existsInList(newEntity, oldSettingsEntity.getFeedbackFilters()))
			.toList();
	}

	private static boolean existsInList(FeedbackFilterEmbeddable objectToCheck, List<FeedbackFilterEmbeddable> list) {
		return ofNullable(list).orElse(emptyList()).stream()
			.anyMatch(entity -> 
					equalsIgnoreCase(entity.getKey(), objectToCheck.getKey()) &&
					equalsIgnoreCase(entity.getValue(), objectToCheck.getValue()));
	}
}
