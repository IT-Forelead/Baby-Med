CREATE TABLE IF NOT EXISTS delivery_statuses
(
    name    VARCHAR NOT NULL PRIMARY KEY,
    deleted BOOLEAN NOT NULL DEFAULT false
);

INSERT INTO delivery_statuses
VALUES ('sent'),
       ('delivered'),
       ('failed'),
       ('undefined');

CREATE TABLE IF NOT EXISTS message_types
(
    name    VARCHAR NOT NULL PRIMARY KEY,
    deleted BOOLEAN NOT NULL DEFAULT false
);

INSERT INTO message_types
VALUES ('registration');

CREATE TABLE IF NOT EXISTS messages
(
    id              UUID PRIMARY KEY,
    sent_date       TIMESTAMP NOT NULL,
    phone           VARCHAR   NOT NULL,
    text            VARCHAR   NOT NULL,
    message_type    VARCHAR   NOT NULL
        CONSTRAINT fk_message_type REFERENCES message_types (name) ON UPDATE CASCADE ON DELETE NO ACTION,
    delivery_status VARCHAR   NOT NULL
        CONSTRAINT fk_delivery_status REFERENCES delivery_statuses (name) ON UPDATE CASCADE ON DELETE NO ACTION DEFAULT 'sent'
);