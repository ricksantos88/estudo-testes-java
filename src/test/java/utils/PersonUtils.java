package utils;

import br.com.estudo.testes.model.Person;

import java.util.Arrays;
import java.util.List;

public class PersonUtils {

    public static Person returnPersonWithoutId() {
        return new Person(
        "Wendel",
        "Santos",
        "teste@email.com",
        "Rua dos Bobos Nº 0",
        "MALE"
        );
    }

    public static Person returnPersonWithIdEqualOne() {
        Person person = returnPersonWithoutId();
        person.setId(1L);
        return person;
    }

    public static List<Person> returnPersonListSizeThree() {
        Person personTwo = new Person(
        "Guilherme",
        "dos Santos",
        "guilherme@email.com",
        "Rua dos Bobos Nº 0",
        "MALE"
        );

        Person personTree = new Person(
                "Larisse",
                "Castro",
                "larisse@email.com",
                "Rua dos Bobos Nº 0",
                "FEMALE"
        );
        return Arrays.asList(returnPersonWithoutId(), personTwo, personTree);
    }

    public static List<Person> returnPersonListSizeTwoToFindAllIntegrationTest() {
        Person personOne = new Person(
                "Guilherme",
                "dos Santos",
                "guilherme@email.com",
                "Rua dos Bobos Nº 0",
                "MALE"
        );

        Person personTwo = new Person(
                "Larisse",
                "Castro",
                "larisse@email.com",
                "Rua dos Bobos Nº 0",
                "FEMALE"
        );
        return Arrays.asList(personOne, personTwo);
    }

}
