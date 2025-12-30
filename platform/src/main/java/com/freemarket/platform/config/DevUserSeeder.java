package com.freemarket.platform.config;

import com.freemarket.platform.entity.MarketActor;
import com.freemarket.platform.repository.MarketActorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DevUserSeeder {

    @Bean
    CommandLineRunner initUsers(MarketActorRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                MarketActor admin = new MarketActor();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPasswordHash(encoder.encode("admin"));
                admin.setContactInfo("Dev admin");
                admin.setIsVerified(true);
                admin.getRoles().add("ADMIN");
                admin.getRoles().add("USER");
                repo.save(admin);
            }

            if (repo.findByUsername("user").isEmpty()) {
                MarketActor user = new MarketActor();
                user.setUsername("user");
                user.setEmail("user@example.com");
                user.setPasswordHash(encoder.encode("user"));
                user.setContactInfo("Dev user");
                user.setIsVerified(true);
                user.getRoles().add("USER");
                repo.save(user);
            }
        };
    }
}
