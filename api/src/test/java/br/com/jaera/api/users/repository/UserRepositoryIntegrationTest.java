package br.com.jaera.api.users.repository;

import br.com.jaera.api.TestcontainersConfiguration;
import br.com.jaera.api.users.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveUserWhenValidDataIsProvided() {
        User user = User.builder()
                .username("johndoe")
                .email("john@example.com")
                .firebaseUid("firebase-123")
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("johndoe", saved.getUsername());
        assertEquals("john@example.com", saved.getEmail());
        assertEquals("firebase-123", saved.getFirebaseUid());
    }

    @Test
    void shouldPopulateAuditFieldsWhenUserIsSaved() {
        User user = User.builder()
                .username("audituser")
                .email("audit@example.com")
                .build();

        User saved = userRepository.save(user);

        assertNotNull(saved.getCreatedAt(), "createdAt should be populated by auditing");
        assertNotNull(saved.getUpdatedAt(), "updatedAt should be populated by auditing");
        assertNotNull(saved.getCreatedBy(), "createdBy should be populated by auditing");
        assertNotNull(saved.getUpdatedBy(), "updatedBy should be populated by auditing");
        assertEquals(0L, saved.getCreatedBy(), "createdBy should be 0 (mock system user)");
        assertEquals(0L, saved.getUpdatedBy(), "updatedBy should be 0 (mock system user)");
    }

    @Test
    void shouldUpdateUpdatedAtWhenUserIsModified() throws InterruptedException {
        User user = User.builder()
                .username("updateuser")
                .email("update@example.com")
                .build();

        User saved = userRepository.save(user);
        var originalUpdatedAt = saved.getUpdatedAt();

        // Small delay to ensure a different timestamp
        Thread.sleep(50);

        saved.setUsername("updateduser");
        User updated = userRepository.save(saved);

        assertNotNull(updated.getUpdatedAt());
        assertTrue(updated.getUpdatedAt().isAfter(originalUpdatedAt),
                "updatedAt should be after the original");
        assertEquals(saved.getCreatedAt(), updated.getCreatedAt(),
                "createdAt should not change on update");
    }

    @Test
    void shouldFindUserByUsernameWhenUserExists() {
        User user = User.builder()
                .username("findme")
                .email("findme@example.com")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("findme");

        assertTrue(found.isPresent());
        assertEquals("findme", found.get().getUsername());
    }

    @Test
    void shouldFindUserByEmailWhenUserExists() {
        User user = User.builder()
                .username("emailuser")
                .email("unique@example.com")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("unique@example.com");

        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getEmail());
    }

    @Test
    void shouldFindUserByFirebaseUidWhenUserExists() {
        User user = User.builder()
                .username("fbuser")
                .email("fbuser@example.com")
                .firebaseUid("fb-uid-456")
                .build();
        userRepository.save(user);

        Optional<User> found = userRepository.findByFirebaseUid("fb-uid-456");

        assertTrue(found.isPresent());
        assertEquals("fb-uid-456", found.get().getFirebaseUid());
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExistByUsername() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    void shouldReturnTrueWhenUserExistsByUsername() {
        User user = User.builder()
                .username("existsuser")
                .email("exists@example.com")
                .build();
        userRepository.save(user);

        assertTrue(userRepository.existsByUsername("existsuser"));
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        assertFalse(userRepository.existsByEmail("ghost@example.com"));
    }
}

