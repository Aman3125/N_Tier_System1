CREATE DATABASE IF NOT EXISTS car_garage_db;
USE car_garage_db;

CREATE TABLE IF NOT EXISTS customer (
                                        customer_id INT NOT NULL AUTO_INCREMENT,
                                        first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    address VARCHAR(150) NOT NULL,
    PRIMARY KEY (customer_id)
    );

CREATE TABLE IF NOT EXISTS vehicle (
                                       vehicle_id INT NOT NULL AUTO_INCREMENT,
                                       customer_id INT NOT NULL,
                                       make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    registration_number VARCHAR(20) NOT NULL,
    year INT NOT NULL,
    PRIMARY KEY (vehicle_id),
    CONSTRAINT fk_vehicle_customer
    FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
    );

CREATE TABLE IF NOT EXISTS service_job (
                                           service_job_id INT NOT NULL AUTO_INCREMENT,
                                           vehicle_id INT NOT NULL,
                                           description VARCHAR(255) NOT NULL,
    status ENUM('PENDING','IN_PROGRESS','COMPLETED') NOT NULL DEFAULT 'PENDING',
    cost DECIMAL(10,2) NOT NULL,
    date_created DATE NOT NULL,
    PRIMARY KEY (service_job_id),
    CONSTRAINT fk_servicejob_vehicle
    FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id)
    );

CREATE INDEX idx_service_job_status ON service_job(status);

INSERT INTO customer (first_name, last_name, phone_number, email, address) VALUES
                                                                               ('John', 'Murphy', '0851111111', 'john.murphy@email.com', '12 Oak Drive, Dublin'),
                                                                               ('Sarah', 'Kelly', '0852222222', 'sarah.kelly@email.com', '8 River Street, Cork'),
                                                                               ('Michael', 'Byrne', '0853333333', 'michael.byrne@email.com', '21 Hill View, Galway'),
                                                                               ('Emma', 'Doyle', '0854444444', 'emma.doyle@email.com', '15 Green Park, Limerick');

INSERT INTO vehicle (customer_id, make, model, registration_number, year) VALUES
                                                                              (1, 'Toyota', 'Corolla', '12-D-12345', 2012),
                                                                              (1, 'Ford', 'Focus', '151-D-54321', 2015),
                                                                              (2, 'Volkswagen', 'Golf', '181-C-67890', 2018),
                                                                              (2, 'Hyundai', 'i30', '191-C-11223', 2019),
                                                                              (3, 'BMW', '320D', '171-G-99887', 2017),
                                                                              (4, 'Audi', 'A4', '202-D-44556', 2020);

INSERT INTO service_job (vehicle_id, description, status, cost, date_created) VALUES
                                                                                  (1, 'Oil change and filter replacement', 'COMPLETED', 89.99, '2026-01-10'),
                                                                                  (2, 'Brake pad replacement', 'IN_PROGRESS', 220.00, '2026-01-12'),
                                                                                  (3, 'Full service', 'PENDING', 300.00, '2026-01-15'),
                                                                                  (4, 'Battery replacement', 'COMPLETED', 140.50, '2026-01-18'),
                                                                                  (5, 'Engine diagnostic check', 'PENDING', 95.00, '2026-01-20'),
                                                                                  (6, 'Tyre replacement', 'IN_PROGRESS', 400.00, '2026-01-22');