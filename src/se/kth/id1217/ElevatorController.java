package se.kth.id1217;

import java.util.concurrent.ArrayBlockingQueue;

public class ElevatorController implements Runnable {

    private static final int DEFAULT_POSITION = 0;
    private static final double DEFAULT_SPEED = 0.000157;
    private static final double DELTA = 0.01;
    private static final int CAPACITY = 50;

    private double speed;
    private double position;
    private final int id;
    private HardwareController hwc;
    private ArrayBlockingQueue<Integer> commandQueue; // TODO stack?

    public ElevatorController(HardwareController hwc, int id) {
        this.hwc = hwc;
        this.id = id;
        this.position = DEFAULT_POSITION;
        this.speed = DEFAULT_SPEED;
        commandQueue = new ArrayBlockingQueue<Integer>(CAPACITY);
    }

    @Override
    public void run() {
        try {
            while (true) {
                int command = commandQueue.take();
                goToFloor(command);
            }
        } catch (Exception e) {
        }
    }

    public void addCommand(int floor) {
        commandQueue.add(floor);
    }

    private void goToFloor(int floor) {
        double destination = (double) floor;
        if (Math.abs(position - destination) < DELTA)
            return;

        hwc.handleDoor(id, DoorAction.DoorClose);
        // TODO vänta på att dörrarna stängs
        MotorAction action = ((double) destination) < position ? MotorAction.MotorDown
                : MotorAction.MotorUp;
        hwc.handleMotor(id, action);

        boolean done = false;
        while (!done) {
            try {
                Thread.sleep(1);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hwc.whereIs(id);
            hwc.handleScale(id, (int) position);
            if (Math.abs(position - destination) < DELTA) {
                done = true;
                hwc.handleMotor(id, MotorAction.MotorStop);
            }
        }

        hwc.handleDoor(id, DoorAction.DoorOpen);
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
