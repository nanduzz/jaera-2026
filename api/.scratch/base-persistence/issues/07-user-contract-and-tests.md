# Grilling: Contrato da entidade User e estratégia de testes

Part of map: `.scratch/base-persistence/map.md`
Type: grilling
Status: open
Blocked by: —

## Question

Fechar o contrato concreto que a spec entregará:

- Entidade `User`: atributos exatos (`username`, `email`, `firebaseUid` + herdados), validações de nível entidade (se houver), imutabilidade/valores default.
- `UserRepository`: queries extras exigidas (`findByUsername`, `findByEmail`, `findByFirebaseUid`? existência/duplicidade?), todas com `Long` id.
- Estratégia de testes da fundação: o que é teste unitário vs. integração com Testcontainers PostgreSQL; nomenclatura `should[Action]When[Condition]`; como a spec manda documentar (RestDocs fica para fase de endpoints?).
- Critérios de aceite da futura implementação (o que um agente precisa ler na spec para implementar sem perguntar).

Skills: `/grilling` + `/domain-modeling`.
