package be.rmangels.skeleton.e2e;

import be.rmangels.skeleton.e2e.testapplication.TestApplication;
import be.rmangels.skeleton.interfaces.stub.Stubs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.ObjectMapperConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.Timeout;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.TimeUnit;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.HttpClientConfig.httpClientConfig;
import static com.jayway.restassured.http.ContentType.JSON;

public abstract class E2EIntegrationTest {

    protected TestApplication testApplication = TestApplication.getInstance().start();

    @Rule
    public Timeout globalTimeout = new Timeout(45000000, TimeUnit.SECONDS);

    @Before
    public void doBefore() {
        Stubs.getInstance().resetStubs();
    }

    protected ApplicationContext applicationContext() {
        return testApplication.applicationContext();
    }

    protected <T> T getBean(Class<T> clazz) {
        return applicationContext().getBean(clazz);
    }

    @Before
    public void configureRestAssuredObjectMapper() {
        ObjectMapper objectMapper = getBean(ObjectMapper.class);
        RestAssured.config = RestAssuredConfig
                .config()
                .httpClient(httpClientConfig().setParam("CONNECTION_MANAGER_TIMEOUT", 15000))
                .objectMapperConfig(
                        new ObjectMapperConfig()
                                .jackson2ObjectMapperFactory((aClass, s) -> objectMapper));
    }

    protected String urlFor(String path) {
        return testApplication.contextPath() + path;
    }

    protected RequestSpecification givenRequest() {
        return given()
                .baseUri(testApplication.hostUrl())
                .port(testApplication.port())
                .contentType(JSON);
    }
}
