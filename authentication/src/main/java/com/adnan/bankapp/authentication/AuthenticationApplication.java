package com.adnan.bankapp.authentication;

import com.adnan.bankapp.authentication.entity.Credentials;
import com.adnan.bankapp.authentication.enums.Role;
import com.adnan.bankapp.authentication.repository.UserRepository;
import com.bankapp.common.enums.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableFeignClients
@EnableMethodSecurity
@RequiredArgsConstructor
public class AuthenticationApplication implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(AuthenticationApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if(userRepository.count()==0){
			Credentials credentials = new Credentials("adnankppro@gmail.com");
			credentials.setRole(Role.SUPER_ADMIN);
			credentials.setStatus(UserStatus.ACTIVE);
			credentials.setPassword(passwordEncoder.encode("adnan"));
			userRepository.save(credentials);
		}
	}
}
