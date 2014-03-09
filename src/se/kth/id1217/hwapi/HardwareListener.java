package se.kth.id1217.hwapi;

/**
 * Public interface for a hardware listener.
 */
public interface HardwareListener {

    /**
     * Callback for a floor button press.
     * 
     * @param fbpd
     *            Details about the button press.
     */
    public void onFloorButton(FloorButtonPressDesc fbpd);

    /**
     * Callback for a cabin button press.
     * 
     * @param cbpd
     *            Details about the button press.
     */
    public void onCabinButton(CabinButtonPressDesc cbpd);

    /**
     * Callback for a position update.
     * 
     * @param cpd
     *            Details about the position.
     */
    public void onPosition(CabinPositionDesc cpd);

    /**
     * Callback for a speed update.
     * 
     * @param sd
     *            Details about the speed.
     */
    public void onSpeed(SpeedDesc sd);

    /**
     * Callback for an error.
     * 
     * @param ed
     *            Details about the error.
     */
    public void onError(ErrorDesc ed);

}