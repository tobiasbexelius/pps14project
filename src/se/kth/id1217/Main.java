package se.kth.id1217;

public class Main implements HardwareListener {

    public static void main(String[] args) throws Exception {
        Main m = new Main();
        SocketHardwareController shwc = new SocketHardwareController();
        shwc.addHardwareListener(m);
        shwc.connect("localhost", 4711);
    }

    @Override
    public void onFloorButton(FloorButtonPressDesc ed) {
        System.out.printf("Floor button pressed: floor=%d type=%s\n",
                ed.getFloor(), ed.getType());
    }

    @Override
    public void onCabinButton(CabinButtonPressDesc ed) {
        System.out.printf("Cabin button pressed: cabin=%d floor=%d\n",
                ed.getCabin(), ed.getFloor());
    }

    @Override
    public void onPosition(CabinPositionDesc ed) {
        System.out.printf("Cabin position updated: cabin=%d pos=%f\n",
                ed.getCabin(), ed.getPosition());
    }

    @Override
    public void onSpeed(SpeedDesc ed) {
        System.out.printf("Speed changed: %f\n", ed.getSpeed());
    }

    @Override
    public void onError(ErrorDesc ed) {
        System.out.printf("Error: %s\n", ed.getMessage());
    }

}
