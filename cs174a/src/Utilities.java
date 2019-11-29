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
  	    //frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
	}
	public static boolean valid_id(String id){
		if(id.equals("")){
			return false;
		}
		//Add more restrictions if any.
		return true;
	}
	public static boolean valid_pin_format(String pin){
		//If less or greater than 4 chars it is invalid
		if(pin.length()  != 4){
			System.out.println("Length invalid: "+ pin.length());
			return false;
		}
		//Check that all chars are digits
		for (int i=0; i < pin.length();i++){
			if (Character.isLetter(pin.charAt(i))){
				System.out.println("Invalid character: "+ pin.charAt(i));
				return false;
			}
		}
		return true;
	}
	public static boolean valid_money_input(String amount){
		double m;
		//Trim spaces and try to parse
		try{
			m=Double.parseDouble(amount.trim());
		}
		catch(NumberFormatException e){
			System.err.println(e);
			return false;
		}
		//Amount cant be negative
		if(m < 0){
			return false;
		}
		return true;
	}
	public static boolean valid_date(String d){
		if(d.equals("")){
			System.out.println("Empty date");
			return false;
		}
		String [] date= d.split("-");
		if(date.length != 3){
			System.out.println("Date: "+date);
			return false;
		}
		int year;
		int month;
		int day;
		try{
			//Parse Int strips leading 0. Error if it parses double
			year = Integer.parseInt(date[2]);
			month = Integer.parseInt(date[0]);
			day = Integer.parseInt(date[1]);
		}
		catch(NumberFormatException e){
			System.err.println(e);
			return false;
		}	
		System.out.println("Year: "+ year);	
		System.out.println("Month: "+ month);
		System.out.println("Day: "+ day);
		//If negative or unreasonably large
		if (year < 0 || year > 9999){
			return false;
		}
		if(month <=0 || month >12){
			return false;
		}
		if(day <=0 || day > 31){
			return false;
		}
		return true;
	}
	
}