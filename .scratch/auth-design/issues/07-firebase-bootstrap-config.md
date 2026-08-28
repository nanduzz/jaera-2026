Type: grilling
Status: open

## Question

Bootstrap do Firebase (Admin SDK) e configuração via properties/env.

Como inicializar o Firebase de forma que:

- **Dev** use o emulador — `FIREBASE_AUTH_EMULATOR_HOST=localhost:9099`, projeto `demo-no-project` (já em `compose.yaml`).
- **Produção** use o projeto real — service account via `GOOGLE_APPLICATION_CREDENTIALS` + project id, configuráveis por properties/env.

Definir:

- Nomes de properties (ex.: `jaera.firebase.project-id`, `jaera.firebase.credentials-path`/`jaera.firebase.credentials-base64`).
- Como condicionar ao emulador (presença da env `FIREBASE_AUTH_EMULATOR_HOST`) vs credenciais reais.
- Onde o bean de configuração vive (pacote `security/config`) e o momento de inicialização do `FirebaseApp`.
- Placeholders/documentação para o operador configurar o projeto real sem expor segredos no repositório.
