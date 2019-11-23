package cs174a;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.Scanner;


public class Transaction {
	public String to_acct;
	public String from_acct;
	public String cust_id;
	public String date;
	public String transaction_type;
	public double amount;

	public enum TransactionType {
		DEPOSIT,
		WITHDRAWAL,
		TOP_UP,
		PAY_FRIEND
	}

	public static Transaction create_transaction(String to_acct, String from_acct, String cust_id,
									 String date, String transaction_type, double amount, OracleConnection connection){
		
		Transaction transaction = null;
		String query = String.format("INSERT INTO transactions (to_acct, from_acct, cust_id, t_date, t_type, amount) " +
	    							 "VALUES ('%s', '%s', '%s', '%s', '%s', %f)",
	    							  to_acct, from_acct, cust_id, date, transaction_type, amount);
		try( Statement statement = connection.createStatement() ) {
			try{
				int updates = statement.executeUpdate( query );
				if(updates == 0){
					return null;
				}
				transaction = new Transaction(to_acct, from_acct, cust_id, date, transaction_type, amount);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return transaction;
	}

	public static boolean cust_owns_acct(String a_id, String c_id, OracleConnection connection){
		String query = String.format("SELECT * FROM custaccounts WHERE c_id = '%s' AND a_id = '%s'",
											c_id, a_id);
		// Check customer owns the account
		try( Statement statement = connection.createStatement() ) {
			try( ResultSet rs = statement.executeQuery( query ) ){
				if(rs.next()){
					return true;
				}
			}catch(SQLException e){
				e.printStackTrace();
				return false;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static boolean withdraw(String from_acct, String cust_id, String date, 
						 Transaction.TransactionType type, double amount, OracleConnection connection){
		// Check customer exists
		if(Customer.get_cust_by_id(cust_id, connection) == null){
			System.err.println("Withdraw failed -- customer doesn't exist");
			return false;
		}

		// Check customer owns account
		if(!Transaction.cust_owns_acct(from_acct, cust_id, connection)){
			System.err.println("Withdraw failed -- customer doesn't own account");
			return false;
		}

		// Make sure account is NOT a pocket account
		Account account = Account.get_account_by_id(from_acct, connection);
		if(account != null && account.account_type.equals("" + Testable.AccountType.POCKET)){
			System.err.println("Withdraw failed -- cannot deposit to pocket account");
			return false;
		}

		// Transfer money into the account
		if(!Transaction.transfer_money("", from_acct, amount, connection)){
			return false;
		}

		// Create a transaction record
		Transaction transaction = Transaction.create_transaction("", from_acct, cust_id, date,"" + type, amount, connection);
		if(transaction == null){
			System.err.println("Withdraw failed -- could not create transaction");
			return false;
		}

		return true;
	}

	public static boolean deposit(String to_acct, String cust_id, String date, 
						 Transaction.TransactionType type, double amount, OracleConnection connection){
		
		// Check customer exists
		if(Customer.get_cust_by_id(cust_id, connection) == null){
			System.err.println("Deposit failed -- customer doesn't exist");
			return false;
		}

		// Check customer owns account
		if(!Transaction.cust_owns_acct(to_acct, cust_id, connection)){
			System.err.println("Deposit failed -- customer doesn't own account");
			return false;
		}

		// Make sure account is NOT a pocket account
		Account account = Account.get_account_by_id(to_acct, connection);
		if(account != null && account.account_type.equals("" + Testable.AccountType.POCKET)){
			System.err.println("Deposit failed -- cannot deposit to pocket account");
			return false;
		}

		// Transfer money into the account
		if(!Transaction.transfer_money(to_acct, "", amount, connection)){
			return false;
		}

		// Create a transaction record
		Transaction transaction = Transaction.create_transaction(to_acct, "", cust_id, date,"" + type, amount, connection);
		if(transaction == null){
			System.err.println("Deposit failed -- could not create transaction");
			return false;
		}

		return true;
	}

	// Transfer money between account(s) if they exist and are not closed
	public static boolean transfer_money(String to_acct, String from_acct, double amount, OracleConnection connection){
		// Make sure at least one of to or from is specified
		if(to_acct == "" && from_acct == ""){
			return false;
		}

		// Get accounts, returning null on ""
		Account from_acct_temp = Account.get_account_by_id(from_acct, connection);
		Account to_acct_temp = Account.get_account_by_id(to_acct, connection);

		// Check required accounts exist
		if(to_acct != "" && to_acct_temp == null ||
			from_acct != "" && from_acct_temp == null){
			System.err.println("One or both accounts supplied do not exist");
			return false;
		}

		// Check account is open
		if(from_acct != "" && from_acct_temp != null && !from_acct_temp.is_open ||
			to_acct != "" && to_acct_temp != null && !to_acct_temp.is_open){
			System.err.println("A transaction cannot be done on a closed acct -- failed");
			return false;
		}

		if(from_acct != ""){
			// Check transaction won't result in negative balance
			if(from_acct_temp.balance - amount < 0){
				System.err.println("Transaction would result in a negative balance -- failed");
				return false;
			}

			// Subtract amount from this account
			String query = String.format("UPDATE accounts SET balance = %s WHERE a_id = '%s'",
											from_acct_temp.balance - amount, from_acct);
			try( Statement statement = connection.createStatement() ) {
				try{
					int updates = statement.executeUpdate( query );
					if(updates == 0){
						return false;
					}
				}catch(SQLException e){
					e.printStackTrace();
					return false;
				}
			}catch(SQLException e){
				e.printStackTrace();
				return false;
			}

			// If balance is in 0 < x < 0.01 -- close account
			if(from_acct_temp.balance - amount <= 0.01 && from_acct_temp.balance >= 0){
				if(!Account.close_account_by_id(from_acct, connection)){
					return false;
				}
			}
		}

		if(to_acct != ""){
			// Add amount to this account
			String query = String.format("UPDATE accounts SET balance = %s WHERE a_id = '%s'",
											to_acct_temp.balance + amount, to_acct);
			try( Statement statement = connection.createStatement() ) {
				try{
					int updates = statement.executeUpdate( query );
					if(updates == 0){
						return false;
					}
				}catch(SQLException e){
					e.printStackTrace();
					return false;
				}
			}catch(SQLException e){
				e.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public Transaction(String to_acct, String from_acct, String cust_id,
						String date, String transaction_type, double amount){
		this.to_acct = to_acct;
		this.from_acct = from_acct;
		this.cust_id = cust_id;
		this.date = date;
		this.transaction_type = transaction_type;
		this.amount = amount;
	}

}