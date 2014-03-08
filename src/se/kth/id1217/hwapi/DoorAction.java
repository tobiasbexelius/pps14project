package se.kth.id1217.hwapi;

public enum DoorAction {

    DoorClose(-1), DoorOpen(1), DoorStop(0);

    private final int value;

    private DoorAction(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}
