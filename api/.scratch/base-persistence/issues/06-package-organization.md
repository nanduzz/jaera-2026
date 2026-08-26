# Grilling: Organização de pacotes do monólito modular e classes compartilhadas

Part of map: `.scratch/base-persistence/map.md`
Type: grilling
Status: open
Blocked by: —

## Question

Onde vive o código compartilhado da fundação e como os domínios se separam?

A spec deve definir:

- Pacote para BaseEntity/BaseRepository e infra compartilhada (ex.: `br.com.jaera.api.shared`? `common`? `core`?).
- Layout por domínio (`br.com.jaera.api.<domain>.<layer>`) aplicado ao domínio `users` como exemplo concreto.
- Regras de fronteira entre domínios (o que é permitido importar entre eles) — suficiente para evoluir a microsserviços.
- Posição dos testes e recursos (changelogs) espelhando essa estrutura.

Skills: `/grilling` + `/domain-modeling`.
