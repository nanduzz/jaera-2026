# Task: Consolidar a spec em docs/specs/base-persistence.md

Part of map: `.scratch/base-persistence/map.md`
Type: task
Status: open
Blocked by: 03, 04, 05, 06, 07

## Question

Escrever `docs/specs/base-persistence.md` em português consolidando as decisões dos tickets fechados:

1. Visão geral da fundação de persistência e do monólito modular.
2. BaseEntity (campos, auditoria, mock de createdBy/updatedBy com TODOs).
3. BaseRepository (design escolhido + justificativa vs. alternativas).
4. User/UserRepository como exemplo concreto (contrato completo).
5. Convenções Liquibase (.sql, master changelog, rollback instructions, tabela users).
6. Estrutura de pacotes.
7. Estratégia de testes (unitários + Testcontainers).

Critério de pronto: um agente de IA consegue implementar a fundação lendo apenas a spec + AGENTS.md, sem precisar decidir nada. Trabalho em branch `feat/base-persistence-spec`, commit preparado conforme regra GitFlow do AGENTS.md (PR only).
