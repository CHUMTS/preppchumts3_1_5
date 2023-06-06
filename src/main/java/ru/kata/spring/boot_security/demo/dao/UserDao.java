package ru.kata.spring.boot_security.demo.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

@Repository
public interface UserDao extends CrudRepository<User, Long> {

    List<User> findAll();

    User findUserByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    User findUserByEmail(@Param("email") String email);

}
