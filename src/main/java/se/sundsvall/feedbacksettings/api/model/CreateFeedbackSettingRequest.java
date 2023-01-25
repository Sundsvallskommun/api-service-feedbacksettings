package se.sundsvall.feedbacksettings.api.model;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.feedbacksettings.api.validation.UniqueFeedbackChannels;
import se.sundsvall.feedbacksettings.api.validation.UniqueFilterKeys;

@Schema(description = "Request model for creating a new feedback setting")
public class CreateFeedbackSettingRequest {

	@Schema(description = "Unique id for the person to whom the feedback setting shall apply", example = "15aee472-46ab-4f03-9605-68bd64ebc73f")
	@ValidUuid
	private String personId;

	@Schema(description = "Unique id for the company to which the feedback setting shall apply if the setting refers to an organizational representative", example = "15aee472-46ab-4f03-9605-68bd64ebc84a")
	@ValidUuid(nullable = true)
	private String organizationId;

	@ArraySchema(schema = @Schema(implementation = RequestedFeedbackFilter.class))
	@UniqueFilterKeys
	private List<@Valid RequestedFeedbackFilter> filters;

	@ArraySchema(schema = @Schema(implementation = RequestedFeedbackChannel.class))
	@UniqueFeedbackChannels
	private List<@Valid RequestedFeedbackChannel> channels;

	public static CreateFeedbackSettingRequest create() {
		return new CreateFeedbackSettingRequest();
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public CreateFeedbackSettingRequest withPersonId(String personId) {
		this.personId = personId;
		return this;
	}

	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}

	public CreateFeedbackSettingRequest withOrganizationId(String organizationId) {
		this.organizationId = organizationId;
		return this;
	}

	public List<RequestedFeedbackFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<RequestedFeedbackFilter> filters) {
		this.filters = filters;
	}

	public CreateFeedbackSettingRequest withFilters(List<RequestedFeedbackFilter> filters) {
		this.filters = filters;
		return this;
	}

	public List<RequestedFeedbackChannel> getChannels() {
		return channels;
	}

	public void setChannels(List<RequestedFeedbackChannel> channels) {
		this.channels = channels;
	}

	public CreateFeedbackSettingRequest withChannels(List<RequestedFeedbackChannel> channels) {
		this.channels = channels;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(channels, filters, organizationId, personId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CreateFeedbackSettingRequest other = (CreateFeedbackSettingRequest) obj;
		return Objects.equals(channels, other.channels) && Objects.equals(filters, other.filters)
			&& Objects.equals(organizationId, other.organizationId) && Objects.equals(personId, other.personId);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CreateFeedbackSettingRequest [personId=").append(personId).append(", organizationId=")
			.append(organizationId).append(", filters=").append(filters).append(", channels=").append(channels)
			.append("]");
		return builder.toString();
	}
}
