package se.kth.id1217;

import java.util.ArrayList;
import java.util.List;

public class MasterController implements HardwareListener {

    private final HardwareController hwc;
    private final List<Elevator> elevators;
    private final List<ElevatorController> elevatorControllers;

    public MasterController(HardwareController hwc, int numberOfElevators)
            throws Exception {
        this.hwc = hwc;

        // Create elevators and controllers
        elevators = new ArrayList<Elevator>(numberOfElevators);
        elevatorControllers = new ArrayList<ElevatorController>(
                numberOfElevators);
        for (int i = 0; i < numberOfElevators; i++) {
            Elevator elevator = new Elevator(i + 1);
            elevator.openDoor();
            elevators.add(i, elevator);

            ElevatorController controller = new ElevatorController(hwc,
                    elevator);
            elevatorControllers.add(i, controller);

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
            ec.addCommand(cbpd.getFloor());
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

        double minCost = Double.MAX_VALUE;
        ElevatorController minCostElevatorController = null;
        for (ElevatorController ec : elevatorControllers) {
            double cost = ec.costToServe(fbpd);
            if (cost < minCost) {
                minCost = cost;
                minCostElevatorController = ec;
            }
        }

        minCostElevatorController.addCommand(fbpd.getFloor());
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
