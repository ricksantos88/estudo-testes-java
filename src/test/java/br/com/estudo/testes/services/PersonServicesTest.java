package br.com.estudo.testes.services;

import br.com.estudo.testes.exceptions.EmailSavedException;
import br.com.estudo.testes.exceptions.ResourceNotFoundException;
import br.com.estudo.testes.model.Person;
import br.com.estudo.testes.repositories.PersonRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import utils.PersonUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServicesTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonServices personServices;

    private Person personWithoutId;
    private Person personWithId;

    @BeforeEach
    void setUp() {
        personWithoutId = PersonUtils.returnPersonWithoutId();
        personWithId = PersonUtils.returnPersonWithIdEqualOne();

    }

    @Test
    @DisplayName("test given person object when save then return person object")
    void testGivenPersonObjectWhenSaveThenReturnSavedPerson() {
        given(personRepository.findByEmail(anyString())).willReturn(Optional.empty());
        given(personRepository.save(personWithoutId)).willReturn(personWithId);

        Person savedPerson = personServices.create(personWithoutId);

        assertThat(savedPerson, notNullValue());
        assertThat(savedPerson.getId(), greaterThan(0L));
    }

    @Test
    @DisplayName("test given person object with email in user when save then throw exception")
    void testGivenPersonObjectWithEmailInUseWhenSaveThenThrowException() {
        given(personRepository.findByEmail(anyString())).willReturn(Optional.of(personWithId));

        EmailSavedException personEmailSavedException = assertThrows(EmailSavedException.class, () -> {
            personServices.create(personWithoutId);
        });

        String expectedMessage = "Email " + personWithoutId.getEmail() + " used by other person.";
        assertThat(
            personEmailSavedException.getMessage(), is(equalTo(expectedMessage))
        );
    }

    @Test
    @DisplayName("test given existing email when save person then throws exception")
    void testGivenExistingEmailWhenSavePersonThenThrowsException() {
        given(personRepository.findByEmail(anyString())).willReturn(Optional.of(personWithId));

        assertThrows(EmailSavedException.class, () -> {
            personServices.create(personWithoutId);
        });

        verify(personRepository, never()).save(ArgumentMatchers.any(Person.class));
    }

    @Test
    @DisplayName("test given persons list when find all then persons list")
    void testGivenPersonsListWhenFindAllThenPersonsList() {
        given(personRepository.findAll()).willReturn(List.copyOf(PersonUtils.returnPersonListSizeThree()));

        List<Person> personList = personServices.findAll();

        assertThat(personList, notNullValue());
        assertThat(personList.size(), equalTo(3));
    }

    @Test
    @DisplayName("test given findAll method when called then return empty persons list")
    void testGivenFindAllMethodWhenCalledThenReturnEmptyPersonsList() {
        given(personRepository.findAll()).willReturn(Collections.emptyList());

        List<Person> personList = personServices.findAll();

        assertThat(personList.isEmpty(), is(equalTo(true)));
    }

    @Test
    @DisplayName("test given person id when find by id then return persons object")
    void testGivenPersonIdWhenFindByIdThenReturnPersonsObject() {
        given(personRepository.findById(anyLong())).willReturn(Optional.of(personWithId));

        Person savedPerson = personServices.findById(anyLong());

        assertThat(savedPerson, notNullValue());
        assertThat(savedPerson.getId(), is(equalTo(personWithId.getId())));
    }

    @Test
    @DisplayName("test given person id when find by id then throws")
    void testGivenPersonIdWhenFindByIdThenThrows() {
        given(personRepository.findById(anyLong())).willReturn(Optional.empty());

        ResourceNotFoundException personFindByIdException = assertThrows(ResourceNotFoundException.class, () -> {
            personServices.findById(anyLong());
        });

        assertThat(personFindByIdException.getMessage(), is(equalTo("No records found for this ID!")));
    }

    @Test
    @DisplayName("test given person object when update then return updated persons object")
    void testGivenPersonObjectWhenUpdateThenReturnUpdatedPersonsObject() {
        given(personRepository.findById(personWithId.getId())).willReturn(Optional.of(personWithId));

        String expectedEmail = "new@email.com";
        String expectedLastName = "Silva";

        personWithId.setEmail(expectedEmail);
        personWithId.setLastName(expectedLastName);

        given(personRepository.save(personWithId)).willReturn(personWithId);

        Person updatedPerson = personServices.update(personWithId);

        assertThat(updatedPerson, notNullValue());
        assertThat(updatedPerson.getEmail(), is(equalTo(expectedEmail)));
        assertThat(updatedPerson.getLastName(), is(equalTo(expectedLastName)));
    }

    @Test
    @DisplayName("test given person id when delete person then do nothing")
    void testGivenPersonIdWhenDeletePersonThenDoNothing() {
        given(personRepository.findById(personWithId.getId())).willReturn(Optional.of(personWithId));

        willDoNothing().given(personRepository).delete(personWithId);

        personServices.delete(personWithId.getId());

        verify(personRepository, times(1)).delete(personWithId);
    }

}