package br.com.jaera.api.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jaera.firebase")
public class FirebaseProperties {

    /**
     * Firebase project ID. Defaults to emulator project.
     */
    private String projectId = "demo-no-project";

    /**
     * Whether to use the Firebase Auth emulator. Defaults to true.
     */
    private boolean emulatorEnabled = true;

    /**
     * Host and port for Firebase Auth Emulator. Defaults to localhost:9099.
     */
    private String emulatorHost = "localhost:9099";

    /**
     * Path to service account JSON credentials file for production.
     */
    private String credentialsPath;
}
