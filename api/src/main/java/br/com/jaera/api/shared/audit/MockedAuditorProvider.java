package br.com.jaera.api.shared.audit;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Mock implementation of {@link CurrentAuditorProvider} that always returns {@code 0L}
 * as the current auditor (representing a "system" user).
 *
 * <p>This is a placeholder until Firebase Auth / Spring Security integration is implemented.
 * The future real implementation will read the authenticated user ID from the
 * {@code SecurityContextHolder}.</p>
 */
@Component
public class MockedAuditorProvider implements CurrentAuditorProvider {

    /**
     * System user ID used as a placeholder for auditing fields.
     */
    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    public Optional<Long> getCurrentAuditor() {
        // TODO: Replace with SecurityContextHolder lookup when Firebase Auth is implemented.
        //       Future implementation: SecurityContextAuditorProvider reading from SecurityContext.
        return Optional.of(SYSTEM_USER_ID);
    }
}
