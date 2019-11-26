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

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ImageIcon;
import javax.swing.JTabbedPane;
import javax.swing.*;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;



public class Interface extends JFrame {
	private JPanel bankTellerTab;
	private JTabbedPane tabView;
	private JTabbedPane tabbedPane;
	private CustomerInterface customer;
	private BankTellerInterface bankTeller;
	private OracleConnection connection;


	
    public Interface(OracleConnection connection) {
    	setTitle("Bank");
       	Utilities.setWindow(this);

       	customer= new CustomerInterface(connection);
       	bankTeller= new BankTellerInterface(connection);

	    tabView =new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);  
	    tabView.setBounds(50,50,200,200);  
	    tabView.add("Customer", customer);  
	    tabView.add("Bank Teller", bankTeller);   

	    //Add tabview to Frame.
	    add(tabView); 
	    setDefaultCloseOperation(EXIT_ON_CLOSE);

       
        
    }
}

