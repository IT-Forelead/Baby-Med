DROP TABLE visits CASCADE;
CREATE TABLE IF NOT EXISTS visits
(
    id             UUID PRIMARY KEY,
    created_at     TIMESTAMP NOT NULL,
    user_id        UUID      NOT NULL
        CONSTRAINT fk_user_id REFERENCES users (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    patient_id     UUID      NOT NULL
        CONSTRAINT fk_patient_id REFERENCES patients (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    service_id     UUID      NOT NULL
        CONSTRAINT fk_service_id REFERENCES services (id) ON UPDATE CASCADE ON DELETE NO ACTION,
    cheque_id      UUID      NOT NULL,
    payment_status VARCHAR   NOT NULL
        CONSTRAINT fk_payment_status REFERENCES payment_statuses (name) ON UPDATE CASCADE ON DELETE NO ACTION DEFAULT 'not_paid',
    deleted        BOOLEAN   NOT NULL                                                                         DEFAULT false
);