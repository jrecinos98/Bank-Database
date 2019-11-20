


build:
	javac -classpath ./lib/ojdbc8.jar:. src/* -d bin

run:
	java -classpath ./bin:./lib/* Main