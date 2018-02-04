package be.rmangels.skeleton.e2e.testapplication;

import be.rmangels.skeleton.jar.Application;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class TestTomcat {

    private static TestTomcat INSTANCE;

    private boolean started = false;
    private ConfigurableApplicationContext applicationContext;

    private TestTomcat() {
    }

    public String hostUrl() {
        return "http://localhost";
    }

    public int port() {
        return applicationContext().getEnvironment().getProperty("server.port", Integer.class);
    }

    public String contextPath() {
        return applicationContext().getEnvironment().getProperty("server.context-path");
    }

    public String baseUrl() {
        return hostUrl() + ":" + port();
    }

    public TestTomcat start() {
        if (!started) {
            startTomcat();
            started = true;
        }
        return this;
    }

    public void stop() {
        if (started) {
            applicationContext().close();
            started = false;
        }
    }

    private void startTomcat() {
        applicationContext = new SpringApplicationBuilder()
                .sources(Application.class)
                .run();
    }

    public ConfigurableApplicationContext applicationContext() {
        return applicationContext;
    }

    public static TestTomcat getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestTomcat();
        }
        return INSTANCE;
    }

    public static void main(String[] args) {
        TestTomcat.getInstance().start();
    }
}
