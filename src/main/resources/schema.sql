DROP TABLE IF EXISTS dog_breed;

CREATE TABLE dog_breed
(
    id        SERIAL PRIMARY KEY,
    breed     VARCHAR(256) NOT NULL,
    sub_breed VARCHAR(256),
    image     BYTEA
);