package br.com.jaera.api.security.config;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(FirebaseProperties.class)
public class FirebaseConfig {

    private final FirebaseProperties properties;

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setProjectId(properties.getProjectId());

            if (isEmulatorMode()) {
                log.info("Firebase Auth running in EMULATOR mode (project: {})", properties.getProjectId());
                optionsBuilder.setCredentials(GoogleCredentials.create(
                        new AccessToken("emulator-fake-token", null)));
            } else {
                log.info("Firebase Auth running in PRODUCTION mode (project: {})", properties.getProjectId());
                optionsBuilder.setCredentials(GoogleCredentials.getApplicationDefault());
            }

            FirebaseApp.initializeApp(optionsBuilder.build());
        }

        return FirebaseAuth.getInstance();
    }

    private boolean isEmulatorMode() {
        String emulatorHost = System.getenv("FIREBASE_AUTH_EMULATOR_HOST");
        return emulatorHost != null && !emulatorHost.isBlank();
    }
}
