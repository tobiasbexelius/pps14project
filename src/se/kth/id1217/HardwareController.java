package se.kth.id1217;

public interface HardwareController {
    public void handleDoor(int cabin, DoorAction action);

    public void handleMotor(int cabin, MotorAction action);

    public void handleScale(int cabin, int floor);

    public void whereIs(int cabin);

    public void getSpeed();

    public void terminate();
}
