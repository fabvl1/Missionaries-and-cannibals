Missionaries and Cannibals

This repository contains a complete solution to the classic Missionaries and Cannibals problem. It includes both an automatic implementation using breadth-first search (BFS) and an interactive graphical interface to solve the problem manually.

---

Problem Description

Three missionaries and three cannibals must cross a river using a boat that can only carry up to two people at a time. At no point, on either shore, can the cannibals outnumber the missionaries (if there are missionaries present), as this would endanger the missionaries.

---

Project Structure

· Estado.java: Represents the problem state (number of missionaries and cannibals on each shore and the boat's position).
· Tree.java: Search tree node, used to reconstruct the solution.
· AlgoritmoSolucion.java: Implements breadth-first search (BFS) to find the optimal solution.
· App.java: Runs the automatic solution and prints the steps to the console.
· InterfazManual.java: Graphical interface (Swing) to solve the problem manually, with animations and interactive controls.

---

How to Run

Requirements

· Java 8 or higher

Automatic Execution (Console)

Compile the files:

javac Estado.java Tree.java AlgoritmoSolucion.java App.java

Run the application:

java App

You will see the optimal steps to solve the problem in the console.

---

Manual Execution (Graphical Interface)

Compile the interface file:

javac InterfazManual.java

Run the interface:

java InterfazManual

You can move missionaries and cannibals manually, view the graphical state of the river, the boat, and the characters, and receive hints or undo movements.

---

Credits

Developed for educational purposes and practice in search algorithms and graphical interface development in Java.