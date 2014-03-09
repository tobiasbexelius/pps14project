package se.kth.id1217;

import java.util.ArrayList;
import java.util.List;

import se.kth.id1217.hwapi.CabinButtonPressDesc;
import se.kth.id1217.hwapi.CabinPositionDesc;
import se.kth.id1217.hwapi.ErrorDesc;
import se.kth.id1217.hwapi.FloorButtonPressDesc;
import se.kth.id1217.hwapi.HardwareController;
import se.kth.id1217.hwapi.HardwareListener;
import se.kth.id1217.hwapi.SpeedDesc;

/**
 * The master controller. Listens for events from the hardware and passes them
 * on to elevator controllers.
 */
public class MasterController implements HardwareListener {

    private final List<Elevator> elevators;
    private final List<ElevatorController> elevatorControllers;

    /**
     * Instantiates a master controller.
     * 
     * @param hwc
     *            An instance of a hardware controller.
     * @param numberOfElevators
     *            The number of floors on the hardware.
     */
    public MasterController(HardwareController hwc, int numberOfElevators) {
        elevators = new ArrayList<Elevator>(numberOfElevators);
        elevatorControllers = new ArrayList<ElevatorController>(
                numberOfElevators);

        // Create elevators and controllers
        for (int i = 0; i < numberOfElevators; i++) {
            // Create the elevator
            Elevator elevator = new Elevator(i + 1);
            elevator.openDoor();
            elevators.add(i, elevator);

            // Create the elevator controller
            ElevatorController controller = new ElevatorController(hwc,
                    elevator);
            elevatorControllers.add(i, controller);

            // Spawn a new thread for the elevator controller
            new Thread(controller).start();
        }
    }

    @Override
    public void onCabinButton(CabinButtonPressDesc cbpd) {
        System.err.printf("Cabin button pressed: cabin=%s floor=%d \n",
                cbpd.getCabin(), cbpd.getFloor());
        System.err.flush();
        ElevatorController ec = elevatorControllers.get(cbpd.getCabin() - 1);
        if (cbpd.isEmergencyStop()) {
            ec.emergencyStop();
        } else {
            ec.addCommand(new FloorCommand(cbpd.getFloor()));
        }
    }

    @Override
    public void onError(ErrorDesc ed) {
        System.err.println("ERROR: " + ed.getMessage());
        System.err.flush();
    }

    @Override
    public void onFloorButton(FloorButtonPressDesc fbpd) {
        System.err.printf("Floor button pressed: floor=%d type=%s\n",
                fbpd.getFloor(), fbpd.getType());
        System.err.flush();

        // Find the elevator with the lowest cost of serving the button press
        double minCost = Double.MAX_VALUE;
        ElevatorController minCostElevatorController = null;
        for (ElevatorController ec : elevatorControllers) {
            double cost = ec.costToServe(fbpd);
            if (cost < minCost) {
                minCost = cost;
                minCostElevatorController = ec;
            }
        }

        minCostElevatorController.addCommand(new FloorCommand(fbpd.getFloor(),
                fbpd.getType()));
    }

    @Override
    public void onPosition(CabinPositionDesc cpd) {
        elevators.get(cpd.getCabin() - 1).setPosition(cpd.getPosition());
    }

    @Override
    public void onSpeed(SpeedDesc sd) {
        for (Elevator e : elevators) {
            e.setSpeed(sd.getSpeed());
        }
    }

}
