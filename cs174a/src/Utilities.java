package cs174a;
import java.util.Scanner;

public class Utilities{
	public static String prompt(String p){
		System.out.println(p);
		Scanner in = new Scanner(System.in);
		String resp = in.nextLine();
		return resp;
	}
}