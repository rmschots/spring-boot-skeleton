package be.rmangels.skeleton.interfaces.monitor;

import be.rmangels.skeleton.interfaces.stub.StubServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.lang.String.format;

public class StubServers {

    private static final Logger LOGGER = LoggerFactory.getLogger(StubServers.class);

    private static StubServers instance;

    private final List<StubServer> registeredStubServers = newArrayList();

    public static StubServers getInstance() {
        if (instance == null) {
            instance = new StubServers();
        }
        return instance;
    }

    private StubServers() {
    }

    public static void main(String[] args) {
        new StubServers().start();
    }

    public void register(StubServer stubServer) {
        if (registeredStubServers.contains(stubServer)) {
            throw new IllegalStateException("stubServer already registered");
        }
        registeredStubServers.add(stubServer);
    }

    void start() {
        startServers();
    }

    private void startServers() {
        for (StubServer server : registeredStubServers) {
            int maxRetries = 20;
            for (int i = 0; i <= maxRetries; i++) {
                try {
                    server.startGooiExceptionIndienPoortNogNietVrij();
                    LOGGER.info(format("$$ StubServer %s started @ port %d", server.getClass().getSimpleName(), server.getPort()));
                    break;
                } catch (Exception e) {
                    LOGGER.warn(format("$$ StubServer %s failed to start on port %d, retrying (%d/%d)", server.getClass().getSimpleName(), server.getPort(), i, maxRetries));
                    sleepUninterruptibly(100, TimeUnit.MILLISECONDS);
                }
                if (i == maxRetries) {
                    LOGGER.error(format("After %d tries, stubserver (%s) still failed", maxRetries, server.getClass()));
                    throw new RuntimeException(format("After %d tries, stubserver (%s) still failed", maxRetries, server.getClass()));
                }
            }
        }
    }

    void stop() {
        for (StubServer server : registeredStubServers) {
            server.stop();
        }
        LOGGER.info("$$ All Stubservers stopped.");
    }
}
