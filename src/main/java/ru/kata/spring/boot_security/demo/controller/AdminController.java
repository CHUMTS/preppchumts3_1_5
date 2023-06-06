package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.model.UserDTO;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;
    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping
    public ModelAndView usersListPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("allUsers");
        return modelAndView;
    }

    @GetMapping("/roles")
    public Set<Role> getAllRoles(){
        return roleService.getAllRoles();
    }

    @GetMapping("/list")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @PutMapping
    public ResponseEntity<Void> receiveUserEditForm(@RequestBody UserDTO dto){
        userService.saveUser(userService.mapDTOToUser(dto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Void> receiveNewUserForm(@RequestBody UserDTO dto){
        userService.saveUser(userService.mapDTOToUser(dto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}/delete")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
        userService.removeUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
