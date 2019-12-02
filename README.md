
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

# To-do
* Make strings for the database creation instead of reading from file
* Test GUI monthly things
* Add Leap year fucntionality
* Make sure can only do monthly on last day of month
* Make monthly statement show all accounts
* Slideshow?
