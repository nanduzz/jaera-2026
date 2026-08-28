package br.com.jaera.api.shared.audit;

import org.springframework.data.domain.AuditorAware;

public interface CurrentAuditorProvider extends AuditorAware<Long> {
}
