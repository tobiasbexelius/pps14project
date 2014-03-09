package se.kth.id1217.hwapi;

/**
 * Details about the cabin button press event.
 */
public class CabinButtonPressDesc {

    private static final int EMERGENCY_STOP = 32000;

    private final int cabin;
    private final int floor;

    public CabinButtonPressDesc(int cabin, int floor) {
        this.cabin = cabin;
        this.floor = floor;
    }

    public int getCabin() {
        return cabin;
    }

    public int getFloor() {
        return floor;
    }

    public boolean isEmergencyStop() {
        return floor == EMERGENCY_STOP;
    }

}
