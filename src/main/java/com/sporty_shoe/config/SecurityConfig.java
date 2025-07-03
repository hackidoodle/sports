package com.sporty_shoe.config;

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
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/products", "/products/**", "/login", "/register", "/user/register",
                                "/user/login", "/css/**", "/js/**", "/images/**")
                        .permitAll()
                        .requestMatchers("/admin/**").permitAll() // We'll handle admin authentication manually
                        .requestMatchers("/cart/**", "/purchase/**", "/user/**").permitAll() // Allow cart and purchase
                                                                                             // endpoints
                        .anyRequest().permitAll() // Allow all requests for now to avoid authentication issues
                )
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}
