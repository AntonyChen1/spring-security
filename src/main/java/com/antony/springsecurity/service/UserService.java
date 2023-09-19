package com.antony.springsecurity.service;


import com.antony.springsecurity.bean.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User getByUsername(String username);
}
