package cs174a;                                             // THE BASE PACKAGE FOR YOUR APP MUST BE THIS ONE.  But you may add subpackages.

// You may have as many imports as you need.
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

import java.util.Scanner;
import java.util.ArrayList;
/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection connection;                   // Example connection object to your DB.
	private Interface gui;
	// connection descriptor.
	final static String DB_URL= "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
	final static String DB_USER = "c##ncduncan";
	final static String DB_PASSWORD = "3937679";
	/**
	 * Default constructor.
	 * DO NOT REMOVE.
	 */
	App() {
		// TODO: Any actions you need.

	}

	/**
	 * This is an example access operation to the DB.
	 */
	void exampleAccessToDB()
	{
		// Statement and ResultSet are AutoCloseable and closed automatically.
		try( Statement statement = this.connection.createStatement() )
		{
			try( ResultSet resultSet = statement.executeQuery( "select owner, table_name from all_tables" ) )
			{
				while( resultSet.next() )
					System.out.println( resultSet.getString( 1 ) + " " + resultSet.getString( 2 ) + " " );
			}
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
		}
	}

	////////////////////////////// Implement all of the methods given in the interface /////////////////////////////////
	// Check the Testable.java interface for the function signatures and descriptions.

	@Override
	public String initializeSystem()
	{
		
		// Initialize your system.  Probably setting up the DB connection.
		Properties info = new Properties();
		info.put( OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER );
		info.put( OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD );
		info.put( OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20" );

		try
		{
			OracleDataSource ods = new OracleDataSource();
			ods.setURL( DB_URL );
			ods.setConnectionProperties( info );
			connection = (OracleConnection) ods.getConnection();
			return "0";
		}
		catch( SQLException e )
		{
			System.err.println( e.getMessage() );
			return "1";
		}


	}

	@Override
	public String listClosedAccounts()
	{
		ArrayList<String> closed_accts = Account.get_closed_accounts(this.connection);
		String closed = "0";
		if(closed_accts == null){
			return "1";
		}else{
			for(int i = 0; i < closed_accts.size(); i++){
				closed += " " + closed_accts.get(i);
			}
			return closed;
		}
	}

	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address)
	{
		Account new_acct =  Account.create_account(accountType, id, initialBalance,
										 tin, name, address, this.connection);
		if(new_acct == null){
			return "1";
		}else{
			String response = String.format("0 %s %s %.2f %s", id, "" + accountType, initialBalance, tin);
			return response;
		}
	}

	@Override
	public String payFriend(String from, String to, double amount ){
		Transaction transact = Transaction.pay_friend_no_owner_check(to, from, Bank.get_date(this.connection), 
						 Transaction.TransactionType.PAY_FRIEND, amount, connection);
		if(transact == null){
			return "1";
		}else{
			double fromNewBalance = Account.get_account_balance(from, this.connection);
			double toNewBalance = Account.get_account_balance(to, this.connection);
			String response = String.format("0 %.2f %.2f", fromNewBalance, toNewBalance);
			return response;
		}
	}

	@Override
	public String topUp( String accountId, double amount ){
		Account account = Account.get_account_by_id(accountId, this.connection);
		String linked_id = Account.get_linked(accountId, this.connection);
		if(linked_id != ""){
			Transaction transact = Transaction.top_up_no_owner_check(accountId, linked_id, Bank.get_date(this.connection),
						 amount, connection);
			if(transact != null){
				double pocket_balance = Account.get_account_balance(accountId, this.connection);
				double linked_balance = Account.get_account_balance(linked_id, this.connection);
				String resp = String.format("0 %.2f %.2f", linked_balance, pocket_balance);
				return resp;
			}			
		}
		return "1";
	}

	@Override
	public String showBalance( String accountId ){
		double balance = Account.get_account_balance(accountId, this.connection);
		if(balance == -1){
			return "1";
		}else{
			String response = String.format("0 %.2f", balance);
			return response;
		}
	}

	@Override
	public String deposit( String accountId, double amount ){
		double old = Account.get_account_balance(accountId, this.connection);
		boolean transact = Transaction.deposit_no_owner_check(accountId, Bank.get_date(this.connection), 
						 Transaction.TransactionType.DEPOSIT, amount, this.connection);
		double new_b = Account.get_account_balance(accountId, this.connection);
		if(transact == false || old == -1 || new_b == -1){
			return "1";
		}else{
			String response = String.format("0 %.2f %.2f", old, new_b);
			return response;
		}
	}

	@Override
	public String createCustomer( String accountId, String tin, String name, String address ){
		Account account = Account.get_account_by_id(accountId, this.connection);
		if(account == null){
			return "1";
		}else{
			Customer cust = Customer.create_customer(tin, name, address,this.connection);
			if(cust == null){
				return "1";
			}else{
				if(Account.create_acct_ownership(accountId, tin, this.connection)){
					return "0";
				}else{
					return "1";
				}
			}
		}

	}

	@Override
	public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin ){
		Account account = Account.create_pocket_account(id, linkedId, initialTopUp,
											    tin, this.connection);
		if(account == null){
			return "1";
		}else{
			String resp = String.format("0 %s %s %.2f %s", id, "" + Testable.AccountType.POCKET, initialTopUp, tin);
			return resp;
		}
	}

	@Override
	public String setDate( int year, int month, int day ){
		boolean success = Bank.set_date( "" + year, "" + month, "" + day, this.connection);
		if(success){
			return "0 " + year + "-" + Bank.pretty_month("" + month) + "-" + Bank.pretty_day("" + day);
		}else{
			return "1";
		}
	}

	@Override
	public String dropTables(){
		if(DBSystem.execute_queries_from_file("./scripts/destroy_db.sql", this.connection)){
			return "0";
		}else{
			return "1";
		}
	}

	@Override
	public String createTables(){
		Bank.bank_set_up(this.connection);
		if(DBSystem.execute_queries_from_file("./scripts/create_db.sql", this.connection)){
			return "0";
		}else{
			return "1";
		}
	}

	public String close_connection(){
		try{
			this.connection.close();
			System.err.println("Connection closed!");
			return "0";
		}catch(SQLException e){
			e.printStackTrace();
			return "1";
		}
	}
	public void run_gui(){
		this.gui = new Interface(this.connection);
	}

	public void run_cli(){
		try{
			// Translate CLI to GUI
			Scanner in = new Scanner(System.in);
			System.out.println("Enter (1) for GUI or (2) for unit tests");
			String resp = in.nextLine();
			if(resp.equals("1")){
				Interface gui = new Interface(connection);
				gui.setVisible(true);
			}else if (resp.equals("2")){
				Tester tester = new Tester();
				tester.run_tests(connection);
			}
			else{
				System.out.println("Did not recognize input -- should be 1 , 2, or 3");
			}
			this.close_connection();
			
		} catch( Exception e ) {
			System.err.println( e.getMessage() );
			e.printStackTrace();
			this.close_connection();
		}

	}
}
