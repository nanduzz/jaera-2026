# Grilling: Mecânica de auditoria (createdAt/updatedAt/createdBy/updatedBy)

Part of map: `.scratch/base-persistence/map.md`
Type: grilling
Status: open
Blocked by: 02

## Question

Como os campos de auditoria do `BaseEntity` serão populados nesta fase (mock) e como o contrato deixará a troca trivial quando Spring Security/Firebase Auth entrar?

A spec deve definir:

- Mecanismo escolhido (auditoria nativa vs. callbacks), com base no research.
- O mock concreto de `createdBy`/`updatedBy` (valor fixo? constante tipo `0L` / `"system"`?) e o formato dos comentários TODO.
- O contrato futuro: quem fornece o usuário logado (`AuditorAware` lendo do Security Context) e o que muda na ativação.
- Nullability das colunas no Liquibase durante a fase de mock.

Skills: `/grilling` + `/domain-modeling`.
