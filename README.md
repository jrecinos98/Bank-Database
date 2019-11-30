
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

# 

# Questions
* Check if account exists before creation
* Allow pocket and student accounts to change interest?

# To-do
* Make strings for the database creation instead of reading from file
* Make unit tests that test deposit and other transactions with nonexistant accounts, customers, etc.
* Make sure customer cannot link to a closed account
* Change types to varchar instead of char
* Test payfriend where it's FTM for both pocket accounts
* Try every transaction with a nonexistant account
* Pocket account accrue interest as a transaction as this could attempt to make negative on balance?
* Add Leap year fucntionality
* Add bank branch to account creation
* Disallow transactions to same account
* Make sure everything compiles correctly
* Prompt for account on transaction if own multiple