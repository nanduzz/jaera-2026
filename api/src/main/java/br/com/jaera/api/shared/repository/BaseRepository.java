package br.com.jaera.api.shared.repository;

import br.com.jaera.api.shared.domain.BaseEntity;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository interface providing standard CRUD operations for all domain aggregates.
 *
 * <p>Extends {@link ListCrudRepository} (Spring Data 3+) so that {@code findAll()},
 * {@code saveAll()}, etc. return {@code List} instead of {@code Iterable}.</p>
 *
 * <p>Domain repositories should extend this interface and add domain-specific
 * query methods via Spring Data derived queries or {@code @Query} annotations.</p>
 *
 * @param <T> the aggregate root type, must extend {@link BaseEntity}
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity> extends ListCrudRepository<T, Long> {
}
