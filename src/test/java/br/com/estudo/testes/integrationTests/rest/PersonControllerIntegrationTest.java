package br.com.estudo.testes.integrationTests.rest;

import br.com.estudo.testes.config.TestsConfig;
import br.com.estudo.testes.integrationTests.testconteiners.AbstractIntegrationTest;
import br.com.estudo.testes.model.Person;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import utils.PersonUtils;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PersonControllerIntegrationTest extends AbstractIntegrationTest {

    private static RequestSpecification requestSpecification;
    private static ObjectMapper objectMapper;
    private static Person person;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        requestSpecification = new RequestSpecBuilder()
                .setBasePath("/person")
                .setPort(TestsConfig.SERVER_PORT)
                    .addFilter(new RequestLoggingFilter(LogDetail.ALL))
                    .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
                .build();

        person = PersonUtils.returnPersonWithoutId();
    }

    @Test
    @Order(1)
    @DisplayName("integration test given person object when create person should return a person object")
    void integrationTestGivenPersonObjectWhenCreatePersonShouldReturnAPersonObject() throws JsonProcessingException {
        var content = given()
            .spec(requestSpecification)
                .contentType(TestsConfig.CONTENT_TYPE_JSON)
                .body(person)
            .when()
                .post()
            .then()
                .statusCode(200)
                    .extract()
                        .body().asString();

        Person createdPerson = objectMapper.readValue(content, Person.class);


        assertNotNull(createdPerson);
        assertTrue(createdPerson.getId() > 0);
        assertEquals(person.getEmail(), createdPerson.getEmail());
        person = createdPerson;
    }

    @Test
    @Order(2)
    @DisplayName("integration test given person object when update person should return a updated person object")
    void integrationTestGivenPersonObjectWhenUpdatePersonShouldReturnAnUpdatedPersonObject() throws JsonProcessingException {
        String expectedNewLastName = "Silva";
        String expectedNewEmail = "novo@email.com";
        person.setLastName(expectedNewLastName);
        person.setEmail(expectedNewEmail);

        var content = given()
            .spec(requestSpecification)
                .contentType(TestsConfig.CONTENT_TYPE_JSON)
                .body(person)
            .when()
                .put()
            .then()
                .statusCode(200)
                    .extract()
                    .body().asString();

        Person updatedPerson = objectMapper.readValue(content, Person.class);

        assertNotNull(updatedPerson);
        assertTrue(updatedPerson.getId() > 0);
        assertEquals(person.getEmail(), updatedPerson.getEmail());
        person = updatedPerson;
    }

    @Test
    @Order(3)
    @DisplayName("integration test given person object when find by id should return a person object")
    void integrationTestGivenPersonObjectWhenFindByIdShouldReturnAPersonObject() throws JsonProcessingException {

        var content = given()
                .spec(requestSpecification).pathParam("id", person.getId())
                .when()
                    .get("/{id}")
                .then()
                    .statusCode(200)
                        .extract()
                            .body().asString();

        Person foundPerson = objectMapper.readValue(content, Person.class);

        assertNotNull(foundPerson);
        assertTrue(foundPerson.getId() > 0);
        assertEquals(person.getFirstName(), foundPerson.getFirstName());
        assertEquals(person.getLastName(), foundPerson.getLastName());
        assertEquals(person.getEmail(), foundPerson.getEmail());
//        person = foubdPerson;
    }

    @Test
    @Order(4)
    @DisplayName("integration test when find all should return a person list")
    void integrationTestWhenFindAllShouldReturnAPersonList() throws JsonProcessingException {
        PersonUtils.returnPersonListSizeTwoToFindAllIntegrationTest().forEach(person -> {
            given()
                .spec(requestSpecification)
                    .contentType(TestsConfig.CONTENT_TYPE_JSON)
                    .body(person)
                .when()
                    .post()
                .then()
                    .statusCode(200);
        });


        var content = given()
                .spec(requestSpecification)
                .when()
                    .get()
                .then()
                    .statusCode(200)
                        .extract()
                            .body().asString();

        Person[] personArray = objectMapper.readValue(content, Person[].class);
        List<Person> personList = Arrays.asList(personArray);
        Person personThree = personList.get(2);

        assertNotNull(personList);
        assertEquals(3, personList.size());
        assertEquals("Larisse", personThree.getFirstName());
        assertEquals("Castro", personThree.getLastName());
        assertEquals("larisse@email.com", personThree.getEmail());
    }

    @Test
    @Order(5)
    @DisplayName("integration test given person object when delete should return a no content")
    void integrationTestGivenPersonObjectWhenDeleteShouldReturnNoContent() throws JsonProcessingException {

        given()
            .spec(requestSpecification).pathParam("id", person.getId())
            .when()
                .delete("/{id}")
            .then()
                .statusCode(204);

    }


}
