package cs174a;


// JDBC Imports
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

import java.util.Scanner;
import java.util.ArrayList;

public class CustomerInterface extends JPanel{
	private OracleConnection connection;
	private ArrayList<JButton> action_buttons;

	public CustomerInterface(OracleConnection connection){
		this.connection = connection;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		set_up_buttons();
	
	}
	

	public void run(){
		System.out.println("-- CustomerInterface --");
		String resp = Utilities.prompt(
			"1) to create a customer\n" +
			"2) to login as existing customer\n" +
			"3) to update PIN\n" +
			"4) to delete a customer\n" +
			"5) to create database tables\n" + 
			"6) to destroy database tables\n"
		);
		if(resp.equals("1")){
			this.create_cust();
		}else if(resp.equals("2")){
			this.login();
		}else if(resp.equals("3")){
			this.change_pin();
		}else if(resp.equals("4")){
			this.delete_cust();
		}else if(resp.equals("5")){
			this.create_tables();
		}else if(resp.equals("6")){
			this.destroy_tables();
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

	public void create_tables(){
		if(DBSystem.execute_queries_from_file("./scripts/create_db.sql", this.connection)){
			System.out.println("Successfully created tables");
		}else{
			System.out.println("Error creating tables");
		}
	}

	public void destroy_tables(){
		if(DBSystem.execute_queries_from_file("./scripts/destroy_db.sql", this.connection)){
			System.out.println("Successfully destroyed tables");
		}else{
			System.out.println("Error destroying tables");
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

	private void set_up_buttons(){
		create_buttons();
		for(int i=0; i< action_buttons.size();i++){
			action_buttons.get(i).addMouseListener(new ButtonListener(i+1));
			this.add(action_buttons.get(i));
		}
	}
	private void create_buttons(){
		this.action_buttons= new ArrayList<JButton>(10);
		this.action_buttons.add(0, new JButton("Create Customer"));
		this.action_buttons.add(1, new JButton("Log In"));
		this.action_buttons.add(2, new JButton("Update PIN"));
		this.action_buttons.add(3, new JButton("Delete Customer"));
		this.action_buttons.add(4, new JButton("Create Table"));
		this.action_buttons.add(5, new JButton("Delete Table"));
		this.action_buttons.add(6, new JButton("Deposit"));
		this.action_buttons.add(7, new JButton("Top Up"));
		this.action_buttons.add(8, new JButton("Withdrawal"));
		this.action_buttons.add(9, new JButton("Purchase"));
	}


	class ButtonListener extends MouseAdapter{
		private int action;
		public ButtonListener(int action){
			super();
			this.action= action;
		}
		public void mouseClicked(MouseEvent e){
			JFrame new_frame = new JFrame();
			new_frame.setVisible(true);
			Utilities.setWindow(new_frame);
			//If left clicked
			if(e.getButton() ==  MouseEvent.BUTTON1){
				switch(this.action){
					case 1:
						create_cust();
						break;
					case 2:
						login();
						break;
					case 3:
						change_pin();
						break;
					case 4:
						delete_cust();
						break;
					case 5:
						create_tables();
						break;
					case 6:
						deposit();
						break;
					case 7:
						top_up();
						break;
					case 8:
						withdrawal();
						break;
					case 9:
						purchase();
						break;
				}
			}
		}
	}

}