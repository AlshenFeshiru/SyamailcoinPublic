CREATE DATABASE syamailcoin;

\c syamailcoin;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    wallet_address VARCHAR(50) UNIQUE NOT NULL,
    ml_dsa_hash VARCHAR(512),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wallets (
    id SERIAL PRIMARY KEY,
    address VARCHAR(50) UNIQUE NOT NULL,
    balance DECIMAL(40, 20) DEFAULT 0,
    is_genesis BOOLEAN DEFAULT FALSE,
    is_treasury BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE blocks (
    id SERIAL PRIMARY KEY,
    block_index BIGINT UNIQUE NOT NULL,
    hash VARCHAR(72) UNIQUE NOT NULL,
    previous_hash VARCHAR(72),
    timestamp BIGINT NOT NULL,
    exponential_value DECIMAL(40, 20),
    proof DOUBLE PRECISION,
    accumulation DECIMAL(100, 50),
    version INTEGER DEFAULT 1,
    storage_balanced BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
    id SERIAL PRIMARY KEY,
    tx_hash VARCHAR(72) UNIQUE NOT NULL,
    from_address VARCHAR(50) NOT NULL,
    to_address VARCHAR(50) NOT NULL,
    amount DECIMAL(40, 20) NOT NULL,
    timestamp BIGINT NOT NULL,
    block_index BIGINT,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (block_index) REFERENCES blocks(block_index)
);

CREATE TABLE virtual_nand (
    id SERIAL PRIMARY KEY,
    owner_address VARCHAR(50) NOT NULL,
    parameter_level INTEGER CHECK (parameter_level BETWEEN 1 AND 10),
    storage_capacity BIGINT,
    bandwidth_requirement VARCHAR(50),
    gpu_usage DECIMAL(5, 2),
    battery_drain DECIMAL(5, 2),
    bonus_amount DECIMAL(40, 20),
    purchase_price DECIMAL(40, 20),
    payment_status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activated_at TIMESTAMP,
    FOREIGN KEY (owner_address) REFERENCES wallets(address)
);

CREATE TABLE landmarks (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL,
    parameter_signature VARCHAR(255) UNIQUE NOT NULL,
    energy_level DECIMAL(40, 20) DEFAULT 0,
    member_count INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_activity TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE landmark_members (
    id SERIAL PRIMARY KEY,
    landmark_id INTEGER NOT NULL,
    wallet_address VARCHAR(50) NOT NULL,
    contribution DECIMAL(40, 20) DEFAULT 0,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (landmark_id) REFERENCES landmarks(id),
    FOREIGN KEY (wallet_address) REFERENCES wallets(address),
    UNIQUE(landmark_id, wallet_address)
);

CREATE TABLE payment_records (
    id SERIAL PRIMARY KEY,
    wallet_address VARCHAR(50) NOT NULL,
    payment_method VARCHAR(50),
    amount_fiat DECIMAL(20, 2),
    amount_sac DECIMAL(40, 20),
    currency VARCHAR(10),
    payment_provider VARCHAR(50),
    transaction_id VARCHAR(255),
    status VARCHAR(20) DEFAULT 'pending',
    webhook_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    confirmed_at TIMESTAMP,
    FOREIGN KEY (wallet_address) REFERENCES wallets(address)
);

CREATE TABLE blockrecursive_references (
    id SERIAL PRIMARY KEY,
    block_index BIGINT NOT NULL,
    references_block_index BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (block_index) REFERENCES blocks(block_index),
    FOREIGN KEY (references_block_index) REFERENCES blocks(block_index)
);

CREATE TABLE system_config (
    key VARCHAR(100) PRIMARY KEY,
    value TEXT NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO system_config (key, value) VALUES 
    ('current_stage', '0'),
    ('stage_remaining', '4104313.1758309230208'),
    ('total_supply', '235294'),
    ('genesis_hash', ''),
    ('treasury_balance', '0');

CREATE INDEX idx_transactions_from ON transactions(from_address);
CREATE INDEX idx_transactions_to ON transactions(to_address);
CREATE INDEX idx_blocks_index ON blocks(block_index);
CREATE INDEX idx_wallets_address ON wallets(address);
CREATE INDEX idx_landmarks_signature ON landmarks(parameter_signature);

INSERT INTO wallets (address, balance, is_genesis) VALUES ('SAC000000001', 235294, TRUE);
INSERT INTO wallets (address, balance, is_treasury) VALUES ('SACTREASURY001', 0, TRUE);
