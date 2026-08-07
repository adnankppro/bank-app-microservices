package com.adnan.bankapp.authentication.security;

import com.adnan.bankapp.authentication.entity.Credentials;
import com.adnan.bankapp.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credentials credentials = userRepository.getByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        return new MyUserDetails(credentials);
    }
}
