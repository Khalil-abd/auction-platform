package com.auction.auth.config;

import com.auction.auth.model.User;
import com.auction.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername("kaito")) {
            log.info("Default user 'kaito' already exists, skipping seed");
            return;
        }

        User defaultUser = User.builder()
                .username("kaito")
                .email("kaito@auction.local")
                .password(passwordEncoder.encode("Aptx-4869"))
                .roles("ROLE_USER")
                .build();

        userRepository.save(defaultUser);
        log.info("Default user 'kaito' seeded successfully (id: {})", defaultUser.getId());
    }
}
