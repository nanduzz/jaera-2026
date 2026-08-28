--liquibase formatted sql
--changeset jaera:20260828-002-create-roles-tables

CREATE TABLE roles (
    id   BIGSERIAL    PRIMARY KEY,
    name VARCHAR(50)  NOT NULL,
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE user_roles (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT    NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT uq_user_roles UNIQUE (user_id, role_id)
);

-- Seed initial roles
INSERT INTO roles (name) VALUES ('USER'), ('ADMIN'), ('STORE_OWNER');

--rollback DROP TABLE user_roles;
--rollback DROP TABLE roles;
