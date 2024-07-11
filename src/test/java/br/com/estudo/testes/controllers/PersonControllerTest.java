package br.com.estudo.testes.controllers;

import br.com.estudo.testes.exceptions.ResourceNotFoundException;
import br.com.estudo.testes.model.Person;
import br.com.estudo.testes.services.PersonServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import utils.PersonUtils;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonServices personServices;

    private Person personWithoutId;
    private Person personWithId;

    @BeforeEach
    void setUp() {
        personWithoutId = PersonUtils.returnPersonWithoutId();
        personWithId = PersonUtils.returnPersonWithIdEqualOne();

    }

    @Test
    @DisplayName("test given person object when create person then return saved person")
    void testGivenPersonObjectWhenCreatePersonThenReturnSavedPerson() throws Exception {
        given(personServices.create(any(Person.class))).willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(
            post("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personWithoutId))
        );

        // Assertions
        response.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is(personWithoutId.getEmail())));

    }

    @Test
    @DisplayName("test given list of persons when find all person then return person list")
    void testGivenListOfPersonsWhenFindAllPersonThenReturnPersonList() throws Exception {
        List<Person> personList = PersonUtils.returnPersonListSizeThree();
        given(personServices.findAll()).willReturn(personList);

        ResultActions response = mockMvc.perform(get("/person"));

        // Assertions
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.size()", is(personList.size())));

    }

    @Test
    @DisplayName("test given personId when find by id then return person object")
    void testGivenPersonIdWhenFindByIdThenReturnPersonObject() throws Exception {
        Long personId = 1L;
        given(personServices.findById(personId)).willReturn(personWithId);

        ResultActions response = mockMvc.perform(get("/person/{id}", personId));

        // Assertions
        response.andExpect(status().isOk())
            .andDo(print())
            .andExpect(jsonPath("$.id", is(personId.intValue()))) // conversão para valor inteiro por conta do tipo no json
            .andExpect(jsonPath("$.email", is(personWithId.getEmail())));

    }

    @Test
    @DisplayName("test given invalid personId when find by id then return not found")
    void testGivenInvalidPersonIdWhenFindByIdThenReturnNotFound() throws Exception {
        Long personId = 1L;
        given(personServices.findById(personId)).willThrow(ResourceNotFoundException.class);

        ResultActions response = mockMvc.perform(get("/person/{id}", personId));

        // Assertions
        response.andExpect(status().isNotFound()).andDo(print());

    }

    @Test
    @DisplayName("test given update person when update then return updated person object")
    void testGivenUpdatePersonWhenUpdateThenReturnUpdatedPersonObject() throws Exception {
        Long personId = 1L;
        given(personServices.findById(personId)).willReturn(personWithId);
        given(personServices.update(any(Person.class))).willAnswer((invocation) -> invocation.getArgument(0));

        personWithId.setLastName("Salvador");
        personWithId.setEmail("novo@email.com");
        ResultActions response = mockMvc
            .perform(
                put("/person")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(personWithId))
            );

        response.andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is(personWithId.getEmail())))
            .andExpect(jsonPath("$.lastName", is(personWithId.getLastName())));

    }

    @Test
    @DisplayName("test given non existent person when update then return not found")
    void testGivenNonExistentPersonWhenUpdateThenReturnNotFound() throws Exception {
        Long personId = 1L;
        given(personServices.findById(personId)).willThrow(ResourceNotFoundException.class);
        given(personServices.update(any(Person.class))).willAnswer((invocation) -> invocation.getArgument(1));

        personWithId.setLastName("Salvador");
        personWithId.setEmail("novo@email.com");
        ResultActions response = mockMvc
                .perform(
                        put("/person")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(personWithId))
                );

        // Assertions\
        response.andExpect(status().isNotFound()).andDo(print());
    }

    @Test
    @DisplayName("test given personId when delete then return no content")
    void testGivenPersonIdWhenDeleteThenReturnNoContent() throws Exception {
        Long personId = 1L;
        willDoNothing().given(personServices).delete(personId);

        ResultActions response = mockMvc.perform(delete("/person/{id}", personId));

        // Assertions
        response.andExpect(status().isNoContent()).andDo(print());
    }

}