package se.sundsvall.feedbacksettings.integration.db;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

@CircuitBreaker(name = "feedbackSettingsRepository")
public interface FeedbackSettingsRepository extends PagingAndSortingRepository<FeedbackSettingEntity, String>, QueryByExampleExecutor<FeedbackSettingEntity> {}
