package se.kth.id1217;

public interface HardwareListener {
    public void onFloorButton(FloorButtonPressDesc ed);

    public void onCabinButton(CabinButtonPressDesc ed);

    public void onPosition(CabinPositionDesc ed);

    public void onSpeed(SpeedDesc ed);

    public void onError(ErrorDesc ed);
}