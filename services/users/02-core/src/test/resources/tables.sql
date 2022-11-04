CREATE TYPE ROLE AS ENUM ('super_manager', 'doctor', 'tech_admin', 'admin');

CREATE TABLE regions
(
    id      UUID PRIMARY KEY,
    name    VARCHAR NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT false
);

CREATE TABLE towns
(
    id        UUID PRIMARY KEY,
    name      VARCHAR NOT NULL,
    region_id UUID    NOT NULL
        CONSTRAINT fk_region_id REFERENCES regions (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    deleted   BOOLEAN NOT NULL DEFAULT false
);

INSERT INTO "regions" ("id", "name")
VALUES ('ad514b71-3096-4be5-a455-d87abbb081b2', 'Xorazm viloyati');
INSERT INTO "regions" ("id", "name")
VALUES ('dac35ec3-a904-42d7-af20-5d7e853fe1f6', 'Toshkent shahri');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('0d073b76-08ce-4b78-a88c-a0cb6f80eaf9', 'Urganch shahri', 'ad514b71-3096-4be5-a455-d87abbb081b2');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('e24b2941-ac1b-44d3-9a82-0ff5885f47ef', 'Qo`shko`pir tumani', 'ad514b71-3096-4be5-a455-d87abbb081b2');

INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('407ec75a-d1de-4799-a3db-854bcc14e71a', 'Chilonzor tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');
INSERT INTO "towns" ("id", "name", "region_id")
VALUES ('852772ff-6149-4f20-8269-1d7cdb130dcd', 'Mirzo Ulug`bek tumani', 'dac35ec3-a904-42d7-af20-5d7e853fe1f6');

CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    firstname  VARCHAR   NOT NULL,
    lastname   VARCHAR   NOT NULL,
    phone      VARCHAR   NOT NULL UNIQUE,
    role       ROLE      NOT NULL,
    password   VARCHAR   NOT NULL
);

CREATE TABLE customers
(
    id         UUID PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    firstname  VARCHAR   NOT NULL,
    lastname   VARCHAR   NOT NULL,
    region_id  UUID      NOT NULL
        CONSTRAINT fk_region_id REFERENCES regions (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    town_id    UUID      NOT NULL
        CONSTRAINT fk_town_id REFERENCES towns (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    address    VARCHAR   NOT NULL,
    birthday   DATE      NOT NULL,
    phone      VARCHAR UNIQUE,
    deleted    BOOLEAN   NOT NULL DEFAULT false
);

INSERT INTO "customers" ("id", "created_at", "firstname", "lastname", "region_id", "town_id", "address", "birthday", "phone")
VALUES ('f4484324-e6cd-4e48-8d24-638f0a4fabaa', '2022-10-31T10:20:54.813Z', 'Defaut', 'Customer', 'ad514b71-3096-4be5-a455-d87abbb081b2', '0d073b76-08ce-4b78-a88c-a0cb6f80eaf9', 'Al-Xorazmiy', '2002-12-12', '+998901234567');

CREATE TABLE payments
(
    id           UUID PRIMARY KEY,
    created_at   TIMESTAMP NOT NULL,
    customer_id UUID      NOT NULL
        CONSTRAINT fk_customer_id REFERENCES customers (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    price        NUMERIC   NOT NULL,
    deleted      BOOLEAN   NOT NULL DEFAULT false
);
