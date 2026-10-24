--liquibase formatted sql

--changeset student:1-create-engine
CREATE TABLE engine (
                        id              BIGSERIAL    PRIMARY KEY,
                        engine_type     VARCHAR(30)  NOT NULL,
                        volume_liters   NUMERIC(4,2),
                        horsepower      INT          NOT NULL,
                        torque_nm       INT,
                        serial_number   VARCHAR(64)  NOT NULL UNIQUE
);
--rollback DROP TABLE engine;

--changeset student:2-create-transmission
CREATE TABLE transmission (
                              id                BIGSERIAL    PRIMARY KEY,
                              transmission_type VARCHAR(30)  NOT NULL,
                              gears_count       INT,
                              serial_number     VARCHAR(64)  NOT NULL UNIQUE
);
--rollback DROP TABLE transmission;

--changeset student:3-create-on-board-computer
CREATE TABLE on_board_computer (
                                   id               BIGSERIAL    PRIMARY KEY,
                                   firmware_version VARCHAR(50)  NOT NULL,
                                   cpu_model        VARCHAR(100),
                                   ram_mb           INT,
                                   installed_at     TIMESTAMP    NOT NULL DEFAULT NOW()
);
--rollback DROP TABLE on_board_computer;

--changeset student:4-create-wheel
CREATE TABLE wheel (
                       id            BIGSERIAL    PRIMARY KEY,
                       position      VARCHAR(64)  NOT NULL,
                       radius_inch   NUMERIC(4,1) NOT NULL,
                       tire_brand    VARCHAR(100),
                       tire_model    VARCHAR(100),
                       serial_number VARCHAR(64)  NOT NULL UNIQUE,
                       installed_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
--rollback DROP TABLE wheel;

--changeset student:5-create-vehicle
CREATE TABLE vehicle (
                         id                   BIGSERIAL    PRIMARY KEY,
                         vin                  VARCHAR(17)  NOT NULL UNIQUE,
                         brand                VARCHAR(100) NOT NULL,
                         model                VARCHAR(100) NOT NULL,
                         production_year      INT          NOT NULL,
                         color                VARCHAR(50),
                         mileage_km           BIGINT       NOT NULL DEFAULT 0,
                         created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
                         engine_id            BIGINT       UNIQUE REFERENCES engine(id)            ON DELETE SET NULL,
                         transmission_id      BIGINT       UNIQUE REFERENCES transmission(id)      ON DELETE SET NULL,
                         on_board_computer_id BIGINT       UNIQUE REFERENCES on_board_computer(id) ON DELETE SET NULL
);
--rollback DROP TABLE vehicle;

--changeset student:6-create-vehicle-wheel
CREATE TABLE vehicle_wheel (
                               vehicle_id BIGINT NOT NULL REFERENCES vehicle(id) ON DELETE CASCADE,
                               wheel_id   BIGINT NOT NULL UNIQUE REFERENCES wheel(id) ON DELETE CASCADE,
                               PRIMARY KEY (vehicle_id, wheel_id)
);
--rollback DROP TABLE vehicle_wheel;

--changeset student:7-create-index-vehicle-wheel
CREATE INDEX idx_vehicle_wheel_vehicle ON vehicle_wheel(vehicle_id);
--rollback DROP INDEX idx_vehicle_wheel_vehicle;