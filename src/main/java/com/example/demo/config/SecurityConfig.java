package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
            .logout((logout) -> logout
                    .logoutUrl("/logout") // URL untuk trigger logout
                    .logoutSuccessUrl("/login?logout") // Lepas keluar, pergi login
                    .invalidateHttpSession(true) // Padam session dalam server
                    .deleteCookies("JSESSIONID") // Padam kunci dalam browser
                    .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}