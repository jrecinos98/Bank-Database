DROP TABLE  customers CASCADE CONSTRAINTS;
DROP TABLE  accounts CASCADE CONSTRAINTS;
DROP TABLE  pocketlinks CASCADE CONSTRAINTS;
DROP TABLE  transactions CASCADE CONSTRAINTS;
DROP TABLE  custaccounts CASCADE CONSTRAINTS;



CREATE TABLE customers (
	c_id CHAR(9) NOT NULL,
    c_name VARCHAR(32),
	address VARCHAR(64),
	encrypted_pin CHAR(64) NOT NULL,  /* Will by set to 1717   (encrpyted) by default */
	PRIMARY KEY (c_id)
);

CREATE TABLE accounts (
	a_id INTEGER NOT NULL,
	owner_id CHAR(9) NOT NULL,
	account_type VARCHAR(16) NOT NULL,
	bank_branch VARCHAR(32),
	balance REAL NOT NULL CHECK (balance>=0),
	is_open NUMBER(1, 0) DEFAULT 0, 
    interest_date DATE,
	PRIMARY KEY (a_id),
	FOREIGN KEY (owner_id) REFERENCES customers(c_id)
);

CREATE TABLE pocketlinks (
	pocket_id INTEGER NOT NULL,
    link_id INTEGER NOT NULL,
	PRIMARY KEY(pocket_id),
	FOREIGN KEY (link_id) REFERENCES accounts(a_id) ON DELETE CASCADE
);

CREATE TABLE transactions (
	to_acct INTEGER,
	from_acct INTEGER,
	cust_id CHAR(9) NOT NULL,
	t_id INTEGER NOT NULL,
	t_date DATE,
	t_type VARCHAR(16),
	check_number INTEGER,
	amount REAL,
	PRIMARY KEY(t_id),
	FOREIGN KEY(to_acct) REFERENCES accounts(a_id),
	FOREIGN KEY(from_acct) REFERENCES accounts(a_id),
	FOREIGN KEY(cust_id) REFERENCES customers(c_id),
	CONSTRAINT has_to_or_from CHECK( to_acct IS NOT NULL
 									 OR from_acct IS NOT NULL)
);

CREATE TABLE custaccounts (
	c_id CHAR(9) NOT NULL,
	a_id INTEGER NOT NULL,
	PRIMARY KEY(c_id, a_id),
	FOREIGN KEY(c_id) REFERENCES customers(c_id),
	FOREIGN KEY(a_id) REFERENCES accounts(a_id)
);
