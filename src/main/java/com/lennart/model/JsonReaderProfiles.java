package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by LennartMac on 02/01/2024.
 */
public class JsonReaderProfiles {

    public static void main(String[] args) throws Exception {
        new JsonReaderProfiles().checkRecentPostsOfInfluencersForDiscountCodes();
    }

    private void checkRecentPostsOfInfluencersForDiscountCodes() throws Exception {
        JSONParser jsonParser = new JSONParser();

        JSONArray apifyData = (JSONArray) jsonParser.parse(
                new FileReader("/Users/LennartMac/Documents/Projects/insta/src/main/resources/static/apify/users_6mrt.json"));

        int counter = 1;

        for(Object apifyDataElement : apifyData) {
            JSONObject profileJson = (JSONObject) apifyDataElement;
            JSONArray latestPosts  = (JSONArray) profileJson.get("latestPosts");

            if(latestPosts != null) {
                for(Object latestPostObject : latestPosts) {
                    JSONObject latestPost = (JSONObject) latestPostObject;
                    String timeStamp = (String) latestPost.get("timestamp");

                    if(dateIsWithinLastXDays(convertStringDateToDate(timeStamp), 3)) {
                        String caption = (String) latestPost.get("caption");

                        if(captionContainsDiscountWords(caption)) {
                            printIdentifiedDiscountposts(latestPost, counter++);
                        }
                    }
                }
            }
        }
    }

    private void printIdentifiedDiscountposts(JSONObject identifiedPost, int counter) {
        String userName = (String) identifiedPost.get("ownerUsername");
        String url = (String) identifiedPost.get("url");
        String timestamp = (String) identifiedPost.get("timestamp");
        String caption = (String) identifiedPost.get("caption");

        System.out.println("****************************************************************");
        System.out.println(counter);
        System.out.println(userName);
        System.out.println(caption);
        System.out.println();
        System.out.println(url);
        System.out.println("TIME: " + timestamp);
        System.out.println("****************************************************************");
        System.out.println("x");
        System.out.println("x");
        System.out.println("x");
        System.out.println("x");
    }

    private ZonedDateTime convertStringDateToDate(String stringDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Instant instant = Instant.from(formatter.parse(stringDate));
        ZoneId amsterdamZone = ZoneId.of("Europe/Amsterdam");
        ZonedDateTime amsterdamDateTime = ZonedDateTime.ofInstant(instant, amsterdamZone);
        return amsterdamDateTime;
    }

    private boolean dateIsWithinLastXDays(ZonedDateTime dateToCheck, int days) {
        ZoneId amsterdamZone = ZoneId.of("Europe/Amsterdam");
        ZonedDateTime currentTime = ZonedDateTime.now(amsterdamZone);
        boolean isAfterDaysCheck = dateToCheck.isAfter(currentTime.minusDays(days));
        return isAfterDaysCheck;
    }

    private boolean captionContainsDiscountWords(String caption) {
        boolean containsDiscountWords =
                StringUtils.containsIgnoreCase(caption, "korting") ||
                StringUtils.containsIgnoreCase(caption, "discount") ||
                StringUtils.containsIgnoreCase(caption, "% off") ||
                StringUtils.containsIgnoreCase(caption, "%off") ||
                StringUtils.containsIgnoreCase(caption, "my code") ||
                StringUtils.containsIgnoreCase(caption, "mijn code") ||
                StringUtils.containsIgnoreCase(caption, "de code") ||
                StringUtils.containsIgnoreCase(caption, "code:") ||
                StringUtils.containsIgnoreCase(caption, "code") ||
                StringUtils.containsIgnoreCase(caption, "with code") ||
                StringUtils.containsIgnoreCase(caption, "met code") ||
                StringUtils.containsIgnoreCase(caption, "use code") ||
                StringUtils.containsIgnoreCase(caption, "gebruik code") ||
                StringUtils.containsIgnoreCase(caption, "werbung") ||
                StringUtils.containsIgnoreCase(caption, "anzeige") ||
                StringUtils.containsIgnoreCase(caption, "rabatt") ||
                StringUtils.containsIgnoreCase(caption, "dem code") ||
                StringUtils.containsIgnoreCase(caption, "le code") ||
                StringUtils.containsIgnoreCase(caption, "remise") ||
                StringUtils.containsIgnoreCase(caption, "réduction") ||
                StringUtils.containsIgnoreCase(caption, "reduction");
        return containsDiscountWords;
    }
}
