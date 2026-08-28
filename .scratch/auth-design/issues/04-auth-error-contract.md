Type: grilling
Status: open

## Question

Contrato de erros de autenticação/autorização, em conformidade com o `@ControllerAdvice` global e o i18n via `MessageSource` (AGENTS.md).

Definir:

- Como o filtro e o Spring Security sinalizam **401** (não autenticado: token ausente/inválido/expirado) e **403** (autenticado sem a role necessária).
- Formato do corpo da resposta de erro — **sem vazar stack traces**, usando chaves localizadas do `MessageSource` (ex.: `error.auth.token.invalid`, `error.auth.forbidden`).
- Onde o tratamento vive (entry point de autenticação, `AccessDeniedHandler`, ou delegação ao advice existente) e como evitar duplicação com o advice global já presente.
