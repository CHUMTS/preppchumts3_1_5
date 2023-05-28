package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public Role findByAuthority(String authority){
        return roledao.findByAuthority(authority);
    }

    public Optional<Role> findById(Long id){
        return roledao.findById(id);
    }

    public Set<Role> mapCollectionToRoles(Collection<Long> roles) {
        return roles.stream()
                .map(this::findById)
                .map(Optional::orElseThrow)
                .collect(Collectors.toSet());
    }

}
