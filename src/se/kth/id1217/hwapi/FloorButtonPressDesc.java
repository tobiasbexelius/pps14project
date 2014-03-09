package se.kth.id1217.hwapi;

/**
 * Details about the floor button press event.
 */
public class FloorButtonPressDesc {

    private final int floor;
    private final FloorButtonType type;

    public FloorButtonPressDesc(int floor, FloorButtonType type) {
        this.floor = floor;
        this.type = type;
    }

    public int getFloor() {
        return floor;
    }

    public FloorButtonType getType() {
        return type;
    }

}
