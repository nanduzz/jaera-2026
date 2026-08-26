package br.com.jaera.api.shared.audit;

import org.springframework.data.domain.AuditorAware;

/**
 * Bridge interface for resolving the current auditor (user ID).
 *
 * <p>Decouples the application from Spring Data's {@link AuditorAware} directly,
 * allowing a seamless swap from the current mock implementation to a real
 * {@code SecurityContextHolder}-based provider when Firebase Auth / Spring Security
 * is implemented.</p>
 *
 * @see MockedAuditorProvider
 */
public interface CurrentAuditorProvider extends AuditorAware<Long> {
}
