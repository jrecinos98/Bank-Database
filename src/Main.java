// JDBC Imports
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

// Java imports
import java.util.Scanner;

public class Main {  
	// The recommended format of a connection URL is the long format with the
	// connection descriptor.
	final static String DB_URL= "jdbc:oracle:thin:@cs174a.cs.ucsb.edu:1521/orcl";
	final static String DB_USER = "c##ncduncan";
	final static String DB_PASSWORD = "3937679";

	public static void main(String args[]) throws SQLException {
		// Translate CLI to GUI
		Scanner in = new Scanner(System.in);
		System.out.println("Enter (1) for Bank Teller or (2) for customer");
		String resp = in.nextLine();
		if(resp.equals("1")){
			// Run Bank Teller Interface
			BankTellerInterface bti = new BankTellerInterface();
			bti.run();
		}else if (resp.equals("2")){
			// Run Customer Interface
			CustomerInterface ci = new CustomerInterface();
			ci.run();
		}else{
			System.out.println("Did not recognize input -- should be 1 or 2");
		}
	}
}
















		// Properties info = new Properties();     
		// info.put(OracleConnection.CONNECTION_PROPERTY_USER_NAME, DB_USER);
		// info.put(OracleConnection.CONNECTION_PROPERTY_PASSWORD, DB_PASSWORD);          
		// info.put(OracleConnection.CONNECTION_PROPERTY_DEFAULT_ROW_PREFETCH, "20");    
	
		// OracleDataSource ods = new OracleDataSource();
		// ods.setURL(DB_URL);    
		// ods.setConnectionProperties(info);

		// // With AutoCloseable, the connection is closed automatically.
		// try (OracleConnection connection = (OracleConnection) ods.getConnection()) {
		// 	// Get the JDBC driver name and version 
		// 	DatabaseMetaData dbmd = connection.getMetaData();       
		// 	System.out.println("Driver Name: " + dbmd.getDriverName());
		// 	System.out.println("Driver Version: " + dbmd.getDriverVersion());
		// 	// Print some connection properties
		// 	System.out.println("Default Row Prefetch Value is: " + 
		// 		 connection.getDefaultRowPrefetch());
		// 	System.out.println("Database Username is: " + connection.getUserName());
		// 	System.out.println();
		// 	// Perform a database operation 
		// 	// printEmployees(connection);
		// }   