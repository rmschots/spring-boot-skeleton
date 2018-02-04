package be.rmangels.skeleton.interfaces.stub;

import be.rmangels.skeleton.interfaces.monitor.StubServerMonitorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Sets.newHashSet;
import static com.jayway.awaitility.Awaitility.await;

public class Stubs {
    private static final Logger LOGGER = LoggerFactory.getLogger(Stubs.class);

    private boolean started = false;

    private long maxStubWaitTimeS = 30L;

    private static Stubs instance;

    public synchronized void startStubs() {
        if (!started) {
            started = true;
            StubServerMonitorClient.stopRemoteStubServer();

        }

        LOGGER.info("Starting stubs if not already started...");
        for (StubServer stubServer : stubServers()) {
            if (!stubServer.isStarted()) {
                await()
                        .ignoreExceptions()
                        .atMost(maxStubWaitTimeS, TimeUnit.SECONDS)
                        .until(() -> {
                            try {
                                stubServer.start();
                            } catch (Exception e) {
                                LOGGER.error("Error starting stub " + stubServer.getName(), e);
                                throw e;
                            }
                            return stubServer.isStarted();
                        });
            }
        }
    }

    public void setMaxStubWaitTimeInSeconds(int numberOfSeconds) {
        maxStubWaitTimeS = numberOfSeconds;
    }

    public void resetStubs() {
        stubServers()
                .forEach(StubServer::reset);
    }

    public synchronized void stopStubs() {
        stubServers()
                .forEach(StubServer::stop);
        started = false;
    }


    private HashSet<StubServer> stubs = newHashSet(
//            XXXStubServer.getInstance()
    );

    public static Stubs getInstance() {
        if (instance == null) {
            instance = new Stubs();
        }
        return instance;
    }

    public Set<StubServer> stubServers() {
        return stubs;
    }

    public static void main(String[] args) {
        Stubs.getInstance().startStubs();
    }
}
