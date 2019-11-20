/*
 * Customer is basically a struct to store data
 * returned from the database pertaining to
 * the customer table
 */
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

public class Customer {
	public String c_id;
	public String encrypted_pin;
	public String address;
	public String name;


	// Encrypts the given string using some encrpytion scheme
	// So that pins are stored in an encrypted form on db
	public static String encrypt_pin(String unencrypted) throws Exception{
		if(unencrypted.length() != 4){
			System.out.println("Bad pin: " + unencrypted);
			throw new Exception("Pin must be 4 characters got " + unencrypted.length());
		}

		// Get integer value of each digit
		int d0 = Integer.parseInt("" + unencrypted.charAt(0));
		int d1 = Integer.parseInt("" + unencrypted.charAt(1));
		int d2 = Integer.parseInt("" + unencrypted.charAt(2));
		int d3 = Integer.parseInt("" + unencrypted.charAt(3));

		// Encrypt each digit making sure still single integer
		d0 = (d0 + 1) % 10;
		d1 = (d1 + 1) % 10;
		d2 = (d2 + 1) % 10;
		d3 = (d3 + 1) % 10;

		return "" + d0 + d1 + d2 + d3;
	}

	// If a customer with given c_id and pin is found, return that customer, else return null
	public static Customer login(String c_id, String unencrypted_pin, OracleConnection connection){
		Customer cust = null;
		ResultSet result = null;
		PreparedStatement stmt = null;
		try{
			String encrypted = Customer.encrypt_pin(unencrypted_pin);

			// Do database lookup on customers table
			try {
				String query = String.format("SELECT * " + 
			    							 "FROM customers C " +
			    							 "WHERE C.c_id = '%s' " + 
			    							 "AND C.encrypted_pin = '%s'", c_id, encrypted);
			    stmt = connection.prepareStatement(query);
			    
			    result = stmt.executeQuery();

			    if(result.next()) {
				    cust = new Customer(
				    	result.getString("c_id"),
				    	unencrypted_pin,
				    	result.getString("address"),
				    	result.getString("c_name")
				    );
				}
			}
			catch (SQLException e){
			    e.printStackTrace();
			}
			finally {
		        try { result.close(); } catch (Exception e) { /* ignored */ }
			    try { stmt.close(); } catch (Exception e) { /* ignored */ }
			}
		}catch(Exception e){
			System.out.println("Error logging in the customer" + c_id);
			e.printStackTrace();
		}
		return cust;
	}

	// Create a customer and throw exception if error in pin encountered
	public Customer(String c_id, String unencrypted_pin, String address, String name) throws Exception{
		this.c_id = c_id;
		this.encrypted_pin = Customer.encrypt_pin(unencrypted_pin);
		this.address = address;
		this.name = name;
	}
}