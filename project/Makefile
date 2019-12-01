
build:
	javac -d out/ -cp ./lib/ojdbc8.jar cs174a/*.java


clean:
	rm ./bin/cs174a/*

run:
	java -cp ./lib/ojdbc8.jar:out:. cs174a.Main
#Need to re build to see changes
gui:
	make build
	java -classpath ./bin:./lib/* cs174a.Main