package se.sundsvall.feedbacksettings.api.model;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import se.sundsvall.feedbacksettings.ContactMethod;
import se.sundsvall.feedbacksettings.api.validation.ValidDestinationFormat;

@Schema(description = "Requested feedback channel model")
@ValidDestinationFormat
public class RequestedFeedbackChannel {

	@Schema(description = "Method of contact", example = "SMS", requiredMode = REQUIRED)
	@NotNull
	private ContactMethod contactMethod;

	@Schema(description = "Alias for the destination", example = "The caretaker for building A")
	private String alias;

	@Schema(description = "Point of destination", example = "0701234567", requiredMode = REQUIRED)
	private String destination;

	@Schema(type = "boolean", description = "Signal if channel should be used or not when sending feedback ", example = "true", requiredMode = REQUIRED)
	@NotNull
	private Boolean sendFeedback;

	public static RequestedFeedbackChannel create() {
		return new RequestedFeedbackChannel();
	}

	public ContactMethod getContactMethod() {
		return contactMethod;
	}

	public void setContactMethod(final ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
	}

	public RequestedFeedbackChannel withContactMethod(final ContactMethod contactMethod) {
		this.contactMethod = contactMethod;
		return this;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(final String alias) {
		this.alias = alias;
	}

	public RequestedFeedbackChannel withAlias(final String alias) {
		this.alias = alias;
		return this;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(final String destination) {
		this.destination = destination;
	}

	public RequestedFeedbackChannel withDestination(final String destination) {
		this.destination = destination;
		return this;
	}

	public Boolean getSendFeedback() {
		return sendFeedback;
	}

	public void setSendFeedback(final Boolean sendFeedback) {
		this.sendFeedback = sendFeedback;
	}

	public RequestedFeedbackChannel withSendFeedback(final Boolean sendFeedback) {
		this.sendFeedback = sendFeedback;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contactMethod, alias, destination, sendFeedback);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final var other = (RequestedFeedbackChannel) obj;
		return contactMethod == other.contactMethod
			&& Objects.equals(alias, other.alias)
			&& Objects.equals(destination, other.destination)
			&& Objects.equals(sendFeedback, other.sendFeedback);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();
		builder.append("RequestedFeedbackChannel [contactMethod=").append(contactMethod)
			.append(", alias=").append(alias).append(", destination=")
			.append(destination).append(", sendFeedback=").append(sendFeedback).append("]");
		return builder.toString();
	}

}
