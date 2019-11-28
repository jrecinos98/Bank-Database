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
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;



import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;


public class CustomerInterface extends JPanel{
	public final static int LOG_IN= 11;
	public final static int ACTIONS_PAGE=12;



	public final static int UPDATE_PIN=1;
	public final static int DEPOSIT=2;
	public final static int TOP_UP=3;
	public final static int WITHDRAWAL=4;
	public final static int PURCHASE=5;
	public final static int LOG_OUT= 6;
	public final static int CREATE_CUSTOMER=7;
	public final static int DELETE_CUSTOMER=8;
	public final static int CREATE_TABLES= 9;
	public final static int DESTROY_TABLES=10;



	private OracleConnection connection;
	private String user_id;

	private ArrayList<JButton> action_buttons;
	private Hashtable<Integer, JPanel> panels;
	private InputForm form;
	private JPanel current_page;
	private JFrame parent_frame;
	

	public CustomerInterface(OracleConnection connection) {
		super(new GridLayout(3, 1));
		this.connection = connection;
		//Initial Screen
		create_pages();
		current_page= panels.get(LOG_IN);
		this.form= (InputForm) current_page;
		add(current_page);
		parent_frame= Interface.main_frame;
	}

	/*Creates all the pages that will be used for the customer interface*/
	private void create_pages(){
		panels= new Hashtable<Integer, JPanel>();
		panels.put(LOG_IN, create_login_page());
		panels.put(ACTIONS_PAGE, create_actions_page());
		panels.put(UPDATE_PIN, create_page(new ArrayList<String> (Arrays.asList("Old PIN:","New PIN:")), "Update PIN", UPDATE_PIN));
		panels.put(DEPOSIT, create_page(new ArrayList<String> (Arrays.asList("Account ID:", "Amount: $")), "Make Deposit", DEPOSIT));
		panels.put(TOP_UP, create_page(new ArrayList<String> (Arrays.asList("Pocket Account ID:", " Linked Account ID:", " Amount: $")), "Transfer to Account", TOP_UP));
		panels.put(WITHDRAWAL, create_page(new ArrayList<String> (Arrays.asList("Account ID", "Tax ID:","Amount: $")), "Make Withdrawal", WITHDRAWAL ));
		panels.put(PURCHASE, create_page(new ArrayList<String> (Arrays.asList("Account ID","Amount: $")), "Make Purchase", WITHDRAWAL ));
	}
	
	public void login(){
		String id = form.getInput(0);
		String pin= form.getInput(1);	

		System.out.println("Customer ID: " + id);
		System.out.println("Customer PIN: " + pin);
		Customer cust = Customer.login(id, pin, this.connection);
		if(cust == null){
			form.setLabel("Verification Failed.", Color.red);
			System.out.println("Verification failed... Are your id/PIN correct?");
			
		}else{
			this.user_id= id;
			System.out.println("User: " + cust.name + " logged in!");
			update_page(ACTIONS_PAGE);
			//Change Panel to actions_panel
	
		}
		//temp
		update_page(ACTIONS_PAGE);
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

		//String id= form.getInput(0);
		String old= form.getInput(0);
		String _new= form.getInput(1);

		System.out.println("Customer ID: " + this.user_id);
		
		System.out.println("Old PIN: " + old);

		System.out.println("New PIN: " + _new);

		//Check that pin format is valid 
		if (!Utilities.valid_pin_format(old)){
			form.setLabel("Invalid old PIN", Color.red);
			return;
		}
		//Check that pin format is valid 
		if (!Utilities.valid_pin_format(_new)){
			form.setLabel("Invalid new PIN", Color.red);
			return;
		}
		if(old.equals(_new)){
			form.setLabel("New PIN cannot be old PIN", Color.red);
			return;
		}

		//temp
		update_page(ACTIONS_PAGE);

		if(Customer.update_pin(this.user_id, old, _new, this.connection)){
			System.out.println("PIN updated!");
			Customer cust = Customer.get_cust_by_id(this.user_id, this.connection);
			if(cust != null){
				//Pop up saying that operation was a success.
				System.out.println("Successfully set pin to " + cust.encrypted_pin);
				update_page(ACTIONS_PAGE);
				return;
			}
		}
		else{
			form.setLabel("Operation Failed. Check old pin is correct.", Color.red);
			System.out.println("Failed to set pin");
					
		}
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
		String date = Bank.get_date(connection);
		Transaction.TransactionType type = Transaction.TransactionType.DEPOSIT;

		String to_acct = form.getInput(0);
		String amount= form.getInput(1);
		//If time permits obtain all accounts for the user and have a drop down menu
		if(to_acct.equals("")){
			form.setLabel("Enter a valid account", Color.red);
			return;
		}
		if(!Utilities.valid_money_input(amount)){
			form.setLabel("Enter a valid amount", Color.red);
			return;
		}
		System.out.println("Account: "+ to_acct);
		System.out.println("Amount: " + amount);
		
		
		update_page(ACTIONS_PAGE);
	
		
		boolean success = Transaction.deposit(to_acct, this.user_id, date, type, Double.parseDouble(amount), connection);
		if(!success){
			form.setLabel("Deposit failed", Color.red);
			System.err.println("Deposit failed");
		}else{
			form.setLabel("Deposit successful", Color.green);
			System.out.println("Deposit success!");

		}
	}

	public void top_up(){

		String date = Bank.get_date(connection);

		Transaction.TransactionType type = Transaction.TransactionType.TOP_UP;

		String pocket_id = form.getInput(0);
		String linked= form.getInput(1);	
		String amount= form.getInput(2);
		if(pocket_id.equals("")){
			form.setLabel("Enter a valid pocket account", Color.red);
			return;
		}
		if(linked.equals("")){
			form.setLabel("Enter a valid account", Color.red);
			return;
		}
		if(!Utilities.valid_money_input(amount)){
			form.setLabel("Enter a valid amount", Color.red);
			return;
		}

		System.out.println("Pocket Account: "+ pocket_id);
		System.out.println("PIN: "+pin);
		System.out.println("Amount: "+amount);
		
		update_page(ACTIONS_PAGE);
		Transaction transaction = Transaction.top_up(to_acct, from_acct, date,  Double.parseDouble(amount), cust_id, connection);
		if(transaction == null){
			form.setLabel("Transaction failed", Color.red);
			System.err.println("Top-Up failure");
		}else{
			form.setLabel("Transaction Successful", Color.green);
			System.out.println("Top-Up success!");
		}
	}

	public void withdrawal(){


		String date = Bank.get_date(connection);

		Transaction.TransactionType type = Transaction.TransactionType.WITHDRAWAL;
		String id = form.getInput(0);
		String pin= form.getInput(1);
		String m= form.getInput(2);		
		System.out.println(id);
		System.out.println(pin);
		System.out.println(m);
		this.form.setLabel("TEST", Color.red);
		update_page(ACTIONS_PAGE);
	
		
		/*double amount = Double.parseDouble(Utilities.prompt("Enter amount:"));

		boolean success = Transaction.withdraw(from_acct, cust_id, date, type, amount, connection);
		if(!success){
			System.err.println("Withdrawal failed");
		}else{
			System.out.println("Withdrawal success!");
		}*/
	}
	public void purchase(){

	}

	/*resets user info and loads sign in page*/
	private void sign_out(){
		this.user_id=null;
	 	update_page(LOG_IN);
	}

	/*Creates the login page*/
	private JPanel create_login_page(){
		
		JButton button= new JButton("Submit PIN");
        button.addMouseListener(new ButtonListener(LOG_IN));
        ArrayList<String> labels= new ArrayList<String> (
        						Arrays.asList("Tax ID:", "PIN"));
        InputForm form = new InputForm(labels, button);
        //JPanel hold= new JPanel();
        //hold.add(form);
        //add(hold);
        return form;
	}

	/*Creates a page with labels and textfields (depends on size of ArrayList)*/
	private JPanel create_page(ArrayList<String> labels, String b_label, int action){
		JButton button= new JButton(b_label);
		button.addMouseListener(new ButtonListener(action));
		InputForm form = new InputForm(labels, button);
		JPanel holder= new JPanel();
		//holder.add(form);
		return form;
	}
	/* Creates the page with user actions*/
	private JPanel create_actions_page(){
		JPanel holder= new JPanel();
		create_buttons();
		for(int i=0; i< action_buttons.size();i++){
			holder.add(action_buttons.get(i));
		}
		
		return holder;
	}
	/* Changes from one page to another*/
	private void update_page(int page){
		this.remove(current_page);
		this.revalidate();
		this.repaint();
		try{
			current_page= panels.get(page);
			if(page != ACTIONS_PAGE){
				//Clear old values from old form
				if(this.form != null)
					this.form.resetFields();
				//Change form to current page
				this.form= (InputForm) current_page;
				this.form.resetFields();
			}
			else{
				this.form= null;
			}
		}
		catch(Exception e){
			System.err.println(e);
		}
		add(current_page);
	}
	private void create_buttons(){
		this.action_buttons= new ArrayList<JButton>(10);
		
		JButton t= new JButton("Update PIN");
		t.addMouseListener(new MouseAdapter() { 
			private int action;
			public void mouseClicked(MouseEvent e) {
                update_page(UPDATE_PIN);
            }
		});
		this.action_buttons.add(t);

		t= new JButton("Deposit");
		t.addMouseListener(new MouseAdapter() { 
			private int action;
			public void mouseClicked(MouseEvent e) {
                update_page(DEPOSIT);
            }
		});
		this.action_buttons.add(t);

		t=  new JButton("Top Up");
		t.addMouseListener(new MouseAdapter() { 
			private int action;
			public void mouseClicked(MouseEvent e) {
                update_page(TOP_UP);
            }
		});
		this.action_buttons.add(t);

		t=new JButton("Withdrawal");
		t.addMouseListener(new MouseAdapter() { 
			private int action;
			public void mouseClicked(MouseEvent e) {
                update_page(WITHDRAWAL);
            }
		});
		this.action_buttons.add(t) ;

		t= new JButton("Purchase");
		t.addMouseListener(new MouseAdapter() { 
			private int action;
			public void mouseClicked(MouseEvent e) {
                update_page(PURCHASE);
            }
		});
		this.action_buttons.add( t);

		t= new JButton("Sign Out");
		t.addMouseListener(new MouseAdapter() { 
			private int action;
			public void mouseClicked(MouseEvent e) {
                sign_out();
            }
		});
		this.action_buttons.add(t);

		/*
		this.action_buttons.add( new JButton("Create Customer"));
		this.action_buttons.add( new JButton("Delete Customer"));
		this.action_buttons.add( new JButton("Create Table"));
		this.action_buttons.add( new JButton("Delete Table"));*/
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
			return fields.get(l_num).getText();
		}
		public void setLabel(String text, Color c){
			this.message.setText(text);
			this.message.setForeground(c);
		}
		public void resetLabel(){
			this.message.setText("");
		}
		public void resetFields(){
			for (int i =0; i < fields.size(); i++){
				//System.out.println("Erasing fields");
				fields.get(i).setText("");
			}
			resetLabel();
		}
	}

	class ButtonListener extends MouseAdapter{
		private int action;
		public ButtonListener(int action){
			super();
			this.action= action;
		}
		public void mouseClicked(MouseEvent e){
			//If left clicked
			if(SwingUtilities.isLeftMouseButton(e)){

				//Reset label/error message upon button click. Not needed but playing it safe
				form.resetLabel();

				switch(this.action){
					case CustomerInterface.LOG_IN:
						login();
						break;
					case CustomerInterface.UPDATE_PIN:
						change_pin();
						break;
					case CustomerInterface.DEPOSIT:
						deposit();
						break;
					case CustomerInterface.TOP_UP:
						top_up();
						break;
					case CustomerInterface.WITHDRAWAL:
						withdrawal();
						break;
					case CustomerInterface.PURCHASE:
						purchase();
						break;

					case CustomerInterface.LOG_OUT:
						sign_out();
						break;
					case CustomerInterface.CREATE_CUSTOMER:
						create_cust();
						break;
					case CustomerInterface.DELETE_CUSTOMER:
						delete_cust();
						break;
					case CustomerInterface.CREATE_TABLES:
						create_tables();
						break;
					case CustomerInterface.DESTROY_TABLES:
						destroy_tables();
						break;					
				}

			}
			else if(SwingUtilities.isRightMouseButton(e)){
				System.out.println("Mouse Button 2 Pressed");
				update_page(ACTIONS_PAGE);
			}
		}
	}
	public void run(){
		System.out.println("-- CustomerInterface --");
		String resp = Utilities.prompt(
			"1) to create a customer\n" +
			"2) to login as existing customer\n" +
			"3) to update PIN\n" +
			"4) to delete a customer\n" +
			"5) to create database tables\n" + 
			"6) to destroy database tables\n" +
			"7) to deposit money in an account\n" +
			"8) to withdraw money from an account \n" +
			"9) to top up pocket account\n"
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
		}else if(resp.equals("7")){
			this.deposit();
		}else if(resp.equals("8")){
			this.withdrawal();
		}else if(resp.equals("9")){
			this.top_up();
		}
	}

}