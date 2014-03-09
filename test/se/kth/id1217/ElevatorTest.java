package se.kth.id1217;

import junit.framework.TestCase;

import org.junit.Test;

public class ElevatorTest extends TestCase {

    private static final double LESS_THAN_DELTA = Elevator.DELTA
            - Elevator.DELTA / 10;
    private static final double MORE_THAN_DELTA = Elevator.DELTA
            + Elevator.DELTA / 10;

    @Test
    public void testIsAtFloor() {
        Elevator e = new Elevator(0);
        assertTrue(e.isAtFloor());

        e.setPosition(0.5);
        assertFalse(e.isAtFloor());

        e.setPosition(1.0);
        assertTrue(e.isAtFloor());

        e.setPosition(2 - MORE_THAN_DELTA);
        assertFalse(e.isAtFloor());

        e.setPosition(2 - LESS_THAN_DELTA);
        assertTrue(e.isAtFloor());

        e.setPosition(2 + LESS_THAN_DELTA);
        assertTrue(e.isAtFloor());

        e.setPosition(2 + MORE_THAN_DELTA);
        assertFalse(e.isAtFloor());
    }

    @Test
    public void testIsFloorAbove() {
        Elevator e = new Elevator(0);

        e.setPosition(0);
        assertFalse(e.isFloorAbove(0));
        assertTrue(e.isFloorAbove(1));

        e.setPosition(1 - MORE_THAN_DELTA);
        assertTrue(e.isFloorAbove(1));

        e.setPosition(1 - LESS_THAN_DELTA);
        assertFalse(e.isFloorAbove(1));
    }

    @Test
    public void testIsFloorBelow() {
        Elevator e = new Elevator(0);

        e.setPosition(1);
        assertFalse(e.isFloorBelow(1));
        assertTrue(e.isFloorBelow(0));

        e.setPosition(0 + MORE_THAN_DELTA);
        assertTrue(e.isFloorBelow(0));

        e.setPosition(0 + LESS_THAN_DELTA);
        assertFalse(e.isFloorBelow(0));
    }
}
