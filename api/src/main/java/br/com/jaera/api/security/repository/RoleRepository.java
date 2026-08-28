package br.com.jaera.api.security.repository;

import br.com.jaera.api.security.domain.Role;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends ListCrudRepository<Role, Long> {

    Optional<Role> findByName(String name);

    @Query("SELECT r.* FROM roles r JOIN user_roles ur ON r.id = ur.role_id WHERE ur.user_id = :userId")
    List<Role> findRolesByUserId(@Param("userId") Long userId);
}
