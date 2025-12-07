package com.freemarket.platform.service;

import com.freemarket.platform.entity.MarketActor;
import com.freemarket.platform.repository.MarketActorRepository;
import io.micrometer.common.lang.NonNullApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@NonNullApi
public class JpaUserDetailsService implements UserDetailsService {

    private final MarketActorRepository marketActorRepository;

    @Autowired
    public JpaUserDetailsService(MarketActorRepository marketActorRepository) {
        this.marketActorRepository = marketActorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MarketActor user = marketActorRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String[] roles = user.getRoles().toArray(String[]::new);

        return User.withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(roles)
                .build();
    }
}
