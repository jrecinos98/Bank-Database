
build:
	find ./src -type f -name "*.java" > sources.txt
	javac -classpath ./lib/ojdbc8.jar:./src @sources.txt -d ./bin

clean:
	rm ./bin/cs174a/*

run:
	java -classpath ./bin:./lib/* cs174a.Main
gui:
	make build
	java -classpath ./bin:./lib/* cs174a.Main