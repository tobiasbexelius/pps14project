package se.kth.id1217;

public class CabinButtonPressDesc {
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

}
