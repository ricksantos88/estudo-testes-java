package br.com.estudo.testes.repositories;

import br.com.estudo.testes.integrationTests.testconteiners.AbstractIntegrationTest;
import br.com.estudo.testes.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import utils.PersonUtils;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PersonRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private PersonRepository personRepository;

    private Person person;

    @BeforeEach
    void setUp() {
        person = PersonUtils.returnPersonWithoutId();
    }

    @Test
    @DisplayName("test given person object when save then return person object")
    void testGivenPersonObjectWhenSaveThenReturnSavedPerson() {

        var savedPerson = personRepository.save(person);

        assertNotNull(savedPerson);
        assertTrue(savedPerson.getId() > 0);
    }

    @Test
    @DisplayName("test given person list when find all then return person list")
    void testGivenPersonListWhenFindAllThenReturnPersonList() {
        var expectedSize = 3;
        PersonUtils.returnPersonListSizeThree().forEach(person -> personRepository.save(person));

        List<Person> personList = personRepository.findAll();

        assertNotNull(personList);
        assertTrue(personList.size() > 0);
    }

    @Test
    @DisplayName("test given person object when find by id then return person object")
    void testGivenPersonObjectWhenFindByIdThenReturnPersonObject() {

        personRepository.save(person);
        var expectedId = person.getId();
        Person savedPerson = personRepository.findById(expectedId).get();

        assertNotNull(savedPerson);
        assertTrue(savedPerson.getId() > 0);
        assertEquals(expectedId, savedPerson.getId());
    }

    @Test
    @DisplayName("test given person object when find by email then return person object")
    void testGivenPersonObjectWhenFindByEmailThenReturnPersonObject() {
        var emailForThisTest = "emailForThisTest@email.com";
        person.setEmail(emailForThisTest);
        personRepository.save(person);

        Person personByEmail = personRepository.findByEmail(emailForThisTest).get();

        assertNotNull(personByEmail);
        assertEquals(emailForThisTest, personByEmail.getEmail());
    }

    @Test
    @DisplayName("test given person object when find by email when update person then return person object")
    void testGivenPersonObjectWhenFindByEmailWhenUpdatePersonThenReturnPersonObject() {
        personRepository.save(person);

        var savedPersonByEmail = personRepository.findByEmail(person.getEmail()).get();
        var emailToUpdate = "emailForThisTest@email.com";
        var personToUpdate = personRepository.findByEmail(savedPersonByEmail.getEmail()).get();
        personToUpdate.setEmail(emailToUpdate);

        var updatedPerson = personRepository.save(personToUpdate);

        assertNotNull(updatedPerson);
        assertEquals(emailToUpdate, updatedPerson.getEmail());

    }

    @Test
    @DisplayName("test given person object when save then remove person")
    void testGivenPersonObjectWhenSaveThenRemovePerson() {

        var savedPerson = personRepository.save(person);
        personRepository.delete(savedPerson);

        Optional<Person> personOptional = personRepository.findById(savedPerson.getId());

        assertTrue(personOptional.isEmpty());
    }

    @Test
    @DisplayName("test given person object when find first name and last name then return person object")
    void testGivenPersonObjectWhenFindFirstNameAndLastNameThenReturnPersonObject() {

        personRepository.save(person);
        Person savedPerson = personRepository.findByJPQL(person.getFirstName(), person.getLastName());

        assertNotNull(savedPerson);
        assertThat(savedPerson.getFirstName(), is(person.getFirstName()));
    }

    @Test
    @DisplayName("test given person object when find first name and last name with named query then return person object")
    void testGivenPersonObjectWhenFindFirstNameAndLastNameWithNamedQQueryThenReturnPersonObject() {

        personRepository.save(person);
        Person savedPerson = personRepository.findByJPQLNamedQQuery(person.getFirstName(), person.getLastName());

        assertNotNull(savedPerson);
        assertThat(savedPerson.getFirstName(), is(person.getFirstName()));
    }

}