package com.antony.springsecurity.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //$2a$10$1VyZNXNUoMrGsBNNjCwtWOqQzqcg3mqTyvHIyTcLoJF9.LNPXeeYi
        //$2a$10$5PSqRInRoDK1zeSe4/MliOb2k2d1e5fkJhgUKYB8dSffYbEgLlRri
        // String hashpw = BCrypt.hashpw("liferay", BCrypt.gensalt());
        //$2a$10$iKg./Q4JK6jpvReLnwuDRO2CT.lzl.RZfhoAKUyLrMaCCc9j699bK
        //$2a$10$cGn4X.xPC/0KFh1aP.lWM.sa4kztOA19BehaidZZ2VqrFFx8rRvZK
        String hashpw = passwordEncoder.encode("liferay");
        return new User("antony", hashpw,
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin, user"));
    }
}
