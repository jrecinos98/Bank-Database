package cs174a;


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
		String resp = Utilities.prompt(
			"1) to create a customer\n" +
			"2) to login as existing customer\n" +
			"3) to update PIN\n" +
			"4) to delete a customer\n"
		);
		if(resp.equals("1")){
			this.create_cust();
		}else if(resp.equals("2")){
			this.login();
		}else if(resp.equals("3")){
			this.change_pin();
		}else if(resp.equals("4")){
			this.delete_cust();
		}
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

	public void create_cust(){
		String tin = Utilities.prompt("Enter c_id:");
		String name = Utilities.prompt("Enter c_name:");
		String address = Utilities.prompt("Enter address:");
		Customer cust = Customer.create_customer(tin, name, address, this.connection);
		if(cust == null){
			System.out.println("Creation failed... ");
		}else{
			System.out.println("User: " + cust.name + " created!");
		}
	}

	public void change_pin(){
		String id = Utilities.prompt("Enter c_id:");
		String old = Utilities.prompt("Enter old pin:");
		String _new = Utilities.prompt("Enter new pin:");
		if(Customer.update_pin(id, old, _new, this.connection)){
			System.out.println("PIN updated!");
			Customer cust = Customer.get_cust_by_id(id, this.connection);
			if(cust != null){
				System.out.println("Successfully set pin to " + cust.encrypted_pin);
				return;
			}
		}
		System.out.println("Failed to set pin");
	}

	public void delete_cust(){
		String id = Utilities.prompt("Enter c_id:");
		if(Customer.del_cust_by_id(id, this.connection)){
			System.out.println("Successfully removed customer!");
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