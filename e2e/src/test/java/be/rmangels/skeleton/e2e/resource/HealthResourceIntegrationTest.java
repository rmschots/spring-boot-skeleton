package be.rmangels.skeleton.e2e.resource;

import be.rmangels.skeleton.e2e.E2EIntegrationTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.junit.Test;
import org.springframework.boot.actuate.health.Status;

import java.util.Map;

import static com.jayway.restassured.http.ContentType.JSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

public class HealthResourceIntegrationTest extends E2EIntegrationTest {

    private ObjectMapper objectMapper = applicationContext().getBean(ObjectMapper.class);

    @Test
    public void health() {
        TestHealth health = givenRequest()
                .when()
                .get(urlFor("/health"))
                .then()
                .assertThat()
                .statusCode(OK.value())
                .contentType(JSON)
                .extract()
                .body()
                .as(TestHealth.class);
        assertThat(health).isNotNull();
        assertThat(health.getStatus()).isEqualTo(Status.UP);
    }

    @Getter
    public static class TestHealth {

        private Status status;

        private Map<String, Object> details;

        private TestHealth() {
        }
    }
}
