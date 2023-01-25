package se.sundsvall.feedbacksettings.api.model;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.feedbacksettings.api.validation.UniqueFeedbackChannels;
import se.sundsvall.feedbacksettings.api.validation.UniqueFilterKeys;

@Schema(description = "Request model for updating a feedback setting")
public class UpdateFeedbackSettingRequest {

	@ArraySchema(schema = @Schema(implementation = RequestedFeedbackChannel.class))
	@UniqueFeedbackChannels
	private List<@Valid RequestedFeedbackChannel> channels;

	@ArraySchema(schema = @Schema(implementation = RequestedFeedbackFilter.class))
	@UniqueFilterKeys
	private List<@Valid RequestedFeedbackFilter> filters;
	
	public static UpdateFeedbackSettingRequest create() {
		return new UpdateFeedbackSettingRequest();
	}

	public List<RequestedFeedbackChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<RequestedFeedbackChannel> channels) {
		this.channels = channels;
	}

	public UpdateFeedbackSettingRequest withChannels(List<RequestedFeedbackChannel> channels) {
		this.channels = channels;
		return this;
	}

	public List<RequestedFeedbackFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<RequestedFeedbackFilter> filters) {
		this.filters = filters;
	}

	public UpdateFeedbackSettingRequest withFilters(List<RequestedFeedbackFilter> filters) {
		this.filters = filters;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(channels, filters);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UpdateFeedbackSettingRequest other = (UpdateFeedbackSettingRequest) obj;
		return Objects.equals(channels, other.channels) && Objects.equals(filters, other.filters);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UpdateFeedbackSettingRequest [filters=").append(filters).append(", channels=").append(channels)
				.append("]");
		return builder.toString();
	}
}
