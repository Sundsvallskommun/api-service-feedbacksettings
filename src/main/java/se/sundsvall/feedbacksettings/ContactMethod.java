package se.sundsvall.feedbacksettings;

import static java.lang.String.format;
import static org.zalando.problem.Status.BAD_REQUEST;

import java.util.stream.Stream;

import org.zalando.problem.Problem;

public enum ContactMethod {
	SMS, EMAIL;
	
	private static final String INVALID_VALUE_ERROR = "Invalid value for enum ContactMethod: %s";
	
	public static ContactMethod toEnum(String value) {
		return Stream.of(ContactMethod.values())
				.filter(method -> method.name().equalsIgnoreCase(value))
				.findAny()
				.orElseThrow(() -> Problem.valueOf(BAD_REQUEST, format(INVALID_VALUE_ERROR, value)));

	}
}
