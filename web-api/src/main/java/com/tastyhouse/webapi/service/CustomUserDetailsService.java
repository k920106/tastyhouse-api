package com.tastyhouse.webapi.service;

import com.tastyhouse.core.entity.user.Member;
import com.tastyhouse.core.repository.member.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberJpaRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // For simplicity, assigning a default role "USER".
        // In a real application, you might derive roles from the Member entity.
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        return new CustomUserDetails(member, Collections.singleton(authority));
    }
}
