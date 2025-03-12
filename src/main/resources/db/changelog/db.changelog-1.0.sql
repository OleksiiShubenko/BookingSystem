CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE unit (
    id SERIAL PRIMARY KEY,
    num_rooms INT NOT NULL,
    type VARCHAR(255) NOT NULL,
    floor INT NOT NULL,
    cost DOUBLE PRECISION NOT NULL,
    owner_id VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT fk_user FOREIGN KEY (owner_id) REFERENCES users(username)
);

CREATE TABLE booking (
    id SERIAL PRIMARY KEY,
    unit_id INT NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    from_time TIMESTAMP NOT NULL,
    to_time TIMESTAMP NOT NULL,
    status VARCHAR(255),
    payment_id INT,
    booking_time TIMESTAMP,
    cancel_time TIMESTAMP,
    CONSTRAINT fk_unit FOREIGN KEY (unit_id) REFERENCES unit(id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(username)
--    CONSTRAINT fk_payment FOREIGN KEY (payment_id) REFERENCES payment(id)
);

CREATE TABLE payment (
    id SERIAL PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    booking_id INT NOT NULL,
    cost DOUBLE PRECISION,
    status VARCHAR(255),
    created_at TIMESTAMP,
    CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES booking(id)
);

CREATE TABLE event (
    id SERIAL PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP,
    transaction_id VARCHAR(255)
);
