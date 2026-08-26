# Mapa Wayfinder: Fundação de Persistência (BaseEntity / BaseRepository / User)

Label conceitual: `wayfinder:map`

## Destination

Spec em português em `docs/specs/base-persistence.md` descrevendo a fundação de persistência (BaseEntity, BaseRepository, User, UserRepository) + infraestrutura de suporte (organização de pacotes do monólito modular, convenções Liquibase, estratégia de testes), incluindo a decisão de auditoria (`createdBy`/`updatedBy` mockados com TODO até existir Spring Security). Pronta para guiar implementação futura por agente de IA. Sem código neste esforço.

## Notes

- **Domínio:** Java 25 / Spring Boot 4.x / Spring Data JDBC (sem JPA/Hibernate) / Liquibase / PostgreSQL / Testcontainers / Lombok (sem records) / IDs numéricos (`Long`), sem UUID.
- **Skills a consultar:** `/grilling` + `/domain-modeling` para tickets HITL; `/research` para tickets AFK.
- **Preferências fixadas pelo dev:**
  - Spec em português, como arquivo no repo (`docs/specs/base-persistence.md`).
  - Scripts Liquibase em **formato `.sql`**, todos incluídos no **master changelog** para controlar a ordem de execução, e todo script `.sql` deve conter **instruções de rollback**.
  - `createdBy`/`updatedBy` mockados com comentários TODO até a implementação de Spring Security (futura leitura do Security Context).
- **Convenções gerais:** ver AGENTS.md (código 100% inglês, GitFlow — trabalho só em branches `feat/*`, PRs apenas).

## Decisions so far

<!-- uma linha por ticket fechado: [título](caminho) — gist da resposta -->

- [Research: Padrões de herança de repositório no Spring Data JDBC](issues/01-research-repository-inheritance.md) — interface genérica `BaseRepository<T> extends ListCrudRepository<T, Long>` com `@NoRepositoryBean` é o caminho idiomático; DAO abstrato sobre JdbcTemplate quebra auditing; `repositoryBaseClass`/fragments só sob demanda real.
- [Research: Auditoria nativa do Spring Data JDBC](issues/02-research-jdbc-auditing.md) — `@EnableJdbcAuditing` + anotações herdadas em BaseEntity funcionam; `AuditorAware<Long>` atrás de interface ponte própria facilita o mock→Security Context; pegadinha: id pré-setado em entidade nova quebra detecção de novo agregado.
- [Convenções Liquibase](issues/05-liquibase-conventions.md) — formato `.sql`, inclusão obrigatória no master changelog p/ ordenação e rollback instructions em todo script (fixado pelo dev na sessão de charting; ticket detalha naming/organização/tabela users).

## Not yet specified

- Estrutura concreta dos testes de integração Testcontainers (depende das decisões de repositório base, auditoria e Liquibase).
- Contrato da costura mock→Spring Security para auditoria (depende da decisão de auditoria).

## Out of scope

- Controllers/services base e padrões de camada de aplicação.
- Integração real com Spring Security / Firebase Auth (apenas mock + TODO na auditoria).
- Error handling global / i18n / endpoints REST de User.

## Tickets (índice)

- [01-research-repository-inheritance](issues/01-research-repository-inheritance.md)
- [02-research-jdbc-auditing](issues/02-research-jdbc-auditing.md)
- [03-baserepository-design](issues/03-baserepository-design.md)
- [04-auditing-mechanics](issues/04-auditing-mechanics.md)
- [05-liquibase-conventions](issues/05-liquibase-conventions.md)
- [06-package-organization](issues/06-package-organization.md)
- [07-user-contract-and-tests](issues/07-user-contract-and-tests.md)
- [08-write-spec-doc](issues/08-write-spec-doc.md)
