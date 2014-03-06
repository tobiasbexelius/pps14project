package se.kth.id1217;

public class MasterController implements HardwareListener {

    HardwareController hwc;
    private int numElevators;
    private ElevatorController[] elevatorControllers;

    public MasterController(int numElevators, double speed) {
        this.numElevators = numElevators;
        hwc = null; // TODO

        for (int i = 0; i < numElevators; i++) {
            elevatorControllers[i] = new ElevatorController(hwc, i, 0, speed);
            elevatorControllers[i].run();
        }

    }

    @Override
    public void onFloorButton(FloorButtonPressDesc ed) {
        elevatorControllers[0].addCommand(ed.getFloor());
    }

    @Override
    public void onCabinButton(CabinButtonPressDesc ed) {
        elevatorControllers[ed.getCabin()].addCommand(ed.getFloor());
    }

    @Override
    public void onPosition(CabinPositionDesc ed) {
        elevatorControllers[ed.getCabin()].setPosition(ed.getPosition());
    }

    @Override
    public void onSpeed(SpeedDesc ed) {
        for (int i = 0; i < numElevators; i++) {
            elevatorControllers[i].setSpeed(ed.getSpeed());
        }
    }

    @Override
    public void onError(ErrorDesc ed) {
        System.out.println("ERROR: " + ed.getMessage());
    }

}
