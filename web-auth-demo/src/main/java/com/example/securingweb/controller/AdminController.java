package com.example.securingweb.controller;

import com.example.securingweb.model.Role;
import com.example.securingweb.model.User;
import com.example.securingweb.repository.RoleRepository;
import com.example.securingweb.repository.UserRepository;
import com.example.securingweb.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public AdminController(UserService userService, RoleRepository roleRepository, UserRepository userRepository) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/createUser")
    public ModelAndView createUserForm() {
        ModelAndView mav = new ModelAndView("createUser");
        mav.addObject("user", new User());
        mav.addObject("roles", roleRepository.findAll());
        return mav;
    }

    @GetMapping("/userList")
    public ModelAndView listUsers() {
        List<User> users = userRepository.findAll();
        ModelAndView mav = new ModelAndView("userList");
        mav.addObject("users", users);
        return mav;
    }

    @PostMapping("/createUser")
    public ModelAndView createUser(@ModelAttribute User user) {
        Role assignedRole = roleRepository.findById(user.getRoleId()).orElseThrow();

        user.setRoles(Collections.singleton(assignedRole));
        userService.saveUser(user);
        return new ModelAndView("redirect:/admin/createUser");
    }
}
