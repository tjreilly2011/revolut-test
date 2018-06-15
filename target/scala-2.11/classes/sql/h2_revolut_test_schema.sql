CREATE TABLE IF NOT EXISTS customer(
    id BIGINT auto_increment primary key,
    login_name VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    no_of_accounts INT
);

CREATE TABLE IF NOT EXISTS wallet(
    id BIGINT auto_increment primary key,
    cust_id BIGINT,
    name VARCHAR(255),
    currency VARCHAR(255),
    balance DECIMAL(20, 2),
    foreign key (cust_id) references customer(id)
);

CREATE TABLE IF NOT EXISTS wallet_trans(
    id BIGINT auto_increment primary key,
    wallet_id_to_credit BIGINT,
    wallet_id_to_debit BIGINT,
    amount DECIMAL(20, 2),
    time TIMESTAMP,
    foreign key (wallet_id_to_credit) references wallet(id),
    foreign key (wallet_id_to_debit) references wallet(id)
);