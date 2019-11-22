package cs174a;





public class Account{
	public String a_id;
	public String owner_id;
	public String account_type;
	public String bank_branch;
	public double balance;
	public boolean is_open;
	public String interest_date;

	public static Account create_account(Testable.AccountType accountType, String id, double initialBalance,
										 String tin, String name, String address){
		return null;
	}

	public Account(String a_id, String owner_id, String account_type, String bank_branch,
					double balance, boolean is_open, String interest_date){
		// Leave default
		this.a_id = a_id;
		this.owner_id = owner_id;
		this.account_type = account_type;
		this.bank_branch = bank_branch;
		this.balance = balance;
		this.is_open = is_open;
		this.interest_date = interest_date;
	}

}