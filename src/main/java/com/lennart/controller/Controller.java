package com.lennart.controller;

import com.lennart.model.Aandacht;
import com.lennart.model.Analysis;
import com.lennart.model.BnEr;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Configuration
@EnableAutoConfiguration
@RestController
public class Controller extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Controller.class);
    }

    @RequestMapping(value = "/getFollowerGrowth", method = RequestMethod.GET)
    public @ResponseBody List<BnEr> getBnData() throws Exception {
        List<BnEr> bnErList = new Analysis().getBnList();
        return bnErList;
    }

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}
