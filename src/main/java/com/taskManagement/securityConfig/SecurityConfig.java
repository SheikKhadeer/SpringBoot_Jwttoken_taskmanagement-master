package com.taskManagement.securityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.taskManagement.security.CustomUserDetailsService;
import com.taskManagement.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity

public class SecurityConfig {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpsecurity) throws Exception {

//		httpsecurity.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
//				.requestMatchers(HttpMethod.POST, "/api/users/register", "/api/users/login").permitAll()
//				.requestMatchers(HttpMethod.GET, "/api/users/", "/api/users/{id}").permitAll()
//				.requestMatchers(HttpMethod.DELETE, "/api/users/delete/{id}").permitAll().anyRequest().authenticated());
//
//		httpsecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		httpsecurity.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				// public endpoints (no authentication required
				.requestMatchers(HttpMethod.POST, "/api/users").permitAll()// create user
				.requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
				// user endpoints(requires authentication)
				.requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
				.requestMatchers(HttpMethod.GET, "/api/users/{id}").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/api/users/name/{name}").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/api/users/active").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/api/users/deactivate/{id}").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/api/users/delete/{id}").hasRole("ADMIN")
				// task endpoints(require authentication)
				.requestMatchers(HttpMethod.POST, "/api/tasks").hasAnyRole("ADMIN", "USER")// create task
				.requestMatchers(HttpMethod.GET, "/api/tasks").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/api/tasks/user/{userId}").hasRole("USER")
				.requestMatchers(HttpMethod.GET, "/api/tasks/{userId}/tasks/{taskid}").hasRole("USER")
				.requestMatchers(HttpMethod.GET, "/api/tasks/dueDate/{dueDate}").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/api/tasks/priority/{priority}").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.GET, "/api/tasks/status/{status}").hasAnyRole("ADMIN", "USER")
				.requestMatchers(HttpMethod.DELETE, "/api/tasks/{userid}/tasks/{taskid}").hasAnyRole("ADMIN", "USER")
				.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return httpsecurity.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();

	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

}
