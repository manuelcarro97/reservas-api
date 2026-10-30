package io.github.manuelcarro97.reservas.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration(proxyBeanMethods = false)
class AdminUserConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	UserDetailsService adminUser(@Value("${ADMIN_USERNAME}") String username,
			@Value("${ADMIN_PASSWORD}") String password, PasswordEncoder encoder) {
		if (username.isBlank() || password.isBlank()) {
			throw new IllegalStateException("ADMIN_USERNAME and ADMIN_PASSWORD must not be blank");
		}
		return new InMemoryUserDetailsManager(
				User.withUsername(username).password(encoder.encode(password)).roles("ADMIN").build());
	}

}
