Type: grilling
Status: open

## Question

Contrato de proteção de endpoints e do header de token.

Definir:

- **Quais paths são públicos** no `SecurityFilterChain` (`authorizeHttpRequests`): ex.: `actuator/health`, `info`, e possivelmente a documentação (swagger/docs) — e quais exigem autenticação.
- **Contrato do header** — `Authorization: Bearer <idToken>`; como o filtro extrai e valida.
- **Mapeamento de roles por grupo de endpoint futuro** — o que exigirá `USER`, `ADMIN`, `STORE_OWNER` (ex.: rotas de administração → `ADMIN`; criação/gestão de loja → `STORE_OWNER`). Isto é o padrão base; os endpoints concretos vêm depois.
- Conviver com o padrão híbrido já decidido: filtro = público vs autenticado; `@PreAuthorize` = regras de role por método.
