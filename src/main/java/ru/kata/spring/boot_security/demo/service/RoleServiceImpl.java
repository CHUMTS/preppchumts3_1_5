package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roledao;
    @Autowired
    public RoleServiceImpl(RoleDao roledao) {
        this.roledao = roledao;
    }

    @Override
    public Set<Role> getAllRoles() {
        return roledao.findAllRoles();
    }
}
