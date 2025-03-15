package com.tribytegenius.CareerCompass.config;

import com.tribytegenius.CareerCompass.model.AppRole;
import com.tribytegenius.CareerCompass.model.Role;
import com.tribytegenius.CareerCompass.model.User;
import com.tribytegenius.CareerCompass.repository.RoleRepository;
import com.tribytegenius.CareerCompass.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        Role userRole = new Role(AppRole.ROLE_USER);
        Role adminRole = new Role(AppRole.ROLE_ADMIN);

        if (roleRepository.count() == 0) {
            roleRepository.save(userRole);
            roleRepository.save(adminRole);
            System.out.println("Roles created");
        }

        // Create default users
        if (!userRepository.existsByUserName("user")) {
            User user = new User("user", "user@example.com", passwordEncoder.encode("password"));
            userRepository.save(user);
        }

        if (!userRepository.existsByUserName("admin")) {
            User admin = new User("admin", "admin@example.com", passwordEncoder.encode("password"));
            userRepository.save(admin);
        }

        // Update roles for existing users
        userRepository.findByUserName("user").ifPresent(user -> {
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
        });

        userRepository.findByUserName("admin").ifPresent(admin -> {
            admin.setRoles(Set.of(userRole, adminRole));
            userRepository.save(admin);
        });
    }
}
