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

import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;
import java.sql.DatabaseMetaData;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;


public class CustomerInterface extends JPanel{
	public final static int LOG_IN= 0;
	public final static int UPDATE_PIN=2;
	public final static int DEPOSIT=6;
	public final static int WITHDRAWAL=8;

	private OracleConnection connection;
	private String user_id;
	private String user_pin;
	private ArrayList<JButton> action_buttons;
	private InputForm form;
	private JPanel current_panel;
	private JFrame parent_frame;
	private JTextField text_box;
	

	public CustomerInterface(OracleConnection connection) {
		super(new GridLayout(3, 1));
		this.connection = connection;
		//setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		create_login_page();
		//set_up_buttons();
		current_panel= this;
		parent_frame= Interface.main_frame;
		
	
	}
	
	private void render_page(ArrayList<String> labels, String b_label, int action){
		this.removeAll();
		this.validate();
		this.repaint();
		JButton button= new JButton(b_label);
		button.addMouseListener(new ButtonListener(action));
		this.form = new InputForm(labels, button);
		add(form);
	}
	private void create_login_page(){

		JButton button= new JButton("Submit PIN");
        button.addMouseListener(new ButtonListener(LOG_IN));
        ArrayList<String> labels= new ArrayList<String> (
        						Arrays.asList("Tax ID:", "PIN"));
        this.form = new InputForm(labels, button);
        add(form);
	}
	
	private void render_actions_page(){
		this.removeAll();
		this.form= null;
		set_up_buttons();
		this.validate();
		this.repaint();
	}
	private void render_pin_reset(){
		this.removeAll();
		this.validate();
		this.repaint();

		JButton button= new JButton("Update PIN");
		button.addMouseListener(new ButtonListener(UPDATE_PIN));
	
		ArrayList<String> labels= new ArrayList<String> (
        						Arrays.asList("Tax ID:", "Old PIN:","New PIN:"));
        this.form = new InputForm(labels, button);
		add(form);
	}
	private void render_deposit_page(){
		this.removeAll();
		this.validate();
		this.repaint();

		JButton button= new JButton("Make Deposit");
		button.addMouseListener(new ButtonListener(DEPOSIT));
	
		ArrayList<String> labels= new ArrayList<String> (
        						Arrays.asList("Account ID:", "Tax ID:","Amount: $"));
        this.form = new InputForm(labels, button);
		add(form);
	}

	public void login(){

		//Scanner in = new Scanner(System.in);
		//System.out.println("Enter tax id:");
		//String id = in.nextLine();
		//System.out.println("Enter 4 digit PIN:");
		//String pin = in.nextLine();

		String id = form.getInput(0);
		String pin= form.getInput(1);
		this.user_pin=pin;
		this.user_id= id;
		this.render_actions_page();

		//Uncomment after GUI is set up.
		
		/*Customer cust = Customer.login(id, pin, this.connection);
		if(cust == null){
			//System.out.println("Verification failed... Are your id/PIN correct?");
			form.setLabel("Verification Failed.", Color.red);

			
		}else{
			this.user_pin=pin;
			this.user_id= id;
			System.out.println("User: " + cust.name + " logged in!");
			//Change Panel to actions_panel
			this.render_actions_page();
		}*/
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
		/*String id = Utilities.prompt("Enter c_id:");
		String old = Utilities.prompt("Enter old pin:");
		String _new = Utilities.prompt("Enter new pin:");*/
		if(this.form == null){
			render_pin_reset();
		}
		else{
			render_actions_page();
	
		}
		/*if(Customer.update_pin(id, old, _new, this.connection)){
			System.out.println("PIN updated!");
			Customer cust = Customer.get_cust_by_id(id, this.connection);
			if(cust != null){
				System.out.println("Successfully set pin to " + cust.encrypted_pin);
				return;
			}
		}
		System.out.println("Failed to set pin");*/
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
		/*String to_acct = Utilities.prompt("Enter a_id:");
		String cust_id = Utilities.prompt("Enter c_id:");*/
		String date = Bank.get_date();
		Transaction.TransactionType type = Transaction.TransactionType.DEPOSIT;
		//double amount = Double.parseDouble(Utilities.prompt("Enter amount:"));
		if(form == null){
			render_deposit_page();
		}
		else{
			render_actions_page();
	
		}
		/*boolean success = Transaction.deposit(to_acct, cust_id, date, type, amount, connection);
		if(!success){
			System.err.println("Deposit failed");
		}else{
			System.out.println("Deposit success!");
		}*/
	}

	public void top_up(){
		/*String to_acct = Utilities.prompt("Enter pocket id:");
		String from_acct = Utilities.prompt("Enter link id:");
		String cust_id = Utilities.prompt("Enter c_id:");
		String date = Bank.get_date();*/
		Transaction.TransactionType type = Transaction.TransactionType.TOP_UP;
		if(form == null){
			ArrayList<String> labels= new ArrayList<String> (
        						Arrays.asList("Pocket Account ID", " Linked Account ID:","Tax ID: $", " Amount: $"));
        
			render_page(labels,"Transfer to Account",WITHDRAWAL);
		}
		else{
			render_actions_page();
			
		}

		/*double amount = Double.parseDouble(Utilities.prompt("Enter amount:"));

		Transaction transaction = Transaction.top_up(to_acct, from_acct, date, amount, cust_id, connection);
		if(transaction == null){
			System.err.println("Top-Up failure");
		}else{
			System.out.println("Top-Up success!");
		}*/
	}

	public void withdrawal(){
		/*String from_acct = Utilities.prompt("Enter a_id:");
		String cust_id = Utilities.prompt("Enter c_id:");
		String date = Bank.get_date();
		*/
		Transaction.TransactionType type = Transaction.TransactionType.WITHDRAWAL;
		if(form == null){
			ArrayList<String> labels= new ArrayList<String> (
        						Arrays.asList("Account ID", "Tax ID:","Amount: $"));
        
			render_page(labels,"Make Withdrawal",WITHDRAWAL);
		}
		else{
			render_actions_page();
			this.form=null;
		}
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
		this.action_buttons.add(1, new JButton("Update PIN"));
		this.action_buttons.add(2, new JButton("Delete Customer"));
		this.action_buttons.add(3, new JButton("Create Table"));
		this.action_buttons.add(4, new JButton("Delete Table"));
		this.action_buttons.add(5, new JButton("Deposit"));
		this.action_buttons.add(6, new JButton("Top Up"));
		this.action_buttons.add(7, new JButton("Withdrawal"));
		this.action_buttons.add(8, new JButton("Purchase"));
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
	}
	class ButtonListener extends MouseAdapter{
		private int action;
		public ButtonListener(int action){
			super();
			this.action= action;
		}
		public void mouseClicked(MouseEvent e){
			/*JFrame new_frame = new JFrame();
			new_frame.setVisible(true);
			Utilities.setWindow(new_frame);*/
			JPanel new_panel= new JPanel();
			
			//If left clicked
			if(e.getButton() ==  MouseEvent.BUTTON1){
				switch(this.action){
					case CustomerInterface.LOG_IN:
						login();
						break;
					case 1:
						create_cust();
						break;
					case CustomerInterface.UPDATE_PIN:
						change_pin();
						break;
					case 3:
						delete_cust();
						break;
					case 4:
						create_tables();
						break;
					case 5:
						destroy_tables();
						break;
					case CustomerInterface.DEPOSIT:
						deposit();
						break;
					case 7:
						top_up();
						break;
					case CustomerInterface.WITHDRAWAL:
						withdrawal();
						break;
					case 9:
						purchase();
						break;
				}
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