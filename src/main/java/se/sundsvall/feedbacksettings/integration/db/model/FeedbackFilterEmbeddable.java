package se.sundsvall.feedbacksettings.integration.db.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class FeedbackFilterEmbeddable implements Serializable {

	private static final long serialVersionUID = -6098646981404770015L;

	@Column(name = "`key`", nullable = false)
	private String key;

	@Column(name = "value", nullable = false)
	private String value;

	public static FeedbackFilterEmbeddable create() {
		return new FeedbackFilterEmbeddable();
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public FeedbackFilterEmbeddable withKey(String key) {
		this.key = key;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public FeedbackFilterEmbeddable withValue(String value) {
		this.value = value;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(key, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedbackFilterEmbeddable other = (FeedbackFilterEmbeddable) obj;
		return Objects.equals(key, other.key) && Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeedbackFilterEmbeddable [key=").append(key).append(", value=").append(value).append("]");
		return builder.toString();
	}
}
