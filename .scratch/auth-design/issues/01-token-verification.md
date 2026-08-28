Type: research
Status: resolved

## Question

Como verificar o Firebase ID token dentro do filtro de autenticação stateless?

Duas abordagens principais:

- **(a) Firebase Admin SDK** — `FirebaseAuth.getInstance().verifyIdToken(idToken)`. Valida assinatura, expiração e `aud`/`iss`; funciona tanto com o emulador (via `FIREBASE_AUTH_EMULATOR_HOST`) quanto com projeto real. Porém `verifyIdToken` é **blocking** (chamada de rede/síncrona) — relevante sob o modelo de threads do Spring MVC/Boot 4.
- **(b) Spring Security `JwtDecoder` (Nimbus)** — decodifica/valida o JWT contra as chaves públicas do Google (`https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system.gsc` ou `https://securetoken.google.com/<projectId>`), sem acoplar ao Admin SDK.

Decidir qual adotar considerando: Spring Boot 4 / Spring Security 6, o emulador de dev (`demo-no-project`, porta 9099), e o impacto de bloqueio de thread. Entregar: recomendação fundamentada + implicações para o filtro e para os testes de integração.

## Answer

**Decisão: Firebase Admin SDK (`FirebaseAuth.getInstance().verifyIdToken`) como verificador único, exposto à Spring Security 6 via um adaptador `JwtDecoder` no `oauth2ResourceServer()`** — em vez de filtro manual.

- Funciona em dev (emulador, `FIREBASE_AUTH_EMULATOR_HOST`) e prod (projeto real) **sem branch por ambiente**: com a env do emulador setada o SDK aceita tokens sem assinatura; em prod valida assinatura/exp/aud/iss.
- `verifyIdToken` é blocking, mas correto sob o modelo thread-per-request do Spring MVC — não usar decoder reativo.
- `SecurityFilterChain`: `http.oauth2ResourceServer(o -> o.jwt().decoder(firebaseJwtDecoder()))`; o `JwtDecoder.decode()` delega a `verifyIdToken` e devolve um `Jwt` do Spring. Sem `WebSecurityConfigurerAdapter`.
- Mapear para o `User` local via `decodedToken.getUid()` → `UserRepository.findByFirebaseUid`; autoridades vêm do DB (ticket 02).
- O SDK cacheia as chaves públicas do Google; warm-up no startup recomendado.
