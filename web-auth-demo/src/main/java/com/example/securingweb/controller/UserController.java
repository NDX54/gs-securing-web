package com.example.securingweb.controller;

import com.example.securingweb.controller.dto.PrivilegeDTO;
import com.example.securingweb.controller.dto.RoleDTO;
import com.example.securingweb.controller.dto.UserDTO;
import com.example.securingweb.model.Privilege;
import com.example.securingweb.model.Role;
import com.example.securingweb.model.User;
import com.example.securingweb.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/get-all")
    public List<UserDTO> getAllUsers() {
        List<UserDTO> userDTOS = new ArrayList<>();

        for (User user : userRepository.findAll()) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(user.getId());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            userDTO.setEnabled(user.getEnabled());
            userDTO.setTokenExpired(user.getTokenExpired());
            userDTO.setEmail(user.getEmail());
            userDTO.setUsername(user.getUsername());

            for (Role role : user.getRoles()) {
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setId(role.getId());
                roleDTO.setName(role.getName());

                for (Privilege privilege : role.getPrivileges()) {
                    PrivilegeDTO privilegeDTO = new PrivilegeDTO();
                    privilegeDTO.setId(privilege.getId());
                    privilegeDTO.setName(privilege.getName());
                    roleDTO.getPrivileges().add(privilegeDTO);
                }

                userDTO.getRoles().add(roleDTO);
                userDTOS.add(userDTO);
            }
        }

        return userDTOS;
    }

    @GetMapping("/get")
    public UserDTO getUser(@RequestParam("userID") Long userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new RuntimeException("User Not Found"));
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEnabled(user.getEnabled());
        userDTO.setTokenExpired(user.getTokenExpired());
        userDTO.setEmail(user.getEmail());
        userDTO.setUsername(user.getUsername());

        for (Role role : user.getRoles()) {

            RoleDTO roleDTO = new RoleDTO();
            roleDTO.setId(role.getId());
            roleDTO.setName(role.getName());

            for (Privilege privilege : role.getPrivileges()) {
                PrivilegeDTO privilegeDTO = new PrivilegeDTO();
                privilegeDTO.setId(privilege.getId());
                privilegeDTO.setName(privilege.getName());
                roleDTO.getPrivileges().add(privilegeDTO);
            }

            userDTO.getRoles().add(roleDTO);
        }

        return userDTO;
    }
}
