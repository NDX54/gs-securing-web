package com.example.securingweb.component;

import com.example.securingweb.model.Privilege;
import com.example.securingweb.model.Role;
import com.example.securingweb.model.User;
import com.example.securingweb.repository.PrivilegeRepository;
import com.example.securingweb.repository.RoleRepository;
import com.example.securingweb.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    boolean alreadySetup = false;

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PrivilegeRepository privilegeRepository;

    private PasswordEncoder passwordEncoder;

    public SetupDataLoader() {}

    @Autowired
    public SetupDataLoader(UserRepository userRepository, RoleRepository roleRepository, PrivilegeRepository privilegeRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.privilegeRepository = privilegeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (alreadySetup) return;

        /*
        * Two quick notes here. We’ll first look at terminology. We’re using the Privilege – Role terms here.
        * But in Spring, these are slightly different. In Spring, our Privilege is referred to as Role and also as a (granted) authority, which is slightly confusing.
        *
        * This is not a problem for the implementation of course, but it’s definitely worth noting.
        *
        * Second, these Spring Roles (our Privileges) need a prefix. By default, that prefix is “ROLE”, but it can be changed.
        * We’re not using that prefix here, just to keep things simple, but keep in mind that it will be required if we’re not explicitly changing it.
        *
        * Source: https://www.baeldung.com/role-and-privilege-for-spring-security-registration
        * */
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        Privilege deletePrivilege = createPrivilegeIfNotFound("DELETE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege, deletePrivilege);
        List<Privilege> staffPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        List<Privilege> userPrivileges = Arrays.asList(
                readPrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_STAFF", staffPrivileges);
        createRoleIfNotFound("ROLE_USER", userPrivileges);

        createAdminUser();
        createTestUser();
        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        return privilegeRepository.findByName(name).orElseGet(() -> {
            Privilege newPrivilege = new Privilege(name);
            privilegeRepository.save(newPrivilege);
            return newPrivilege;
        });
    }

    @Transactional
    void createRoleIfNotFound(String name, Collection<Privilege> privileges) {

        roleRepository.findByName(name).orElseGet(() -> {
            Role newRole = new Role(name);
            newRole.setPrivileges(privileges);
            roleRepository.save(newRole);
            return newRole;
        });

    }

    private void createAdminUser() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();

        User adminUser = new User();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmail(adminEmail);
        adminUser.setUsername(adminUsername);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setEnabled(true);
        adminUser.setRoles(Arrays.asList(adminRole));

        userRepository.save(adminUser);
    }

    private void createTestUser() {
        if (userRepository.findByEmail("test@test.com").isEmpty()) {
            Role role = roleRepository.findByName("ROLE_STAFF").orElseThrow();
            User user = new User();
            user.setFirstName("Test");
            user.setLastName("Test");
            user.setPassword(passwordEncoder.encode("test"));
            user.setEmail("test@test.com");
            user.setRoles(Arrays.asList(role));
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

}
