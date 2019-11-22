
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

import javax.swing.*;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

public class BankTellerInterface extends JPanel{
	private JButton b1;
	public BankTellerInterface(){
		b1 = new JButton("Hello");
		b1.setMnemonic(KeyEvent.VK_D);
    	b1.setActionCommand("disable");
		setLayout(new FlowLayout());
		add(b1);
	}
	public void run(){
		System.out.println("-- BankTellerInterface --");
	}
}