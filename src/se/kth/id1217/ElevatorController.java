package se.kth.id1217;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class ElevatorController implements Runnable {

    private static final double COST_ACTIVE = 100;
    private static final double COST_WRONG_DIRECTION = 50;

    private Deque<Integer> commandQueue;
    private final Elevator elevator;
    private final HardwareController hwc;
    private boolean emergencyStop;
    private MotorAction currentMotorAction;

    public ElevatorController(HardwareController hwc, Elevator elevator) {
        this.hwc = hwc;
        this.elevator = elevator;

        commandQueue = new ConcurrentLinkedDeque<Integer>();
        emergencyStop = false;
    }

    public double costToServe(FloorButtonPressDesc fbpd) {
        double cost = 0.0;

        if (isActive()) {
            cost += COST_ACTIVE;

            if ((goingUp() && fbpd.getFloor() < elevator.getPosition())
                    || (goingDown() && fbpd.getFloor() > elevator.getPosition())) {
                cost += COST_WRONG_DIRECTION;
            }

            if ((goingUp() && fbpd.getType() == FloorButtonType.GoingDown)
                    || (goingDown() && fbpd.getType() == FloorButtonType.GoingUp)) {
                cost += COST_WRONG_DIRECTION;
            }
        }

        cost += Math.abs(fbpd.getFloor() - elevator.getPosition());

        return cost;
    }

    private boolean goingDown() {
        return currentMotorAction == MotorAction.MotorDown;
    }

    private boolean isActive() {
        return currentMotorAction != MotorAction.MotorStop;
    }

    private boolean goingUp() {
        return currentMotorAction == MotorAction.MotorUp;
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
        currentMotorAction = MotorAction.MotorDown;
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
        currentMotorAction = MotorAction.MotorUp;
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
        currentMotorAction = MotorAction.MotorStop;
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
