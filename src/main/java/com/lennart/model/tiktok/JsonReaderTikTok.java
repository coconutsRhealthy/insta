package com.lennart.model.tiktok;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class JsonReaderTikTok {

    public static void main(String[] args) throws Exception {
        new JsonReaderTikTok().testGetRecentDiscountPostsTiktok();
    }

    private void testGetRecentDiscountPostsTiktok() throws Exception {
        JSONParser jsonParser = new JSONParser();
        JSONArray apifyData = (JSONArray) jsonParser.parse(new FileReader("/Users/lennartmac/Documents/Projects/insta/src/main/resources/static/apify/tiktok_test/users_test/tiktok_users_8jul.json"));
        int counter = 1;

        for(Object apifyDataElement : apifyData) {
            JSONObject recentTiktokPostJson = (JSONObject) apifyDataElement;
            String caption = (String) recentTiktokPostJson.get("text");

            if(captionContainsDiscountWords(caption)) {
                System.out.println("****************************************************************");
                System.out.println(counter++);
                System.out.println((String) ((JSONObject) recentTiktokPostJson.get("authorMeta")).get("name"));
                System.out.println((String) recentTiktokPostJson.get("webVideoUrl"));
                System.out.println(caption);
                System.out.println("TIME: " + recentTiktokPostJson.get("createTimeISO"));
                System.out.println("****************************************************************");
                System.out.println("x");
                System.out.println("x");
                System.out.println("x");
                System.out.println("x");
            }
        }
    }

    public static boolean captionContainsDiscountWords(String caption) {
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
}
