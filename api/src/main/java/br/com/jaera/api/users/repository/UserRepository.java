package br.com.jaera.api.users.repository;

import br.com.jaera.api.shared.repository.BaseRepository;
import br.com.jaera.api.users.domain.User;

import java.util.Optional;

/**
 * Repository for the {@link User} aggregate.
 *
 * <p>Inherits standard CRUD operations from {@link BaseRepository} and adds
 * domain-specific query methods for user lookup by unique fields.</p>
 */
public interface UserRepository extends BaseRepository<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByFirebaseUid(String firebaseUid);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
