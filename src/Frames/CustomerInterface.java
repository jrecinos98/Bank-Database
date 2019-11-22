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

import java.util.Scanner;

public class CustomerInterface {
	OracleConnection connection;

	public CustomerInterface(OracleConnection connection){
		this.connection = connection;
	}

	public void run(){
		System.out.println("-- CustomerInterface --");
		this.login();
	}

	public void login(){
		Scanner in = new Scanner(System.in);
		System.out.println("Enter tax id:");
		String id = in.nextLine();
		System.out.println("Enter 4 digit PIN:");
		String pin = in.nextLine();
		Customer cust = Customer.login(id, pin, this.connection);
		if(cust == null){
			System.out.println("Verification failed... Are your id/PIN correct?");
		}else{
			System.out.println("User: " + cust.name + " logged in!");
		}
	}

	public void deposit(){

	}

	public void top_up(){

	}

	public void withdrawal(){

	}

	public void purchase(){

	}
}