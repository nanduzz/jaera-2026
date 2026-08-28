package br.com.jaera.api.users.repository;

import br.com.jaera.api.shared.repository.BaseRepository;
import br.com.jaera.api.users.domain.User;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByFirebaseUid(String firebaseUid);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
