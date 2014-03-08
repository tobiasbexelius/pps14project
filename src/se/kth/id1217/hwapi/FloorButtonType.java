package se.kth.id1217.hwapi;

public enum FloorButtonType {
    GoingDown(-1), GoingUp(1);

    public static FloorButtonType parse(String s) {
        Integer i = Integer.parseInt(s);

        if (i == 1) {
            return GoingUp;
        } else if (i == -1) {
            return GoingDown;
        } else {
            return null;
        }
    }

    private int value;

    private FloorButtonType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
