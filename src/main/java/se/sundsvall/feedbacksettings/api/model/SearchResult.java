package se.sundsvall.feedbacksettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Search result model")
public class SearchResult {

	@JsonProperty("_meta")
	@Schema(implementation = MetaData.class)
	private MetaData metaData;

	@ArraySchema(schema = @Schema(implementation = WeightedFeedbackSetting.class, accessMode = READ_ONLY))
	private List<WeightedFeedbackSetting> feedbackSettings;

	public static SearchResult create() {
		return new SearchResult();
	}
	
	public List<WeightedFeedbackSetting> getFeedbackSettings() {
		return feedbackSettings;
	}
	
	public void setFeedbackSettings(List<WeightedFeedbackSetting> feedbackSettings) {
		this.feedbackSettings = feedbackSettings;
	}

	public SearchResult withFeedbackSettings(List<WeightedFeedbackSetting> feedbackSettings) {
		this.feedbackSettings = feedbackSettings;
		return this;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public SearchResult withMetaData(MetaData metaData) {
		this.metaData = metaData;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(feedbackSettings, metaData);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SearchResult other = (SearchResult) obj;
		return Objects.equals(feedbackSettings, other.feedbackSettings) && Objects.equals(metaData, other.metaData);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SearchResult [metaData=").append(metaData).append(", feedbackSettings=").append(feedbackSettings)
				.append("]");
		return builder.toString();
	}
}
