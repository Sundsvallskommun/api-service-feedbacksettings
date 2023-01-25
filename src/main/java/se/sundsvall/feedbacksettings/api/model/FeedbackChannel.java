package se.sundsvall.feedbacksettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

import java.util.Objects;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.feedbacksettings.ContactMethod;

@Schema(description = "Feedback channel model")
public class FeedbackChannel {

	@Schema(description = "Method of contact", example = "SMS", accessMode = READ_ONLY)
	private ContactMethod contactMethod;

	@Schema(description = "Alias for the destination", accessMode = READ_ONLY)
	private String alias;

	@Schema(description = "Point of destination", accessMode = READ_ONLY)
	private String destination;

	@Schema(description = "Signal if channel should be used or not when sending feedback", example = "true", accessMode = READ_ONLY)
	private boolean sendFeedback;
	
	public static FeedbackChannel create() {
		return new FeedbackChannel();				
	}

	public ContactMethod getContactMethod() {
		return contactMethod;
	}

	public void setContactMethod(ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
	}

	public FeedbackChannel withContactMethod(ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public FeedbackChannel withAlias(String alias) {
		this.alias = alias;
		return this;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public FeedbackChannel withDestination(String destination) {
		this.destination = destination;
		return this;
	}

	public boolean isSendFeedback() {
		return sendFeedback;
	}

	public void setSendFeedback(boolean sendFeedback) {
		this.sendFeedback = sendFeedback;
	}
	
	public FeedbackChannel withSendFeedback(boolean sendFeedback) {
		this.sendFeedback = sendFeedback;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contactMethod, alias, destination, sendFeedback);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FeedbackChannel other = (FeedbackChannel) obj;
		return contactMethod == other.contactMethod
				&& Objects.equals(alias, other.alias)
				&& Objects.equals(destination, other.destination)
				&& sendFeedback == other.sendFeedback;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FeedbackChannel [contactMethod=").append(contactMethod).append(", alias=")
			.append(alias).append(", destination=").append(destination).append(", sendFeedback=")
			.append(sendFeedback).append("]");
		return builder.toString();
	}
}
