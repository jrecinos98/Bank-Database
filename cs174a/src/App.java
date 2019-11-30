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

	public void run_demo(){
		// Setup
		this.initializeSystem();
		this.dropTables();
		this.createTables();

		// Set date march 1, 2011
		Bank.set_date(""+ 2011, "" + 3, ""+1, this.connection);

		// Create accounts
		this.createCheckingSavingsAccount(Testable.AccountType.STUDENT_CHECKING, "17431", 1200, 
			"344151573", "Joe Pepsi", "3210 State St");
		Customer.update_pin("344151573", "1717", "3692", this.connection);

		this.createCheckingSavingsAccount(Testable.AccountType.STUDENT_CHECKING, "54321", 21000,
		 "212431965" , "Hurryson Ford", "678 State St");
		Customer.update_pin("212431965", "1717", "3532", this.connection);

		this.createCheckingSavingsAccount(Testable.AccountType.STUDENT_CHECKING, "12121", 1200,
		 "207843218" , "David Copperfill", "1357 State St");
		Customer.update_pin("207843218", "1717", "8582", this.connection);

		this.createCheckingSavingsAccount(Testable.AccountType.INTEREST_CHECKING, "41725", 15000,
		 "201674933" , "George Brush", "5346 Foothill Av");
		Customer.update_pin("201674933", "1717", "9824", this.connection);

		this.createCheckingSavingsAccount(Testable.AccountType.INTEREST_CHECKING, "93156", 2000000,
		 "209378521" , "Kelvin Costner", "Santa Cruz #3579");
		Customer.update_pin("209378521", "1717", "4659", this.connection);

		this.createPocketAccount("53027", "12121", 50, "207843218");

		this.createCheckingSavingsAccount(Testable.AccountType.SAVINGS, "43942", 1289,
		 "361721022" , "Alfred Hitchcock", "6667 El Colegio #40");
		Customer.update_pin("361721022", "1717", "1234", this.connection);

		this.createCustomer("43942", "400651982" , "Pit Wilson", "911 State St");
		Customer.update_pin("400651982", "1717", "1821", this.connection);

		this.createCheckingSavingsAccount(Testable.AccountType.SAVINGS, "29107", 34000,
		 "209378521" , "Kelvin Costner", "Santa Cruz #3579");
		
		this.createCheckingSavingsAccount(Testable.AccountType.SAVINGS, "19023", 2300,
		 "412231856" , "Cindy Laugher", "7000 Hollister");
		Customer.update_pin("412231856", "1717", "3764", this.connection);

		this.createPocketAccount("60413", "43942", 20, "400651982");

		this.createCheckingSavingsAccount(Testable.AccountType.SAVINGS, "32156", 1000,
		 "188212217" , "Magic Jordon", "3852 Court Rd");
		Customer.update_pin("188212217", "1717", "7351", this.connection);

		this.createCheckingSavingsAccount(Testable.AccountType.INTEREST_CHECKING, "76543", 8456,
		 "212116070" , "Li Kung", "2 People's Rd Beijing");
		Customer.update_pin("212116070", "1717", "9173", this.connection);
		// Create non-primary owners

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
			String response = String.format("0 %.2f", math.abs(balance));
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
