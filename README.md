
# To get remote access to csil files
`sudo sshfs ncduncan@csil-19.cs.ucsb.edu:/cs/student/ncduncan/github/CS174A_Project ./cs174a`

# To run oracle from command line
## Make sure using pulse VPN to access school network
`sudo /usr/local/pulseUi`
`sqlplus c##YourNetID/YourPerm@cs174a.cs.ucsb.edu/ORCL`

# To reset database (drop/create tables)
* Run sqlplus as above
* use `@./scripts/create_db.sql` from project root directory

# To compile
`javac -classpath ./lib/ojdbc8.jar src/* -d bin`

# To run
`java -cp ./bin Main`



# Questions
* What if a customer with a given ssn already exists in db?
* Can create customer get called without create account? Need to verify initial acct exists?
* Adding multiple owners to an account via testable createAccount method?
* Can create a customer with a linked account that is closed?
* Can a pocket account have multiple owners?
* Can we assume that account creations/customer creations always given unique id?

# To-do
* Update to store interest date in global (bank) table instead of within each account
* Make strings for the database creation instead of reading from file
* Make unit tests that test deposit and other transactions with nonexistant accounts, customers, etc.
* Check for checkings/savings vs pocket accounts on transactions
* Check on datatype for transactions/money instead of real
* On closing a checking/savings account, close all pocket accounts linked to it
* Add $5 fee to pocket account first transaction of the month
* Create function called at end of every transaction to close any accounts at 0 or 0.01 and to reverse any transactions that result in negative balance (just to be safe as should be handled in each transaction)
* Implement is_ftm() function
* Make sure customer cannot link to a closed account