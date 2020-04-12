package com.okta.springbootgradle;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("/")
    public String home(java.security.Principal user) {
        return "Hello " + user.getName() + "!";
    }

    @RequestMapping("/allow-anonymous")
    public String anonymous() {
        return "Hello whoever!";
    }
    
}