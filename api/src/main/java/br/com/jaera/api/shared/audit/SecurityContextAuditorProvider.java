package br.com.jaera.api.shared.audit;

import br.com.jaera.api.security.domain.JaeraPrincipal;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!no-security")
public class SecurityContextAuditorProvider implements CurrentAuditorProvider {

    private static final Long SYSTEM_USER_ID = 0L;

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.of(SYSTEM_USER_ID);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof JaeraPrincipal jaeraPrincipal) {
            return Optional.of(jaeraPrincipal.getId());
        }

        return Optional.of(SYSTEM_USER_ID);
    }
}
