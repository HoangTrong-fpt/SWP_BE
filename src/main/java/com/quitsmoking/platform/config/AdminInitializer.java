package com.quitsmoking.platform.config;

import com.quitsmoking.platform.entity.Account;
import com.quitsmoking.platform.entity.Coach;
import com.quitsmoking.platform.enums.Gender;
import com.quitsmoking.platform.enums.Role;
import com.quitsmoking.platform.repository.AuthenticationRepository;
import com.quitsmoking.platform.repository.CoachRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner initAdmin(AuthenticationRepository accountRepository,
                                       CoachRepository coachRepository) {
        return args -> {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String rawPassword = "group5";
            Random random = new Random();

            // Tạo admin nếu chưa có
            if (!accountRepository.existsByUsername("admin")) {
                Account admin = new Account();
                admin.setUsername("admin");
                admin.setEmail("admin@quitsmoking.com");
                admin.setFullName("Admin");
                admin.setRole(Role.ADMIN);
                admin.setActive(true);
                admin.setPassword(encoder.encode("admin"));
                admin.setPhoneNumber("0912345678");
                admin.setGender(Gender.MALE);
                admin.setAvatarUrl("https://ui-avatars.com/api/?name=Admin&background=000000&color=fff");
                accountRepository.save(admin);
            }

            // Danh sách khách hàng
            List<String> customerNames = Arrays.asList(
                    "Nguyen Van Binh", "Le Thi Thanh", "Tran Quang Huy", "Pham Minh Chau", "Hoang Kim Dung",
                    "Vo Thanh Trung", "Ngo Thi Hoa", "Dang Van Toan", "Bui Ngoc Han", "Phan Tuan Anh",
                    "Ly Minh Phuc", "Dang Thi Bich", "Nguyen Tuan Kiet", "Doan Nhu Quynh", "Ho Thi Cam",
                    "Nguyen Hoang Linh", "Pham Bao Khanh", "Tran Hieu Hanh", "Nguyen Minh Hoang", "Le Hoang Long"
            );

            // Danh sách coach
            List<String> coachNames = Arrays.asList(
                    "Nguyen Trung Kien", "Tran Thi Mai", "Hoang Quoc Cuong", "Nguyen Thi Ngoc", "Pham Van Hieu",
                    "Le Minh Duc", "Tran Thi Bao", "Hoang Thi Lan", "Dang Huy Nam", "Pham Ngoc Anh"
            );

            // Insert khách hàng
            for (int i = 0; i < customerNames.size(); i++) {
                String fullName = customerNames.get(i);
                String username = toUsername(fullName);
                if (!accountRepository.existsByUsername(username)) {
                    Account customer = new Account();
                    customer.setUsername(username);
                    customer.setEmail(username + "@quitsmoking.com");
                    customer.setFullName(fullName);
                    customer.setRole(Role.CUSTOMER);
                    customer.setActive(true);
                    customer.setPassword(encoder.encode(rawPassword));
                    customer.setGender(random.nextBoolean() ? Gender.MALE : Gender.FEMALE);
                    customer.setPhoneNumber("09" + String.format("%08d", i + 1));
                    customer.setAvatarUrl("https://ui-avatars.com/api/?name=" + username + "&background=2196F3&color=fff&size=128");
                    accountRepository.save(customer);
                }
            }

            // Insert coach
            for (int i = 0; i < coachNames.size(); i++) {
                String fullName = coachNames.get(i);
                String username = toUsername(fullName);
                if (!accountRepository.existsByUsername(username)) {
                    Account coachAccount = new Account();
                    coachAccount.setUsername(username);
                    coachAccount.setEmail(username + "@quitsmoking.com");
                    coachAccount.setFullName(fullName);
                    coachAccount.setRole(Role.COACH);
                    coachAccount.setActive(true);
                    coachAccount.setPassword(encoder.encode(rawPassword));
                    coachAccount.setGender(random.nextBoolean() ? Gender.MALE : Gender.FEMALE);
                    coachAccount.setPhoneNumber("09" + String.format("%08d", i + 100));
                    coachAccount.setAvatarUrl("https://ui-avatars.com/api/?name=" + username + "&background=4CAF50&color=fff&size=128");

                    Account saved = accountRepository.save(coachAccount);

                    Coach coach = new Coach();
                    coach.setAccount(saved);
                    coach.setFullName(fullName);
                    coach.setEmail(saved.getEmail());
                    coach.setPhone(coachAccount.getPhoneNumber());
                    coach.setAvatarUrl(coachAccount.getAvatarUrl());
                    coach.setDescription("Chuyên gia huấn luyện cai thuốc: " + fullName);
                    coachRepository.save(coach);
                }
            }
        };
    }

    private static String toUsername(String fullName) {
        String[] parts = fullName.trim().split(" ");
        String name = parts[parts.length - 1]; // chỉ lấy tên
        String normalized = Normalizer.normalize(name.toLowerCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return normalized;
    }
}
