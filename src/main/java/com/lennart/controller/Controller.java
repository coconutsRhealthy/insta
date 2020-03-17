package com.lennart.controller;

import com.lennart.model.funda.PostCode;
import com.lennart.model.funda.PostCodeInfoRetriever;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;


@Configuration
@EnableAutoConfiguration
@RestController
public class Controller extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Controller.class);
    }

    @RequestMapping(value = "/getPostCodeInfo", method = RequestMethod.POST)
    public @ResponseBody PostCode getPostCodeInfo(@RequestBody String[] dataFromClient) throws Exception {
        String postCodeString = dataFromClient[0];
        String searchPeriod = dataFromClient[1];

        PostCode postCode = new PostCodeInfoRetriever().getPostCodeData(postCodeString);
        return postCode;
    }

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}
