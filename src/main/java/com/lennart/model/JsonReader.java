package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by LennartMac on 05/12/2022.
 */
public class JsonReader {

//  nakdfashion / benakd / nakd
//  loavies / girlsgoneloavies / loaviesdiscount
//  sheinpartner
//  strongerlabel / strongermoments
//  gutsgusto
//  getdrezzed
//  begoldennl
//  mimamsterdam
//  paulie__pocket, pauliepocket
//  myjewellery
//  famousstore
//  airup
//  bjornborg
//  myproteinnl
//  esuals
    //terstal / mijnterstallook
    //loopearplugs
    //hunkemoller

    //myburga / burga
    //gymshark
    //snuggs
    //goboony
    //ginatricot
    //otrium / otriumcreators
    //pinkgellac
    //geurwolkje
    //body&fit
    //kaptenandson
    //stevemaddeneu
    //emmasleep
    //hellofresh
    //lookfantastic
    //leolive
    //only.nederland
    //sellpy
    //esncom
    //aybl
    //lyko_nl
    //maniacnails.official



//    public static void main(String[] args) throws Exception {
//        new JsonReader().printDiscountPostsForHashtag("wearglas");
//    }

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


    //  zzz nakdfashion / benakd / nakd
    //  zzz loavies / girlsgoneloavies / loaviesdiscount
    //  zzz sheinpartner
    //  zzz strongerlabel / strongermoments
    //  zzz gutsgusto
    //  zzz getdrezzed
    //  zzz begoldennl
    //  zzz mimamsterdam
    //  zzz paulie__pocket, pauliepocket
    //  zzz myjewellery
    //  zzz famousstore
    //  zzz airup
    //  zzz bjornborg
    //  zzz myproteinnl
    //  zzz esuals
        //zzz terstal / mijnterstallook
        //zzz loopearplugs
        //zzz hunkemoller

        //zzz myburga / burga
        //zzz gymshark
        //zzz snuggs
        //zzz goboony
        //zzz ginatricot
        //zzz otrium / otriumcreators
        //zzz pinkgellac
        //zzz geurwolkje
        //zzz body&fit
        //zzz kaptenandson
        //zzz stevemaddeneu
        //zzz emmasleep
        //zzz hellofresh
        //zzz lookfantastic
        //zzz leolive
        //zzz only.nederland
        //zzz sellpy
        //zzz esncom
        //zzz aybl
        //zzz lyko_nl
        //zzz maniacnails.official

    public static void main(String[] args) throws Exception {
        new JsonReader().overallMethod();
        //new JsonReader().openaiShizzle();
    }

    private void overallMethod() throws Exception {
        List<String> hashTagsForBand = Arrays.asList("nakdfashion");

        JSONArray jsonArray = new JSONArray();

        for(String hashtag : hashTagsForBand) {
            jsonArray.addAll(getLatestPostsForHashtag(hashtag));
            //jsonArray.addAll(getCompanyTaggedPostsForHashtag(hashtag));
        }

        JSONArray sortedJsonArray = sort(jsonArray);

        printData(sortedJsonArray);

//        testPrintMethod(jsonArray);
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println("****************");
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        testPrintMethod(sortedJsonArray);

    }

    private void testPrintMethod(JSONArray array) {
        for(Object latestPostDataElement : array) {
            JSONObject latestPost = (JSONObject) latestPostDataElement;
            String date = (String) latestPost.get("timestamp");
            System.out.println(date);
        }
    }

    private JSONArray sort(JSONArray unsortedJsonArray) {
        JSONArray sortedJsonArray = new JSONArray();

        List<JSONObject> jsonValues = new ArrayList<>();

        for (int i = 0; i < unsortedJsonArray.size(); i++) {
            jsonValues.add((JSONObject) unsortedJsonArray.get(i));
        }

        Collections.sort(jsonValues, new Comparator<JSONObject>() {
            //You can change "Name" with "ID" if you want to sort by ID
            private static final String KEY_NAME = "Name";

            @Override
            public int compare(JSONObject a, JSONObject b) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");

                String date1 = (String) a.get("timestamp");
                String date2 = (String) b.get("timestamp");

                if(date1 != null && date2 != null) {
                    date1 = date1.replace("T", " ");
                    date2 = date2.replace("T", " ");

                    date1 = date1.replace(".000", " ");
                    date2 = date2.replace(".000", " ");

                    ZonedDateTime zonedDateTime1 = ZonedDateTime.parse(date1, formatter);
                    ZonedDateTime zonedDateTime2 = ZonedDateTime.parse(date2, formatter);

                    if(zonedDateTime1.isBefore(zonedDateTime2)) {
                        return 1;
                    } else if(zonedDateTime1.equals(zonedDateTime2)) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    System.out.println("No timestamp for post available!");
                    return 0;
                }
            }
        });

        for (int i = 0; i < unsortedJsonArray.size(); i++) {
            sortedJsonArray.add(jsonValues.get(i));
        }

        return sortedJsonArray;
    }

    @SuppressWarnings("unchecked")
    private JSONArray getLatestPostsForHashtag(String hashtagToUse) throws Exception {
        JSONParser jsonParser = new JSONParser();

        JSONArray apifyData = (JSONArray) jsonParser.parse(
                new FileReader("/Users/lennartmac/Documents/Projects/insta/src/main/resources/static/apify/companies/27dec_nakdfashion.json"));

        JSONArray postDataForHashtag = new JSONArray();

        for(Object apifyDataElement : apifyData) {
            JSONObject hashtagJson = (JSONObject) apifyDataElement;

            JSONArray hashtags = (JSONArray) hashtagJson.get("hashtags");

            if(hashtags != null && hashtags.contains(hashtagToUse)) {
                JSONObject postDetails = new JSONObject();
                postDetails.put("caption", hashtagJson.get("caption"));
                postDetails.put("timestamp", hashtagJson.get("timestamp"));
                postDetails.put("url", hashtagJson.get("url"));
                postDataForHashtag.add(postDetails);
            }
        }

        return postDataForHashtag;
    }

    private JSONArray getCompanyTaggedPostsForHashtag(String hashtag) throws Exception {
        return JsonReaderTagged.getTaggedPostsForCompany(hashtag, "/Users/lennartmac/Documents/Projects/insta/src/main/resources/static/apify/companies/17nov_tagged_stronger.json");
    }

    private void printData(JSONArray latestPostsForBrand) {
        int counter = 0;

        for(Object latestPostDataElement : latestPostsForBrand) {
            JSONObject latestPost = (JSONObject) latestPostDataElement;

            String caption = (String) latestPost.get("caption");

            if(captionContainsDiscountWords(caption) && !captionDoesNotContain(caption)) {
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

    private boolean captionContainsDiscountWords(String caption) {
        boolean containsDiscountWords =
                StringUtils.containsIgnoreCase(caption, "korting") ||
                StringUtils.containsIgnoreCase(caption, "discount") ||
                StringUtils.containsIgnoreCase(caption, "% off") ||
                StringUtils.containsIgnoreCase(caption, "%off") ||
                StringUtils.containsIgnoreCase(caption, "% of") ||
                StringUtils.containsIgnoreCase(caption, "%of") ||
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

    private boolean captionDoesNotContain(String caption) {
        boolean containsDiscountWords =
                StringUtils.containsIgnoreCase(caption, "on idealofsweden.com") ||
                StringUtils.containsIgnoreCase(caption, "Kortingscode ABOUTYOU -20");
        return containsDiscountWords;
    }
}
