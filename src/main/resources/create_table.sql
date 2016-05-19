CREATE TABLE mission (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    mission_name VARCHAR(32) NOT NULL,
    level_required INT,
    capacity INT,
    available BOOLEAN
);

CREATE TABLE hero (
    id BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    missionId BIGINT REFERENCES mission (id),
    hero_name VARCHAR(32) NOT NULL,
    hero_level INT
);



