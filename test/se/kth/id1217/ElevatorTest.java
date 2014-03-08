package se.kth.id1217;

import junit.framework.TestCase;

import org.junit.Test;

public class ElevatorTest extends TestCase {

    @Test
    public void testIsAtFloor() {
        Elevator e = new Elevator(0);
        assertTrue(e.isAtFloor());

        e.setPosition(0.5);
        assertFalse(e.isAtFloor());

        e.setPosition(1.0);
        assertTrue(e.isAtFloor());

        e.setPosition(1.9);
        assertFalse(e.isAtFloor());

        e.setPosition(1.9991);
        assertTrue(e.isAtFloor());
    }
}
