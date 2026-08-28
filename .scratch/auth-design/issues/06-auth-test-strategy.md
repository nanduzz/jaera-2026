Type: research
Status: resolved
Blocked by: 01, 02, 07

## Question

Estratégia de testes de integração do filtro de autenticação, respeitando AGENTS.md (`@SpringBootTest` + Testcontainers PostgreSQL + RestDocs para todo endpoint).

Como obter um **ID token Firebase válido** dentro dos testes?

- **(a) Emulador Firebase em Testcontainers** — sobe o Auth emulator e cria usuário via Admin SDK; testa o caminho real de verificação (alinhado ao dev).
- **(b) Mock de `FirebaseAuth.verifyIdToken`** — retorna um `FirebaseToken` stub; isola o filtro sem rede.
- **(c) `JwtDecoder` testável** — emite um JWT assinado localmente com a chave de teste.

Decidir a abordagem que atende RestDocs + Testcontainers e o emulador de dev, e como documentar (via RestDocs) os endpoints protegidos e a resposta 401/403. Depende de 01 (mecanismo de verificação), 02 (authorities do DB) e 07 (bootstrap do Firebase nos testes).

## Answer

**Decisão: primário = emulador Firebase em Testcontainers; fallback = `@MockBean FirebaseAuth` para a maioria dos testes; pular `JwtDecoder` local.**

- Subir o Auth emulator via `nl.group9.testcontainers:firebase-emulator-container` junto do PostgreSQL, com `FIREBASE_AUTH_EMULATOR_HOST` + project `demo-no-project`; criar usuário (`FirebaseAuth.createUser`) e obter idToken real pelas endpoints do Identity Toolkit (`accounts:signInWithPassword`).
- RestDocs: documentar o 2xx com token real no header `Bearer`; documentar 401 (token ausente/inválido) e 403 (sem role) — exercita o filtro verdadeiro.
- Para os demais endpoints (auth não é o foco), `@MockBean FirebaseAuth` retornando `FirebaseToken` stub é rápido e isolado; reservar o caminho de emulador para a suíte do filtro de auth.
- `(c) JwtDecoder local` só faz sentido se refatorar para resource server; hoje o filtro usa `verifyIdToken`, então testaria a camada errada.
