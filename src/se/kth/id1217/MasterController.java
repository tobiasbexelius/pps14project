package se.kth.id1217;

import java.util.ArrayList;
import java.util.List;

public class MasterController implements HardwareListener {

    HardwareController hwc;
    private int numElevators;
    private List<ElevatorController> elevatorControllers;

    public MasterController(int numElevators, double speed) throws Exception {
        this.numElevators = numElevators;
        SocketHardwareController shwc = new SocketHardwareController();
        shwc.connect("localhost", 4711);
        shwc.addHardwareListener(this);
        this.hwc = shwc;

        elevatorControllers = new ArrayList<ElevatorController>(numElevators);
        for (int i = 0; i < numElevators; i++) {
            ElevatorController controller = new ElevatorController(hwc, i, 0, speed);
            elevatorControllers
                    .add(i, controller);
            new Thread(controller).start();
        }
    }

    @Override
    public void onFloorButton(FloorButtonPressDesc ed) {
        System.err.printf("Floor button pressed: floor=%d type=%s\n",
                ed.getFloor(), ed.getType());
        System.err.flush();
        elevatorControllers.get(0).addCommand(ed.getFloor());
    }

    @Override
    public void onCabinButton(CabinButtonPressDesc ed) {
        System.err.printf("Cabin button pressed: cabin=%s floor=%d \n",
                ed.getCabin(), ed.getFloor());
        System.err.flush();
        elevatorControllers.get(ed.getCabin() - 1).addCommand(ed.getFloor());
    }

    @Override
    public void onPosition(CabinPositionDesc ed) {
        System.err.printf("Cabin position updated: cabin=%d pos=%f\n",
                ed.getCabin(), ed.getPosition());
        System.err.flush();
        elevatorControllers.get(ed.getCabin() - 1)
                .setPosition(ed.getPosition());
    }

    @Override
    public void onSpeed(SpeedDesc ed) {
        System.err.printf("Speed changed: speed=%f\n", ed.getSpeed());
        System.err.flush();
        for (int i = 0; i < numElevators; i++) {
            elevatorControllers.get(i).setSpeed(ed.getSpeed());
        }
    }

    @Override
    public void onError(ErrorDesc ed) {
        System.err.println("ERROR: " + ed.getMessage());
        System.err.flush();
    }

}
