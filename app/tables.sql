CREATE TYPE ROLE AS ENUM ('super_manager', 'tech_admin', 'admin');

CREATE TABLE users
(
    id        UUID PRIMARY KEY,
    firstname VARCHAR NOT NULL,
    lastname  VARCHAR NOT NULL,
    phone     VARCHAR NOT NULL,
    role      ROLE    NOT NULL,
    password  VARCHAR NOT NULL,
    deleted   BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE regions
(
    id      UUID PRIMARY KEY,
    region  VARCHAR NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE towns
(
    id        UUID PRIMARY KEY,
    town      VARCHAR NOT NULL,
    region_id UUID    NOT NULL
        CONSTRAINT fk_region_id REFERENCES regions (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    deleted   BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE clients
(
    id        UUID PRIMARY KEY,
    firstname VARCHAR NOT NULL,
    lastname  VARCHAR NOT NULL,
    region_id UUID    NOT NULL
        CONSTRAINT fk_region_id REFERENCES regions (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    town_id   UUID    NOT NULL
        CONSTRAINT fk_town_id REFERENCES towns (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    address   VARCHAR NOT NULL,
    birthday  DATE    NOT NULL,
    phone     VARCHAR UNIQUE,
    deleted   BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE payments
(
    id         UUID PRIMARY KEY,
    client_id  UUID      NOT NULL
        CONSTRAINT fk_client_id REFERENCES clients (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    price      NUMERIC   NOT NULL,
    created_at TIMESTAMP NOT NULL,
    deleted    BOOLEAN   NOT NULL DEFAULT false
);