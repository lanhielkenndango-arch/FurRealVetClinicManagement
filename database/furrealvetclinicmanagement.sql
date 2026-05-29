-- FurReal Vet Clinic Management
-- Database script for Final Project Laboratory Activity submission.

CREATE DATABASE IF NOT EXISTS furrealvetclinicmanagement
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE furrealvetclinicmanagement;

CREATE TABLE IF NOT EXISTS clients (
    client_id INT PRIMARY KEY,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    phone VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password_text VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS pets (
    pet_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    pet_name VARCHAR(80) NOT NULL,
    pet_type VARCHAR(40) NOT NULL,
    breed VARCHAR(80) NOT NULL,
    age INT NOT NULL,
    CONSTRAINT fk_pets_client
        FOREIGN KEY (client_id)
        REFERENCES clients(client_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS clinic_services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(100) NOT NULL,
    category VARCHAR(60) NOT NULL,
    price DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    service_date VARCHAR(40) NOT NULL DEFAULT ''
);

CREATE TABLE IF NOT EXISTS visits (
    visit_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id INT NOT NULL,
    pet_id INT NOT NULL,
    visit_date VARCHAR(40) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Scheduled',
    total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_visits_client
        FOREIGN KEY (client_id)
        REFERENCES clients(client_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_visits_pet
        FOREIGN KEY (pet_id)
        REFERENCES pets(pet_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS visit_services (
    visit_service_id INT AUTO_INCREMENT PRIMARY KEY,
    visit_id INT NOT NULL,
    service_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    line_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    CONSTRAINT fk_visit_services_visit
        FOREIGN KEY (visit_id)
        REFERENCES visits(visit_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_visit_services_service
        FOREIGN KEY (service_id)
        REFERENCES clinic_services(service_id)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

-- Starter service records used by the visit and transaction form.
INSERT INTO clinic_services (service_name, category, price, service_date)
SELECT 'Rabbies Vaccine', 'Vaccines', 350.00, ''
WHERE NOT EXISTS (SELECT 1 FROM clinic_services WHERE service_name = 'Rabbies Vaccine');

INSERT INTO clinic_services (service_name, category, price, service_date)
SELECT 'Teeth Cleaning', 'Dental', 1500.00, ''
WHERE NOT EXISTS (SELECT 1 FROM clinic_services WHERE service_name = 'Teeth Cleaning');

INSERT INTO clinic_services (service_name, category, price, service_date)
SELECT 'Ear Cleaning', 'General', 250.00, ''
WHERE NOT EXISTS (SELECT 1 FROM clinic_services WHERE service_name = 'Ear Cleaning');

INSERT INTO clinic_services (service_name, category, price, service_date)
SELECT 'Nail Trimming', 'Grooming', 150.00, ''
WHERE NOT EXISTS (SELECT 1 FROM clinic_services WHERE service_name = 'Nail Trimming');

INSERT INTO clinic_services (service_name, category, price, service_date)
SELECT 'Checkup', 'General', 100.00, ''
WHERE NOT EXISTS (SELECT 1 FROM clinic_services WHERE service_name = 'Checkup');

INSERT INTO clinic_services (service_name, category, price, service_date)
SELECT 'Haircutting', 'Grooming', 500.00, ''
WHERE NOT EXISTS (SELECT 1 FROM clinic_services WHERE service_name = 'Haircutting');

INSERT INTO clinic_services (service_name, category, price, service_date)
SELECT 'Styling', 'Grooming', 700.00, ''
WHERE NOT EXISTS (SELECT 1 FROM clinic_services WHERE service_name = 'Styling');

INSERT INTO clinic_services (service_name, category, price, service_date)
SELECT 'Flea and Tick Dips', 'Grooming', 400.00, ''
WHERE NOT EXISTS (SELECT 1 FROM clinic_services WHERE service_name = 'Flea and Tick Dips');
