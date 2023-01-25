package se.sundsvall.feedbacksettings.api.model;

import java.util.List;
import java.util.Objects;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Feedback filter model")
public class FeedbackFilter {
	
	@Schema(description = "Unique key for the filter", example = "categories")
	private String key;
	
	@ArraySchema(schema = @Schema(description = "List of values for the filter", example = "[\"broadband\", \"electricity\"]", implementation = String.class))
	private List<String> values;
	
	public static FeedbackFilter create() {
		return new FeedbackFilter();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public FeedbackFilter withKey(String key) {
		this.key = key;
		return this;
	}

	public List<String> getValues() {
		return values;
	}
	
	public void setValues(List<String> values) {
		this.values = values;
	}

	public FeedbackFilter withValues(List<String> values) {
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
		FeedbackFilter other = (FeedbackFilter) obj;
		return Objects.equals(key, other.key) && Objects.equals(values, other.values);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeedbackFilter [key=").append(key).append(", values=").append(values).append("]");
		return builder.toString();
	}

}
