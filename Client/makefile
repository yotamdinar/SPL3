CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system

all: BGSclient 
	g++ -pthread -o bin/BGSclient bin/connectionHandler.o bin/BGSclient.o $(LDFLAGS) 

BGSclient: bin/connectionHandler.o bin/BGSclient.o

	
bin/connectionHandler.o: src/connectionHandler.cpp 
	g++ $(CFLAGS) -pthread -o bin/connectionHandler.o src/connectionHandler.cpp

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -pthread -o bin/BGSclient.o src/BGSclient.cpp

bin/test.o: src/test.cpp
	g++ $(CFLAGS) -pthread -o bin/test.o src/test.cpp

bin/test: bin/test.o bin/connectionHandler.o
	g++ -pthread -o bin/test bin/connectionHandler.o bin/test.o $(LDFLAGS)


runtest: bin/test
	bin/test

.PHONY: clean
clean:
	rm -f bin/*
