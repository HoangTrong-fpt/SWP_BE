package com.quitsmoking.platform.config;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Configuration
public class AdminInitializer {
    @Bean
    public CommandLineRunner initAdmin(AuthenticationRepository accountRepository) {
        return args -> {
            boolean hasAdmin = accountRepository.findAll().stream()
                    .anyMatch(acc -> acc.getRole() != null && acc.getRole().name().equals("ADMIN"));
            if (!hasAdmin) {
                Account admin = new Account();
                admin.setUsername("admin");
                admin.setEmail("admin@quitsmoking.com");
                admin.setFullName("Admin");
                admin.setRole(Role.ADMIN);
                admin.setActive(true);

                // Mã hóa password
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String rawPassword = "123456";
                admin.setPassword(encoder.encode(rawPassword));

                accountRepository.save(admin);

            }
        };
    }
}
