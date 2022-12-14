CREATE TABLE IF NOT EXISTS doctor_shares
(
    id             UUID PRIMARY KEY,
    service_id     UUID      NOT NULL
        CONSTRAINT fk_service_id REFERENCES services (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    user_id        UUID      NOT NULL
        CONSTRAINT fk_user_id REFERENCES users (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    percent        INT NOT NULL DEFAULT 0,
    deleted        BOOLEAN   NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS checkup_expenses
(
    id                  UUID PRIMARY KEY,
    created_at          TIMESTAMP NOT NULL,
    doctor_share_id     UUID      NOT NULL
        CONSTRAINT fk_doctor_share_id REFERENCES doctor_shares (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    price               NUMERIC NOT NULL DEFAULT 0,
    deleted             BOOLEAN   NOT NULL DEFAULT false
);
