# Research: Auditoria nativa do Spring Data JDBC (@CreatedDate / AuditorAware)

Part of map: `.scratch/base-persistence/map.md`
Type: research
Status: resolved
Blocked by: —

## Question

Quais as capacidades nativas de auditoria do Spring Data JDBC (versão usada pelo Spring Boot 4.x)?

1. Anotações `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedDate` em campos herdados de uma superclasse (`BaseEntity`) funcionam? Requisitos (`@EnableJdbcAuditing`, `@EntityCallback`, new-aggregate detection via `@Version` ou `Persistable`)?
2. Como popular `createdBy`/`updatedBy` via `AuditorAware` — assinatura, registro via `@EnableJdbcAuditing(auditorAwareRef = ...)`, comportamento quando retorna vazio?
3. Alternativas sem auditoria nativa (callbacks `BeforeSaveCallback`, `AfterLoadCallback`) — prós/contras para campos de auditoria numa base class comum.
4. Comportamento com campos nulos em inserts (colunas nullable?) e impacto em testes Testcontainers.

Entregar: mecanismo recomendado, código-exemplo mínimo de configuração, e armadilhas conhecidas (ex.: auditoria só em agregados novos sem `@Version`/`Persistable`).

## Answer

# Pesquisa: Auditoria em Spring Data JDBC com `BaseEntity`

## 1. Suporte nativo

Sim, existe: `@EnableJdbcAuditing` (`org.springframework.data.jdbc.repository.config`). Internamente usa Entity Callbacks (`RelationalAuditingCallback`) delegando para `IsNewAwareAuditingHandler` do Spring Data Commons.

- Anotações (de `org.springframework.data.annotation`): `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`.
- Tipos de data suportados: `java.time` (Instant, LocalDateTime), long/Long, Date, Calendar.
- **Anotações em superclasse funcionam**: o mapeamento percorre toda a hierarquia da classe — campos auditáveis no `BaseEntity` são detectados nas subclasses.
- `@EnableJdbcAuditing` registra bean `IsNewAwareAuditingHandler` + callback; atributos úteis: `auditorAwareRef`, `dateTimeProviderRef`, `setDates` (default true), `modifyOnCreate` (default true → `@LastModifiedDate/@LastModifiedBy` também preenchidos na criação).
- ⚠️ Spring Boot NÃO autoconfigura auditoria para JDBC — precisa de `@Configuration` com `@EnableJdbcAuditing`.
- ⚠️ Auditoria só atua no aggregate root.

## 2. AuditorAware<T>

```java
public interface AuditorAware<T> { Optional<T> getCurrentAuditor(); }
```

- Um único bean `AuditorAware` é detectado automaticamente; com múltiplos, usar `auditorAwareRef`.
- `Optional.empty()` → campos `createdBy/updatedBy` ficam `null`, sem exceção. Datas continuam sendo preenchidas.

## 3. ⚠️ Pegadinha principal: detecção new-vs-existing

Ordem da estratégia `isNew`:
1. `Persistable.isNew()` se implementado;
2. campo `@Version`: null/0 ⇒ novo;
3. default: inspeção do `@Id` — id null ou 0 ⇒ novo; senão ⇒ não-novo.

Consequências:
- Com IDs gerados pelo banco e entidades criadas com `id == null`: fluxo padrão funciona sem `@Version` nem `Persistable`.
- **Quebra com ID atribuído pela aplicação** (id natural setado): framework assume "não-novo", `createdAt` fica null → issue oficial [spring-data-relational #982](https://github.com/spring-projects/spring-data-jdbc/issues) confirmada pelo Jens Schauder.
- **Update com instância nova construída na mão** (sem carregar do banco): `createdAt` null → UPDATE grava NULL. Solução: sempre partir de `findById(...)` antes de salvar (padrão recomendado na service layer).

Documentar como restrição: *"nunca criar instância nova com id pré-definido"*.

## 4. Alternativa BeforeConvertCallback / BeforeSaveCallback

Controle total da lógica de isNew; funciona com id natural. Contras: reinventa auditoria nativa; gestão manual de createdBy; ordem de callbacks importa. Reservar apenas para cenário de ID natural.

**Recomendação: auditoria nativa (`@EnableJdbcAuditing`).**

## 5. Nullability e testes

- No mock, `created_by`/`updated_by` receberão valor fixo (`0L` = system), então já podem ser `NOT NULL` no Liquibase.
- Cinto de segurança DDL: colunas de criação como `DEFAULT now() NOT NULL`. Atenção: UPDATE de instância com `createdAt == null` ainda sobrescreve com NULL — mitigar carregando a entidade antes do save.
- Testcontainers: nenhum impacto específico (auditoria roda no processo Java). Asserts: `assertNotNull(saved.getCreatedAt())`; com `modifyOnCreate=true`, insert preenche createdAt e updatedAt. Preferir `Instant` (alinhado à regra UTC do projeto).

## Exemplo mínimo recomendado

```java
@Getter @Setter
public abstract class BaseEntity {
    @Id private Long id;
    @CreatedDate private Instant createdAt;
    @LastModifiedDate private Instant updatedAt;
    @CreatedBy private Long createdBy;
    @LastModifiedBy private Long updatedBy;
}
```

```java
@Configuration
@EnableJdbcAuditing(auditorAwareRef = "currentAuditorProvider")
public class AuditingConfig {}
```

```java
// Interface ponte: amanhã troca-se só a implementação, zero mudança no resto do app.
public interface CurrentAuditorProvider extends AuditorAware<Long> {}

class MockedAuditorProvider implements CurrentAuditorProvider {
    @Override
    public Optional<Long> getCurrentAuditor() {
        // TODO: replace with SecurityContextHolder lookup when Firebase Auth lands
        return Optional.of(0L); // 0 = system/mock user
    }
}
```

Futura impl real: `SecurityContextAuditorProvider` lendo `SecurityContextHolder` — mesma assinatura, plug-and-play.

## Fontes

1. https://docs.spring.io/spring-data/commons/reference/auditing.html
2. https://docs.spring.io/spring-data/relational/reference/data-commons/auditing.html
3. https://docs.spring.io/spring-data/relational/reference/data-commons/is-new-state-detection.html
4. https://docs.spring.io/spring-data/jdbc/docs/current/api/org/springframework/data/jdbc/repository/config/EnableJdbcAuditing.html
5. https://docs.spring.io/spring-data/relational/reference/data-commons/entity-callbacks.html
6. https://github.com/spring-projects/spring-data-jdbc/issues/982
7. https://github.com/spring-projects/spring-data-relational/issues/2202
8. https://github.com/spring-projects/spring-data-relational/issues/613
9. https://stackoverflow.com/questions/73051733/createddate-set-to-null-on-update-spring-data-jdbc
