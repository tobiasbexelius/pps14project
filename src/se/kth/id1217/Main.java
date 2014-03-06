package se.kth.id1217;

public class Main {

    public static void main(String[] args) throws Exception {
        SocketHardwareController shwc = new SocketHardwareController();
        shwc.connect("localhost", 4711);
        MasterController mc = new MasterController(shwc, 3);
        shwc.addHardwareListener(mc);
    }

}
