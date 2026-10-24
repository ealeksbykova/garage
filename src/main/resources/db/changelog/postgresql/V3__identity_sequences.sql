--liquibase formatted sql

--changeset lab-pg:3-identities dbms:postgresql
-- Explicit seed IDs do not advance BIGSERIAL sequences.
SELECT setval(pg_get_serial_sequence('engine', 'id'), (SELECT max(id) FROM engine));
SELECT setval(pg_get_serial_sequence('transmission', 'id'), (SELECT max(id) FROM transmission));
SELECT setval(pg_get_serial_sequence('on_board_computer', 'id'), (SELECT max(id) FROM on_board_computer));
SELECT setval(pg_get_serial_sequence('wheel', 'id'), (SELECT max(id) FROM wheel));
SELECT setval(pg_get_serial_sequence('vehicle', 'id'), (SELECT max(id) FROM vehicle));
