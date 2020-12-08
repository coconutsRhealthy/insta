package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 18/11/2020.
 */
public class Korting {

    private void printDiscountAccounts() throws Exception {
        List<String> users = new Aandacht().fillUserList(false);

        int counter = 0;

        for(String user : users) {
            if(givesDiscount(user)) {
                System.out.println(user);
            }
            System.out.println(counter++);
        }
    }

    private boolean givesDiscount(String username) throws Exception {
        boolean givesDiscount = false;

        try {
            Document document = Jsoup.connect("https://www.instagram.com/" + username).get();

            String bodyText = document.body().html();

            if(StringUtils.containsIgnoreCase(bodyText, "korting")) {
                givesDiscount = true;
            } else if(StringUtils.containsIgnoreCase(bodyText, "discount")) {
                givesDiscount = true;
            } else if(StringUtils.containsIgnoreCase(bodyText, "% off")) {
                givesDiscount = true;
            } else if(StringUtils.containsIgnoreCase(bodyText, "%off")) {
                givesDiscount = true;
            } else if(StringUtils.containsIgnoreCase(bodyText, "my code")) {
                givesDiscount = true;
            }
        } catch (Exception e) {
            System.out.println("%% - Exception: " + username);
        }

        return givesDiscount;
    }

    //korting op de eerste drie boxen. #HelloFreshNL #partner"}}]},"edge_media_to_comment":{"count":67},"comments_disabled":false,"taken_at_timestamp":1605367285,"edge_liked_by":{"count":9001}

//    private void getTimeStampForKorting(String longKortingString) {
//        String testString = "korting op de eerste drie boxen. #HelloFreshNL #partner\"}}]},\"edge_media_to_comment\":{"count":67},"comments_disabled":false,"taken_at_timestamp":1605367285,"edge_liked_by":{"count":9001}
//
//
//    }

//    public static void main(String[] args) throws Exception {
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
//
//
//        new Korting().testConnectingWithProxy();
//    }

//    private void testConnectingWithProxy() throws Exception {
//
////        URL url = new URL("https://www.google.com/");
////        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("47.91.88.100", 1080)); // or whatever your proxy is
////        HttpURLConnection uc = (HttpURLConnection)url.openConnection(proxy);
////
////        uc.connect();
////
////        String line = null;
////        StringBuffer tmp = new StringBuffer();
////        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
////        while ((line = in.readLine()) != null) {
////            tmp.append(line);
////        }
////
////        Document doc = Jsoup.parse(String.valueOf(tmp));
//
//
//
////        System.setProperty("http.proxyHost", "51.254.69.243");
////        System.setProperty("http.proxyPort", "3128");
//        //System.setProperty("https.proxyHost", "54.34.56.223");
//        //System.setProperty("https.proxyPort", "8080");
////        System.setProperty("java.net.useSystemProxies", "true");
////
////        System.getProperties().put("https.proxyHost", "eije");
////        System.getProperties().put("https.proxyPort", "hmm");
//
//        //System.setProperty("http.proxyHost", "51.154.69.243");
////        //System.setProperty("http.proxyPort", "3126");
////        System.setProperty("http.proxyHost","");
////        System.setProperty("http.proxyPort","");
////        System.setProperty("https.proxyHost","");
////        System.setProperty("https.proxyPort","");
//
//        //System.setProperty("http.proxyHost","eije");
//        //System.out.println(System.getProperties().getProperty("https.proxyHost"));
//
//        //System.out.println(System.getProperty("java.net.useSystemProxies"));
//
////        System.setProperty("http.proxyHost", "aaa");
////        System.setProperty("http.proxyPort", "bbb");
////        System.setProperty("java.net.useSystemProxies", "true");
////        Document doc = Jsoup.connect("https://www.google.com").get();
//
//        Document doc = Jsoup //
//                .connect("https://www.nu.nl") //
//                //.proxy("22.22", 8080) // sets a HTTP proxy
//                .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2") //
//                .header("Content-Language", "en-US") //
//                .get();
//
//        System.out.println(doc.html());
//
//        //System.setProperty("http.proxyHost", null);
//        //System.setProperty("http.proxyPort", null);
//    }

//    public static void main(String[] args) throws Exception {
//        //System.out.println("ja hoooiii!");
//
//        Korting korting = new Korting();
////
////        //for(int i = 0; i < 100; i++) {
////
//////        for(int i = 0; i < 10; i++) {
//////            System.out.println("aap");
//////        }
////
////            //try {
////                String script = korting.getRelevantScriptAsString("annanooshin");
////                double followers = korting.getFollowers(script);
////                System.out.println("followertjesz: " + followers);
//                //TimeUnit.SECONDS.sleep(56);
//            //} catch (Exception e) {
//            //    System.out.println("EXCEPTION!");
//            //    e.printStackTrace();
//                //TimeUnit.SECONDS.sleep(56);
//            //}
//        //}
//    }

//    public static void main(String[] args) throws Exception {
//        new Korting().letsSeePicuki();
//    }

    private void letsSeePicuki() throws Exception {
        Document document = Jsoup.connect("https://www.picuki.com/profile/juliamekkes").get();

        System.out.println(document.html());
    }

    private String getRelevantScriptAsString(String username) throws Exception {
        String scriptToReturn = null;

        Document document = Jsoup.connect("https://www.instagram.com/" + username).
                header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.67 Safari/537.36")
                .get();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println(document.html());

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();


        //                .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2") //
//                .header("Content-Language", "en-US") //

        //System.out.println(document.html());

        Elements scripts = document.getElementsByTag("script");

        for(Element script : scripts) {
            String scriptAsString = script.toString();

            if(scriptAsString.contains("edge_followed_by")) {
                scriptToReturn = scriptAsString;
            }
        }

        return scriptToReturn;
    }

    private double getFollowers(String script) {
        String followers = script.substring(script.indexOf("edge_followed_by"));
        followers = followers.substring(0, followers.indexOf(","));
        followers = removeAllNonNumericCharacters(followers);
        return Double.valueOf(followers);
    }

    public String removeAllNonNumericCharacters(String string) {
        String stringToReturn = string.replaceAll("[^\\d.]", "");

        if(stringToReturn.startsWith(".")) {
            stringToReturn = "0" + stringToReturn;
        }

        return stringToReturn;
    }


    private void overallMethod() throws Exception {
        Map<String, List<String>> kortingWordsUsed = identifyKortingWordsUsed("naomivanasofficial");

        List<String> kortingTimeStamps = new ArrayList<>();

        for(Map.Entry<String, List<String>> entry : kortingWordsUsed.entrySet()) {
            kortingTimeStamps.addAll(getKortingTimeStamps(entry.getKey(), entry.getValue()));
        }

        kortingTimeStamps.stream().forEach(timeStampString -> System.out.println(convertTimeStampToDate(timeStampString)));
    }

    private Map<String, List<String>> identifyKortingWordsUsed(String username) throws Exception {
        Document document = Jsoup.connect("https://www.instagram.com/" + username)
                .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.67 Safari/537.36")
                .get();

        List<String> kortingsWordsPresentOnPage = new ArrayList<>();

        String bodyText = document.body().html();

        if(StringUtils.containsIgnoreCase(bodyText, "korting")) {
            kortingsWordsPresentOnPage.add("korting");
        } else if(StringUtils.containsIgnoreCase(bodyText, "discount")) {
            kortingsWordsPresentOnPage.add("discount");
        } else if(StringUtils.containsIgnoreCase(bodyText, "% off")) {
            kortingsWordsPresentOnPage.add("% off");
        } else if(StringUtils.containsIgnoreCase(bodyText, "%off")) {
            kortingsWordsPresentOnPage.add("%off");
        } else if(StringUtils.containsIgnoreCase(bodyText, "my code")) {
            kortingsWordsPresentOnPage.add("my code");
        }

        Map<String, List<String>> bodyWithKortingWords = new HashMap<>();
        bodyWithKortingWords.put(bodyText, kortingsWordsPresentOnPage);

        return bodyWithKortingWords;
    }

    private List<String> getKortingTimeStamps(String bodyText, List<String> kortingsWordsOnPage) {
        List<String> partAfterKortingWordSubstrings = new ArrayList<>();

        for(String kortingsWord : kortingsWordsOnPage) {
            String bodyCopy = bodyText;

            while(StringUtils.containsIgnoreCase(bodyCopy, kortingsWord)) {
                bodyCopy = bodyCopy.substring(bodyCopy.indexOf(kortingsWord) + kortingsWord.length(), bodyCopy.length());
                partAfterKortingWordSubstrings.add(bodyCopy);
            }
        }

        List<String> timeStamps = new ArrayList<>();

        for(String partAfterKortingWord : partAfterKortingWordSubstrings) {
            if(partAfterKortingWord.contains("taken_at_timestamp")) {
                String timeStampPart = partAfterKortingWord.substring(partAfterKortingWord.indexOf
                        ("taken_at_timestamp"), partAfterKortingWord.length());

                int firstIndexOfDoublePoint = timeStampPart.indexOf(":") + 1;
                int firstIndexOfComma = timeStampPart.indexOf(",");

                if(firstIndexOfDoublePoint > 0 && firstIndexOfComma > 0 && firstIndexOfComma > firstIndexOfDoublePoint) {
                    String timeStamp = timeStampPart.substring(firstIndexOfDoublePoint, firstIndexOfComma);
                    timeStamps.add(timeStamp);
                }
            }
        }

        return timeStamps;
    }

    private String convertTimeStampToDate(String timeStampString) {
        long timeStamp = Long.parseLong(timeStampString);
        timeStamp = timeStamp * 1000;
        Date dateOfKorting = new Date(timeStamp);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(dateOfKorting);
    }
}
