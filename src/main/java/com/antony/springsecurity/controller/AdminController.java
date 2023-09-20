package com.antony.springsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @GetMapping("demo")
    public String demo(){
        return "Create a demo by Antony\n" +
                "<br/>\n" +
                "<a href=\"/mylogout\">logout</a>";
    }
}
