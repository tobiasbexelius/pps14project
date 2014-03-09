package se.kth.id1217;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

import se.kth.id1217.hwapi.DoorAction;
import se.kth.id1217.hwapi.FloorButtonPressDesc;
import se.kth.id1217.hwapi.FloorButtonType;
import se.kth.id1217.hwapi.HardwareController;
import se.kth.id1217.hwapi.MotorAction;

public class ElevatorController implements Runnable {

    private static final double COST_ACTIVE = 10;
    private static final double COST_WRONG_DIRECTION = 5;
    private static final double BONUS_ALONG_ROUTE = -15;
    private static final double BONUS_COMMAND_IN_QUEUE = -1000;

    private Deque<FloorCommand> commandQueue;
    private final Elevator elevator;
    private final HardwareController hwc;
    private boolean emergencyStop;
    private MotorAction currentMotorAction;

    private final Semaphore commandInQueue;

    public ElevatorController(HardwareController hwc, Elevator elevator) {
        this.hwc = hwc;
        this.elevator = elevator;

        commandQueue = new ConcurrentLinkedDeque<FloorCommand>();
        commandInQueue = new Semaphore(0);
        emergencyStop = false;
    }

    public double costToServe(FloorButtonPressDesc fbpd) {
        double cost = 0.0;
        final int floorTo = fbpd.getFloor();

        if (commandQueue.contains(new FloorCommand(fbpd.getFloor(), fbpd
                .getType()))) {
            cost += BONUS_COMMAND_IN_QUEUE;
        }

        if (isActive()) {
            cost += COST_ACTIVE;

            if ((goingUp() && floorTo < elevator.getPosition())
                    || (goingDown() && floorTo > elevator.getPosition())) {
                cost += COST_WRONG_DIRECTION;
            }

            if (floorAlongCurrentRoute(floorTo)) {
                if ((goingUp() && fbpd.getType() == FloorButtonType.GoingUp)
                        || (goingDown() && fbpd.getType() == FloorButtonType.GoingDown)) {
                    cost += BONUS_ALONG_ROUTE;
                }
            }
        }

        cost += Math.abs(floorTo - elevator.getPosition());

        System.out.println(cost + "\t " + commandQueue);
        return cost;
    }

    private boolean floorAlongCurrentRoute(int floor) {
        int turningFloor = getTurningFloor();
        return (goingUp() && turningFloor >= floor && elevator.getPosition() <= floor)
                || (goingDown() && turningFloor <= floor && elevator
                        .getPosition() >= floor);
    }

    private boolean goingDown() {
        return !commandQueue.isEmpty()
                && elevator.isFloorBelow(commandQueue.peek().getFloor());
    }

    private boolean isActive() {
        return !commandQueue.isEmpty();
    }

    private boolean goingUp() {
        return !commandQueue.isEmpty()
                && elevator.isFloorAbove(commandQueue.peek().getFloor());
    }

    private boolean isStopped() {
        return currentMotorAction == MotorAction.MotorStop;
    }

    public synchronized void addCommand(FloorCommand floorCommand) {
        if (!commandQueue.contains(floorCommand)
                && !(elevator.isAtFloor(floorCommand.getFloor()) && isStopped())) {
            commandQueue.add(floorCommand);
            commandInQueue.release();
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

    private void goToFloor(FloorCommand floorCommand) {
        emergencyStop = false;

        if (elevator.isFloorAbove(floorCommand.getFloor())) {
            closeDoor();
            goUp();
        } else if (elevator.isFloorBelow(floorCommand.getFloor())) {
            closeDoor();
            goDown();
        }

        boolean done = false;
        while (!done) {
            if (emergencyStop) {
                stop();
                commandQueue.clear();
                commandInQueue.drainPermits();
                emergencyStop = false;
                return;
            }

            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            updatePosition();
            updateScale();
            if (elevator.isAtFloor()
                    && commandQueue.contains(new FloorCommand(elevator
                            .getFloor()))) {
                commandQueue.remove(new FloorCommand(elevator.getFloor()));
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
            try {
                commandInQueue.acquire();

                FloorCommand command = commandQueue.peek();
                goToFloor(command);

            } catch (InterruptedException e) {
                // Ignore
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
    }

    private int getTurningFloor() {
        int turningFloor = -1;
        if (goingUp()) {
            turningFloor = Integer.MIN_VALUE;
            for (FloorCommand fc : commandQueue) {
                int f = fc.getFloor();
                if (f > turningFloor) {
                    turningFloor = f;
                } else {
                    break;
                }
            }
        } else if (goingDown()) {
            turningFloor = Integer.MAX_VALUE;
            for (FloorCommand fc : commandQueue) {
                int f = fc.getFloor();
                if (f < turningFloor) {
                    turningFloor = f;
                } else {
                    break;
                }
            }
        }
        return turningFloor;

    }
}
