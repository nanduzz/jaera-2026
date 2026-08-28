Type: grilling
Status: open

## Question

Modelo de dados e carregamento de authorities a partir do banco (decisão já travada: roles vivem no DB, opção B da grillagem).

Definir:

- **Schema** — tabela `roles` (`id`, `name` UNIQUE: USER/ADMIN/STORE_OWNER) + tabela de junção `user_roles` (`user_id`, `role_id`); seeds iniciais para as três roles; um `ADMIN` inicial associado (seed/manual, sem endpoint de gestão).
- **Carregamento** — no filtro, a partir do `firebase_uid` do token: `UserRepository.findByFirebaseUid(...)` + join de roles → montar `GrantedAuthority` (ex.: `ROLE_USER`, `ROLE_ADMIN`, `ROLE_STORE_OWNER`).
- **Convenção de nomes** — o Spring exige o prefixo `ROLE_` para `hasRole('ADMIN')`; confirmar se armazenamos com ou sem prefixo e como mapear.
- **Cardinalidade** — `user_roles` (N:N) é necessário, ou a role é 1:N simples por usuário? (STORE_OWNER pode ganhar contexto relacional futuro, mas a role em si é 1:N por ora.)

Resultado: schema Liquibase + estratégia de carregamento de authorities acordados.
