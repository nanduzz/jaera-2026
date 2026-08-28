package br.com.jaera.api.security.repository;

import br.com.jaera.api.security.domain.UserRole;
import org.springframework.data.repository.ListCrudRepository;

public interface UserRoleRepository extends ListCrudRepository<UserRole, Long> {
}
