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

/**
 * The most important class for your application.
 * DO NOT CHANGE ITS SIGNATURE.
 */
public class App implements Testable
{
	private OracleConnection connection;                   // Example connection object to your DB.
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
		return "0 it works!";
	}

	@Override
	public String createCheckingSavingsAccount( AccountType accountType, String id, double initialBalance, String tin, String name, String address)
	{
		return "0 " + id + " " + accountType + " " + initialBalance + " " + tin;
	}

	@Override
	public String payFriend( String from, String to, double amount ){
		return "1";
	}

	@Override
	public String topUp( String accountId, double amount ){
		return "1";
	}

	@Override
	public String showBalance( String accountId ){
		return "1";
	}

	@Override
	public String deposit( String accountId, double amount ){
		return "1";
	}

	@Override
	public String createCustomer( String accountId, String tin, String name, String address ){
		return "1";
	}

	@Override
	public String createPocketAccount( String id, String linkedId, double initialTopUp, String tin ){
		return "1";
	}

	@Override
	public String setDate( int year, int month, int day ){
		return "1";
	}

	@Override
	public String dropTables(){
		return "1";
	}

	@Override
	public String createTables(){
		return "1";
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

	public void run_cli(){
		try{
			// Translate CLI to GUI
			Scanner in = new Scanner(System.in);
			System.out.println("Enter (1) for Bank Teller (2) for customer or (3) for GUI or (4) for unit tests");
			String resp = in.nextLine();
			if(resp.equals("1")){
				// Run Bank Teller Interface
				BankTellerInterface bti = new BankTellerInterface(connection);
				bti.run();
			}else if (resp.equals("2")){
				// Run Customer Interface
				CustomerInterface ci = new CustomerInterface(connection);
				ci.run();
			}else if (resp.equals("3")){
				Interface gui = new Interface(connection);
				gui.setVisible(true);
			}
			else{
				System.out.println("Did not recognize input -- should be 1 or 2");
			}
		} catch( Exception e ) {
			System.err.println( e.getMessage() );
			e.printStackTrace();
			this.close_connection();
		}

	}
}
