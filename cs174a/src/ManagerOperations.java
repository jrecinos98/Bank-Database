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

public class ManagerOperations {

	public String enter_check_transaction(String from_acct, String cust_id, double amount, OracleConnection connection){
		// Make check transaction
		Transaction transact = Transaction.write_check(from_acct, cust_id, Bank.get_date(connection), 
		 				 Transaction.TransactionType.WRITE_CHECK, amount, connection);
		// If success, return check number (generated by date + trnsaaction id)
		if(transact != null){
			return "cno" + Bank.get_year(connection)+Bank.get_month(connection)+Bank.get_day(connection)+transact.t_id;
		}else{
			return "-1";
		}
	}

	public boolean generate_monthly_statement(){
		// Generate monthly statement
		return false;
	}

	public ArrayList<String> list_closed_accounts(OracleConnection connection){
		// Get all closed accounts
		ArrayList<String> accounts = Account.get_closed_accounts(connection);
		return accounts;
	}

	public ArrayList<String> generate_dter(OracleConnection connection){
		// Execute query and return all customers with sums >= 10000
		ArrayList<String> customers = new ArrayList<String>();
		String query = String.format("SELECT C.c_id, SUM(T.amount)" +
									 "FROM custaccounts C, transactions T" +
									 "WHERE C.a_id = T.to_acct AND (T.t_type = 'WIRE' OR T.t_type = 'TRANSFER' OR T.t_type = 'DEPOSIT')" +
									 "GROUP BY C.c_id" +
									 "HAVING SUM(T.amount) >= 10000");
		try( Statement statement = connection.createStatement() ) {
			try( ResultSet rs = statement.executeQuery( query )){
				while(rs.next()){
					customers.add(rs.getString("c_id"));
				}
			}catch(SQLException e){
				e.printStackTrace();
				return null;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return null;
		}
		return customers;
	}

	public boolean customer_report(){
		// TODO
		return false;
	}

	public boolean add_interest(){
		// TODO
		return false;
	}

	public boolean create_account(Testable.AccountType accountType, String id, double initialBalance,
										 String tin, String name, String address, OracleConnection connection){
		Account account = Account.create_account(accountType, id, initialBalance,
										 tin, name, address, connection);
		if(account != null){
			return true;
		}
		return false;
	}

	public boolean create_pocket_account(String id, String linkedId, double initialTopUp,
											    String tin, OracleConnection connection){
		// Creates a new pocket account and retuns true if successful
		boolean created = create_pocket_account(id, linkedId, initialTopUp,
											    tin, connection);
		return created;
	}

	public boolean add_owner_to_account(String new_owner_id, String a_id){
		return false;
	}

	public boolean delete_closed_acc_and_cust(){
		// TODO
		return false;
	}

	public boolean delete_transactions(OracleConnection connection){
		// Simply delete all transactions and return true on success
		String query = String.format("DELETE FROM transactions");
		try( Statement statement = connection.createStatement() ) {
			try{
				int updates = statement.executeUpdate( query );
				
			}catch(SQLException e){
				e.printStackTrace();
				return false;
			}
		}catch(SQLException e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
}