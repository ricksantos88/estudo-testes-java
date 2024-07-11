package br.com.estudo.testes.integrationTests.swagger;

import br.com.estudo.testes.config.TestsConfig;
import br.com.estudo.testes.integrationTests.testconteiners.AbstractIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("test should display swagger ui page")
    void testShouldDisplaySwaggerUiPage() {
        var content = given().basePath("/swagger-ui/index.html").port(TestsConfig.SERVER_PORT)
            .when().get()
            .then().statusCode(200)
            .extract().body().asString();

        Assertions.assertTrue(content.contains("Swagger UI"));
    }

}
