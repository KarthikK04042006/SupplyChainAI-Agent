-- Create Database
CREATE DATABASE IF NOT EXISTS supply_chain_db;
USE supply_chain_db;

-- Users Table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    profile_picture VARCHAR(255),
    enabled BOOLEAN DEFAULT TRUE
);

-- Roles Table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- User Roles Mapping
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Upload History Table
CREATE TABLE upload_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    upload_time DATETIME NOT NULL,
    file_size BIGINT,
    row_count INT,
    status VARCHAR(20),
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Supply Chain Data Table
CREATE TABLE supply_chain_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id VARCHAR(50),
    order_date DATE,
    ship_date DATE,
    ship_mode VARCHAR(50),
    customer_id VARCHAR(50),
    customer_name VARCHAR(100),
    segment VARCHAR(50),
    country VARCHAR(50),
    city VARCHAR(50),
    state VARCHAR(50),
    region VARCHAR(50),
    product_id VARCHAR(50),
    category VARCHAR(50),
    sub_category VARCHAR(50),
    product_name VARCHAR(255),
    sales DOUBLE,
    quantity INT,
    discount DOUBLE,
    profit DOUBLE,
    shipping_cost DOUBLE,
    order_priority VARCHAR(50),
    upload_id BIGINT,
    FOREIGN KEY (upload_id) REFERENCES upload_history(id) ON DELETE CASCADE
);

-- Insert Default Admin User (password: admin123)
INSERT INTO roles (name) VALUES ('ROLE_ADMIN'), ('ROLE_USER');
INSERT INTO users (username, password, email, first_name, last_name) 
VALUES ('admin', '$2a$10$X/O1.z.Z1q8/Y7.z.Z1q8.z.Z1q8.z.Z1q8.z.Z1q8.z.Z1q8', 'admin@supplychain.ai', 'System', 'Admin');
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);
