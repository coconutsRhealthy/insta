package com.lennart.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

@Configuration
@EnableAutoConfiguration
@RestController
public class Controller {

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}
