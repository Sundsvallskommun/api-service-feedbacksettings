package se.sundsvall.feedbacksettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Weighted feedback setting model")
public class WeightedFeedbackSetting extends FeedbackSetting {

	@Schema(description = "Percentual match compared to filters in request", example = "37", accessMode = READ_ONLY)
	private int matchingPercent;

	public static WeightedFeedbackSetting create() {
		return new WeightedFeedbackSetting();
	}

	@Override
	public WeightedFeedbackSetting withId(String id) {
		setId(id);
		return this;
	}

	@Override
	public WeightedFeedbackSetting withPersonId(String personId) {
		setPersonId(personId);
		return this;
	}

	@Override
	public WeightedFeedbackSetting withOrganizationId(String organizationId) {
		setOrganizationId(organizationId);
		return this;
	}

	@Override
	public WeightedFeedbackSetting withFilters(List<FeedbackFilter> filters) {
		setFilters(filters);
		return this;
	}

	@Override
	public WeightedFeedbackSetting withChannels(List<FeedbackChannel> channels) {
		setChannels(channels);
		return this;
	}

	@Override
	public WeightedFeedbackSetting withCreated(OffsetDateTime created) {
		setCreated(created);
		return this;
	}

	@Override
	public WeightedFeedbackSetting withModified(OffsetDateTime modified) {
		setModified(modified);
		return this;
	}

	public int getMatchingPercent() {
		return matchingPercent;
	}

	public void setMatchingPercent(int matchingPercent) {
		this.matchingPercent = matchingPercent;
	}

	public WeightedFeedbackSetting withMatchingPercent(int matchingPercent) {
		this.matchingPercent = matchingPercent;
		return this;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + Objects.hash(matchingPercent);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		WeightedFeedbackSetting other = (WeightedFeedbackSetting) obj;
		return matchingPercent == other.matchingPercent;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WeightedFeedbackSetting [matchingPercent=").append(matchingPercent).append(", getId()=").append(getId()).append(", getPersonId()=").append(getPersonId()).append(", getOrganizationId()=").append(getOrganizationId()).append(
			", getFilters()=").append(getFilters()).append(", getChannels()=").append(getChannels()).append(", getCreated()=").append(getCreated()).append(", getModified()=").append(getModified()).append(", toString()=").append(super.toString())
			.append(", getClass()=").append(getClass()).append("]");
		return builder.toString();
	}
}
