package se.kth.id1217.hwapi;

/**
 * Enum representing the different floor button types.
 */
public enum FloorButtonType {

    GoingDown(-1), GoingUp(1);

    /**
     * Parses the provided string and returns the corresponding floor button
     * type. Returns null if no match is found.
     * 
     * @param s
     *            The string to parse.
     * @return A floor button type, or null if none is found.
     */
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
