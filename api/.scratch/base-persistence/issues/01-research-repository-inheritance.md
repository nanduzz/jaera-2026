# Research: Padrões de herança de repositório no Spring Data JDBC

Part of map: `.scratch/base-persistence/map.md`
Type: research
Status: resolved
Blocked by: —

## Question

Como expressar um "BaseRepository" reutilizável no Spring Data JDBC (Spring Boot 4.x)? O modelo nativo usa interfaces estendendo `CrudRepository`, sem herança de implementação. Investigar e comparar:

1. Interface genérica base (`interface BaseRepository<T> extends CrudRepository<T, Long>`) — o que funciona e o que não.
2. Classe abstrata DAO sobre `NamedParameterJdbcTemplate`.
3. Custom repository fragments / `@EnableJdbcRepositories(repositoryBaseClass = ...)` — custom base repository class: como funciona, limitações, se é idiomático.
4. Híbridos (ex.: interface genérica + classe utilitária).

Entregar: prós/contras de cada opção, o que é idiomático no Spring Data JDBC moderno, riscos com Testcontainers/auditoria, e recomendação fundamentada para o contexto deste projeto (Lombok, sem records, IDs Long).

## Answer

# Pesquisa: Abstração de "BaseRepository" no Spring Data JDBC

**Contexto:** Java 25, Spring Boot 4.x (Spring Data JDBC), PostgreSQL, Lombok, `Long` IDs, Liquibase. Objetivo: reutilizar comportamento padrão de save/update/deleteById/findById entre repositórios de domínio.

## Opção 1 — Interface genérica `BaseRepository<T> extends CrudRepository<T, Long>`

### Como funciona
O Spring Data Commons resolve os parâmetros genéricos da hierarquia de interfaces para descobrir o tipo de entidade e o tipo do ID. Métodos que casam com assinaturas de `CrudRepository` são **roteados automaticamente** para a implementação base do módulo (`SimpleJdbcRepository`) — não é preciso escrever nada. É literalmente o padrão da documentação oficial ("If many repositories in your application should have the same set of methods you can define your own base interface").

```java
@NoRepositoryBean
public interface BaseRepository<T> extends CrudRepository<T, Long> {}

public interface UserRepository extends BaseRepository<User> {}
```

### Pontos críticos
- **`@NoRepositoryBean` é obrigatório** na interface base; sem ele o Spring Data tenta instanciá-la diretamente e falha.
- Evitar hierarquias genéricas profundas onde o ID só é resolvido em níveis abaixo (risco histórico DATACMNS-501). Com um único nível (`BaseRepository<T>` → `UserRepository`), funciona sem problemas.
- Preferir `ListCrudRepository<T, ID>` (Spring Data 3+) em vez de `CrudRepository`: mesmos métodos, mas `findAll()`/`saveAll()` retornam `List`.
- Table naming e resolução de agregado não são afetados (via `@Table` + `NamingStrategy`).

### Prós/Contras
- ✅ Zero infraestrutura; 100% suportado e documentado; auditing/eventos/transações intactos; Testcontainers indiferente.
- ❌ Não permite adicionar *comportamento* (só expõe métodos existentes).

## Opção 2 — Classe DAO abstrata sobre `NamedParameterJdbcTemplate` / `DataAccessStrategy`

SQL escrito à mão em cada DAO. ❌ Reimplementa o que o framework faz (viola KISS); perde aggregate mapping, eventos, entity callbacks, optimistic locking e **auditing**; `DataAccessStrategy` não é API pública estável (risco alto em upgrades); exige RowMapper manual e mais código de teste.

## Opção 3 — `@EnableJdbcRepositories(repositoryBaseClass = ...)` + fragments

Dois mecanismos distintos:

**a) Custom base class:** classe estendendo `SimpleJdbcRepository` registrada globalmente via `repositoryBaseClass`; precisa de construtor compatível `(JdbcAggregateOperations, PersistentEntity<T,?>, JdbcConverter)`. Disponível desde SD JDBC 2.1. Afeta TODOS os repositórios.

**b) Fragments:** interface `CustomizedSave<T>` + classe `CustomizedSaveImpl<T>` detectada por convenção; por repositório; pode sobrescrever até `save()`; beans Spring normais.

⚠️ Risco: acoplamento ao construtor de `SimpleJdbcRepository`, que já mudou entre minor versions. Auditing continua funcionando (é via entity callbacks dentro do `JdbcAggregateTemplate`) exceto se a base class sobrescrever `save()` sem delegar a `entityOperations.save(...)`.

## Opção 4 — Híbrido (interface genérica + helper/fragments sob demanda)

Simples hoje, rota de evolução clara, nada acoplado a internals.

## Comparativo

| Critério | Interface genérica | DAO abstrato | repositoryBaseClass | Híbrido |
|---|---|---|---|---|
| Suportado oficialmente | ✅ | ⚠️ parcial | ✅ | ✅ |
| Complexidade | Mínima | Alta | Média-alta | Baixa |
| Perde auditing/eventos | Não | **Sim (risco)** | Só se mal feito | Não |
| Risco em upgrade | Baixíssimo | Alto | Médio | Baixíssimo |

## Recomendação

**Opção 1 com `ListCrudRepository`, evoluindo para fragments da Opção 3 somente sob demanda real:**

```java
@NoRepositoryBean
public interface BaseRepository<T> extends ListCrudRepository<T, Long> {}
```

- NÃO usar Opção 2: reimplementa aggregate mapping, quebra auditing/callbacks e cria dívida contra API interna.
- NÃO usar Opção 3 agora: over-engineering; quando houver comportamento transversal genuíno (soft delete global etc.), preferir fragments à base class global.
- Compatibilidade total: auditing, Liquibase, Testcontainers e RestDocs funcionam sem alteração.

## Fontes

1. https://docs.spring.io/spring-data/relational/reference/repositories/definition.html
2. https://docs.spring.io/spring-data/relational/reference/repositories/core-concepts.html
3. https://docs.spring.io/spring-data/relational/reference/repositories/custom-implementations.html
4. https://docs.spring.io/spring-data/jdbc/docs/current/api/org/springframework/data/jdbc/repository/config/EnableJdbcRepositories.html
5. https://github.com/spring-projects/spring-data-relational/blob/main/spring-data-jdbc/src/main/java/org/springframework/data/jdbc/repository/support/SimpleJdbcRepository.java
6. https://blog.frankel.ch/custom-spring-data-repository/
7. https://github.com/spring-projects/spring-data-commons/issues/969
8. https://docs.spring.io/spring-data/relational/reference/jdbc/auditing.html
