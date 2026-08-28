package br.com.jaera.api.shared.audit;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("no-security")
public class MockedAuditorProvider implements CurrentAuditorProvider {

    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.of(SYSTEM_USER_ID);
    }
}
