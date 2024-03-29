package se.kth.id1217.hwapi;

/**
 * Enum representing the different motor actions.
 */
public enum MotorAction {

    MotorDown(-1), MotorStop(0), MotorUp(1);

    private final int value;

    private MotorAction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
