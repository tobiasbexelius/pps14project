package se.kth.id1217;

import se.kth.id1217.hwapi.FloorButtonType;

public class FloorCommand {

    private final int floor;
    private final FloorButtonType type;

    public FloorCommand(int floor, FloorButtonType type) {
        this.floor = floor;
        this.type = type;
    }

    public FloorCommand(int floor) {
        this(floor, null);
    }

    public int getFloor() {
        return floor;
    }

    public FloorButtonType getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("FloorCommand<%d, %s>", floor, type != null ? type
                : "Unknown");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FloorCommand)) {
            return false;
        }
        FloorCommand other = (FloorCommand) obj;

        return other.floor == floor;
    }

    @Override
    public int hashCode() {
        return floor;
    }

}
