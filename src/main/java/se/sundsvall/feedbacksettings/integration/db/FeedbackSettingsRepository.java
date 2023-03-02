package se.sundsvall.feedbacksettings.integration.db;

import javax.transaction.Transactional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import se.sundsvall.feedbacksettings.integration.db.model.FeedbackSettingEntity;

@Transactional
@CircuitBreaker(name = "feedbackSettingsRepository")
public interface FeedbackSettingsRepository extends PagingAndSortingRepository<FeedbackSettingEntity, String>, QueryByExampleExecutor<FeedbackSettingEntity> {}
