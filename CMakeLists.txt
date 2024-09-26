cmake_minimum_required(VERSION 3.29)
project(SC3020_Project_1)

set(CMAKE_CXX_STANDARD 20)

add_executable(SC3020_Project_1 main.cpp
        Record.cpp
        Record.h
        Block.cpp
        Block.h
        Address.cpp
        Address.h)
