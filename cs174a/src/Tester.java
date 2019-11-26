package cs174a;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import oracle.jdbc.OracleConnection;

import java.util.Scanner;
import java.util.ArrayList;

public class Tester{
	private OracleConnection connection;

	public String result(String method, int status){
		String output = method;
		if(status == 0){
			output += " " + "PASS";
		}else if (status == 1){
			output += " " + "FAIL";
		}else{
			output += " " + "ERROR";
		}
		return output;
	}
	public int pass(){ return 0; }
	public int fail(){ return 1; }
	public int error(){ return 2; }

	public void setup(OracleConnection connection){
		// Initialize your system.  Probably setting up the DB connection.
		this.connection = connection;

		if(DBSystem.execute_queries_from_file("./scripts/destroy_db.sql", this.connection)){
			System.out.println("Successfully destroyed tables");
		}else{
			System.out.println("Error destroying tables");
		}

		if(DBSystem.execute_queries_from_file("./scripts/create_db.sql", this.connection)){
			System.out.println("Successfully created tables");
		}else{
			System.out.println("Error creating tables");
		}
	}

	public void teardown(){
		try{
			this.connection.close();
			System.out.println("Tests finished running");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public int test_create_checking_acct(){
		try{
			Account acct = Account.create_account(Testable.AccountType.INTEREST_CHECKING, "1", 1000.00,
										 "111222111", "james", "sample_address", this.connection);
			if(acct == null){
				return fail();
			}

			Customer owner = Customer.get_cust_by_id("111222111", this.connection);
			if(owner == null){
				return fail();
			}

			return pass();
		}catch(Exception e){
			e.printStackTrace();
		}
		return error();

	}

	public int test_create_pocket_acct(){
		try{
			Account linked = Account.get_account_by_id("1", this.connection);
			double balance = linked.balance;
			Account acct = Account.create_pocket_account("2", "1", 50.00,
												    "111222111", this.connection);
			linked = Account.get_account_by_id("1", this.connection);
			if(linked.balance != balance - 50.00 - 5.00){
				return fail();
			}

			if(acct == null){
				return fail();
			}

			return pass();
		}catch(Exception e){
			e.printStackTrace();
		}
		return error();
		
	}

	public int test_close_acct(){
		try{
			return fail();
		}catch(Exception e){
			e.printStackTrace();
		}
		return error();
	}

	public int test_add_cust_to_acct(){
		try{
			return fail();
		}catch(Exception e){
			e.printStackTrace();
		}
		return error();
	}

	public int test_pocket_acct_topup(){
		try{
			return fail();
		}catch(Exception e){
			e.printStackTrace();
		}
		return error();
	}

	public int test_acct_transfer(){
		try{
			Account to_acct = Account.get_account_by_id("1", this.connection);
			Account other_acct = Account.create_account(Testable.AccountType.SAVINGS, "3", 1500.00,
										 "111222111", "", "", this.connection);
			Account not_owned_acct = Account.create_account(Testable.AccountType.SAVINGS, "4", 6900.00,
										 "222444222", "True Slav", "Mother Russia", this.connection);
			if(other_acct == null || not_owned_acct == null){
				System.err.println("Could not create account 3/4");
				return error();
			}

			double balance_to_acct = to_acct.balance;
			double balance_other_acct = other_acct.balance;
			double balance_not_owned_acct = not_owned_acct.balance;

			Transaction transact = Transaction.transfer(to_acct.a_id, other_acct.a_id, "111222111",
			 Bank.get_date(this.connection), Transaction.TransactionType.TRANSFER, 350, this.connection);
			if(transact == null){
				return fail();
			}

			double new_balance_to_acct = Account.get_account_by_id("1", this.connection).balance;
			double new_balance_other_acct = Account.get_account_by_id("3", this.connection).balance;

			if(new_balance_to_acct != balance_to_acct + 350 || new_balance_other_acct != balance_other_acct - 350){
				return fail();
			}

			transact = Transaction.transfer(not_owned_acct.a_id, other_acct.a_id, "111222111",
			Bank.get_date(this.connection), Transaction.TransactionType.TRANSFER, 350, this.connection);
			if(transact != null){
				return fail();
			}


			return pass();
		}catch(Exception e){
			e.printStackTrace();
		}
		return error();
	}

	public int test_pocket_acct_purchase(){
		try{
			Account pocket = Account.get_account_by_id("2", this.connection);
			double balance = pocket.balance;

			// Make sure this fails
			Transaction tran = Transaction.purchase("2", Bank.get_date(this.connection), 222, 
										"111222111", this.connection);
			if(tran != null){
				return fail();
			}

			pocket = Account.get_account_by_id("2", this.connection);
			if(pocket.balance != balance){
				return fail();
			}

			tran = Transaction.purchase("2", Bank.get_date(this.connection), 25, 
										"111222111", this.connection);

			pocket = Account.get_account_by_id("2", this.connection);
			if(tran == null || pocket.balance != balance - 25.0){
				return fail();
			}

			// Should fail as not a pocket account
			tran = Transaction.purchase("1", Bank.get_date(this.connection), 25, 
										"111222111", this.connection);
			if(tran != null){
				return fail();
			}

			// !!!!!Test setting date forward a month and -5 dollars!!!!!
			
			return pass();

		}catch(Exception e){
			e.printStackTrace();
		}
		return error();
		
	}

	public void run_tests(OracleConnection connection){
		// TODO: CHECK THAT ALL TRANSACTIONS CANNOT BE DONE BY A NON-OWNER
		// TODO: WRITE TESTS TO CHECK EVERY ACCOUNT GETS CLOSED ON 0 OR .01 BALANCE

		this.setup(connection);
		ArrayList<String> results = new ArrayList<String>();
		results.add(result("test_create_checking_acct():", this.test_create_checking_acct()));
		results.add(result("test_create_pocket_acct():", this.test_create_pocket_acct()));
		results.add(result("test_pocket_acct_purchase():", this.test_pocket_acct_purchase()));
		results.add(result("test_pocket_acct_topup():", this.test_pocket_acct_topup()));
		results.add(result("test_add_cust_to_acct():", this.test_add_cust_to_acct()));
		results.add(result("test_close_acct():", this.test_close_acct()));
		results.add(result("test_acct_transfer():", this.test_acct_transfer()));


		System.err.println("\n----- RESULTS -----");
		for(int i = 0; i < results.size(); i++){
			System.err.println(results.get(i));
		}

		this.teardown();
	}


}