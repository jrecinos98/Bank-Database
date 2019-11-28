CREATE TABLE customers (
	c_id VARCHAR(9) NOT NULL,
    c_name VARCHAR(32),
	address VARCHAR(64),
	encrypted_pin VARCHAR(64) NOT NULL,  /* Will by set to 1717   (encrpyted) by default */
	PRIMARY KEY (c_id)
)
#NEW#
CREATE TABLE accounts (
	a_id VARCHAR(32) NOT NULL,
	owner_id VARCHAR(9) NOT NULL,
	account_type VARCHAR(32) NOT NULL,
	bank_branch VARCHAR(32),
	balance REAL NOT NULL CHECK (balance>=0),
	is_open NUMBER(1, 0) DEFAULT 0, 
    interest_date VARCHAR(10),
	PRIMARY KEY (a_id),
	FOREIGN KEY (owner_id) REFERENCES customers(c_id)
)
#NEW#
CREATE TABLE pocketlinks (
	pocket_id VARCHAR(32) NOT NULL,
    link_id VARCHAR(32) NOT NULL,
	PRIMARY KEY(pocket_id),
	FOREIGN KEY (link_id) REFERENCES accounts(a_id) ON DELETE CASCADE
)
#NEW#
CREATE TABLE transactions (
	t_id INTEGER GENERATED BY DEFAULT ON NULL AS IDENTITY,
	to_acct VARCHAR(32),
	from_acct VARCHAR(32),
	cust_id VARCHAR(9),
	t_date VARCHAR(10),
	t_type VARCHAR(16),
	amount REAL,
	PRIMARY KEY(t_id),
	FOREIGN KEY(to_acct) REFERENCES accounts(a_id),
	FOREIGN KEY(from_acct) REFERENCES accounts(a_id),
	FOREIGN KEY(cust_id) REFERENCES customers(c_id),
	CONSTRAINT has_to_or_from CHECK( to_acct IS NOT NULL
 									 OR from_acct IS NOT NULL)
)
#NEW#
CREATE TABLE custaccounts (
	c_id VARCHAR(9) NOT NULL,
	a_id VARCHAR(32) NOT NULL,
	PRIMARY KEY(c_id, a_id),
	FOREIGN KEY(c_id) REFERENCES customers(c_id),
	FOREIGN KEY(a_id) REFERENCES accounts(a_id) ON DELETE CASCADE
)
#NEW#
CREATE TABLE bank (
	b_id INTEGER,
	day VARCHAR(2),
	month VARCHAR(2),
	year VARCHAR(4),
	chk_int_intrst REAL,
	sav_intrst REAL,
	last_intrst_date VARCHAR(10),
	PRIMARY KEY(b_id)
)

