# Fundação de Persistência (BaseEntity / BaseRepository / User)

> Spec de apoio à implementação por agente de IA. Combine esta leitura com o `AGENTS.md` do
> projeto (convenções gerais, GitFlow, i18n, regras de domínio). Esta spec descreve **apenas**
> a fundação de persistência — não abrange controllers, serviços de aplicação, nem integração
> real com Spring Security / Firebase Auth.

## 1. Visão geral

O projeto é um **monólito modular** (Spring Boot 4.x + Java 25) cuja persistência usa
**Spring Data JDBC** (sem JPA/Hibernate) sobre **PostgreSQL**, com migrações **Liquibase** e
testes de integração via **Testcontainers**.

A fundação de persistência é o conjunto de classes e convenções compartilhadas por todos os
domínios:

- `BaseEntity` — entidade base com campos de auditoria.
- `BaseRepository<T>` — repositório base genérico com CRUD padrão.
- Suporte de auditoria (`@EnableJdbcAuditing` + `CurrentAuditorProvider` mockado).
- `User` / `UserRepository` — primeiro agregado concreto, servindo de referência para os demais.
- Migração Liquibase da tabela `users`.

Regras gerais obrigatórias (do `AGENTS.md`):

- Código 100% em **inglês** (classes, métodos, logs, mensagens).
- **Lombok** em vez de `record`. IDs numéricos (`Long`), nunca UUID.
- Trabalho somente em branches `feat/*`; commits e PRs seguem GitFlow (PR only para `develop`).

## 2. BaseEntity

Pacote: `br.com.jaera.api.shared.domain`

```java
@Getter
@Setter
public abstract class BaseEntity {

    @Id
    private Long id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    private Long createdBy;

    @LastModifiedBy
    private Long updatedBy;
}
```

Contrato:

- Toda entidade de agregado **estende** `BaseEntity`.
- Campos de auditoria são preenchidos automaticamente pelo Spring Data JDBC (ver seção 3).
- `createdAt` / `updatedAt` usam `java.time.Instant` (UTC no backend).
- `createdBy` / `updatedBy` são `Long` (ID do usuário logado).

**Regra crítica:** nunca instancie uma entidade nova com `id` pré-definido. O Spring Data JDBC
usa a presença de `id` para decidir se o agregado é novo; um `id` setado falsamente faz a
detecção de "novo agregado" falhar e os campos de auditoria deixam de ser populados.

## 3. Auditoria (createdBy / updatedBy)

Mecanismo escolhido: **auditoria nativa do Spring Data JDBC**, não callbacks manuais.

- `AuditingConfig` (`br.com.jaera.api.shared.config`) é anotada com `@Configuration`
  `@EnableJdbcAuditing`. Spring Boot **não** auto-configura auditoria para JDBC; esta classe
  registra o `IsNewAwareAuditingHandler` e os callbacks que populam `@CreatedDate`,
  `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`.
- O "usuário atual" é resolvido por um bean que implementa `AuditorAware<Long>`.

Costura mock → Spring Security (ponte de troca trivial):

- `CurrentAuditorProvider` (`br.com.jaera.api.shared.audit`) é uma **interface ponte** que
  estende `AuditorAware<Long>`. Ela desacopla a aplicação do Spring Data diretamente, permitindo
  trocar a implementação sem tocar nas entidades nem no `AuditingConfig`.
- `MockedAuditorProvider` é a implementação ativa nesta fase. Retorna `Optional.of(0L)`
  ("system user"). Campos populados com `0L` na fase de mock.

```java
public interface CurrentAuditorProvider extends AuditorAware<Long> { }

@Component
public class MockedAuditorProvider implements CurrentAuditorProvider {
    private static final Long SYSTEM_USER_ID = 0L;
    @Override
    public Optional<Long> getCurrentAuditor() {
        // TODO: Replace with SecurityContextHolder lookup when Firebase Auth is implemented.
        return Optional.of(SYSTEM_USER_ID);
    }
}
```

Ativação futura (quando Spring Security / Firebase Auth entrar):

- Criar `SecurityContextAuditorProvider implements CurrentAuditorProvider` que lê o ID do
  usuário autenticado do `SecurityContextHolder`.
- Trocar o `@Component` ativo (ou qualificar via perfil) — **não** mexer em `BaseEntity` nem em
  `AuditingConfig`.

Nullability no banco durante a fase de mock: as colunas `created_by` / `updated_by` são
`NOT NULL DEFAULT 0`, então a auditoria mockada nunca deixa nulo.

## 4. BaseRepository

Pacote: `br.com.jaera.api.shared.repository`

```java
@NoRepositoryBean
public interface BaseRepository<T> extends ListCrudRepository<T, Long> {
}
```

Decisão e justificativa:

- **Interface genérica + `@NoRepositoryBean`** é o caminho idiomático do Spring Data. A anotação
  impede que o Spring tente instanciar um repositório concreto para o tipo genérico `T`.
- Estende `ListCrudRepository` (Spring Data 3+) para que `findAll()`, `saveAll()`, etc. retornem
  `List` em vez de `Iterable` — mais ergonômico para paginação/cursor.
- **Não** usar classe abstrata sobre `JdbcTemplate`: quebraria o preenchimento automático de
  auditoria (os callbacks de auditoria só rodam via `CrudRepository`/`ListCrudRepository`).
- **Não** usar `repositoryBaseClass` ou fragments nesta fase — só sob demanda real de lógica
  customizada compartilhada.

Métodos padrão garantidos pela fundação (via `ListCrudRepository`): `save`, `saveAll`,
`findById`, `findAll`, `existsById`, `deleteById`, `delete`, `deleteAll`, `count`.
Cada domínio adiciona queries via derived queries ou `@Query`.

## 5. User / UserRepository (exemplo concreto)

Domínio `users`: `br.com.jaera.api.users.domain` / `.repository`.

```java
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table("users")
public class User extends BaseEntity {
    private String username;
    private String email;
    private String firebaseUid;
}

public interface UserRepository extends BaseRepository<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByFirebaseUid(String firebaseUid);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

Contrato de `User`:

- Atributos próprios: `username`, `email`, `firebaseUid` (herda `id` + campos de auditoria).
- `username`, `email`, `firebaseUid` são únicos (restrições no banco — ver seção 6).
- `firebaseUid` é **anulável** (pode não haver vínculo Firebase ainda); múltiplos `NULL` são
  permitidos pela restrição unique do PostgreSQL.

Queries extras exigidas e já cobertas por teste: busca por `username`, `email`, `firebaseUid`
(retornam `Optional`) e verificação de existência por `username`/`email`.

## 6. Convenções Liquibase

- Scripts em **formato `.sql`** (não XML/YAML), sob
  `src/main/resources/db/changelog/migrations/`.
- Todo script `.sql` inicia com `--liquibase formatted sql` e um `--changeset` com
  `author:YYYYMMDD-NNN-descrição`.
- Todo script **deve conter instruções de rollback** (`--rollback ...`).
- Inclusão obrigatória no **master changelog**
  (`src/main/resources/db/changelog/db.changelog-master.yaml`), que controla a ordem de execução:

```yaml
databaseChangeLog:
  - include:
      file: db/changelog/migrations/20260826-001-create-users-table.sql
```

Tabela `users` (migração `20260826-001-create-users-table.sql`):

```sql
CREATE TABLE users (
    id           BIGSERIAL      PRIMARY KEY,
    username     VARCHAR(50)    NOT NULL,
    email        VARCHAR(255)   NOT NULL,
    firebase_uid VARCHAR(128)   NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by   BIGINT         NOT NULL DEFAULT 0,
    updated_by   BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT uq_users_username     UNIQUE (username),
    CONSTRAINT uq_users_email        UNIQUE (email),
    CONSTRAINT uq_users_firebase_uid UNIQUE (firebase_uid)
);

--rollback DROP TABLE users;
```

## 7. Estrutura de pacotes

Monólito modular organizado por **domínio → camada**:

```
br.com.jaera.api
├── shared                  # código compartilhado da fundação (cross-domain)
│   ├── domain              # BaseEntity
│   ├── repository          # BaseRepository
│   ├── audit               # CurrentAuditorProvider, MockedAuditorProvider
│   └── config              # AuditingConfig
└── <domain>                # ex.: users
    ├── domain              # User (entidade / agregado)
    ├── repository          # UserRepository
    ├── controller          # (fora de escopo nesta fase)
    └── service             # (fora de escopo nesta fase)
```

Regras de fronteira entre domínios:

- Domínios podem depender de `shared.*` livremente.
- Um domínio **não** deve importar classes de camada interna de outro domínio (ex.: `orders`
  não importa `users.repository`). Comunicação entre domínios ocorre por suas interfaces
  públicas (repositórios/serviços), preservando o caminho para microsserviços.
- Testes espelham a estrutura: `src/test/java/br/com/jaera/api/<domain>/repository/...`.

## 8. Estratégia de testes

- **Teste de integração** obrigatório para cada novo domínio, via `@SpringBootTest` +
  Testcontainers (PostgreSQL). A configuração de container fica em
  `br.com.jaera.api.TestcontainersConfiguration` (`@TestConfiguration` + `@ServiceConnection`).
- Nomenclatura: `should[Action]When[Condition]()`.
- Cobertura mínima da fundação (`UserRepositoryIntegrationTest`):
  - `shouldSaveUserWhenValidDataIsProvided`
  - `shouldPopulateAuditFieldsWhenUserIsSaved` (inclui `createdBy`/`updatedBy == 0L`)
  - `shouldUpdateUpdatedAtWhenUserIsModified` (e não alterar `createdAt`)
  - `shouldFindUserByUsernameWhenUserExists` / `...ByEmail` / `...ByFirebaseUid`
  - `shouldReturnEmptyWhenUserDoesNotExistByUsername`
  - `shouldReturnTrueWhenUserExistsByUsername` / `shouldReturnFalseWhenUserDoesNotExistByEmail`
- **RestDocs** fica para a fase de endpoints REST (fora de escopo aqui).
- Atenção: `TestcontainersConfiguration` **deve ser `public`** para ser importado por testes em
  outros pacotes.

## Fora de escopo

- Controllers/services de `User` e padrões de camada de aplicação.
- Error handling global / i18n / endpoints REST.
- Integração real com Spring Security / Firebase Auth (apenas mock + TODO na auditoria).
