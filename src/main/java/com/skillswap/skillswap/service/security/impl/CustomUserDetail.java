package com.skillswap.skillswap.service.security.impl;

import com.skillswap.skillswap.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@AllArgsConstructor
public class CustomUserDetail implements UserDetails {

    private User user;

    // Returns the authorities/roles of the user for Spring Security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    // Returns the user's password for authentication
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Returns the username used for authentication (email in this case)
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Indicates if the user account has not expired (always true here)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Indicates if the user account is not locked (always true here)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Indicates if the user credentials (password) have not expired (always true here)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Indicates if the user account is enabled (active) (always true here)
    @Override
    public boolean isEnabled() {
        return true;
    }
}