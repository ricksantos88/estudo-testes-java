package br.com.estudo.testes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.estudo.testes.model.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByEmail(String email);

    @Query("select p from Person p where p.firstName = ?1 and p.lastName = ?2")
    Person findByJPQL(String firstName, String lastName);

    @Query("select p from Person p where p.firstName =:firstName and p.lastName =:lastName")
    Person findByJPQLNamedQQuery(@Param("firstName") String firstName, @Param("lastName")String lastName);
}