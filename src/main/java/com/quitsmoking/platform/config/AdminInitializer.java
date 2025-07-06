package com.quitsmoking.platform.config;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.CoachRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(AuthenticationRepository accountRepository,
                                       CoachRepository coachRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String rawPassword = "123456";

            // 1. Tạo admin nếu chưa có
            boolean hasAdmin = accountRepository.findAll().stream()
                    .anyMatch(acc -> acc.getRole() != null && acc.getRole().name().equals("ADMIN"));
            if (!hasAdmin) {
                Account admin = new Account();
                admin.setUsername("admin");
                admin.setEmail("admin@quitsmoking.com");
                admin.setFullName("Admin");
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                admin.setPassword(encoder.encode(rawPassword));
                accountRepository.save(admin);
            }

            // 2. Tạo 20 CUSTOMER
            for (int i = 1; i <= 20; i++) {
                String username = "customer" + i;
                if (!accountRepository.existsByUsername(username)) {
                    Account customer = new Account();
                    customer.setUsername(username);
                    customer.setEmail("customer" + i + "@quitsmoking.com");
                    customer.setFullName("Customer " + i);
                    customer.setRole(Role.CUSTOMER);
                    customer.setActive(true);
                    customer.setPassword(encoder.encode(rawPassword));
                    accountRepository.save(customer);
                }
            }

            // 3. Tạo 10 COACH và chèn vào bảng Coach
            for (int i = 1; i <= 10; i++) {
                String username = "coach" + i;
                if (!accountRepository.existsByUsername(username)) {
                    Account coachAccount = new Account();
                    coachAccount.setUsername(username);
                    coachAccount.setEmail("coach" + i + "@quitsmoking.com");
                    coachAccount.setFullName("Coach " + i);
                    coachAccount.setRole(Role.COACH);
                    coachAccount.setActive(true);
                    coachAccount.setPassword(encoder.encode(rawPassword));
                    Account savedCoachAccount = accountRepository.save(coachAccount);

                    // Thêm bản ghi Coach
                    Coach coach = new Coach();
                    coach.setAccount(savedCoachAccount);
                    coach.setFullName(savedCoachAccount.getFullName());
                    coach.setEmail(savedCoachAccount.getEmail());
                    coach.setPhone("09000000" + i); // hoặc null nếu không cần
                    coach.setAvatarUrl("https://ui-avatars.com/api/?name=Coach" + i + "&background=00796B&color=fff&size=128");
                    coach.setDescription("Coach mặc định số " + i);
                    coachRepository.save(coach);
                }
            }
        };
    }
}
