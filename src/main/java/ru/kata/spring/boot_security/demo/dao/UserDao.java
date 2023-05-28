package ru.kata.spring.boot_security.demo.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public interface UserDao extends CrudRepository<User, Long> {

    List<User> findAll();

    User findUserByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.email = :email")
    User findUserByEmail(@Param("email") String email);

}
