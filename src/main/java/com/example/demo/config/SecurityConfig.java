package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/login", "/css/**").permitAll() // Benarkan akses ke page tertentu
                        .anyRequest().authenticated() // Page lain SEMUA kena login
                )
                .formLogin((form) -> form
                        .loginPage("/login") // Beritahu URL ke page login
                        .defaultSuccessUrl("/dashboard", true) // Jika login berjaya, pergi ke dashboard
                        .permitAll()
                )
                .logout((logout) -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Buat satu user admin sementara (dalam memori)
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("tuisyen123")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}