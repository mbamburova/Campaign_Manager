CREATE TABLE hero (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    hero_name VARCHAR(32) NOT NULL,
    hero_level INT
);

CREATE TABLE mission (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    mission_name VARCHAR(32) NOT NULL,
    level_required INT,
    capacity INT,
    available BOOLEAN
);
