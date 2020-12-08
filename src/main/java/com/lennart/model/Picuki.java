package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 01/12/2020.
 */
public class Picuki {

    private Connection con;

    //ook een goede: https://gramho.com/
    //picuki, bigsta, gramho, instapiks

//    public static void main(String[] args) throws Exception {
//        try {
//            new Picuki().nightlyRunOfFollowersTest();
//        } catch (Exception e) {
//            System.out.println("vage timeunit excepzion!");
//            e.printStackTrace();
//        }
//
////        List<String> users = new Aandacht().fillUserList(false);
////        Picuki picuki = new Picuki();
////        int counter = 0;
////
////        for(String user : users) {
////            counter++;
////            picuki.getPicukiH1(user, counter);
////            TimeUnit.SECONDS.sleep(90);
////        }
//    }

    private void getPicukiH1(String userName, int counter) throws Exception {
        try {
            Document document = Jsoup.connect("https://www.picuki.com/profile/" + userName).get();
            System.out.println("" + counter + " " + document.getElementsByTag("h2").get(0).text());
        } catch (Exception e) {
            System.out.println("" + counter + " Error!");
            e.printStackTrace();
        }
    }

    private void fillDbWithUsernames() throws Exception {
        List<String> users = new Aandacht().fillUserList(false);

        initializeDbConnection();

        for(String user : users) {
            Statement st = con.createStatement();
            st.executeUpdate("INSERT INTO followers (username, amount_of_followers) VALUES ('" + user + "', -5)");
            st.close();
        }
    }

//    private void identifyLastKortingTime(String username) {
//        Document document = Jsoup.connect("https://www.picuki.com/profile/" + username).get();
//        String fullHtml = document.html();
//
//
//
//
//
//    }

//    public static void main(String[] args) throws Exception {
//        new Picuki().testje();
//    }
//
//    private void testje() throws Exception {
//        Map<String, List<String>> kortingWords = new Picuki().identifyKortingWordsUsed("moderosaofficial");
//
//        for(Map.Entry<String, List<String>> entry : kortingWords.entrySet()) {
//            List<String> lastPostTimes = new Picuki().getKortingWordLastPostTime(entry.getKey(), entry.getValue());
//
//            String lastPostTimeToUse = "none";
//
//            if(!lastPostTimes.isEmpty()) {
//                lastPostTimeToUse = lastPostTimes.get(0);
//            }
//
//            System.out.println("wacht");
//        }
//
//    }

//    public static void main(String[] args) throws Exception {
//        new Picuki().nightlyRunKortingTest();
//    }

    private void nightlyRunKortingTest() throws Exception {
        List<String> users = new Aandacht().fillUserList(false);
        Picuki picuki = new Picuki();
        int counter = 0;

        for(String user : users) {
            try {
                counter++;
                System.out.println("**** " + counter + ") USER: " + user + " ****");

                Map<String, List<String>> kortingWords = picuki.identifyKortingWordsUsed(user);

                for(Map.Entry<String, List<String>> entry : kortingWords.entrySet()) {
                    List<String> lastPostTimes = picuki.getKortingWordLastPostTimes(entry.getKey(), entry.getValue());
                    lastPostTimes = picuki.sortLastPostTimesFromNewestToOldest(lastPostTimes);

                    String lastPostTimeToUse = "none";

                    if(!lastPostTimes.isEmpty()) {
                        lastPostTimeToUse = lastPostTimes.get(0);
                    }

                    updateKortingDb(lastPostTimeToUse, user);
                }
            } catch (Exception e) {
                System.out.println("ERREUURRR");
                e.printStackTrace();
            }

            TimeUnit.SECONDS.sleep(90);
        }
    }

    private void updateKortingDb(String lastKortingPostTime, String username) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("UPDATE followers SET last_korting = '" + lastKortingPostTime + "' WHERE username = '" + username + "'");
        st.close();

        closeDbConnection();
    }

    private Map<String, List<String>> identifyKortingWordsUsed(String username) throws Exception {
        Document document = Jsoup.connect("https://www.picuki.com/profile/" + username).get();

        List<String> kortingsWordsPresentOnPage = new ArrayList<>();

        String bodyText = document.html();

        if(StringUtils.containsIgnoreCase(bodyText, "korting")) {
            kortingsWordsPresentOnPage.add("korting");
        }

        if(StringUtils.containsIgnoreCase(bodyText, "discount")) {
            kortingsWordsPresentOnPage.add("discount");
        }

        if(StringUtils.containsIgnoreCase(bodyText, "% off")) {
            kortingsWordsPresentOnPage.add("% off");
        }

        if(StringUtils.containsIgnoreCase(bodyText, "%off")) {
            kortingsWordsPresentOnPage.add("%off");
        }

        if(StringUtils.containsIgnoreCase(bodyText, "my code")) {
            kortingsWordsPresentOnPage.add("my code");
        }

        Map<String, List<String>> bodyWithKortingWords = new HashMap<>();
        bodyWithKortingWords.put(bodyText, kortingsWordsPresentOnPage);

        return bodyWithKortingWords;
    }

    private List<String> getKortingWordLastPostTimes(String bodyText, List<String> kortingsWordsOnPage) {
        List<String> partAfterKortingWordSubstrings = new ArrayList<>();

        for(String kortingsWord : kortingsWordsOnPage) {
            String bodyCopy = bodyText;

            if(StringUtils.containsIgnoreCase(bodyCopy, kortingsWord)) {
                bodyCopy = bodyCopy.substring(bodyCopy.toLowerCase().indexOf(kortingsWord.toLowerCase()) +
                        kortingsWord.length(), bodyCopy.length());
                partAfterKortingWordSubstrings.add(bodyCopy);
            }
        }

        List<String> times = new ArrayList<>();

        for(String partAfterKortingWord : partAfterKortingWordSubstrings) {
            if(partAfterKortingWord.contains("<div class=\"time\">") &&
                    partAfterKortingWord.indexOf("<div class=\"time\">") + 60 < partAfterKortingWord.length()) {
                String kortingTimeForWord = partAfterKortingWord.substring(
                        partAfterKortingWord.indexOf("<div class=\"time\">"),
                        partAfterKortingWord.indexOf("<div class=\"time\">") + 60);

                kortingTimeForWord = kortingTimeForWord.substring(
                        kortingTimeForWord.indexOf("span>") + 5, kortingTimeForWord.indexOf("</span"));

                times.add(kortingTimeForWord);
            }
        }

        return times;
    }

    private List<String> sortLastPostTimesFromNewestToOldest(List<String> lastPostTimes) {
        List<String> dayPostTimes = new ArrayList<>();
        List<String> weekPostTimes = new ArrayList<>();
        List<String> monthPostTimes = new ArrayList<>();
        List<String> yearPostTimes = new ArrayList<>();

        for(String postTime : lastPostTimes) {
            if(postTime.contains("day")) {
                dayPostTimes.add(postTime);
            } else if(postTime.contains("week")) {
                weekPostTimes.add(postTime);
            } else if(postTime.contains("month")) {
                monthPostTimes.add(postTime);
            } else if(postTime.contains("year")) {
                yearPostTimes.add(postTime);
            }
        }

        Collections.sort(dayPostTimes, new LastPostTimeComparator());
        Collections.sort(weekPostTimes, new LastPostTimeComparator());
        Collections.sort(monthPostTimes, new LastPostTimeComparator());
        Collections.sort(yearPostTimes, new LastPostTimeComparator());

        List<String> sorted = new ArrayList<>();
        sorted.addAll(dayPostTimes);
        sorted.addAll(weekPostTimes);
        sorted.addAll(monthPostTimes);
        sorted.addAll(yearPostTimes);

        return sorted;
    }

    private class LastPostTimeComparator implements Comparator<String> {
        @Override
        public int compare(String lastPostTime1, String lastPostTime2) {
            int lastPostTime1Integer = Integer.valueOf(lastPostTime1.substring(0, lastPostTime1.indexOf(" ")));
            int lastPostTime2Integer = Integer.valueOf(lastPostTime2.substring(0, lastPostTime1.indexOf(" ")));

            int toReturn;

            if(lastPostTime1Integer < lastPostTime2Integer) {
                toReturn = -1;
            } else if(lastPostTime1Integer == lastPostTime2Integer) {
                toReturn = 0;
            } else {
                toReturn = 1;
            }

            return toReturn;
        }
    }

    private void nightlyRunOfFollowersTest() throws Exception {
        List<String> users = new Aandacht().fillUserList(false);

        for(String user : users) {
            int followers;

            try {
                followers = getPicukiFollowers(user);
            } catch (Exception e) {
                followers = -1;
            }

            try {
                updateUserAndFollowerAmountInDb(user, followers);
            } catch (Exception e) {
                System.out.println("Sicke sql exception yow");
                e.printStackTrace();
            }

            TimeUnit.SECONDS.sleep(90);
        }
    }

    private int getPicukiFollowers(String username) throws Exception {
        int followers = -1;

        Document document = Jsoup.connect("https://www.picuki.com/profile/" + username).get();
        Elements allSpanElements = document.select("span.bold");

        for(Element spanElement : allSpanElements) {
            if(spanElement.text().contains("Followers")) {
                String followerString = spanElement.text();
                followerString = followerString.substring(0, followerString.indexOf(" "));
                followerString = followerString.replace(",", "");
                followers = Integer.valueOf(followerString);
            }
        }

        return followers;
    }

    private String getFullKortingPostText(String fullHtml, String kortingsWord) throws Exception {
        String partOfHtmlBeforeKortingsWord = fullHtml.substring(0, fullHtml.indexOf(kortingsWord));
        String partOfHtmlAfterKortingsWord = fullHtml.substring(fullHtml.indexOf(kortingsWord), fullHtml.length());

        String firstHalfOfKortingPostText = partOfHtmlBeforeKortingsWord.substring
                (partOfHtmlBeforeKortingsWord.lastIndexOf("alt=") + 5, partOfHtmlBeforeKortingsWord.length());

        String secondHalfOfKortingPostText = partOfHtmlAfterKortingsWord.substring
                (0, partOfHtmlAfterKortingsWord.indexOf("\">"));

        String fullKortingPostText = firstHalfOfKortingPostText + secondHalfOfKortingPostText;
        return fullKortingPostText;
    }

    private void updateUserAndFollowerAmountInDb(String username, int amountOfFollowers) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("UPDATE followers SET amount_of_followers = " + amountOfFollowers + " WHERE username = '" + username + "'");
        st.close();

        closeDbConnection();
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/influencers", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }


}
