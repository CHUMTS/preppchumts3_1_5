package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface RoleService {

    Role findByAuthority(String authority);

    Optional<Role> findById(Long id);

    Set<Role> getAllRoles();

    Set<Role> mapCollectionToRoles(Collection<Long> roles);
}
