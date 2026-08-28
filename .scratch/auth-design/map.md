# Map: Design de autenticação e autorização (Spring Security + Firebase)

> Tracker local (fallback): `.scratch/auth-design/`. Ticktes filhos em `issues/NN-<slug>.md`. Frente = arquivos open, unblocked, unclaimed.

## Destination

Design de autenticação e autorização da Jaera API (Spring Security + Firebase), **travado como spec + ADRs** — não o código. Cobertura:

- Filtro **stateless** que valida o Firebase ID token (Bearer) a cada request.
- **Auto-provision** do `User` local (role `USER`) a partir do `firebase_uid` quando não existir.
- Carregamento de **authorities** de tabelas `roles`/`user_roles` no PostgreSQL (roles `USER`, `ADMIN`, `STORE_OWNER`).
- Proteção de endpoints via `SecurityFilterChain` (público vs autenticado) + `@PreAuthorize` para roles específicas.
- **Sem endpoint de gestão de roles** (roles por seed/manual).
- Dev = **emulador Firebase** (Auth na porta `9099`, projeto `demo-no-project`); produção = projeto real via properties/env + service account.

## Notes

- Domínio transversal: pacote `br.com.jaera.api.security` (filtro, provider, config).
- Skills a consultar por sessão: `/java-springboot` (Spring Security 6 / Boot 4 APIs), `/tdd` (Testcontainers + RestDocs), `/research` (tickets de research).
- Preferências vigentes (AGENTS.md): Spring Data JDBC (sem JPA), Lombok (sem `record`), i18n via `MessageSource` para erros voltados ao usuário, erros via `@ControllerAdvice` global sem vazar stack traces.
- Spring Security 6/Boot 4: sem `WebSecurityConfigurerAdapter` — usar bean `SecurityFilterChain`.
- Emulador já existe em `compose.yaml` (`firebase-emulator`, `FIREBASE_AUTH_EMULATOR_HOST=localhost:9099`); `docker/firebase.json` habilita auth/firestore/storage/ui.

## Decisions so far

<!-- index — uma linha por ticket resolvido -->

- [Verificar token Firebase (01)](issues/01-token-verification.md) — Admin SDK `verifyIdToken` exposto como `JwtDecoder` no `oauth2ResourceServer`; emulador + prod sem branch; blocking ok em MVC.
- [Estratégia de testes de auth (06)](issues/06-auth-test-strategy.md) — emulador Firebase em Testcontainers com token real + `@MockBean FirebaseAuth` para maioria; pular JwtDecoder local.

## Not yet specified

<!-- névoa em direção ao destino; não afiada o suficiente para virar ticket -->

- **Semântica relacional de `STORE_OWNER`** — a role existe, mas seu significado (quais lojas) depende do domínio de Stores, que é futuro.
- **Renovação de token / expiração de sessão no cliente** — contrato de refresh fica no frontend (fora do destino).
- **MFA / confirmação de e-mail** — possível futuro, fora deste design.
- **Autorização por posse de recurso** (ex.: usuário só edita o próprio recurso, além de role) — possível futuro.
- **Integração do frontend** (envio do Bearer, interceptors) — frontend vazio, fora do destino; apenas o contrato de header é coberto.

## Out of scope

<!-- trabalho além do destino; não se gradua -->

- **Provisionamento do projeto Firebase real** (criar projeto, baixar service account) — tarefa operacional futura; o design só define o ponto de configuração (properties/env).
- **Implementação do código de autenticação** — o destino é o design, não o código entregue.
- **Domínio de Stores** (e a semântica relacional de `STORE_OWNER`) — esforço futuro.
