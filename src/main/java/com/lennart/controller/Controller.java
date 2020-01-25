package com.lennart.controller;

import com.lennart.model.Analysis;
import com.lennart.model.BnEr;
import com.lennart.model.ImageProcessor;
import com.lennart.model.funda.HousePersister;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
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
        boolean bottom = false;

        if(date.contains("Reverse")) {
            bottom = true;
            date = date.replace("Reverse", "");
        }

        List<BnEr> bnErList = new Analysis().getBnList("absoluteFollowers", date, 1, bottom);
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

    @RequestMapping(value = "/simpleHistogramM2", method = RequestMethod.GET)
    public @ResponseBody List<Integer> makeSimpleHistogramM2() throws Exception {
        return new HousePersister().getAllM2Prices();
    }

    @RequestMapping(value = "/simpleHistogramPrice", method = RequestMethod.GET)
    public @ResponseBody List<Integer> makeSimpleHistogramPrice() throws Exception {
        return new HousePersister().getAllPrices();
    }

    @RequestMapping(value = "/doubleHistogram", method = RequestMethod.GET)
    public @ResponseBody List<List<Integer>> makekDoubleHistogram() throws Exception {
        List<List<Integer>> listOfLists = new ArrayList<>();

        listOfLists.add(new HousePersister().getAllPricesForCity("Zwolle"));
        listOfLists.add(new HousePersister().getAllPricesForCity("Amsterdam"));

        return listOfLists;
    }

    @RequestMapping(value = "/doubleHistogramM2", method = RequestMethod.GET)
    public @ResponseBody List<List<Integer>> makekDoubleHistogramM2() throws Exception {
        List<List<Integer>> listOfLists = new ArrayList<>();

        listOfLists.add(new HousePersister().getAllM2PricesForCity("Zwolle"));
        listOfLists.add(new HousePersister().getAllM2PricesForCity("Amsterdam"));

        return listOfLists;
    }

    public static void main(String[] args) {
        SpringApplication.run(Controller.class, args);
    }
}
