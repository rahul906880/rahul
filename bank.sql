create database bank_management;
use bank_management;


-- Create the users table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    form_no VARCHAR(10) UNIQUE,
    name VARCHAR(100),
    father_name VARCHAR(100),
    dob DATE,
    gender VARCHAR(10),
    email VARCHAR(100),
    marital_status VARCHAR(20),
    address VARCHAR(255),
    city VARCHAR(50),
    pincode VARCHAR(10),
    state VARCHAR(50),
    religion VARCHAR(50),
    category VARCHAR(20),
    income VARCHAR(20),
    education VARCHAR(50),
    occupation VARCHAR(50),
    pan_number VARCHAR(10),
    aadhar_number VARCHAR(12),
    senior_citizen VARCHAR(3),
    existing_account VARCHAR(3)
);

-- Create the accounts table
CREATE TABLE accounts (
    account_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    account_number VARCHAR(16) UNIQUE,
    account_type VARCHAR(50),
    balance DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create the login table
CREATE TABLE login (
    account_id INT,
    card_number VARCHAR(16) UNIQUE,
    pin_hash VARCHAR(60),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);

-- Create the transactions table
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_id INT,
    transaction_date DATETIME,
    type VARCHAR(20),
    amount DECIMAL(10,2),
    FOREIGN KEY (account_id) REFERENCES accounts(account_id)
);
ALTER TABLE accounts ADD COLUMN facilities VARCHAR(255);
ALTER TABLE accounts ADD CONSTRAINT positive_balance CHECK (balance >= 0);
USE bank_management;

-- Adding index on users.form_no (already UNIQUE, but explicit index for clarity)
CREATE INDEX idx_users_form_no ON users (form_no);

-- Adding index on login.card_number (already UNIQUE, but explicit index for clarity)
CREATE INDEX idx_login_card_number ON login (card_number);
SHOW INDEXES FROM users;
SHOW INDEXES FROM login;

select * from users;

select * from accounts;
select * from login;
select * from transactions;