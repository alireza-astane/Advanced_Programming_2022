# Advanced Programming Course Repository

This repository contains exercises and a project from the Advanced Programming course at Sharif University of Technology (SUT) under the supervision of Dr. Tefagh.

## Overview

The repository is structured around several exercises and a final project. All code is written in Java and demonstrates various concepts taught during the course. The repository covers topics from basic Java classes and collections to advanced topics such as GUI programming, network communication, and functional programming.

## Tools and Techniques

- **Java Development:** All exercises and projects are implemented using Java.  
- **Object-Oriented Programming:** Demonstrates the use of classes, inheritance, polymorphism, abstract classes, enums, and generics.
- **Collections Framework:** Utilizes data structures such as `List`, `Queue`, `Stack`, `Set`, `Map`, and utility methods from the Collections framework.
- **String Manipulation and File I/O:** Implements techniques in string handling, regular expressions, recursion, and branching.
- **Functional Programming:** Contains examples of using lambda expressions, streams API, and network programming for both server-client applications and GUI development.
- **Graphical User Interface (GUI):** Used in exercises (e.g., a graphical simulator for Coup card game in [exercise_4](exercise_4/README.md)) and the project.
- **Network Programming:** The project simulates two websites using a client-server model.

## Repository Structure

- **exercise_3/**  
  Contains the "Game of Generals" exercise with two versions of the game: one with GUI (using Swing) and one without.
  
- **exercise_4/**  
  Implements a graphical simulation of the Coup card game in a four-player mode (one human player and three simple bots) using the JavaFX library.
  
- **exercise_5/**  
  A set of exercises focused on data types, collections, and matrix operations. Multiple implementations illustrate different aspects such as linked lists, stacks, map operations, and basic command processing.
  
- **exercise_6/**  
  Contains a simulator for the Mind card game based on a Host/Client structure (without GUI).
  
- **Project/**  
  The final project simulates aspects of SUT websites (cw.sharif.edu & edu.sharif.edu) as a client-server application using Java, Gradle, and JavaFX.
  
- **LICENSE.md**  
  Provides licensing details under the MIT License.
  
- **README.md (root)**  
  This file summarizes the repository contents, objectives, processes, and build instructions.

## Objectives and Process

The primary objectives of this course were to:
- Learn and apply advanced Java programming concepts.
- Develop proficiency with object-oriented design and problem-solving using Java.
- Gain hands-on experience with common data structures and algorithms.
- Understand functional programming constructs in Java.
- Build networked and GUI applications using JavaFX and Swing.
- Simulate real-world applications through a final project.

The process involved working on a series of exercises that gradually introduced more complex topics:
1. **Basic Java Classes and Data Types:** Focusing on class structure, access modifiers, enums, inheritance, and polymorphism.
2. **Collections:** Managing different data types and operations on Lists, Queues, Stacks, Sets, and Maps.
3. **String Manipulation and Recursion:** Implementing file handling, regular expressions, and branching logic.
4. **Functional Programming:** Incorporating concepts such as lambda expressions, stream API usage, GUI design, and network programming.

## Course Syllabus Topics

- **Java Classes:**  
  Package structure, Access modifiers, Enum, Inheritance, Polymorphism, Abstract classes, The Object class, and Generics basics.
  
- **Java Collections:**  
  Handling Lists, Queues, Stacks, Sets, Maps, Iterators and using the Collections utility class.
  
- **String Manipulation and Recursion:**  
  File operations, Regular expressions, Recursion techniques, and control branching.
  
- **Java Functional Programming:**  
  Incorporating GUI (Swing & JavaFX), Network programming, Stream API, and Lambda expressions.

Make sure you have Java, Gradle, and any necessary libraries (such as JavaFX) installed and correctly configured in your environment to reproduce the build and execution steps.





## Project Overview

The final project simulates aspects of real-world web applications, specifically recreating functionalities of SUT websites (e.g., cw.sharif.edu and edu.sharif.edu) using a client-server architecture. The project is built using Java, Gradle, and JavaFX, integrating various advanced programming concepts learned during the course into a cohesive and practical application.

## Project Objectives

The main objectives of the project are:
- **Apply Advanced Java Concepts:** Demonstrate proficiency in object-oriented and functional programming in Java.
- **Client-Server Communication:** Develop and implement network communication between client and server components.
- **Graphical User Interface:** Utilize JavaFX to create a responsive and user-friendly interface.
- **Real-World Simulation:** Mimic functionalities of university web services to provide practical, hands-on experience in system simulation and integration.
- **Integration of Course Topics:** Incorporate topics such as collections, lambda expressions, stream API, and GUI design to showcase a well-rounded application of course material.
- **Documentation and Presentation:** Provide clear documentation and user guides to explain the project's functionality and features.

- ** Data Handling:** Implement data structures and algorithms to manage user data, course information, and system operations by MySQL.



## Project Structure

The repository is organized as follows:

- **Project/**  
  Contains the full implementation of the final project.
  - **src/**  
    Source code organized into packages for client, server, and common utilities.
  - **build.gradle**  
    Gradle configuration file to handle dependencies and build tasks.
  - **resources/**  
    Contains configuration files and application resources such as FXML files for JavaFX.
  - **docs/**  
    Documentation for the project including design documents and user guides.
  
---
## Reproducing the Exercises and the Final Project

To ensure you can build and run all exercises as well as the final project, follow these steps:

### Prerequisites

- **Java:** Make sure the Java Development Kit (JDK) is installed and available in your system's PATH.
- **Gradle:** For the final project, install Gradle or use the Gradle wrapper provided in the repository.
- **JavaFX:** If your system or project requires JavaFX, ensure it is correctly installed and configured.

### Build and Run Instructions

The repository includes a Makefile that automates the build process. The Makefile compiles the Java source code for each exercise and builds the final project.

#### Using the Makefile

1. **Navigate to the Repository Root:**
   Open your terminal and navigate to the root of the repository:
   ```sh
   cd /home/alireza-astane/Desktop/Advanced_Programming_2022
   ```

2. **Build All Exercises and the Project:**
To compile all exercises (Exercise 3 to Exercise 6) and build the final project, run:
   ```sh
   make
   ```

3. **Build a Specific Module:**

To build a specific exercise, for example, Exercise 4 – Coup Card Game Simulator:
```sh
make exercise_4
```

To build only the final project:
```sh
make project
```
Clean Build Artifacts: To remove all compiled binaries and clean up the project builds, run:
```sh
make clean
```



### How It Works
#### Compilation:
The Makefile uses javac with defined source and binary directories for each exercise. For instance, source files in exercise_6/src are compiled into exercise_6/bin.

#### Project Build via Gradle:
The final project is compiled and built by navigating to the Project directory and invoking Gradle with the command gradle build.

This setup ensures that anyone checking out the repository can quickly reproduce and execute all exercises and the final project by following these standardized steps.



This repository demonstrates a progressive learning and application of advanced Java programming concepts, culminating in real-world applications built upon the course content.

