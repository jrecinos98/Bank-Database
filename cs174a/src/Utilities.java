package cs174a;
import java.util.Scanner;

import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Toolkit;

public class Utilities{
	public static String prompt(String p){
		System.out.println(p);
		Scanner in = new Scanner(System.in);
		String resp = in.nextLine();
		return resp;
	}
	public static void setWindow(JFrame frame){
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
 	    int height = screenSize.height;
  	    int width = screenSize.width;
  	    frame.setSize(width/2, height/2);
  	    frame.setLocationRelativeTo(null);
  	    frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
	}
}