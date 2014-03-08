package se.kth.id1217;

import se.kth.id1217.hwapi.DoorAction;

public class Main {

    public static void main(String[] args) throws Exception {
        // Check parameter count
        if (args.length != 3) {
            System.err
                    .println("Usage: java se.kth.id1217.Main hostname port number_of_elevators");
            System.exit(1);
        }

        // Parse command line parameters
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        int numberOfElevators = Integer.parseInt(args[2]);

        // Create and connect socket handler
        SocketHardwareController shwc = new SocketHardwareController();
        shwc.connect(hostname, port);

        // Attach master controller
        MasterController mc = new MasterController(shwc, numberOfElevators);
        shwc.addHardwareListener(mc);

        // Open all doors
        shwc.handleDoor(0, DoorAction.DoorOpen);
    }

}
