package se.sundsvall.feedbacksettings.api.model;

import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.feedbacksettings.api.validation.UniqueFilterValues;

@Schema(description = "Requested feedback filter model")
public class RequestedFeedbackFilter {
	
	@Schema(description = "Unique key for the filter", example = "categories")
	@NotBlank
	private String key;
	
	@ArraySchema(schema = @Schema(description = "List of values for the filter", example = "[\"broadband\", \"electricity\"]", implementation = String.class), uniqueItems = true)
	@NotEmpty
	@UniqueFilterValues
	private List<@NotBlank String> values;
	
	public static RequestedFeedbackFilter create() {
		return new RequestedFeedbackFilter();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public RequestedFeedbackFilter withKey(String key) {
		this.key = key;
		return this;
	}

	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}

	public RequestedFeedbackFilter withValues(List<String> values) {
		this.values = values;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, values);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestedFeedbackFilter other = (RequestedFeedbackFilter) obj;
		return Objects.equals(key, other.key) && Objects.equals(values, other.values);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RequestedFeedbackFilter [key=").append(key).append(", values=").append(values).append("]");
		return builder.toString();
	}
}
