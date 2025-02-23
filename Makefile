# Adjust the JAVA compiler and flags as needed
JAVAC = javac
JFLAGS = -d bin

# Directories for each exercise
EX3_SRC = exercise_3/src
EX3_BIN = exercise_3/bin

EX4_SRC = exercise_4/src
EX4_BIN = exercise_4/bin

EX5_SRC = exercise_5/src
EX5_BIN = exercise_5/bin

EX6_SRC = exercise_6/src
EX6_BIN = exercise_6/bin

# Default target: compile all and build the project
all: exercise_3 exercise_4 exercise_5 exercise_6 project

## Exercise 3 - Game of Generals
exercise_3:
    @echo "Building Exercise 3..."
    mkdir -p $(EX3_BIN)
    $(JAVAC) $(EX3_SRC)/*.java $(JFLAGS) $(EX3_BIN)

## Exercise 4 - Coup Card Game Simulator
exercise_4:
    @echo "Building Exercise 4..."
    mkdir -p $(EX4_BIN)
    $(JAVAC) $(EX4_SRC)/*.java $(JFLAGS) $(EX4_BIN)

## Exercise 5 - Data Structures and Algorithms Exercises
exercise_5:
    @echo "Building Exercise 5..."
    mkdir -p $(EX5_BIN)
    $(JAVAC) $(EX5_SRC)/*.java $(JFLAGS) $(EX5_BIN)

## Exercise 6 - Mind Card Game Simulator
exercise_6:
    @echo "Building Exercise 6..."
    mkdir -p $(EX6_BIN)
    $(JAVAC) $(EX6_SRC)/*.java $(JFLAGS) $(EX6_BIN)

## Project - Final Project using Gradle
# Assumes that Gradle is installed or that ./gradlew is configured
project:
    @echo "Building Final Project..."
    cd Project && gradle build

## Clean binaries and builds
clean:
    @echo "Cleaning exercise builds and project..."
    rm -rf $(EX3_BIN) $(EX4_BIN) $(EX5_BIN) $(EX6_BIN)
    cd Project && gradle clean

.PHONY: all exercise_3 exercise_4 exercise_5 exercise_6 project clean