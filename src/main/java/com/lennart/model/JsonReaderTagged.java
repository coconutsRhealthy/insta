package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

/**
 * Created by LennartMac on 03/01/2024.
 */
public class JsonReaderTagged {

    public static void main(String[] args) throws Exception {
        new JsonReaderTagged().checkUserTaggedPostsForDiscountCodes("loavies");
    }

    private void checkUserTaggedPostsForDiscountCodes(String company) throws Exception {
        JSONParser jsonParser = new JSONParser();

        JSONArray apifyData = (JSONArray) jsonParser.parse(
                new FileReader("/Users/lennartmac/Documents/Projects/insta/src/main/resources/static/apify/specific_tagged/loavies_1jul.json"));

        int counter = 1;

        for(Object apifyDataElement : apifyData) {
            JSONObject taggedUserJson = (JSONObject) apifyDataElement;
            String caption = (String) taggedUserJson.get("caption");

            if(captionContainsDiscountWordsGeneral(caption) || captionContainsDiscountWordsCompanySpecific(caption, company)) {
                printIdentifiedDiscountposts(taggedUserJson, counter++);
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

    private boolean captionContainsDiscountWordsGeneral(String caption) {
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

    private boolean captionContainsDiscountWordsCompanySpecific(String caption, String company) {
        switch (company) {
            case "burga":
                return StringUtils.containsIgnoreCase(caption, "XNL") ||
                        StringUtils.containsIgnoreCase(caption, "NL");
            case "gymshark":
                return false;
            case "stevemaddeneu":
                return StringUtils.containsIgnoreCase(caption, "15") ||
                        StringUtils.containsIgnoreCase(caption, "20") ||
                        StringUtils.containsIgnoreCase(caption, "25");
            case "sellpy":
                return StringUtils.containsIgnoreCase(caption, "15");
            default:
                return false;
        }
    }

    public static JSONArray getTaggedPostsForCompany(String company, String jsonPath) throws Exception {
        JSONParser jsonParser = new JSONParser();
        JSONArray apifyData = (JSONArray) jsonParser.parse(new FileReader(jsonPath));
        JSONArray taggedPostsForCompany = new JSONArray();

        for(Object apifyDataElement : apifyData) {
            JSONObject taggedUserJson = (JSONObject) apifyDataElement;

            if(taggedUserJson.get("error") == null) {
                String inputUrl = (String) taggedUserJson.get("inputUrl");

                if(inputUrl != null) {
                    String taggedCompany = inputUrl.substring(inputUrl.lastIndexOf('/') + 1);

                    if(taggedCompany.contains(company) || company.contains(taggedCompany)) {
                        String originalCaption = (String) taggedUserJson.get("caption");
                        String alteredCaption = "** TAGGED USER JSON **\n" + originalCaption;
                        taggedUserJson.put("caption", alteredCaption);
                        taggedPostsForCompany.add(taggedUserJson);
                    }
                }
            } else {
                System.out.println("Error in tagged Json! Json object ignored");
            }
        }

        return taggedPostsForCompany;
    }
}
