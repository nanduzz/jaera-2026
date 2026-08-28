package br.com.jaera.api.users.domain;

import br.com.jaera.api.shared.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents an application user.
 *
 * <p>This is the first concrete aggregate root, serving as the reference implementation
 * for all future domain entities that extend {@link BaseEntity}.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("users")
public class User extends BaseEntity {

    private String username;

    private String email;

    private String firebaseUid;
}
