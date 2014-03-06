package se.kth.id1217;

public class CabinPositionDesc {
    private final int cabin;
    private final double position;

    public CabinPositionDesc(int cabin, double position) {
        this.cabin = cabin;
        this.position = position;
    }

    public int getCabin() {
        return cabin;
    }

    public double getPosition() {
        return position;
    }

}
