#!/bin/bash

# Check if the build directory exists
if [ ! -d "build" ]; then
    # Step 1: Create build directory
    mkdir build

    # Step 2: Compile Java files and output to the build directory
    javac -d build *.java

    # Step 3: Change directory to the build folder
    cd build || exit

    # Step 4: Package the classes into a jar file
    jar cvf grp11_project1.jar *

    # Step 5: Copy the games.txt file to the build folder
    cp ../games.txt .

else
    javac -d build *.java

    cd build || exit

    jar cvf grp11_project1.jar *

fi

# Step 6: Run the Main class from the jar file
java Main
