package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

/**
 * Created by LennartMac on 05/12/2022.
 */
public class JsonReader {

//  nakdfashion / benakd / nakd
//  sheinpartner
//  veromoda / veromodawomen
//  lyko
//  icaniwill / iciw
//  strongerlabel / strongermoments
//  gutsgusto
//  chiquelle
//  safirashine
//  tessvfashion
//  myjewellery
//  kidsbrandstore
//  famousstore
//  airup
//  bjornborg
//  myproteinnl
//  wearglas



    //  zzz nakdfashion / benakd / nakd
//  zzz sheinpartner
//  zzz veromoda / veromodawomen
//  zzz lyko
//  zzz icaniwill / iciw
//  zzz strongerlabel / strongermoments
//  zzz gutsgusto
//  zzz chiquelle
//  zzz safirashine
//  zzz tessvfashion
//  zzz myjewellery
//  zzz kidsbrandstore
//  zzz famousstore
//  zzz airup
//  zzz bjornborg
//  zzz myproteinnl
//  zzz wearglas

    public static void main(String[] args) throws Exception {
        new JsonReader().printDiscountPostsForHashtag("wearglas");
    }

    private void printDiscountPostsForHashtag(String hashtagToUse) throws Exception {
        JSONParser jsonParser = new JSONParser();

        JSONArray apifyData = (JSONArray) jsonParser.parse(
                new FileReader("/Users/LennartMac/Documents/Projects/insta/src/main/resources/static/20dec.json"));

        int counter = 0;

        for(Object apifyDataElement : apifyData) {
            JSONObject hashtagJson = (JSONObject) apifyDataElement;

            String hashtag = (String) hashtagJson.get("name");

            if(hashtag.equals(hashtagToUse)) {
                JSONArray latestPosts = (JSONArray) hashtagJson.get("latestPosts");

                System.out.println("HASHTAG: " + hashtag);
                System.out.println("x");

                for(Object latestPostDataElement : latestPosts) {
                    JSONObject latestPost = (JSONObject) latestPostDataElement;

                    String caption = (String) latestPost.get("caption");

                    if(captionContainsDiscountWords(caption)) {
                        counter++;
                        String timestamp = (String) latestPost.get("timestamp");
                        String url = (String) latestPost.get("url");

                        System.out.println("****************************************************************");
                        System.out.println(counter);
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
                }
            }
        }
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
