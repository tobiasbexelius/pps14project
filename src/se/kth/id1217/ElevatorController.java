package se.kth.id1217;

import java.util.concurrent.ArrayBlockingQueue;

public class ElevatorController implements Runnable {

    private static final int CAPACITY = 50;

    private ArrayBlockingQueue<Integer> commandQueue; // TODO stack?
    private final Elevator elevator;
    private final HardwareController hwc;

    public ElevatorController(HardwareController hwc, Elevator elevator) {
        this.hwc = hwc;
        this.elevator = elevator;

        commandQueue = new ArrayBlockingQueue<Integer>(CAPACITY);
    }

    public void addCommand(int floor) {
        commandQueue.add(floor);
    }

    private void closeDoor() {
        hwc.handleDoor(elevator.getId(), DoorAction.DoorClose);
    }

    private void goDown() {
        hwc.handleMotor(elevator.getId(), MotorAction.MotorDown);
    }

    private void goToFloor(int floor) {
        if (elevator.isAtFloor(floor)) {
            return;
        }

        closeDoor();
        // TODO vänta på att dörrarna stängs
        if (floor > elevator.getPosition()) {
            goUp();
        } else {
            goDown();
        }

        boolean done = false;
        while (!done) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            updatePosition();
            updateScale();
            if (elevator.isAtFloor(floor)) {
                done = true;
                stop();
            }
        }

        openDoor();
    }

    private void goUp() {
        hwc.handleMotor(elevator.getId(), MotorAction.MotorUp);
    }

    private void openDoor() {
        hwc.handleDoor(elevator.getId(), DoorAction.DoorOpen);
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

    private void stop() {
        hwc.handleMotor(elevator.getId(), MotorAction.MotorStop);
    }

    private void updatePosition() {
        hwc.whereIs(elevator.getId());
    }

    private void updateScale() {
        hwc.handleScale(elevator.getId(), elevator.getFloor());
    }

}
