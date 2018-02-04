package be.rmangels.skeleton.interfaces.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class StubServerMonitor extends Thread {

    private static final int PORT = 5000;
    private static final Logger LOGGER = LoggerFactory.getLogger(StubServerMonitor.class);

    private ServerSocket serverSocket;

    private StubServers stubServers = StubServers.getInstance();

    private boolean halt = false;

    private synchronized void startStubs() {
        LOGGER.info("Starting stub servers");
        stubServers.start();
    }

    private synchronized void stopStubs() {
        LOGGER.info("Stopping stub servers");
        stubServers.stop();
    }

    public StubServerMonitor() {
        setDaemon(true);
        setName("StubServerMonitor");
        startStubServerMonitor();
    }

    private void startStubServerMonitor() {
        try {
            serverSocket = new ServerSocket(PORT, 1, InetAddress.getByName("127.0.0.1"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        start();
    }

    @Override
    public void run() {

        while (!halt) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();

                LineNumberReader lin = new LineNumberReader(new InputStreamReader(socket.getInputStream()));

                String cmd = lin.readLine();
                if ("stop".equals(cmd)) {
                    stopStubs();
                } else if ("start".equals(cmd)) {
                    startStubs();
                } else if ("halt".equals(cmd)) {
                    halt = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                closeSilently(socket);
                socket = null;
            }
        }
    }

    private void closeSilently(Socket socket) {
        if (socket != null) {
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }
}
