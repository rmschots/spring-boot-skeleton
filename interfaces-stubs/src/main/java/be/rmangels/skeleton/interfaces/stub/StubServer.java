package be.rmangels.skeleton.interfaces.stub;

import be.rmangels.skeleton.interfaces.monitor.StubServers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class StubServer {

    private static Logger LOGGER = LoggerFactory.getLogger(StubServer.class);
    private boolean isStarted = false;

    public StubServer() {
        StubServers.getInstance().register(this);
    }

    public abstract int getPort();

    public abstract void startGooiExceptionIndienPoortNogNietVrij() throws Exception;

    public abstract void reset();

    public final boolean isStarted() {
        return isStarted;
    }

    public final void start() {
        if (!isStarted) {
            LOGGER.info(String.format("Stubserver %s (port %s): Starting...", getName(), getPort()));
            doStart();
            LOGGER.info(String.format("Stubserver %s (port %s): Started", getName(), getPort()));
            isStarted = true;
        } else {
            LOGGER.warn(String.format("Stubserver %s (port %s) already started: Ignoring request.", getName(), getPort()));
        }
    }

    public final void stop() {
        if (isStarted) {
            LOGGER.info(String.format("Stubserver %s (port %s): Stopping...", getName(), getPort()));
            doStop();
            LOGGER.info(String.format("Stubserver %s (port %s): Stopped", getName(), getPort()));
            isStarted = false;
        } else {
            LOGGER.warn(String.format("Stubserver %s (port %s) already stopped: Ignoring request.", getName(), getPort()));
        }
    }

    protected String getName() {
        return this.getClass().getSimpleName();
    }

    protected abstract void doStart();

    protected abstract void doStop();

}
