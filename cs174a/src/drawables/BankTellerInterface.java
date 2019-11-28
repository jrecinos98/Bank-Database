
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
import java.util.Arrays;

import javax.swing.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;




public class BankTellerInterface extends JPanel{
	public enum BankTellerActions{
		CHECK_TRANSACTION,
		MONTHLY_STATEMENT,
		LIST_CLOSED,
		DTER,
		CUSTOMER_REPORT,
		ADD_INTEREST,
		CREATE_ACCOUNT,
		DELETE_ClOSED_ACCOUNTS,
		DELETE_TRANSACTIONS
	}

	private OracleConnection connection;
	private JButton b1;
	private InputForm form;

	public BankTellerInterface(OracleConnection connection){
		this.connection = connection;	

		ArrayList<String> labels= new ArrayList<String>( Arrays.asList("Test 1", "Test 2"));	
		JButton b= new JButton("Get Text");
		b.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                System.out.println(form.getInput(0));
                System.out.println(form.getInput(1));
            }
		});
		form = new InputForm(labels, b);

		setLayout(new FlowLayout());
		add(form);
	}
	class InputForm extends JPanel{
		private JButton button;
		private JLabel message;
		private ArrayList<JTextField> fields;

		public InputForm(ArrayList<String> l, JButton b){
			super(new GridLayout(3, 1));
			fields = new ArrayList<JTextField>();
			for(int i=0; i< l.size();i++){
				add(new JLabel(l.get(i)));
				JTextField t;
				t= new JTextField();
				t.setHorizontalAlignment(JTextField.CENTER);
				fields.add(i,t);
				add(t);
			}
			this.message= new JLabel();
			add(message);
			this.button= b;
			add(button);

			//Odd number of texboxes looks fucky. Gotta do this nasty shit
			if(l.size()%2 != 0){
				add(new JLabel());
				add(new JLabel());
			}
		}
		public String getInput(int l_num){
			System.out.println(message);
			System.out.println(fields.get(l_num).getText());
			return fields.get(l_num).getText();
		}
		public void setLabel(String text, Color c){
			this.message.setText(text);
			this.message.setForeground(c);
		}
		public void resetFields(){
			for (int i =0; i < fields.size(); i++){
				//System.out.println("Erasing fields");
				fields.get(i).setText("");
			}
		}
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
	class ButtonListener extends MouseAdapter{
		private CustomerInterface.CustomerActions action;
		public ButtonListener(CustomerInterface.CustomerActions action){
			super();
			this.action= action;
		}
		public void mouseClicked(MouseEvent e){
			//If left clicked
			if(SwingUtilities.isLeftMouseButton(e)){

				//Reset label/error message upon button click. Not needed but playing it safe
				form.resetLabel();

				switch(this.action){
					case LOG_IN:
						login();
						break;
					case UPDATE_PIN:
						change_pin();
						break;
					case DEPOSIT:
						deposit();
						break;
					case TOP_UP:
						top_up();
						break;
					case WITHDRAWAL:
						withdrawal();
						break;
					case PURCHASE:
						purchase();
						break;
					case TRANSFER:
						transfer();
						break;	
					case COLLECT:
						collect();
						break;	
					case WIRE:
						wire();
						break;
					case PAY_FRIEND:
						pay_friend();
						break;		
					case LOG_OUT:
						sign_out();
						break;	

				}

			}
			else if(SwingUtilities.isRightMouseButton(e)){
				//Essentially a back key
				update_page(CustomerActions.ACTIONS_PAGE);
			}
		}
	}
}