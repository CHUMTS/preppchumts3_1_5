package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class MainController {

    private final RoleService roleService;
    private final UserService userService;
    @Autowired
    public MainController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping(value = "/main")
    public String usersListPage(Model model){
        model.addAttribute("usersList", userService.getAllUsers());
        model.addAttribute("loggedUser",
                userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName()));
        model.addAttribute("allRoles", roleService.getAllRoles());
        model.addAttribute("newUser", new User());
        return "allUsers";
    }

    @PostMapping(value = "/main/{id}/edit", consumes = "application/x-www-form-urlencoded")
    public String receiveUserEditForm(@RequestBody User user, @RequestParam("roles") List<Long> roleIds){
        user.setRoles(roleService.mapCollectionToRoles(roleIds));
        userService.saveUser(user);
        return "redirect:/main";
    }

    @DeleteMapping(value = "/main/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id){
        userService.removeUserById(id);
        return "redirect:/main";
    }

    @PostMapping(value = "/main")
    public String receiveNewUserForm(User user, @RequestParam("roles") List<Long> roleIds){
        user.setRoles(roleService.mapCollectionToRoles(roleIds));
        userService.saveUser(user);
        return "redirect:/main";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

}
