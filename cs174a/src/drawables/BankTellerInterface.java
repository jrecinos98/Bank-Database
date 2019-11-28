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


public class BankTellerInterface extends JPanel{
	public enum BankTellerActions{
		ACTIONS_PAGE,
		CHECK_TRANSACTION,
		MONTHLY_STATEMENT,
		LIST_CLOSED,
		DTER,
		CUSTOMER_REPORT,
		ADD_INTEREST,
		CREATE_ACCOUNT,
		DELETE_TRANSACTIONS,
		DELETE_CLOSED
	}

	private OracleConnection connection;
	private ArrayList<JButton> action_buttons;
	private Hashtable<BankTellerActions, JPanel> panels;
	private InputForm form;
	private JPanel current_page;
	private JFrame parent_frame;


	public BankTellerInterface(OracleConnection connection){
		super(new GridLayout(4, 4));
		this.connection = connection;
		create_pages();
		//Initial Screen
		current_page= panels.get(BankTellerActions.ACTIONS_PAGE);
		add(current_page);
		//parent_frame= Interface.main_frame;
	}

	/*Creates all the pages that will be used for the bankTeller interface*/
	private void create_pages(){
		panels= new Hashtable<BankTellerActions, JPanel>();
		//panels.put(CustomerActions.LOG_IN, create_login_page());

		panels.put(BankTellerActions.ACTIONS_PAGE, create_actions_page());
		panels.put(BankTellerActions.CHECK_TRANSACTION, create_page(new ArrayList<String> (Arrays.asList("")), "Create Check", BankTellerActions.CHECK_TRANSACTION));
		
		panels.put(BankTellerActions.MONTHLY_STATEMENT, create_page(new ArrayList<String> (Arrays.asList("")), "Generate Statement", BankTellerActions.MONTHLY_STATEMENT));
		panels.put(BankTellerActions.LIST_CLOSED, create_page(new ArrayList<String> (Arrays.asList("" )), "List Closed Accounts", BankTellerActions.LIST_CLOSED));
		panels.put(BankTellerActions.DTER, create_page(new ArrayList<String> (Arrays.asList("")), "Generate DTER", BankTellerActions.DTER ));
		panels.put(BankTellerActions.CUSTOMER_REPORT, create_page(new ArrayList<String> (Arrays.asList("")), "Generate Report", BankTellerActions.CUSTOMER_REPORT ));
		panels.put(BankTellerActions.ADD_INTEREST, create_page(new ArrayList<String> (Arrays.asList("")), "Add Interest", BankTellerActions.ADD_INTEREST ));
		panels.put(BankTellerActions.CREATE_ACCOUNT, create_page(new ArrayList<String> (Arrays.asList("")), "Create New Account", BankTellerActions.CREATE_ACCOUNT ));
		panels.put(BankTellerActions.DELETE_TRANSACTIONS, create_page(new ArrayList<String> (Arrays.asList("")), "Delete All Transactions", BankTellerActions.DELETE_TRANSACTIONS ));
		panels.put(BankTellerActions.DELETE_CLOSED, create_page(new ArrayList<String> (Arrays.asList("")), "Delete All Closed Accounts", BankTellerActions.DELETE_CLOSED ));
		
		
	}
	/* Changes from one page to another*/
	private void update_page(BankTellerActions page){
		this.remove(current_page);
		this.revalidate();
		this.repaint();
		try{
			current_page= panels.get(page);
			//Actions page has no form associated with it
			if(page != BankTellerActions.ACTIONS_PAGE){
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
	/*Creates a page with labels and textfields (depends on size of ArrayList)*/
	private JPanel create_page(ArrayList<String> labels, String b_label, BankTellerActions action){
		JButton button= new JButton(b_label);
		button.addMouseListener(new ButtonListener(action));
		InputForm form = new InputForm(labels, button);
		JPanel holder= new JPanel();
		//holder.add(form);
		return form;
	}

	/* Creates the page with user actions*/
	private JPanel create_actions_page(){
		JPanel holder= new JPanel(new GridLayout(2, 1));
		create_render_buttons();
		for(int i=0; i< action_buttons.size();i++){
			holder.add(action_buttons.get(i));
		}	
		return holder;
	}
	//These buttons are used to render new pages from the ACTIONS_PAGE.
	//They perform no operations other than rendering a new JPanel.
	private void create_render_buttons(){
		this.action_buttons= new ArrayList<JButton>(10);

		JButton t= new JButton("Create Account");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(BankTellerActions.CREATE_ACCOUNT);
            }
		});
		this.action_buttons.add(t);

		t= new JButton("Write Check");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(BankTellerActions.CHECK_TRANSACTION);
            }
		});		
		this.action_buttons.add(t);

		t= new JButton("Add Interest");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(BankTellerActions.ADD_INTEREST);
            }
		});
		this.action_buttons.add( t);


		t= new JButton("View Closed Accounts");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(BankTellerActions.LIST_CLOSED);
            }
		});
		this.action_buttons.add(t);
	
		t= new JButton("Generate Customer Report");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(BankTellerActions.CUSTOMER_REPORT);
            }
		});
		this.action_buttons.add(t);

		t=new JButton("Monthly Statement");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(BankTellerActions.MONTHLY_STATEMENT);
            }
		});
		this.action_buttons.add(t);
		

		t= new JButton("Delete Closed Accounts");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(BankTellerActions.DELETE_CLOSED);
            }
		});
		this.action_buttons.add( t);

		t= new JButton("Delete Transactions");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(BankTellerActions.DELETE_TRANSACTIONS);
            }
		});
		this.action_buttons.add(t);

		t=  new JButton("Generate DTER");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(BankTellerActions.DTER);
            }
		});
		this.action_buttons.add(t);

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
	public void check_transaction(){

	}
	public void monthly_statement(){

	}
	public void dter(){

	}
	public void cust_report(){

	}
	public void add_interest(){

	}
	public void delete_closed(){

	}
	public void delete_transactions(){

	}
	class ButtonListener extends MouseAdapter{
		private BankTellerInterface.BankTellerActions action;
		public ButtonListener(BankTellerInterface.BankTellerActions action){
			super();
			this.action= action;
		}
		public void mouseClicked(MouseEvent e){
			//If left clicked
			if(SwingUtilities.isLeftMouseButton(e)){

				//Reset label/error message upon button click. Not needed but playing it safe
				form.resetLabel();
				switch(this.action){
					case CHECK_TRANSACTION:
						check_transaction();
						break;
					case MONTHLY_STATEMENT:
						monthly_statement();
						break;
					case LIST_CLOSED:
						show_closed();
						break;
					case DTER:
						dter();
						break;
					case CUSTOMER_REPORT:
						cust_report();
						break;
					case ADD_INTEREST:
						add_interest();
						break;
					case CREATE_ACCOUNT:
						create_acct();
						break;	
					case DELETE_CLOSED:
						delete_closed();
						break;	
					case DELETE_TRANSACTIONS:
						delete_transactions();
						break;

				}

			}
			else if(SwingUtilities.isRightMouseButton(e)){
				//Essentially a back key
				update_page(BankTellerActions.ACTIONS_PAGE);
			}
		}
	}
}