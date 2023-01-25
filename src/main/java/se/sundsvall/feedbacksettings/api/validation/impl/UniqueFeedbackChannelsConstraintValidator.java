package se.sundsvall.feedbacksettings.api.validation.impl;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.ObjectUtils.allNotNull;
import static reactor.util.function.Tuples.of;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import se.sundsvall.feedbacksettings.api.model.RequestedFeedbackChannel;
import se.sundsvall.feedbacksettings.api.validation.UniqueFeedbackChannels;

public class UniqueFeedbackChannelsConstraintValidator implements ConstraintValidator<UniqueFeedbackChannels, Collection<RequestedFeedbackChannel>> {

	@Override
	public boolean isValid(final Collection<RequestedFeedbackChannel> value, final ConstraintValidatorContext context) {
		Collection<RequestedFeedbackChannel> nonEmptyElements = ofNullable(value).orElse(Collections.emptyList())
			.stream()
			.filter(containsData())
			.toList();

		long distictElements = nonEmptyElements.stream()
			.map(channel -> of(channel.getContactMethod(), channel.getDestination()))
			.distinct()
			.count();

		return distictElements == nonEmptyElements.size();
	}

	private static Predicate<RequestedFeedbackChannel> containsData() {
		return p -> allNotNull(p, p.getContactMethod(), p.getDestination());
	}
}
