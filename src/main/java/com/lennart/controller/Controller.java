package com.lennart.controller;

import com.lennart.model.Analysis;
import com.lennart.model.BnEr;
import com.lennart.model.ImageProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;


@Configuration
@EnableAutoConfiguration
@RestController
public class Controller extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Controller.class);
    }

    @RequestMapping(value = "/getFollowersForDate", method = RequestMethod.POST)
    public @ResponseBody List<BnEr> getBnDataForDate(@RequestBody String date) throws Exception {
        List<BnEr> bnErList = new Analysis().getBnList("absoluteFollowers", date, 1);
        return bnErList;
    }

    @RequestMapping(value = "/makeScreenshot", method = RequestMethod.GET)
    public void makeScreenshotAnsSaveToDisc() throws Exception {
        String dateString = new Date().toString();
        dateString = dateString.replaceAll(" ", "_");

        System.setProperty("java.awt.headless", "false");

        BufferedImage bufferedImage = ImageProcessor.getBufferedImageScreenShot(437, 106, 805, 720);
        ImageProcessor.saveBufferedImage(bufferedImage, "/Users/lennartpopma/Documents/instaproject/screenshots_per_day/" + dateString + ".png");
    }

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}
