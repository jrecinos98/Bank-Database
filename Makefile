


build:
	javac -classpath ./lib/ojdbc8.jar:. @sources.txt -d bin

run:
	java -classpath ./bin:./lib/* Main

clean:
	rm -rf bin