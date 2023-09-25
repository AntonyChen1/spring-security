package com.antony.springsecurity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
    @RequestMapping("/showLogin")
    public String showLogin(Model model){
        model.addAttribute("msg", "hello thymeleaf");
        return "/login";
    }
}
