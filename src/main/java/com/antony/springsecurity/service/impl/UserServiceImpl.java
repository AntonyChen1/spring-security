package com.antony.springsecurity.service.impl;

import com.antony.springsecurity.bean.Permission;
import com.antony.springsecurity.bean.User;
import com.antony.springsecurity.repository.PermissionRepository;
import com.antony.springsecurity.repository.UserRepository;
import com.antony.springsecurity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public User getByUsername(String username) {
        return userRepository.findUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = getByUsername(username);
        List<GrantedAuthority> authorities = new ArrayList<>();
        System.out.println("loadUserByUsername of username: " + username);
        if(user!=null){
            System.out.println(MessageFormat.format
                    ("Password: {0}", user.getPassword()));

            List<Permission> permissions = permissionRepository.selectByUserId(user.getId());

            permissions.forEach(permission -> {
                if (permission!=null && !StringUtils.isEmpty(permission.getEnname())){
                    GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(permission.getEnname());
                    authorities.add(grantedAuthority);
                }
            });

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),user.getPassword(),authorities);
        }else {
            throw new UsernameNotFoundException("No user found");
        }

    }
}