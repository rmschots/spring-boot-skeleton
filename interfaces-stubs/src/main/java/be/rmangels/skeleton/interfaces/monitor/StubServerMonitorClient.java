package be.rmangels.skeleton.interfaces.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class StubServerMonitorClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StubServerMonitorClient.class);
    public static final int PORT = 5000;

    public static void stopRemoteStubServer() {
        LOGGER.info("stopping remote stubs...");
        boolean result = sendCommand("stop");
        if (result) {
            try {
                Thread.sleep(4000);
            } catch (Exception e) {
            }
        }

        autoStartRemoteStubsOnClientShutdown();
    }

    private static void autoStartRemoteStubsOnClientShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                setName("StubServerMonitorShutdown");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore
                }
                StubServerMonitorClient.startRemoteStubServer();
            }
        });
    }

    public static void startRemoteStubServer() {
        LOGGER.info("starting remote stubs...");
        sendCommand("start");
    }

    private static boolean sendCommand(String command) {
        try (Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), PORT)) {
            OutputStream out = socket.getOutputStream();
            out.write((command + "\r\n").getBytes());
            out.flush();
            socket.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return false;
        }
        return true;
    }

}
