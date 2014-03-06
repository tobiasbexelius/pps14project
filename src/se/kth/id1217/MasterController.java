package se.kth.id1217;

import java.util.ArrayList;
import java.util.List;

public class MasterController implements HardwareListener {

    private final HardwareController hwc;
    private final List<Elevator> elevators;
    private final List<ElevatorController> elevatorControllers;

    public MasterController(HardwareController hwc, int numberOfElevators) throws Exception {
        this.hwc = hwc;

        // Create elevators and controllers
        elevators = new ArrayList<Elevator>(numberOfElevators);
        elevatorControllers = new ArrayList<ElevatorController>(
                numberOfElevators);
        for (int i = 0; i < numberOfElevators; i++) {
            Elevator elevator = new Elevator(i+1);
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
        elevatorControllers.get(cbpd.getCabin() - 1)
                .addCommand(cbpd.getFloor());
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

        // TODO: replace with clever logic
        elevatorControllers.get(0).addCommand(fbpd.getFloor());
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
