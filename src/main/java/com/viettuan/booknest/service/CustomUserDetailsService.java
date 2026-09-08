package com.viettuan.booknest.service;

import com.viettuan.booknest.entity.User;
import com.viettuan.booknest.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException(
                    "Không tìm thấy người dùng"
            );
        }

        String roleName = user.getRole().getName();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(roleName)
                .disabled(!user.getActive())
                .build();
    }
}