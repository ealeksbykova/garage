--liquibase formatted sql

--changeset student:100-insert-engines
INSERT INTO engine (id, engine_type, volume_liters, horsepower, torque_nm, serial_number) VALUES
                                                                                              (1, 'PETROL',   1.6,  110, 155, 'ENG-VW-0001'),
                                                                                              (2, 'DIESEL',   2.0,  150, 340, 'ENG-VW-0002'),
                                                                                              (3, 'ELECTRIC', 0, 204, 310, 'ENG-TS-0001'),
                                                                                              (4, 'HYBRID',   1.8,  122, 142, 'ENG-TY-0001');
--rollback DELETE FROM engine WHERE id BETWEEN 1 AND 4;

--changeset student:101-insert-transmissions
INSERT INTO transmission (id, transmission_type, gears_count, serial_number) VALUES
                                                                                 (1, 'MANUAL',    5, 'TRN-VW-0001'),
                                                                                 (2, 'AUTOMATIC', 8, 'TRN-VW-0002'),
                                                                                 (3, 'CVT',       0, 'TRN-TY-0001'),
                                                                                 (4, 'ROBOTIC',   7, 'TRN-BM-0001');
--rollback DELETE FROM transmission WHERE id BETWEEN 1 AND 4;

--changeset student:102-insert-on-board-computers
INSERT INTO on_board_computer (id, firmware_version, cpu_model, ram_mb, installed_at) VALUES
                                                                                          (1, 'v1.4.2', 'ARM Cortex-M7',   512,  '2024-01-15 10:00:00'),
                                                                                          (2, 'v2.0.0', 'Qualcomm SA8155', 2048, '2024-03-20 14:30:00'),
                                                                                          (3, 'v3.1.1', 'NVIDIA Orin',     4096, '2024-06-01 09:15:00');
--rollback DELETE FROM on_board_computer WHERE id BETWEEN 1 AND 3;

--changeset student:103-insert-wheels
INSERT INTO wheel (id, position, radius_inch, tire_brand, tire_model, serial_number, installed_at) VALUES
                                                                                                       (1,  'FRONT_LEFT',  16.0, 'Michelin',    'Primacy 4',        'WHL-VW-0001-FL', '2024-01-15 10:00:00'),
                                                                                                       (2,  'FRONT_RIGHT', 16.0, 'Michelin',    'Primacy 4',        'WHL-VW-0001-FR', '2024-01-15 10:00:00'),
                                                                                                       (3,  'REAR_LEFT',   16.0, 'Michelin',    'Primacy 4',        'WHL-VW-0001-RL', '2024-01-15 10:00:00'),
                                                                                                       (4,  'REAR_RIGHT',  16.0, 'Michelin',    'Primacy 4',        'WHL-VW-0001-RR', '2024-01-15 10:00:00'),
                                                                                                       (5,  'FRONT_LEFT',  17.0, 'Continental', 'PremiumContact 6', 'WHL-VW-0002-FL', '2024-03-20 14:30:00'),
                                                                                                       (6,  'FRONT_RIGHT', 17.0, 'Continental', 'PremiumContact 6', 'WHL-VW-0002-FR', '2024-03-20 14:30:00'),
                                                                                                       (7,  'REAR_LEFT',   17.0, 'Continental', 'PremiumContact 6', 'WHL-VW-0002-RL', '2024-03-20 14:30:00'),
                                                                                                       (8,  'REAR_RIGHT',  17.0, 'Continental', 'PremiumContact 6', 'WHL-VW-0002-RR', '2024-03-20 14:30:00'),
                                                                                                       (9,  'FRONT_LEFT',  18.0, 'Pirelli',     'P Zero',           'WHL-TS-0001-FL', '2024-06-01 09:15:00'),
                                                                                                       (10, 'FRONT_RIGHT', 18.0, 'Pirelli',     'P Zero',           'WHL-TS-0001-FR', '2024-06-01 09:15:00'),
                                                                                                       (11, 'REAR_LEFT',   18.0, 'Pirelli',     'P Zero',           'WHL-TS-0001-RL', '2024-06-01 09:15:00'),
                                                                                                       (12, 'REAR_RIGHT',  18.0, 'Pirelli',     'P Zero',           'WHL-TS-0001-RR', '2024-06-01 09:15:00');
--rollback DELETE FROM wheel WHERE id BETWEEN 1 AND 12;

--changeset student:104-insert-vehicles
INSERT INTO vehicle (id, vin, brand, model, production_year, color, mileage_km, created_at,
                     engine_id, transmission_id, on_board_computer_id) VALUES
                                                                           (1, 'WVWZZZ1JZXW000001', 'Volkswagen', 'Golf',    2020, 'Black', 45000, '2024-01-15 10:00:00', 1, 1, 1),
                                                                           (2, 'WVWZZZ3CZLE000002', 'Volkswagen', 'Passat',  2022, 'White', 18000, '2024-03-20 14:30:00', 2, 2, 2),
                                                                           (3, '5YJ3E1EA7KF000003', 'Tesla',      'Model 3', 2023, 'Red',   12000, '2024-06-01 09:15:00', 3, 3, 3),
                                                                           (4, 'XW8ZZZ61ZJG000004', 'Volkswagen', 'Polo',    2019, 'Blue',  78000, '2024-02-01 08:00:00', NULL, NULL, NULL);
--rollback DELETE FROM vehicle WHERE id BETWEEN 1 AND 4;

--changeset student:105-insert-vehicle-wheel
INSERT INTO vehicle_wheel (vehicle_id, wheel_id) VALUES
                                                     (1, 1), (1, 2), (1, 3), (1, 4),
                                                     (2, 5), (2, 6), (2, 7), (2, 8),
                                                     (3, 9), (3, 10), (3, 11), (3, 12);
--rollback DELETE FROM vehicle_wheel WHERE vehicle_id BETWEEN 1 AND 3;