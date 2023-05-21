package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

@RequestMapping("/admin")
@Controller
public class AdminController {

    private final RoleService roleService;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public AdminController(UserService userService, BCryptPasswordEncoder encoder, RoleService roleService) {
        this.bCryptPasswordEncoder = encoder;
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/users")
    public String usersListPage(Model model){
        model.addAttribute("usersList", userService.getAllUsers());
        return "allUsers";
    }

    @GetMapping(value = "/users/new")
    public String newUserInputForm(Model model){
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("user", new User());
        return "new";
    }

    @GetMapping(value = "users/{id}/edit")
    public String userEditForm(@PathVariable("id") Long id, Model model){
        if (userService.findUserById(id).isEmpty()) {
            return "adminMainPage";
        }
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("user", userService.findUserById(id).get());
        return "edit";
    }

    @GetMapping(value = "/users/{id}")
    public String showUser(@PathVariable("id") long id, Model model){
        model.addAttribute("user", userService.findUserById(id).get());
        return "showUser";
    }

    @PatchMapping(value = "users/{id}/edit")
    public String receiveUserEditForm( User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @DeleteMapping(value = "/users/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id){
        userService.removeUserById(id);
        return "redirect:/admin/users";
    }

    @PostMapping(value = "/users")
    public String receiveNewUserForm(User user){
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userService.saveUser(user);
        return "redirect:/admin/users";
    }
}
