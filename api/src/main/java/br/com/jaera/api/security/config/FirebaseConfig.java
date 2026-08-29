package br.com.jaera.api.security.config;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.internal.FirebaseProcessEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
                String emulatorHost = getEmulatorHost();
                log.info("Firebase Auth running in EMULATOR mode (host: {}, project: {})", emulatorHost, properties.getProjectId());
                FirebaseProcessEnvironment.setenv("FIREBASE_AUTH_EMULATOR_HOST", emulatorHost);
                optionsBuilder.setCredentials(GoogleCredentials.create(
                        new AccessToken("emulator-fake-token", null)));
            } else {
                log.info("Firebase Auth running in PRODUCTION mode (project: {})", properties.getProjectId());
                optionsBuilder.setCredentials(loadProductionCredentials());
            }

            FirebaseApp.initializeApp(optionsBuilder.build());
        }

        return FirebaseAuth.getInstance();
    }

    private boolean isEmulatorMode() {
        if (StringUtils.hasText(System.getenv("FIREBASE_AUTH_EMULATOR_HOST"))) {
            return true;
        }
        if (properties.isEmulatorEnabled()) {
            return true;
        }
        return properties.getProjectId() != null && properties.getProjectId().startsWith("demo-");
    }

    private String getEmulatorHost() {
        String envHost = System.getenv("FIREBASE_AUTH_EMULATOR_HOST");
        if (StringUtils.hasText(envHost)) {
            return envHost;
        }
        if (StringUtils.hasText(properties.getEmulatorHost())) {
            return properties.getEmulatorHost();
        }
        return "localhost:9099";
    }

    private GoogleCredentials loadProductionCredentials() throws IOException {
        if (StringUtils.hasText(properties.getCredentialsPath())) {
            try (InputStream is = new FileInputStream(properties.getCredentialsPath())) {
                return GoogleCredentials.fromStream(is);
            }
        }
        return GoogleCredentials.getApplicationDefault();
    }
}
