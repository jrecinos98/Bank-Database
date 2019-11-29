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

public class SystemInterface extends JPanel{

	public enum SystemActions{
		ACTIONS_PAGE,
		CREATE_CUSTOMER,
		DELETE_CUSTOMER,
		CREATE_TABLES,
		DESTROY_TABLES,
		SET_DATE
	}

	private OracleConnection connection;
	private ArrayList<JButton> action_buttons;
	private Hashtable<SystemActions, JPanel> panels;
	private InputForm form;
	private JPanel current_page;
	private JFrame parent_frame;
	
	public SystemInterface(OracleConnection connection){
		super(new GridLayout(4, 2));
		this.connection = connection;
		create_pages();
		//Initial Screen
		current_page= panels.get(SystemActions.ACTIONS_PAGE);
		add(current_page);

	}
	/*Creates all the pages that will be used for the bankTeller interface*/
	private void create_pages(){
		panels= new Hashtable<SystemActions, JPanel>();
		//panels.put(CustomerActions.LOG_IN, create_login_page());

		panels.put(SystemActions.ACTIONS_PAGE, create_actions_page());
		
		panels.put(SystemActions.CREATE_CUSTOMER, create_page(new ArrayList<String> (Arrays.asList("")), "Create Customer", SystemActions.CREATE_CUSTOMER));
		panels.put(SystemActions.DELETE_CUSTOMER, create_page(new ArrayList<String> (Arrays.asList("")), "Delete Customer", SystemActions.DELETE_CUSTOMER));
		panels.put(SystemActions.SET_DATE, create_page(new ArrayList<String> (Arrays.asList("")), "Set Date", SystemActions.SET_DATE ));
		
		
	}
	/* Changes from one page to another*/
	private void update_page(SystemActions page){
		this.remove(current_page);
		this.revalidate();
		this.repaint();
		try{
			current_page= panels.get(page);
			//Actions page has no form associated with it
			if(page != SystemActions.ACTIONS_PAGE){
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
	private JPanel create_page(ArrayList<String> labels, String b_label, SystemActions action){
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
	
	private void create_render_buttons(){
		this.action_buttons= new ArrayList<JButton>(10);

		JButton t= new JButton("Set Date");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(SystemActions.SET_DATE);
            }
		});		
		this.action_buttons.add(t);

		t= new JButton("Create Customer");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(SystemActions.CREATE_CUSTOMER);
            }
		});		
		this.action_buttons.add(t);

		t=new JButton("Delete Customer");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                update_page(SystemActions.DELETE_CUSTOMER);
            }
		});
		this.action_buttons.add(t);

		t= new JButton("Create Tables");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                //update_page(SystemActions.CREATE_TABLES);
                create_tables();
            }
		});
		this.action_buttons.add(t);

		t=  new JButton("Destroy Tables");
		t.addMouseListener(new MouseAdapter() { 
			public void mouseClicked(MouseEvent e) {
                //update_page(SystemActions.DESTROY_TABLES);
                destroy_tables();
            }
		});
		this.action_buttons.add(t);


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
	public void set_date(){
		String d= form.getInput(0);
		if(!Utilities.valid_date(d)){
			form.setLabel("Invalid date.Format: MM-DD-YYYY", Color.red);
			return;
		}
		//split at dash
		String[] date= d.split("-");
		try{
			Bank.set_date(date[2],date[0],date[1], this.connection);
		}catch(Exception e){
			form.setLabel("An error occured", Color.red);
			return;
		}
		System.out.println("Date set to: "+ d);
		update_page(SystemActions.ACTIONS_PAGE);

	}

	class ButtonListener extends MouseAdapter{
		private SystemInterface.SystemActions action;
		public ButtonListener(SystemInterface.SystemActions action){
			super();
			this.action= action;
		}
		public void mouseClicked(MouseEvent e){
			//If left clicked
			if(SwingUtilities.isLeftMouseButton(e)){
				//Reset label/error message upon button click. Not needed but playing it safe
				form.resetLabel();
				switch(this.action){
					case CREATE_CUSTOMER:
						create_cust();
						break;
					case DELETE_CUSTOMER:
						delete_cust();
						break;
					case SET_DATE:
						set_date();
						break;
				}

			}
			else if(SwingUtilities.isRightMouseButton(e)){
				//Essentially a back key
				update_page(SystemActions.ACTIONS_PAGE);
			}
		}
	}

}