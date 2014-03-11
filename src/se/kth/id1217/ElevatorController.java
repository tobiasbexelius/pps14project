package se.kth.id1217;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Semaphore;

import se.kth.id1217.hwapi.DoorAction;
import se.kth.id1217.hwapi.FloorButtonPressDesc;
import se.kth.id1217.hwapi.FloorButtonType;
import se.kth.id1217.hwapi.HardwareController;
import se.kth.id1217.hwapi.MotorAction;

/**
 * An elevator controller. Controls the motor and door of a single elevator.
 */
public class ElevatorController implements Runnable {

    private static final int DOOR_MOTION_DELAY = 1500;
    private static final double COST_ACTIVE = 10;
    private static final double COST_WRONG_DIRECTION = 5;
    private static final double BONUS_ALONG_ROUTE = -15;
    private static final double BONUS_COMMAND_IN_QUEUE = -1000;
    private static final double BONUS_STOPPED_AT_FLOOR = -1500;

    private Deque<FloorCommand> commandQueue;
    private final Elevator elevator;
    private final HardwareController hwc;
    private boolean emergencyStop;
    private MotorAction currentMotorAction;
    private final Semaphore addCommandCalled;

    /**
     * Creates a new elevator controller.
     * 
     * @param hwc
     *            A hardware controller.
     * @param elevator
     *            The elevator to control.
     */
    public ElevatorController(HardwareController hwc, Elevator elevator) {
        this.hwc = hwc;
        this.elevator = elevator;

        commandQueue = new ConcurrentLinkedDeque<FloorCommand>();
        addCommandCalled = new Semaphore(0);
        emergencyStop = false;
    }

    /**
     * Heuristic to calculate the "cost" of serving a given floor button press.
     *
     * @param fbpd
     *            The given floor button press.
     * @return A cost.
     */
    public double costToServe(FloorButtonPressDesc fbpd) {
        final int floorTo = fbpd.getFloor();

        // Cost is initially 0.
        double cost = 0.0;

        // Reduce cost for elevator stationary at requested floor.
        if (elevator.isAtFloor(floorTo) && commandQueue.isEmpty()
                && elevator.isDoorOpen()) {
            cost += BONUS_STOPPED_AT_FLOOR;
        }

        // Reduce cost for elevator already tasked with serving an identical
        // request.
        if (commandQueue.contains(new FloorCommand(fbpd.getFloor(), fbpd
                .getType()))) {
            cost += BONUS_COMMAND_IN_QUEUE;
        }

        if (isActive()) {
            // Add cost for being active.
            cost += COST_ACTIVE;

            // Add cost for being a request in the opposite direction.
            if ((goingUp() && floorTo < elevator.getPosition())
                    || (goingDown() && floorTo > elevator.getPosition())) {
                cost += COST_WRONG_DIRECTION;
            }

            // Reduce cost for being a request along the current route.
            if (floorAlongCurrentRoute(floorTo)) {
                if ((goingUp() && fbpd.getType() == FloorButtonType.GoingUp)
                        || (goingDown() && fbpd.getType() == FloorButtonType.GoingDown)) {
                    cost += BONUS_ALONG_ROUTE;
                }
            }
        }

        // Add cost equal to the distance.
        cost += Math.abs(floorTo - elevator.getPosition());

        return cost;
    }

    /**
     * Is the given floor along the current route of this elevator?
     *
     * @param floor
     *            The given floor.
     * @return T/F
     */
    private boolean floorAlongCurrentRoute(int floor) {
        int turningFloor = getTurningFloor();
        boolean floorAlongRouteUp = goingUp() && turningFloor >= floor
                && (elevator.isAtFloor(floor) || elevator.isFloorAbove(floor));
        boolean floorAlongRouteDown = goingDown() && turningFloor <= floor
                && (elevator.isAtFloor(floor) || elevator.isFloorBelow(floor));
        return floorAlongRouteUp || floorAlongRouteDown;
    }

    /**
     * Is the elevator currently heading down?
     *
     * @return
     */
    private boolean goingDown() {
        FloorCommand fc = getNextFloorCommand();
        return fc != null && elevator.isFloorBelow(fc.getFloor());
    }

    private FloorCommand getNextFloorCommand() {
        for (FloorCommand fc : commandQueue) {
            if (elevator.isAtFloor(fc.getFloor())) {
                continue;
            }
            return fc;
        }

        return null;
    }

    /**
     * Is the elevator active? I.e. does it have any commands in its queue?
     *
     * @return
     */
    private boolean isActive() {
        return !commandQueue.isEmpty();
    }

    /**
     * Is the elevator currently heading up?
     *
     * @return
     */
    private boolean goingUp() {
        FloorCommand fc = getNextFloorCommand();
        return fc != null && elevator.isFloorAbove(fc.getFloor());
    }

    /**
     * Is the elevator currently stopped?
     *
     * @return
     */
    private boolean isStopped() {
        return currentMotorAction == MotorAction.MotorStop;
    }

    /**
     * Ask this elevator to serve the given request.
     *
     * @param floorCommand
     *            The given request.
     */
    public void addCommand(FloorCommand floorCommand) {
        // Don't add the command to the queue if it's already present in the
        // queue, or if this elevator is currently stopped on the same floor.
        if (!commandQueue.contains(floorCommand)
                && !(elevator.isAtFloor(floorCommand.getFloor()) && isStopped())) {
            commandQueue.add(floorCommand);
        }
        addCommandCalled.release();
    }

    /**
     * Tells the hardware to close the elevator door, if not already closed.
     * Will wait a short while for the door to close.
     */
    private void closeDoor() {
        if (elevator.isDoorOpen()) {
            hwc.handleDoor(elevator.getId(), DoorAction.DoorClose);
            waitForDoor();
            elevator.closeDoor();
        }
    }

    /**
     * Waits a short while for the door to close. Scales with the speed of the
     * elevator.
     */
    private void waitForDoor() {
        try {
            long duration = Math.round(DOOR_MOTION_DELAY
                    * Elevator.DEFAULT_SPEED / elevator.getSpeed());
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    /**
     * Tells the hardware to send the elevator downwards.
     */
    private void goDown() {
        hwc.handleMotor(elevator.getId(), MotorAction.MotorDown);
        currentMotorAction = MotorAction.MotorDown;
    }

    /**
     * Process the given command. Closes the doors, travels (might stop along
     * the way) and opens the doors.
     *
     * @param floorCommand
     */
    private void goToFloor(FloorCommand floorCommand) {
        // Always clear emergency stop when instructed to go to a new floor
        emergencyStop = false;

        // Close door and start moving up/down
        if (elevator.isFloorAbove(floorCommand.getFloor())) {
            closeDoor();
            goUp();
        } else if (elevator.isFloorBelow(floorCommand.getFloor())) {
            closeDoor();
            goDown();
        }

        boolean done = false;
        while (!done) {
            // Handle emergency stop
            if (emergencyStop) {
                stop();
                commandQueue.clear();
                addCommandCalled.drainPermits();
                emergencyStop = false;
                return;
            }

            try {
                Thread.sleep(0, 500000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // Update position and scale
            updatePosition();
            updateScale();

            // Stop at floor if it's in our queue
            if (elevator.isAtFloor()) {
                List<FloorCommand> commands = findCommandsInQueueByFloorAndCurrentDirection(elevator
                        .getFloor());
                if (!commands.isEmpty()) {
                    System.err
                            .printf("Stopping at floor %d, removing the following commands: %s\n",
                                    elevator.getFloor(), commands);
                    System.err.flush();
                    commandQueue.removeAll(commands);
                    done = true;
                    stop();
                    openDoor();
                }
            }
        }
    }

    /**
     * Finds commands in the command queue matching the given floor and the
     * current direction of this elevator, if any.
     *
     * @param floor
     *            The given floor.
     * @return
     */
    private List<FloorCommand> findCommandsInQueueByFloorAndCurrentDirection(
            int floor) {
        List<FloorCommand> commands = new LinkedList<FloorCommand>();

        // Find the button type to look for
        FloorButtonType type = null;
        if (goingUp()) {
            type = FloorButtonType.GoingUp;
        } else if (goingDown()) {
            type = FloorButtonType.GoingDown;
        }

        // Search command queue for matching commands
        for (FloorCommand fc : commandQueue) {
            // Floor matches
            if (fc.getFloor() == floor) {
                // Direction matches
                if (fc == commandQueue.getFirst() || fc.getType() == null
                        || type == null || fc.getType() == type) {
                    commands.add(fc);
                }
            }
        }

        return commands;
    }

    /**
     * Tells the hardware to send the elevator upwards.
     */
    private void goUp() {
        hwc.handleMotor(elevator.getId(), MotorAction.MotorUp);
        currentMotorAction = MotorAction.MotorUp;
    }

    /**
     * Tells the hardware to open the elevator door, if not already open. Will
     * wait a short while for the door to open and to let passengers in.
     */
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
                addCommandCalled.acquire();
            } catch (InterruptedException e) {
                // Ignore
            }

            // Process the floor command if there is one.
            FloorCommand command = commandQueue.peek();
            if (command != null) {
                goToFloor(command);
            }
        }
    }

    /**
     * Tells the hardware to send the elevator upwards.
     */
    private void stop() {
        hwc.handleMotor(elevator.getId(), MotorAction.MotorStop);
        currentMotorAction = MotorAction.MotorStop;
    }

    /**
     * Asks the hardware for the current position of this elevator.
     */
    private void updatePosition() {
        hwc.whereIs(elevator.getId());
    }

    /**
     * Tells the hardware to update the scale for this elevator.
     */
    private void updateScale() {
        hwc.handleScale(elevator.getId(), elevator.getFloor());
    }

    /**
     * Trigger the emergency stop.
     */
    public void emergencyStop() {
        emergencyStop = true;
    }

    /**
     * Calculate the floor which this elevator will turn at.
     *
     * @return
     */
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
