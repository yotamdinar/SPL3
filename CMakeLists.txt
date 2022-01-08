cmake_minimum_required(VERSION 3.21)
project(SPL3)

set(CMAKE_CXX_STANDARD 14)

include_directories(Client/include)

add_executable(SPL3
        Client/bin/BGSclient
        Client/bin/BGSclient.o
        Client/bin/connectionHandler.o
        Client/include/connectionHandler.h
        Client/src/BGSclient.cpp
        Client/src/connectionHandler.cpp
        Client/src/test.cpp
        Client/makefile)
