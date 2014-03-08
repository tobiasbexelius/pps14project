package se.kth.id1217.hwapi;

public interface HardwareListener {
    public void onFloorButton(FloorButtonPressDesc fbpd);

    public void onCabinButton(CabinButtonPressDesc cbpd);

    public void onPosition(CabinPositionDesc cpd);

    public void onSpeed(SpeedDesc sd);

    public void onError(ErrorDesc ed);
}