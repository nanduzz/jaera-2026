# AI Agent Instructions for Jaera API Project

## 1. Project Overview
This project is a Quick Commerce API (similar to iFood, but for any fast-delivery products like medicines, tools, daily essentials). 
The goal is to build a robust, scalable, and highly cohesive backend that allows rapid delivery of general goods.

## 2. Tech Stack
- **Java 25**
- **Spring Boot 4.x**
- **Database:** PostgreSQL
- **Persistence:** Spring Boot Starter Data JDBC (NO JPA/Hibernate)
- **Migrations:** Liquibase
- **Testing:** JUnit 5, Testcontainers (PostgreSQL), Spring RestDocs
- **Boilerplate:** Lombok

## 3. Architecture: Modular Monolith
The project follows a **Modular Monolith** architecture to ensure future transition to microservices if necessary.
- **Package Structure:** Organized by Domain, then by Layer.
  - Template: `br.com.jaera.api.<domain>.<layer>`
  - Example: 
    - `br.com.jaera.api.users.controller`
    - `br.com.jaera.api.users.service`
    - `br.com.jaera.api.users.repository`
    - `br.com.jaera.api.stores.controller`
    - etc.
- **Rule:** Domains should be as decoupled as possible. Avoid tight coupling between different domain services.

## 4. Coding Standards & Conventions
- **Language:** 100% English for code (classes, variables, methods, comments, and internal logs).
- **Lombok vs. Records:** ALWAYS use Lombok (e.g., `@Data`, `@Getter`, `@Setter`, `@Builder`, `@AllArgsConstructor`, `@NoArgsConstructor`). **DO NOT** use Java `record` types.
- **Simplicity (KISS):** Do not over-engineer. Avoid unnecessary complexity, excessive abstractions, or Java Reflections if a simple, straightforward approach works.
- **Clean Code:** Keep methods small, well-named, and strictly focused on a single responsibility.

## 5. Persistence & Database (Spring Data JDBC)
- **NO JPA:** Do not use `@Entity`, `@OneToMany`, etc. Use Spring Data JDBC annotations (`@Table`, `@Id`, `@MappedCollection`, `@Query`).
- **IDs:** Use Numeric types (`Long`) for database IDs to facilitate Cursor-Based Pagination. **DO NOT** use UUIDs.
- **Base Domain Class:** All entities must inherit from a standard base class containing auditing fields:
  - `Long id`
  - `LocalDateTime createdAt` (or `Instant`)
  - `LocalDateTime updatedAt`
  - `Long createdBy`
  - `Long updatedBy`
  - *Note for Agent: Use Spring Data JDBC `@CreatedDate`, `@LastModifiedDate` or equivalent callbacks to manage these fields automatically.*
- **Base Repository:** Create and use a base repository interface with generic methods (Save, Update, Delete By ID, Find By ID) that specific domain repositories will extend. Use standard Spring Data JDBC aggregate patterns, but feel free to write `@Query` for complex fetches to avoid N+1 issues.

## 6. API, Error Handling & i18n
- **Error Handling:** Use a global `@ControllerAdvice`.
- **Security:** Never leak stack traces or internal database exceptions to the client.
- **Internationalization (i18n):** The application will be global. ALL user-facing error messages must be localized using Spring's `MessageSource` (e18n). Do not hardcode string error messages in the responses.
- **Time/Dates:** Handle all dates and times natively in UTC on the backend. The API should accept and return ISO 8601 strings. Localization of dates happens on the frontend, but the backend must be timezone-agnostic.

## 7. Testing & Documentation
- **Mandatory Tests:** Every new endpoint MUST have an integration test using `@SpringBootTest` and Testcontainers (PostgreSQL).
- **RestDocs:** Tests must generate documentation via Spring RestDocs automatically. 
- **Naming Convention:** Use clear, descriptive names for tests. 
  - Standard: `should[Action]When[Condition]()` (e.g., `shouldCreateUserWhenValidDataIsProvided()`).
- **🚨 CRITICAL RULE FOR AI AGENTS (TIME/TOKEN MANAGEMENT):**
  - If a test fails, you are allowed a **MAXIMUM of 2 attempts** to fix it.
  - If the test remains broken after 2 attempts, **STOP**. Do not enter an infinite loop of fixing tests.
  - Do a **Handoff to the Developer**: Explain briefly what is failing and wait for human intervention. Do not waste context window or tokens fighting complex test setup issues.

## 8. Business Rules
- Specific business rules will be provided in separate documentation files as the project evolves. When in doubt about business logic, ask the developer. Do not hallucinate core business rules.

## 9. Version Control & Branching Strategy (GitFlow)
This project strictly follows a GitFlow-inspired branching model. As an AI Agent, you must adhere strictly to these version control rules to prevent accidental overrides, conflicts, or unauthorized merges.

- **Main Branches (Protected):**
  - `develop`: The main integration branch. Contains the latest delivered development changes for the next release.
  - `release`: Contains code being prepared for the upcoming production release.
  - `hotfix`: Branches used for urgent production patches. Hotfixes must always be merged back into both `release` (or `main`) and `develop` to prevent regression.

- **Working Branches (Agent's Domain):**
  - All new work MUST be done in a dedicated temporary branch created from `develop`.
  - Naming convention for new features: `feat/[objective]` (e.g., `feat/create-user-domain`, `feat/payment-integration`).
  - Naming convention for fixes: `fix/[issue-name]`.

- **🚨 CRITICAL RULES FOR AI AGENTS (GIT & MERGE POLICIES):**
  - **NO DIRECT COMMITS:** You are STRICTLY FORBIDDEN from committing code directly to `develop`, `release`, `hotfix`, or `main`.
  - **PULL REQUESTS ONLY:** All code must enter the protected branches exclusively via Pull Requests (PRs).
  - **NO UNAUTHORIZED MERGES:** You MUST NOT merge any Pull Request into `develop`, `release`, or `hotfix` UNLESS the developer explicitly commands you to do so (e.g., "You can merge this PR now"). 
  - **Workflow:** When you finish a task, leave a commit ready to add the changes to the `feat/` branch, if possible create a temp file with the suggested commit message, and instructions for the developer to create a commit and PR. Wait for the developer to review and merge it.