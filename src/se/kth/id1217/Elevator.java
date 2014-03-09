package se.kth.id1217;

/**
 * Represents an elevator. Holds data about id, position, the door and speed.
 */
public class Elevator {

    public static final double DEFAULT_POSITION = 0.0;
    public static final double DEFAULT_SPEED = 0.000157;
    private static final double DELTA = 0.01;

    private boolean doorOpen;
    private final int id;
    private double position;
    private double speed;

    /**
     * Instantiates an elevator.
     * 
     * @param id
     *            The ID of this elevator.
     */
    public Elevator(int id) {
        this.id = id;
        position = DEFAULT_POSITION;
        speed = DEFAULT_SPEED;
        doorOpen = false;
    }

    /**
     * Marks the door as closed.
     */
    public synchronized void closeDoor() {
        doorOpen = false;
    }

    /**
     * Returns the number of the closest floor.
     * 
     * @return The number of the closest floor.
     */
    public synchronized int getFloor() {
        return (int) Math.round(position);
    }

    /**
     * Returns the ID of this elevator.
     * 
     * @return The ID of this elevator.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current position of this elevator.
     * 
     * @return The current position.
     */
    public synchronized double getPosition() {
        return position;
    }

    /**
     * Returns the current speed of this elevator.
     * 
     * @return The current speed.
     */
    public synchronized double getSpeed() {
        return speed;
    }

    /**
     * Returns <tt>true</tt> iff the elevator is at the specified floor (taking
     * into consideration a slight margin of error). Returns <tt>false</tt>
     * otherwise.
     * 
     * @param floor
     *            The number of the floor.
     * @return <tt>true</tt> iff the elevator is at the specified floor.
     */
    public synchronized boolean isAtFloor(int floor) {
        return Math.abs(position - floor) < DELTA;
    }

    /**
     * Returns <tt>true</tt> iff the elevator is at any floor (taking into
     * consideration a slight margin of error). Returns <tt>false</tt>
     * otherwise.
     * 
     * @param floor
     *            The number of the floor.
     * @return <tt>true</tt> iff the elevator is at any floor.
     */
    public synchronized boolean isAtFloor() {
        return Math.abs(position - Math.round(position)) < DELTA;
    }

    /**
     * Returns <tt>true</tt> iff the door is marked as open. Returns
     * <tt>false</tt> otherwise.
     * 
     * @return <tt>true</tt> iff the door is marked as open.
     */
    public synchronized boolean isDoorOpen() {
        return doorOpen;
    }

    /**
     * Marks the door as open.
     */
    public synchronized void openDoor() {
        doorOpen = true;
    }

    /**
     * Updates the position of this elevator.
     * 
     * @param position
     *            The new position.
     */
    public synchronized void setPosition(double position) {
        this.position = position;
    }

    /**
     * Updates the speed of this elevator.
     * 
     * @param speed
     *            The new speed.
     */
    public synchronized void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Returns <tt>true</tt> iff the given floor is above the elevator (taking
     * into consideration a slight margin of error). Returns <tt>false</tt>
     * otherwise.
     * 
     * @param floor
     *            The number of the floor.
     * @return <tt>true</tt> iff the given floor is above the elevator.
     */
    public synchronized boolean isFloorAbove(int floor) {
        return floor > position + DELTA;
    }

    /**
     * Returns <tt>true</tt> iff the given floor is below the elevator (taking
     * into consideration a slight margin of error). Returns <tt>false</tt>
     * otherwise.
     * 
     * @param floor
     *            The number of the floor.
     * @return <tt>true</tt> iff the given floor is below the elevator.
     */
    public synchronized boolean isFloorBelow(int floor) {
        return floor < position - DELTA;
    }

}
