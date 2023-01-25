package se.sundsvall.feedbacksettings.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanEquals;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanHashCode;
import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanToString;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class UpdateFeedbackSettingRequestTest {

	private static final List<RequestedFeedbackFilter> FILTERS = List.of(RequestedFeedbackFilter.create());
	private static final List<RequestedFeedbackChannel> CHANNELS = List.of(RequestedFeedbackChannel.create());
	
	@Test
	void testBean() {
		assertThat(UpdateFeedbackSettingRequest.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters(),
			hasValidBeanHashCode(),
			hasValidBeanEquals(),
			hasValidBeanToString()));
	}
	
	@Test
	void testCreatePattern() {
		UpdateFeedbackSettingRequest request = UpdateFeedbackSettingRequest.create()
				.withChannels(CHANNELS)
				.withFilters(FILTERS);
		
		assertThat(request.getChannels()).isEqualTo(CHANNELS);
		assertThat(request.getFilters()).isEqualTo(FILTERS);
	}
	
	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(UpdateFeedbackSettingRequest.create()).hasAllNullFieldsOrProperties();
		assertThat(new UpdateFeedbackSettingRequest()).hasAllNullFieldsOrProperties();
	}
}
