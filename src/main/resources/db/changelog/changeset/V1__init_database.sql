--liquibase formatted sql

--changeset student:1-create-engine
CREATE TABLE engine (
                        id              INTEGER PRIMARY KEY AUTOINCREMENT,
                        engine_type     TEXT    NOT NULL,
                        volume_liters   REAL,
                        horsepower      INTEGER NOT NULL,
                        torque_nm       INTEGER,
                        serial_number   TEXT    NOT NULL UNIQUE
);
--rollback DROP TABLE engine;

--changeset student:2-create-transmission
CREATE TABLE transmission (
                              id                INTEGER PRIMARY KEY AUTOINCREMENT,
                              transmission_type TEXT    NOT NULL,
                              gears_count       INTEGER,
                              serial_number     TEXT    NOT NULL UNIQUE
);
--rollback DROP TABLE transmission;

--changeset student:3-create-on-board-computer
CREATE TABLE on_board_computer (
                                   id               INTEGER PRIMARY KEY AUTOINCREMENT,
                                   firmware_version TEXT    NOT NULL,
                                   cpu_model        TEXT,
                                   ram_mb           INTEGER,
                                   installed_at     TEXT    NOT NULL
);
--rollback DROP TABLE on_board_computer;

--changeset student:4-create-wheel
CREATE TABLE wheel (
                       id            INTEGER PRIMARY KEY AUTOINCREMENT,
                       position      TEXT    NOT NULL,
                       radius_inch   REAL    NOT NULL,
                       tire_brand    TEXT,
                       tire_model    TEXT,
                       serial_number TEXT    NOT NULL UNIQUE,
                       installed_at  TEXT    NOT NULL
);
--rollback DROP TABLE wheel;

--changeset student:5-create-vehicle
CREATE TABLE vehicle (
                         id                   INTEGER PRIMARY KEY AUTOINCREMENT,
                         vin                  TEXT    NOT NULL UNIQUE,
                         brand                TEXT    NOT NULL,
                         model                TEXT    NOT NULL,
                         production_year      INTEGER NOT NULL,
                         color                TEXT,
                         mileage_km           INTEGER NOT NULL DEFAULT 0,
                         created_at           TEXT    NOT NULL,
                         engine_id            INTEGER UNIQUE REFERENCES engine(id)            ON DELETE SET NULL,
                         transmission_id      INTEGER UNIQUE REFERENCES transmission(id)      ON DELETE SET NULL,
                         on_board_computer_id INTEGER UNIQUE REFERENCES on_board_computer(id) ON DELETE SET NULL
);
--rollback DROP TABLE vehicle;

--changeset student:6-create-vehicle-wheel
CREATE TABLE vehicle_wheel (
                               vehicle_id INTEGER NOT NULL REFERENCES vehicle(id) ON DELETE CASCADE,
                               wheel_id   INTEGER NOT NULL UNIQUE REFERENCES wheel(id) ON DELETE CASCADE,
                               PRIMARY KEY (vehicle_id, wheel_id)
);
--rollback DROP TABLE vehicle_wheel;

--changeset student:7-create-index-vehicle-wheel
CREATE INDEX idx_vehicle_wheel_vehicle ON vehicle_wheel(vehicle_id);
--rollback DROP INDEX idx_vehicle_wheel_vehicle;