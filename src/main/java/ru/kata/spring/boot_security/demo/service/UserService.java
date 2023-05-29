package ru.kata.spring.boot_security.demo.service;

import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService extends UserDetailsService {

    Optional<User> findUserById(Long id);

    void saveUser(User user);

    User mapDTOToUser(UserDTO dto);

    void removeUserById(long id);

    List<User> getAllUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);
}
