package se.sundsvall.feedbacksettings.api;

import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import se.sundsvall.dept44.common.validators.annotation.ValidUuid;
import se.sundsvall.feedbacksettings.api.model.CreateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.api.model.FeedbackSetting;
import se.sundsvall.feedbacksettings.api.model.SearchResult;
import se.sundsvall.feedbacksettings.api.model.UpdateFeedbackSettingRequest;
import se.sundsvall.feedbacksettings.service.FeedbackSettingsService;

@RestController
@Validated
@RequestMapping("/settings")
@Tag(name = "FeedbackSettings", description = "Feedback settings")
public class FeedbackSettingsResource {

	@Autowired
	private FeedbackSettingsService feedbackSettingsService;

	@PostMapping(consumes = APPLICATION_JSON_VALUE, produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Create feedback setting")
	@ApiResponse(responseCode = "201", headers = @Header(name = LOCATION, schema = @Schema(type = "string")), description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeedbackSetting.class)))
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<FeedbackSetting> createFeedbackSetting(UriComponentsBuilder uriComponentsBuilder, @NotNull @Valid @RequestBody CreateFeedbackSettingRequest body) {

		FeedbackSetting setting = feedbackSettingsService.createFeedbackSetting(body);
		return created(uriComponentsBuilder.path("/settings/{id}").buildAndExpand(setting.getId()).toUri()).body(setting);
	}

	@PatchMapping(path = "/{id}", consumes = APPLICATION_JSON_VALUE, produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Update feedback setting")
	@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeedbackSetting.class)))
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<FeedbackSetting> updateFeedbackSetting(
		@Parameter(name = "id", description = "Feedback setting ID", example = "81471222-5798-11e9-ae24-57fa13b361e1") @ValidUuid @PathVariable(name = "id", required = true) String id,
		@NotNull @Valid @RequestBody UpdateFeedbackSettingRequest body) {

		return ok(feedbackSettingsService.updateFeedbackSetting(id, body));
	}

	@DeleteMapping(path = "/{id}", produces = APPLICATION_PROBLEM_JSON_VALUE)
	@Operation(summary = "Delete feedback setting")
	@ApiResponse(responseCode = "204", description = "Successful operation")
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<Void> deleteFeedbackSetting(
		@Parameter(name = "id", description = "Feedback setting ID", example = "81471222-5798-11e9-ae24-57fa13b361e1") @ValidUuid @PathVariable(name = "id", required = true) String id) {

		feedbackSettingsService.deleteFeedbackSetting(id);
		return noContent().build();
	}

	@GetMapping(path = "/{id}", produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Get feedback setting by ID")
	@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = FeedbackSetting.class)))
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<FeedbackSetting> getFeedbackSettingById(
		@Parameter(name = "id", description = "Feedback setting ID", example = "81471222-5798-11e9-ae24-57fa13b361e1") @ValidUuid @PathVariable(name = "id", required = true) String id) {

		return ResponseEntity.ok(feedbackSettingsService.getFeedbackSettingById(id));
	}

	@GetMapping(produces = { APPLICATION_JSON_VALUE, APPLICATION_PROBLEM_JSON_VALUE })
	@Operation(summary = "Query feedback settings", description = """
		Query for feedback settings matching sent in person- and/or organizationId. Returned feedback settings contains a matching percent, which is
		100% if request is not extended with filters (i.e. without filters, all returned posts matches incoming parameters to 100%).<br><br>

		If needed, the client can extend the request to include filters. When filters are present they will be taken into account and matched against
		the  filters on the returned feedback settings when calculating matching percent. All posts matching sent in person- and/or organizationId will
		still be returned, but with a calculated matching percent that is based on how filters for the feedback settings posts matches sent in filters.
		<br><br>

		Filters are sent as header values. The convention of the header name is <code>x-filter-[filterkey]</code>. It is possible to include multiple values
		for a filter by sending multiple headers with same filter name and different values for each entry, see example below.<br><br>

		<pre>
		-H 'x-filter-categories: broadband'
		-H 'x-filter-categories: electricity'</pre>
		""")
	@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON_VALUE, schema = @Schema(implementation = SearchResult.class)))
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = { Problem.class, ConstraintViolationProblem.class })))
	@ApiResponse(responseCode = "500", description = "Internal Server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	public ResponseEntity<SearchResult> getFeedbackSettingsByQuery(
		@RequestHeader HttpHeaders headers,
		@Parameter(name = "personId", description = "Person id", example = "15aee472-46ab-4f03-9605-68bd64ebc71a") @RequestParam(value = "personId", required = false) @ValidUuid(nullable = true) String personId,
		@Parameter(name = "organizationId", description = "Organization id", example = "15aee472-46ab-4f03-9605-68bd64ebc84a") @RequestParam(value = "organizationId", required = false) @ValidUuid(nullable = true) String organizationId,
		@Parameter(name = "page", description = "Page number", example = "1") @RequestParam(value = "page", defaultValue = "1") @Min(1) int page,
		@Parameter(name = "limit", description = "Result size per page", example = "20") @RequestParam(value = "limit", defaultValue = "20") @Min(1) @Max(100) int limit) {

		return ok(feedbackSettingsService.getFeedbackSettings(headers, personId, organizationId, page, limit));
	}
}
