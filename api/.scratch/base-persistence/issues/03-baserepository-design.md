# Grilling: Design do repositório base (BaseRepository) no Spring Data JDBC

Part of map: `.scratch/base-persistence/map.md`
Type: grilling
Status: open
Blocked by: 01

## Question

Qual design concreto para o "BaseRepository" neste projeto, dadas as descobertas do ticket de research? A spec deve definir:

- Se `BaseRepository` é interface genérica, custom base repository class (`repositoryBaseClass`), classe abstrata sobre `JdbcTemplate`, ou híbrido.
- Quais métodos padrão a fundação garante (save/update/deleteById/findById) e quais cada domínio adiciona via `@Query`.
- Como `UserRepository` fica como exemplo concreto desse design.
- Impacto na estratégia de testes.

Skills: `/grilling` + `/domain-modeling`. Respeitar preferências da seção Notes do mapa.
