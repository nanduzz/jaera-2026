package br.com.jaera.api.shared.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

/**
 * Base entity providing common auditing fields for all domain aggregates.
 *
 * <p>Auditing is handled automatically by Spring Data JDBC via {@code @EnableJdbcAuditing}.
 * The {@code createdBy}/{@code updatedBy} fields are populated by a
 * {@link br.com.jaera.api.shared.audit.CurrentAuditorProvider} implementation.</p>
 *
 * <p><strong>Important:</strong> Never create a new instance with a pre-set {@code id} — this
 * breaks Spring Data JDBC's new-aggregate detection and causes auditing fields to be skipped.</p>
 */
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    private Long id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;
}
