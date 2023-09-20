package com.antony.springsecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain myFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(formLogin -> formLogin
                .loginPage("/login.html")
                //.usernameParameter("name")
                //.passwordParameter("word")
                .loginProcessingUrl("/user/login") //Same with form action
                .defaultSuccessUrl("/main.html") // Only works when visit http://localhost:8080/login.html
                .failureUrl("/error.html")
        ).logout(logout -> logout
                .logoutUrl("/mylogout")
                .logoutSuccessUrl("/logout.html")
        ).sessionManagement(s -> s
                .maximumSessions(2)
                .maxSessionsPreventsLogin(true) // Default is false, expire the first session
                .expiredUrl("/sessionTimeout.html") // due to too many sessions for the current user
        ).authorizeHttpRequests(au -> au.requestMatchers(
                    "/login.html", "/user/login", "/logout.html",
                    "/error.html", "sessionTimeout.html")
                .permitAll()
                .anyRequest().authenticated()
        ).csrf(c -> c.disable());
        return http.build();
    }
}
