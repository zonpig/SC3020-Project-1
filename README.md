# Execution instructions
1) cd into build folder
2) run `java -cp ./grp11_project1.jar Main`

## if build folder does not exist...
1) run `mkdir build`
2) run `javac -d ./build *.java`
3) cd into build folder
4) run `jar cvf grp11_project1.jar *`
5) run `cp ../games.txt .` to copy games.txt file into build folder
6) from build folder, run `java Main`

## use shell script
1) run `chmod +x build_and_run.sh`
2) run `./build_and_run.sh`