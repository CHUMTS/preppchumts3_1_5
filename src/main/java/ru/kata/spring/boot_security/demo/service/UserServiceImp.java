package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.configs.WebSecurityConfig;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {

    private final UserDao userDao;
    private final BCryptPasswordEncoder encoder = WebSecurityConfig.getPasswordEncoder();
    private final RoleService roleService;

    @Autowired
    public UserServiceImp(UserDao userDao, RoleService service) {
        this.roleService = service;
        this.userDao = userDao;
    }

    @Override
    public void saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userDao.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        return userDao.findById(id);
    }


    @Override
    @Transactional
    public void removeUserById(long id) {
        userDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override   // from UserDetailsService
    public User findUserByUsername(String email) {
        return userDao.findUserByUsername(email); // для аутентификации используем емейл
    }

    @Override   // from UserDetailsService
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.findUserByEmail(email);
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())  // для аутентификации используем емейл
                .password(user.getPassword())
                .authorities(user.getAuthorities())
                .disabled(false)
                .build();
    }

    @Override
    public User findUserByEmail(String email){
        return userDao.findUserByEmail(email);
    }

    @Override
    public User mapDTOToUser(UserDTO dto){
        User user = new User();
        user.setId(dto.getId());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getUsername());
        user.setRoles(roleService.mapCollectionToRoles(dto.getRoles()));
        return user;
    }

}
