package br.com.jaera.api.shared.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;

/**
 * Enables Spring Data JDBC auditing support.
 *
 * <p>Spring Boot does <strong>not</strong> auto-configure auditing for Spring Data JDBC.
 * This configuration registers the {@code IsNewAwareAuditingHandler} and entity callbacks
 * that populate {@code @CreatedDate}, {@code @LastModifiedDate}, {@code @CreatedBy},
 * and {@code @LastModifiedBy} fields in {@link br.com.jaera.api.shared.domain.BaseEntity}.</p>
 *
 * <p>The auditor (user identity) is resolved via the
 * {@link br.com.jaera.api.shared.audit.CurrentAuditorProvider} bean.</p>
 */
@Configuration
@EnableJdbcAuditing
public class AuditingConfig {
}
