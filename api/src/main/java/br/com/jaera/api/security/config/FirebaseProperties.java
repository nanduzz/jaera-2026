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
}
