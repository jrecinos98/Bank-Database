
build:
	#find ./cs174a -type f -name "*.java" > ../cs174a/bin/sources.txt
	
	javac -classpath ./lib/ojdbc8.jar:./cs174a ./cs174a/* -d ./out/

clean:
	rm ./out/cs174a/*

run:
	java -classpath ./out:./lib/* cs174a.Main
#Need to re build to see changes
gui:
	make build
	java -classpath ./out:./lib/* cs174a.Main