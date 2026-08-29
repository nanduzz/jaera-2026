package br.com.jaera.api.security.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class FirebaseConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(FirebaseConfig.class);

    @AfterEach
    void tearDown() {
        for (FirebaseApp app : FirebaseApp.getApps()) {
            app.delete();
        }
    }

    @Test
    void shouldInitializeFirebaseAuthInEmulatorModeByDefault() {
        contextRunner
                .withPropertyValues(
                        "jaera.firebase.project-id=demo-test-project",
                        "jaera.firebase.emulator-enabled=true",
                        "jaera.firebase.emulator-host=localhost:9099"
                )
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(FirebaseAuth.class);
                    FirebaseAuth auth = context.getBean(FirebaseAuth.class);
                    assertThat(auth).isNotNull();
                });
    }

    @Test
    void shouldInitializeFirebaseAuthWithDemoProjectPrefix() {
        contextRunner
                .withPropertyValues(
                        "jaera.firebase.project-id=demo-no-project"
                )
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(FirebaseAuth.class);
                });
    }
}
