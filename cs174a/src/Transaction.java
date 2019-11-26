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
import java.util.ArrayList;



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
		PURCHASE,
		PAY_FRIEND,
		FTM_FEE,
		TRANSFER,
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

	public static Transaction top_up(String to_pocket, String from_link, String date,
								 double amount, String cust_id, OracleConnection connection){
		// Check that we have a link between the two accounts
		if(!Account.accounts_are_linked(to_pocket, from_link, connection)){
			System.err.println("Top up failed -- Accounts are not linked or do not exist");
			return null;
		}
		Account pock_acc = Account.get_account_by_id(to_pocket, connection);
		if(!pock_acc.owner_id.equals(cust_id)){
			System.err.println("Top up failed -- Customer does not own this pocket account");
			return null;
		}

		// Check that owner account has enough money
		Account link_acc = Account.get_account_by_id(from_link, connection);

		// Check if it's the first transaction of the month
		if(Transaction.is_ftm(to_pocket, connection)){
			if(link_acc == null || link_acc.balance - amount - 5.00 <= 0.01){
				System.err.println("Top up failed -- not enough money");
				return null;
			}
		}else{
			if(link_acc == null || link_acc.balance - amount <= 0.01){
				System.err.println("Top up failed -- not enough money");
				return null;
			}
		}

		if(!Transaction.transfer_money(to_pocket, from_link, amount, connection)){
			System.err.println("Top up failed -- Could not transfer money to the pocket account");
			return null;
		}
		Transaction top_up_trans = Transaction.create_transaction(to_pocket, from_link, cust_id,
									 date, "" + Transaction.TransactionType.TOP_UP, amount, connection);
		if(top_up_trans == null){
			System.err.println("Top up failed -- Could not create transaction");
			return null;
		}

		// Charge $5 fee
		if(Transaction.is_ftm(to_pocket, connection)){
			Transaction fee = Transaction.create_transaction("", from_link, cust_id,
									 date, "" + Transaction.TransactionType.FTM_FEE, 5, connection);
		}

		return top_up_trans;
	}



	public static Transaction purchase(String from_pocket, String date, double amount, 
										String cust_id, OracleConnection connection){
		Transaction transact = null;
		Account pock_acc = Account.get_account_by_id(from_pocket, connection);

		if(pock_acc == null || Transaction.is_ftm(from_pocket, connection)){
			if(pock_acc.balance - amount - 5 < 0){
				System.err.println("Purchase failed -- not enough money for pruchase + fee");
				return null;
			}
		}

		// Check customer owns account
		if(!Transaction.cust_owns_acct(from_pocket, cust_id, connection)){
			System.err.println("Purchase failed -- customer doesn't own account");
			return null;
		}

		if(!pock_acc.owner_id.equals(cust_id)){
			System.err.println("Purchase failed -- Customer does not own this pocket account");
			return null;
		}

		if(!pock_acc.account_type.equals("" + Testable.AccountType.POCKET)){
			System.err.println("Must be pocket account");
			return null;
		}

		if(!Transaction.transfer_money("", from_pocket, amount, connection)){
			System.err.println("Purchase failed -- Could not transfer money to the pocket account");
			return null;
		}

		transact = Transaction.create_transaction("", from_pocket, cust_id,
									 date, "" + Transaction.TransactionType.PURCHASE, amount, connection);

		// Charge $5 fee
		if(Transaction.is_ftm(from_pocket, connection)){
			if(!Transaction.transfer_money("", from_pocket, 5, connection)){
				System.err.println("Purchase failed -- Could not transfer fee");
				return null;
			}
			Transaction fee = Transaction.create_transaction("", from_pocket, cust_id,
									 date, "" + Transaction.TransactionType.FTM_FEE, 5, connection);
			if(fee == null){
				System.err.println("Could not create fee transaction");
				return null;
			}
		}

		double new_balance = Account.get_account_balance(from_pocket, connection);
		if(new_balance >= 0 && (new_balance >= 0 || new_balance <= 0.01)){
			Account.close_account_by_id(from_pocket, connection);
		}else{
			return null;
		}

		return transact;
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


	public static Transaction transfer(String to_acct, String from_acct, String cust_id, String date, 
						 Transaction.TransactionType type, double amount, OracleConnection connection){
		
		// Check customer is an owner on both accounts
		ArrayList<String> to_acct_owners = Account.get_account_owners(to_acct, connection);
		ArrayList<String> from_acct_owners = Account.get_account_owners(from_acct, connection);
		boolean owns_to_acct = false;
		boolean owns_from_acct = false;
		for(int i = 0; i < to_acct_owners.size(); i++){
			if(to_acct_owners.get(i).equals(cust_id)){
				owns_to_acct = true;
				break;
			}
		}
		for(int i = 0; i < from_acct_owners.size(); i++){
			if(from_acct_owners.get(i).equals(cust_id)){
				owns_from_acct = true;
				break;
			}
		}

		if(!(owns_to_acct && owns_from_acct)){
			System.err.println("Transfer failed -- customer does not own both accounts");
			return null;
		}

		// Check not transferring over 2000
		if(amount > 2000){
			System.err.println("Transfer failed -- cannot transfer >2000");
			return null;
		}

		// Make sure from account has >= amount
		if(Account.get_account_balance(from_acct, connection) < amount){
			System.err.println("Transfer failed -- from account balance insufficient");
			return null;
		}

		// Try to transfer money
		if(!Transaction.transfer_money(to_acct, from_acct, amount, connection)){
			System.err.println("Transaction failed -- could not transfer money");
			return null;
		}

		// Create a transaction
		Transaction transact = Transaction.create_transaction(to_acct, from_acct, cust_id,
									 Bank.get_date(connection), "" + Transaction.TransactionType.TRANSFER
									 , amount, connection);

		if(transact == null){
			System.err.println("Transfer failed -- could not create transaction");
			return null;
		}

		double from_new_balance = Account.get_account_balance(from_acct, connection);

		// Close from account if 0 <= amount <= 0.01
		if(0 <= from_new_balance && 0.01 >= from_new_balance){
			if(!Account.close_account_by_id(from_acct, connection)){
				System.err.println("Error occurred during account closing");
				return null;
			}
		}

		return transact;
	}


	// STUBBBBBBBBBBBBBB
	public static boolean is_ftm(String a_id, OracleConnection connection){
		return false;
	}

	// Transfer money between account(s) if they exist and are not closed
	public static boolean transfer_money(String to_acct, String from_acct, double amount, OracleConnection connection){
		// Make sure at least one of to or from is specified
		if(to_acct.equals("") && from_acct.equals("")){
			return false;
		}

		// Get accounts, returning null on ""
		Account from_acct_temp = Account.get_account_by_id(from_acct, connection);
		Account to_acct_temp = Account.get_account_by_id(to_acct, connection);

		// Check required accounts exist
		if(!to_acct.equals("") && to_acct_temp == null ||
			!from_acct.equals("") && from_acct_temp == null){
			System.err.println("One or both accounts supplied do not exist");
			return false;
		}

		// Check account is open
		if(!from_acct.equals("") && from_acct_temp != null && !from_acct_temp.is_open ||
			!to_acct.equals("") && to_acct_temp != null && !to_acct_temp.is_open){
			System.err.println("A transaction cannot be done on a closed acct -- failed");
			return false;
		}

		if(!from_acct.equals("")){
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
			if(!from_acct_temp.account_type.equals("" + Testable.AccountType.POCKET) &&
				 from_acct_temp.balance - amount <= 0.01 && 
				from_acct_temp.balance >= 0){
				if(!Account.close_account_by_id(from_acct, connection)){
					return false;
				}
			}
		}

		if(!to_acct.equals("")){
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