# pps14 project: A Multithreaded Controller for the "Green Elevator"

A simulation of a set of elevators.

## How to build

    ant

alternatively

    javac -d bin src/*.java

## How to run

    java -cp lib/elevator.jar elevator.Elevators -tcp &
    java -cp bin se.kth.id1217.Main localhost 4711 1 3
