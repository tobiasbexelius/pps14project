package se.kth.id1217;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import se.kth.id1217.hwapi.CabinButtonPressDesc;
import se.kth.id1217.hwapi.CabinPositionDesc;
import se.kth.id1217.hwapi.DoorAction;
import se.kth.id1217.hwapi.ErrorDesc;
import se.kth.id1217.hwapi.FloorButtonPressDesc;
import se.kth.id1217.hwapi.FloorButtonType;
import se.kth.id1217.hwapi.HardwareController;
import se.kth.id1217.hwapi.HardwareListener;
import se.kth.id1217.hwapi.MotorAction;
import se.kth.id1217.hwapi.SpeedDesc;

public class SocketHardwareController implements HardwareController {
    private List<HardwareListener> listeners;
    private Socket socket;
    private PrintStream out;
    private Thread thread;
    private BufferedReader in;

    public SocketHardwareController() {
        this.listeners = new LinkedList<HardwareListener>();
    }

    public synchronized void addHardwareListener(HardwareListener listener) {
        listeners.add(listener);
    }

    public synchronized void connect(String host, int port)
            throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintStream(socket.getOutputStream());
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        String line = in.readLine();
                        if(line == null){
                            break;
                        }
                        parseLine(line);
                    } catch (IOException e) {
                        break;
                    }
                }
            }

        });
        thread.start();
    }

    private void parseLine(String line) {
        String[] tokens = line.split(" ");
        switch (tokens[0].charAt(0)) {
        case 'b':
            int bfloor = Integer.parseInt(tokens[1]);
            FloorButtonType fbt = FloorButtonType.parse(tokens[2]);
            onFloorButton(new FloorButtonPressDesc(bfloor, fbt));
            break;
        case 'p':
            int pcabin = Integer.parseInt(tokens[1]);
            int pfloor = Integer.parseInt(tokens[2]);
            onCabinButton(new CabinButtonPressDesc(pcabin, pfloor));
            break;
        case 'f':
            int fcabin = Integer.parseInt(tokens[1]);
            double position = Double.parseDouble(tokens[2]);
            onPosition(new CabinPositionDesc(fcabin, position));
            break;
        case 'v':
            double speed = Double.parseDouble(tokens[1]);
            onSpeed(new SpeedDesc(speed));
            break;
        default:
            onError(new ErrorDesc(line));
            break;
        }
    }

    private void onFloorButton(FloorButtonPressDesc fbpd) {
        for (HardwareListener listener : listeners) {
            listener.onFloorButton(fbpd);
        }
    }

    private void onCabinButton(CabinButtonPressDesc cbpd) {
        for (HardwareListener listener : listeners) {
            listener.onCabinButton(cbpd);
        }
    }

    private void onPosition(CabinPositionDesc cpd) {
        for (HardwareListener listener : listeners) {
            listener.onPosition(cpd);
        }
    }

    private void onSpeed(SpeedDesc sd) {
        for (HardwareListener listener : listeners) {
            listener.onSpeed(sd);
        }
    }

    private void onError(ErrorDesc ed) {
        for (HardwareListener listener : listeners) {
            listener.onError(ed);
        }
    }

    @Override
    public synchronized void getSpeed() {
        out.println("v");
        out.flush();
    }

    @Override
    public synchronized void handleDoor(int cabin, DoorAction action) {
        out.printf("d %d %d\n", cabin, action.getValue());
        out.flush();
    }

    @Override
    public synchronized void handleMotor(int cabin, MotorAction action) {
        out.printf("m %d %d\n", cabin, action.getValue());
        out.flush();
    }

    @Override
    public synchronized void handleScale(int cabin, int floor) {
        out.printf("s %d %d\n", cabin, floor);
        out.flush();
    }

    @Override
    public synchronized void terminate() {
        out.println("q");
        out.flush();
    }

    @Override
    public synchronized void whereIs(int cabin) {
        out.printf("w %d\n", cabin);
        out.flush();
    }

}
