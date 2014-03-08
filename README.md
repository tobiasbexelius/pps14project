# pps14 project: A Multithreaded Controller for the "Green Elevator"

A simulation of a set of elevators.

## How to build

    ant

alternatively

    mkdir -p bin
    javac -d bin src/**/*.java

## How to run

    ./run NUMBER_OF_ELEVATORS TOP_FLOOR

for instance

    ./run 2 4

will run the simulation with 2 elevators and 5 floors (BV and floors 1-4).
