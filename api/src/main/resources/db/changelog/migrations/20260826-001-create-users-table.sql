--liquibase formatted sql
--changeset jaera:20260826-001-create-users-table

CREATE TABLE users (
    id           BIGSERIAL      PRIMARY KEY,
    username     VARCHAR(50)    NOT NULL,
    email        VARCHAR(255)   NOT NULL,
    firebase_uid VARCHAR(128)   NULL,
    created_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ    NOT NULL DEFAULT now(),
    created_by   BIGINT         NOT NULL DEFAULT 0,
    updated_by   BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT uq_users_username     UNIQUE (username),
    CONSTRAINT uq_users_email        UNIQUE (email),
    CONSTRAINT uq_users_firebase_uid UNIQUE (firebase_uid)
);

--rollback DROP TABLE users;
