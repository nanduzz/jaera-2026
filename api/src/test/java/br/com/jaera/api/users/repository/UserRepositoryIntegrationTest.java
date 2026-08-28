package br.com.jaera.api.users.repository;

import br.com.jaera.api.TestcontainersConfiguration;
import br.com.jaera.api.users.domain.User;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UserRepositoryIntegrationTest {

    @MockitoBean
    private FirebaseAuth firebaseAuth;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveUserWhenValidDataIsProvided() {
        User user = User.builder()
                .username("testuser")
                .email("test@jaera.com")
                .firebaseUid("firebase-uid-123")
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@jaera.com");
        assertThat(savedUser.getFirebaseUid()).isEqualTo("firebase-uid-123");
    }

    @Test
    void shouldPopulateAuditFieldsWhenUserIsSaved() {
        User user = User.builder()
                .username("audituser")
                .email("audit@jaera.com")
                .firebaseUid("firebase-uid-audit")
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();
        assertThat(savedUser.getCreatedBy()).isEqualTo(0L);
        assertThat(savedUser.getUpdatedBy()).isEqualTo(0L);
    }

    @Test
    void shouldUpdateUpdatedAtWhenUserIsModified() {
        User user = User.builder()
                .username("moduser")
                .email("mod@jaera.com")
                .build();
        User savedUser = userRepository.save(user);
        var originalCreatedAt = savedUser.getCreatedAt();
        var originalUpdatedAt = savedUser.getUpdatedAt();

        savedUser.setUsername("modified-username");
        User updatedUser = userRepository.save(savedUser);

        assertThat(updatedUser.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(updatedUser.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    @Test
    void shouldFindUserByUsernameWhenUserExists() {
        userRepository.save(User.builder()
                .username("findme")
                .email("findme@jaera.com")
                .build());

        Optional<User> found = userRepository.findByUsername("findme");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("findme@jaera.com");
    }

    @Test
    void shouldFindUserByEmailWhenUserExists() {
        userRepository.save(User.builder()
                .username("emailuser")
                .email("email@jaera.com")
                .build());

        Optional<User> found = userRepository.findByEmail("email@jaera.com");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("emailuser");
    }

    @Test
    void shouldFindUserByFirebaseUidWhenUserExists() {
        userRepository.save(User.builder()
                .username("fbuser")
                .email("fb@jaera.com")
                .firebaseUid("uid-123")
                .build());

        Optional<User> found = userRepository.findByFirebaseUid("uid-123");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("fbuser");
    }

    @Test
    void shouldReturnEmptyWhenUserDoesNotExistByUsername() {
        Optional<User> found = userRepository.findByUsername("nonexistent");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenUserExistsByUsername() {
        userRepository.save(User.builder()
                .username("exists")
                .email("exists@jaera.com")
                .build());

        assertThat(userRepository.existsByUsername("exists")).isTrue();
    }

    @Test
    void shouldReturnFalseWhenUserDoesNotExistByEmail() {
        assertThat(userRepository.existsByEmail("nope@jaera.com")).isFalse();
    }
}

