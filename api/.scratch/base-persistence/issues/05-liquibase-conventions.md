# Grilling: Convenções Liquibase (naming, organização, tabela users)

Part of map: `.scratch/base-persistence/map.md`
Type: grilling
Status: open
Blocked by: —

## Question

Fixado pelo dev (não reabrir): scripts em formato `.sql`, todos incluídos no master changelog para controle explícito de ordem, e todo script contém instruções de rollback.

Decidir ainda:

- Convenção de nomes de arquivos e diretórios (ex.: `db/changelog/master.changelog`, pastas por domínio/migração?).
- Formato do rollback dentro do arquivo SQL (`--rollback` statements do formatted SQL changelog).
- Estratégia de mudanças: um changeset por arquivo vs. múltiplos changesets por arquivo; convenções de `id`/`author` dos changesets.
- Schema completo da tabela `users` para a spec: colunas de BaseEntity + `username`, `email`, `firebaseUid`; constraints de unicidade (email? username? firebaseUid?), nullability, índices.

Skills: `/grilling` + `/domain-modeling`.
