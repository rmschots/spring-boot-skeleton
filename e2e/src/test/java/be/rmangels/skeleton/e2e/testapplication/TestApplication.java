package be.rmangels.skeleton.e2e.testapplication;

import be.rmangels.skeleton.infrastructure.test.poller.Poller;
import be.rmangels.skeleton.interfaces.monitor.StubServerMonitor;
import be.rmangels.skeleton.interfaces.stub.Stubs;
import com.jayway.restassured.specification.RequestSpecification;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNot;
import org.springframework.context.ApplicationContext;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

public class TestApplication {

    private static TestApplication INSTANCE = null;

    private final Stubs stubs;
    private final TestTomcat tomcat;
    private boolean started = false;

    private TestApplication() {
        stubs = Stubs.getInstance();
        tomcat = TestTomcat.getInstance();
    }

    public String hostUrl() {
        return tomcat.hostUrl();
    }

    public int port() {
        return tomcat.port();
    }

    public String contextPath() {
        return tomcat.contextPath();
    }

    public ApplicationContext applicationContext() {
        return tomcat.applicationContext();
    }

    public <T> T getBean(Class<T> clazz) {
        return applicationContext().getBean(clazz);
    }

    public TestApplication start() {
        if (!started) {
            startStubs();
            startTomcat();
            waitUntilStarted();
            verifyHealth();
            started = true;
        }
        return this;
    }

    private void startStubs() {
        stubs.startStubs();
        new StubServerMonitor();
    }

    private void startTomcat() {
        tomcat.start();
    }

    private void waitUntilStarted() {
        Poller.of()
                .doAssert(() ->
                        givenRequest()
                                .when()
                                .get(contextPath() + "/health")
                                .then()
                                .assertThat()
                                .statusCode(IsNot.not(NOT_FOUND.value()))
                );
    }

    private void verifyHealth() {
        System.out.println("Verifying health...");
        givenRequest()
                .when()
                .get(contextPath() + "/health")
                .then()
                .assertThat()
                .statusCode(OK.value())
                .contentType(JSON)
                .body(CoreMatchers.not(containsString("DOWN")));
        System.out.println("Health OK!");
    }

    public void stop() {
        if (started) {
            tomcat.stop();
            stubs.stopStubs();
            started = false;
        }
    }

    private RequestSpecification givenRequest() {
        return given()
                .baseUri(hostUrl())
                .port(port());
    }

    public static TestApplication getInstance() {
        if (INSTANCE == null)
            INSTANCE = new TestApplication();
        return INSTANCE;
    }

    public static void main(String[] args) {
        TestApplication.getInstance().start();
    }
}
