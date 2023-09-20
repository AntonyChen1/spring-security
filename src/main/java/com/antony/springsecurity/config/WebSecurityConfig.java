package com.antony.springsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
public class WebSecurityConfig {
    @Autowired
    public DataSource dataSource;

    @Autowired
    private UserDetailsService userServiceImpl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public PersistentTokenRepository persistentTokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();

        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
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
        ).csrf(c -> c
                .disable()
        ).rememberMe(r -> r
                .tokenRepository(persistentTokenRepository())
                .tokenValiditySeconds(3600)
                .userDetailsService(userServiceImpl));
        return http.build();
    }
}
