package com.skillswap.skillswap.service.security.impl;

import com.skillswap.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Loads a user by email for Spring Security authentication
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email) // Fetch user from DB using email
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getEmail(), // Username for authentication
                        user.getPassword(), // Hashed password
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())) // User roles/authorities
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email)); // Throw if user not found
    }
}
