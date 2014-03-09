package se.kth.id1217.hwapi;

/**
 * Public interface for a hardware controller.
 */
public interface HardwareController {

    /**
     * Alters the state of the door on the specified cabin.
     * 
     * @param cabin
     *            The number of the cabin.
     * @param action
     *            The requested door action.
     */
    public void handleDoor(int cabin, DoorAction action);

    /**
     * Alters the state of the motor on the specified cabin.
     * 
     * @param cabin
     *            The number of the cabin.
     * @param action
     *            The requested motor action.
     */
    public void handleMotor(int cabin, MotorAction action);

    /**
     * Sets the scale on the specified cabin to the specified floor.
     * 
     * @param cabin
     *            The number of the cabin.
     * @param floor
     *            The number of the floor.
     */
    public void handleScale(int cabin, int floor);

    /**
     * Asks the hardware to report the position of the specified cabin.
     * 
     * @param cabin
     *            The number of the cabin.
     */
    public void whereIs(int cabin);

    /**
     * Asks the hardware for the current speed.
     */
    public void getSpeed();

    /**
     * Terminates the hardware.
     */
    public void terminate();

}
