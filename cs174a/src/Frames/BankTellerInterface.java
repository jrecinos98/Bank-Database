
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

import java.util.Scanner;
import java.util.ArrayList;

import javax.swing.*;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;




public class BankTellerInterface extends JPanel{

	private OracleConnection connection;

	public BankTellerInterface(OracleConnection connection){
		this.connection = connection;	
		JButton b1;	
		b1 = new JButton("Hello");
		b1.setMnemonic(KeyEvent.VK_D);
    	b1.setActionCommand("disable");
		setLayout(new FlowLayout());
		add(b1);
	}

	public void run(){
		System.out.println("-- BankTellerInterface --");
		String resp = Utilities.prompt(
			"1) to create an account\n" +
			"2) to see closed accounts\n" +
			"3) to create a pocket account\n"
		);
		if(resp.equals("1")){
			this.create_acct();
		}else if(resp.equals("2")){
			this.show_closed();
		}else if(resp.equals("3")){
			this.create_pocket_acct();
		}
	}


	public void create_acct(){
		Testable.AccountType type;
		try{
			type = Testable.AccountType.values()[Integer.parseInt(Utilities.prompt("Enter account type:"))];
		}catch(Exception e){
			System.out.println("Account creation failed... ");
			e.printStackTrace();
			return;
		}
		String a_id = Utilities.prompt("Enter a_id:");
		double initial_balance = Double.parseDouble(Utilities.prompt("Enter initial balance:"));
		String c_id = Utilities.prompt("Enter c_id:");
		String name = Utilities.prompt("Enter name:");
		String address = Utilities.prompt("Enter address:");
		Account acct = Account.create_account(type, a_id, initial_balance,
										 c_id, name,address, this.connection);
		if(acct == null){
			System.out.println("Account creation failed... ");
		}else{
			System.out.println("Account: " + acct.a_id + " created!");
		}
	}

	public void show_closed(){
		ArrayList<String> accounts = Account.get_closed_accounts(this.connection);
		if(accounts == null){
			System.out.println("Finding closed accounts failed...");
			return;
		}
		for(int i = 0; i < accounts.size(); i++){
			System.out.println("a_id: " + accounts.get(i));
		}
		System.out.println("Successfully got closed accounts!");
	}

	public void create_pocket_acct(){
		String id = Utilities.prompt("Enter id:");
		String linkedId = Utilities.prompt("Enter linkedId:");
		double initialTopUp = Double.parseDouble(Utilities.prompt("Enter InitialTopup:"));
		String tin = Utilities.prompt("Enter customer id:");
		Account acct = Account.create_pocket_account(id, linkedId, initialTopUp, tin, this.connection);
		if(acct == null){
			System.err.println("Error: could not create pocket acct");
		}else{
			System.out.println("Successfully created pocket acct!");
		}
	}
}