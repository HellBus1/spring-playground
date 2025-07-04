-- Create Account table
CREATE TABLE account (
    id BIGSERIAL PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    balance DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Transaction table
CREATE TABLE transaction (
    id BIGSERIAL PRIMARY KEY,
    transaction_reference VARCHAR(36) UNIQUE NOT NULL,
    source_account_id BIGINT REFERENCES account(id),
    destination_account_id BIGINT REFERENCES account(id),
    amount DECIMAL(15,2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Transaction Status History table
CREATE TABLE transaction_status_history (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT REFERENCES transaction(id),
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);