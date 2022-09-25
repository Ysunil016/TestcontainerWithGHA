CREATE TABLE person
(
    id             SERIAL       NOT NULL UNIQUE,
    name           VARCHAR(255) NOT NULL,
    preferred_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);