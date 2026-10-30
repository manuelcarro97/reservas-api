package io.github.manuelcarro97.reservas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
			// Sin cookies de sesión no hay superficie CSRF.
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(requests -> requests
				.requestMatchers("/api/v1/restaurants/**", "/api/v1/reservations/**", "/swagger-ui/**",
						"/swagger-ui.html", "/v3/api-docs/**", "/actuator/health", "/error")
				.permitAll()
				.anyRequest()
				.authenticated())
			.httpBasic(Customizer.withDefaults())
			.build();
	}

}
