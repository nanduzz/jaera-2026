# Arquivos base do projeto

# Contexto
Este projeto utilizará arquivos base para padronizar a estrutura, convenções e práticas de desenvolvimento. Por exemplo classes base para entidades, repositórios, controladores e serviços. Além disso, o projeto seguirá convenções de nomenclatura, padrões de arquitetura e práticas recomendadas para garantir consistência e qualidade do código.

Esta atividade tem como objetivo criar os arquivos base para comunicaçao com o banco de dados, como por exmeplo, uma classe BaseEntity, BaseRepository. Para isso podemos iniciar também com o desenvolvimento de uma arquitetura modular monolítica, que permitirá a evolução futura para microsserviços, caso necessário.

A fim de poder implementar também uma classe concreta de exemplo, podemos criar uma entidade chamada `User`, com seus respectivos repositórios. A classe `User` representará um usuário do sistema, com os atributos base e também atributos específicos, como `username`, `email` e `firebaseUid`. A classe `User` será utilizada como exemplo para demonstrar a utilização das classes base e a comunicação com o banco de dados, mas também será uma entidade real do sistema, que poderá ser utilizada em futuras implementações e funcionalidades.

# Entregaveis

- **BaseEntity.java**: Classe base para todas as entidades do sistema, contendo atributos comuns como `id`, `createdAt`, `updatedAt`, `createdBy` e `updatedBy`. Esta classe será utilizada como superclasse para todas as entidades do sistema, garantindo consistência e padronização na estrutura das entidades.
- **BaseRepository.java**: Classe abstrata que define métodos genéricos para operações de persistência, como `save`, `update`, `deleteById` e `findById`. Esta classe será estendida por repositórios específicos de cada entidade, permitindo a reutilização de código e a padronização das operações de persistência. Porem, os metodos `save`, `update`, `deleteById` e `findById` não serãõ sobreescritos, mas sim herdados e utilizados pelos repositórios específicos de cada entidade. Caso necessario as classes que heradam de `BaseEntity` podem ter que implementar métodos adicionais para atender a requisitos específicos de cada entidade, como por exemplo metodos de mapeamento de atributos para colunas do banco de dados e RowMapper para mapear os resultados das consultas SQL para objetos Java. Esses métodos adicionais podem ser metoos abstratos de `BaseEntity`, que deverão ser implementados pelas classes concretas que herdam de `BaseEntity`.
- **User.java**: Classe concreta que representa um usuário do sistema, com atributos específicos como `username`, `email` e `firebaseUid`. Esta classe estenderá a classe `BaseEntity`, herdando os atributos comuns e implementando métodos adicionais para atender aos requisitos específicos da entidade `User`.
- **UserRepository.java**: Repositório específico para a entidade `User`, estendendo a classe `BaseRepository` e implementando métodos adicionais para operações de persistência específicas da entidade `User`, como consultas por `username`, `email` ou `firebaseUid`. Este repositório permitirá a comunicação com o banco de dados para realizar operações CRUD na tabela de usuários.
- **Teste de automatizados**: 
  - **BaseEntityTest.java**: Testes unitários para a classe `BaseEntity`, garantindo que os atributos comuns e métodos herdados funcionem corretamente.
  - **BaseRepositoryTest.java**: Testes unitários para a classe `BaseRepository`, verificando se os métodos genéricos de persistência funcionam corretamente e se podem ser utilizados pelos repositórios específicos.
  - **UserTest.java**: Testes unitários para a classe `User`, garantindo que os atributos específicos e métodos adicionais funcionem corretamente.
  - **UserRepositoryTest.java**: Testes unitários para o repositório `UserRepository`, verificando se as operações de persistência específicas da entidade `User` funcionam corretamente e se podem ser utilizadas para comunicação com o banco de dados.

  