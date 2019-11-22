package cs174a;


public class Transaction {
	public String to_acct;
	public String from_acct;
	public String cust_id;
	public int t_id;
	public String date;
	public String transaction_type;
	public double amount;

	public static Transaction create_transaction(String to_acct, String from_acct, String cust_id, int t_id,
									 String date, String transaction_type, double amount){
		return null;

	}

	public Transaction(String to_acct, String from_acct, String cust_id, int t_id,
						String date, String transaction_type, double amount){
		this.to_acct = to_acct;
		this.from_acct = from_acct;
		this.cust_id = cust_id;
		this.t_id = t_id;
		this.date = date;
		this.transaction_type = transaction_type;
		this.amount = amount;
	}

}