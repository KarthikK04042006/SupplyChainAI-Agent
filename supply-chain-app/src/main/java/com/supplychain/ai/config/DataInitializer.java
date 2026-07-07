package com.supplychain.ai.config;

import com.supplychain.ai.model.Role;
import com.supplychain.ai.model.User;
import com.supplychain.ai.repository.RoleRepository;
import com.supplychain.ai.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Create roles if they don't exist
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));

        // Create default admin user
        if (!userRepository.existsByUsername("admin")) {
            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);

            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@supplychain.ai")
                    .firstName("System")
                    .lastName("Admin")
                    .enabled(true)
                    .roles(adminRoles)
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created (username: admin, password: admin123)");
        }

        // Create a demo user
        if (!userRepository.existsByUsername("demo")) {
            Set<Role> demoRoles = new HashSet<>();
            demoRoles.add(userRole);

            User demo = User.builder()
                    .username("demo")
                    .password(passwordEncoder.encode("demo123"))
                    .email("demo@supplychain.ai")
                    .firstName("Demo")
                    .lastName("User")
                    .enabled(true)
                    .roles(demoRoles)
                    .build();
            userRepository.save(demo);
            log.info("Demo user created (username: demo, password: demo123)");
        }
    }
}
