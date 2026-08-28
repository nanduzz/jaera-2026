Type: grilling
Status: open
Blocked by: 01, 02

## Question

Forma do principal autenticado e como os controllers acessam o usuário corrente.

Após validar o token (ticket 01) e carregar as authorities do DB (ticket 02), o que populate o `SecurityContext`?

- **(a) `CustomUserDetails`** — carrega `id` local, `firebaseUid`, `email`, `authorities`; exposto via `@AuthenticationPrincipal`.
- **(b) `JaeraPrincipal` próprio + argument resolver `@CurrentUser`** — objeto de domínio da API, sem acoplar ao `UserDetails`.
- **(c) Apenas `firebaseUid` como `principal`** + authorities à parte — mínimo, mas controllers precisam de lookup extra para o `User` local.

Decidir o formato do principal e o mecanismo de exposição aos controllers protegidos (ex.: `@CurrentUser User`), considerando o pacote `users` já existente e o `BaseEntity`.
