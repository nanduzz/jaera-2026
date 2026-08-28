package br.com.jaera.api.security;

import br.com.jaera.api.TestcontainersConfiguration;
import br.com.jaera.api.users.domain.User;
import br.com.jaera.api.users.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class SecurityFilterIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FirebaseAuth firebaseAuth;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldAllowAccessToPublicHealthEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andDo(document("health-check"));
    }

    @Test
    void shouldAllowAccessToActuatorHealthWithoutToken() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn401WhenNoTokenIsProvided() throws Exception {
        mockMvc.perform(get("/api/protected-resource"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").exists())
                .andDo(document("error-401"));
    }

    @Test
    void shouldReturn401WhenTokenIsInvalid() throws Exception {
        when(firebaseAuth.verifyIdToken("invalid-token"))
                .thenThrow(new RuntimeException("Token verification failed"));

        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("UNAUTHORIZED"));
    }

    @Test
    void shouldAutoProvisionUserWhenTokenIsValidAndUserDoesNotExist() throws Exception {
        FirebaseToken mockToken = mock(FirebaseToken.class);
        when(mockToken.getUid()).thenReturn("new-uid-123");
        when(mockToken.getEmail()).thenReturn("newuser@jaera.com");
        when(firebaseAuth.verifyIdToken("valid-token")).thenReturn(mockToken);

        // Request a non-existent endpoint with valid auth — should get 404 (auth passed, route not found)
        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isNotFound());

        // Verify user was auto-provisioned
        assertThat(userRepository.findByFirebaseUid("new-uid-123")).isPresent();
        assertThat(userRepository.findByFirebaseUid("new-uid-123").get().getEmail())
                .isEqualTo("newuser@jaera.com");
    }

    @Test
    void shouldReuseExistingUserWhenTokenIsValidAndUserAlreadyExists() throws Exception {
        // Pre-create a user
        userRepository.save(User.builder()
                .username("existing@jaera.com")
                .email("existing@jaera.com")
                .firebaseUid("existing-uid")
                .build());

        FirebaseToken mockToken = mock(FirebaseToken.class);
        when(mockToken.getUid()).thenReturn("existing-uid");
        when(mockToken.getEmail()).thenReturn("existing@jaera.com");
        when(firebaseAuth.verifyIdToken("existing-token")).thenReturn(mockToken);

        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Bearer existing-token"))
                .andExpect(status().isNotFound());

        // Verify no duplicate user was created
        assertThat(userRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldReturn401WhenAuthorizationHeaderHasNoBearerPrefix() throws Exception {
        mockMvc.perform(get("/api/protected-resource")
                        .header("Authorization", "Basic some-credentials"))
                .andExpect(status().isUnauthorized());
    }
}
