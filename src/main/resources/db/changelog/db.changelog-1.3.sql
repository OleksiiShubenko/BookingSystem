CREATE TABLE certificate_key (
    id                   BIGINT PRIMARY KEY,
    service_number       VARCHAR(255) NOT NULL,
    certificate_pem      BYTEA NOT NULL,
    private_key          BYTEA NOT NULL,
    created_at           TIMESTAMP NOT NULL,
    expire_date          TIMESTAMP NOT NULL
);

CREATE SEQUENCE certificate_id_seq START WITH 1 INCREMENT BY 1;
