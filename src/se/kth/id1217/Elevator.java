package se.kth.id1217;

/**
 * Represents an elevator.
 */
public class Elevator {
    private static final double DEFAULT_POSITION = 0.0;
    private static final double DEFAULT_SPEED = 0.000157;
    private static final double DELTA = 0.01;

    private final int id;
    private double position;
    private double speed;

    public Elevator(int id) {
        this.id = id;
        position = DEFAULT_POSITION;
        speed = DEFAULT_SPEED;
    }

    public int getFloor() {
        return (int) position;
    }

    public int getId() {
        return id;
    }

    public double getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isAtFloor(int floor) {
        return Math.abs(position - floor) < DELTA;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
