package se.kth.id1217;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ElevatorController implements Runnable {

    private Deque<Integer> commandQueue;
    private final Elevator elevator;
    private final HardwareController hwc;
    private boolean emergencyStop;

    public ElevatorController(HardwareController hwc, Elevator elevator) {
        this.hwc = hwc;
        this.elevator = elevator;

        commandQueue = new ConcurrentLinkedDeque<Integer>();
        emergencyStop = false;
    }

    public void addCommand(int floor) {
        if (!commandQueue.contains(floor)) {
            commandQueue.add(floor);
        }
    }

    private void closeDoor() {
        if (elevator.isDoorOpen()) {
            hwc.handleDoor(elevator.getId(), DoorAction.DoorClose);
            waitForDoor();
            elevator.closeDoor();
        }
    }

    private void waitForDoor() {
        try {
            long duration = Math.round(1500 * Elevator.DEFAULT_SPEED
                    / elevator.getSpeed());
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    private void goDown() {
        hwc.handleMotor(elevator.getId(), MotorAction.MotorDown);
    }

    private void goToFloor(int floor) {
        emergencyStop = false;

        if (elevator.isAtFloor(floor)) {
            return;
        }

        closeDoor();

        if (floor > elevator.getPosition()) {
            goUp();
        } else {
            goDown();
        }

        boolean done = false;
        while (!done) {
            if (emergencyStop) {
                stop();
                emergencyStop = false;
                return;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            updatePosition();
            updateScale();
            if (elevator.isAtFloor()
                    && commandQueue.contains((int) Math.round(elevator
                            .getPosition()))) {
                commandQueue.remove((int) Math.round(elevator.getPosition()));
                done = true;
                stop();
                openDoor();
            }
        }
    }

    private void goUp() {
        hwc.handleMotor(elevator.getId(), MotorAction.MotorUp);
    }

    private void openDoor() {
        if (!elevator.isDoorOpen()) {
            hwc.handleDoor(elevator.getId(), DoorAction.DoorOpen);
            waitForDoor();
            elevator.openDoor();

            // Keep door open for a while
            waitForDoor();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (commandQueue.peek() != null) {
                int command = commandQueue.peek();
                goToFloor(command);
            }
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

    public void emergencyStop() {
        emergencyStop = true;
        commandQueue.clear();
    }

}
