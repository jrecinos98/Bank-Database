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
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.GridLayout;


public class Interface extends JFrame {
	private JPanel bankTellerTab;
	private JTabbedPane tabView;
	private JTabbedPane tabbedPane;

	private CustomerInterface customer;
	private BankTellerInterface bankTeller;
  private SystemInterface system;

	private OracleConnection connection;
	public static JFrame main_frame;

	
    public Interface(OracleConnection connection) {
    	/*try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }*/
    	setTitle("Bank");

      Utilities.setWindow(this);
      main_frame= this;
       	
      customer= new CustomerInterface(connection);
      bankTeller= new BankTellerInterface(connection);
      system= new SystemInterface(connection);


      JPanel atm_content = new JPanel(new GridBagLayout());
      //content.setBackground(Color.GRAY);
      atm_content.setBorder(new EmptyBorder(100,100,100,100));
      atm_content.add(customer);
       
       	
	    tabView =new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);  
	    tabView.setBounds(50,50,200,200);  
	    tabView.add("ATM", atm_content);  
	    tabView.add("Bank Teller", bankTeller); 
      tabView.add("Administrator", system);  

	    //Add tabview to Frame.
	    add(tabView); 
	    
	    setLocationRelativeTo(null);
        setVisible(true);
        
    }
    public void update_frame(JPanel new_panel){
    	/*this.remove(current_panel);
		this.add(new_panel);
		this.revalidate();*/
    }
}

