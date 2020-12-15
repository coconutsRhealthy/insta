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
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 01/12/2020.
 */
public class Picuki {

    private Connection con;

    //ook een goede: https://gramho.com/
    //picuki, bigsta, gramho, instapiks
    //fullinsta.photo

    //https://instajust.com/

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

//    public static void main(String[] args) throws Exception {
//        new Picuki().eije();
//    }

    private void eije() throws Exception {
        Document document = Jsoup.connect("https://instajust.com/profile/dutchtoy").get();
        String fullHtml = document.html();

        if(fullHtml.contains("Fofs")) {
            System.out.println("storn");
        } else {
            System.out.println("eije");
        }
    }

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
        List<String> users = new Korting().fillKortingUsers();

        initializeDbConnection();

        for(String user : users) {
            Statement st = con.createStatement();
            st.executeUpdate("INSERT INTO followers2 (username, amount_of_followers) VALUES ('" + user + "', -5)");
            st.close();
        }

        closeDbConnection();
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

    public static void main(String[] args) throws Exception {
        String siteName = "instajust";
        String startKortingPostHtmlIndicator = "<span>";
        String endKortingPostHtmlIndicator = "</span>";
        String timeHtmlIdentifier = "<div class=\"article_time\">";

        String siteNamePicu = "picuki";
        String startKortingPostHtmlIndicatorPicu = "alt=";
        String endKortingPostHtmlIndicatorPicu = "\">";
        String timeHtmlIdentifierPicu = "<div class=\"time\">";

        while(true) {
            new Picuki().continuousRunKorting();
        }
    }


    //for Picuki
    //siteName: picuki
    //timeHtmlIdentifier: "<div class=\"time\">"
    //startKortingPostHtmlIndicator: "alt="
    //endKortingPostHtmlIndicator: "\">"

    //for InstaJust
    //siteName: instajust
    //timeHtmlIdentifier: "<div class=\"article_time\">"
    //startKortingPostHtmlIndicator: "<span>"
    //endKortingPostHtmlIndicator: "</span>"

    private void continuousRunKorting() throws Exception {
        List<String> users = new Korting().fillKortingUsers();

        int counter = 0;

        for(String user : users) {
            try {
                nightlyRunLogic(counter, user, "instajust", "<div class=\"article_time\">", "<span>", "</span>");
            } catch (Exception e) {
                System.out.println("instajust error!");

                try {
                    nightlyRunLogic(counter, user, "picuki", "<div class=\"time\">", "alt=", "\">");
                } catch (Exception z) {
                    System.out.println("picuki error!");
                    System.out.println("ERROR complete!!!");
                    updateKortingDb("error", "error", "storn error", user);
                    z.printStackTrace();
                }
            }

            TimeUnit.SECONDS.sleep(90);
        }
    }

    private void nightlyRunLogic(int counter, String user, String siteName, String timeHtmlIdentifier,
                                 String startKortingPostHtmlIndicator, String endKortingPostHtmlIndicator) throws Exception {
        counter++;
        System.out.println("**** " + counter + ") USER: " + user + " ****");

        String fullHtmlForUser = getFullHtmlForUsername(siteName, user);
        Set<String> kortingWordsOnPage = identifyKortingWordsUsed(fullHtmlForUser);

        Map<String, String> lastPostTimesPerKortingsWord =
                getKortingWordLastPostTimes(fullHtmlForUser, kortingWordsOnPage, timeHtmlIdentifier);

        lastPostTimesPerKortingsWord = lastPostTimesPerKortingsWord.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(new LastPostTimeComparator()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        String kortingsWord = "none";
        String lastPostTimeToUse = "none";
        String fullKortingsWordText = "storn none";

        if(!lastPostTimesPerKortingsWord.isEmpty()) {
            kortingsWord = lastPostTimesPerKortingsWord.keySet().stream().collect(Collectors.toList()).get(0);
            lastPostTimeToUse = addSubscriptToPostTimeString(lastPostTimesPerKortingsWord.values().stream().collect(Collectors.toList()).get(0));
            fullKortingsWordText = getFullKortingPostText(fullHtmlForUser, kortingsWord,
                    startKortingPostHtmlIndicator, endKortingPostHtmlIndicator);
        }

        updateKortingDb(kortingsWord, lastPostTimeToUse, fullKortingsWordText, user);
    }

    private void updateKortingDb(String kortingsWord, String lastKortingPostTime, String kortingPostFullText, String username) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("UPDATE followers2 SET last_korting = '" + lastKortingPostTime + "', kortingsword = '" + kortingsWord + "', kortingsword_post_fulltext = '" + kortingPostFullText + "' WHERE username = '" + username + "'");
        st.close();

        closeDbConnection();
    }

    private String getFullHtmlForUsername(String siteName, String username) throws Exception {
        Document document = Jsoup.connect("https://www." + siteName + ".com/profile/" + username).get();
        return document.html();
    }

    public Set<String> identifyKortingWordsUsed(String fullHtml) throws Exception {
        Set<String> kortingsWordsPresentOnPage = new HashSet<>();

        if(StringUtils.containsIgnoreCase(fullHtml, "korting")) {
            kortingsWordsPresentOnPage.add("korting");
        }

        if(StringUtils.containsIgnoreCase(fullHtml, "discount")) {
            kortingsWordsPresentOnPage.add("discount");
        }

        if(StringUtils.containsIgnoreCase(fullHtml, "% off")) {
            kortingsWordsPresentOnPage.add("% off");
        }

        if(StringUtils.containsIgnoreCase(fullHtml, "%off")) {
            kortingsWordsPresentOnPage.add("%off");
        }

        if(StringUtils.containsIgnoreCase(fullHtml, "my code")) {
            kortingsWordsPresentOnPage.add("my code");
        }

        if(StringUtils.containsIgnoreCase(fullHtml, "mijn code")) {
            kortingsWordsPresentOnPage.add("mijn code");
        }

        if(StringUtils.containsIgnoreCase(fullHtml, "de code")) {
            kortingsWordsPresentOnPage.add("de code");
        }

        if(StringUtils.containsIgnoreCase(fullHtml, "code:")) {
            kortingsWordsPresentOnPage.add("code:");
        }

        if(StringUtils.containsIgnoreCase(fullHtml, "with code")) {
            kortingsWordsPresentOnPage.add("with code");
        }

        if(StringUtils.containsIgnoreCase(fullHtml, "met code")) {
            kortingsWordsPresentOnPage.add("met code");
        }

        return kortingsWordsPresentOnPage;
    }

    public Map<String, String> getKortingWordLastPostTimes(String bodyText, Set<String> kortingsWordsOnPage,
                                                            String timeHtmlIdentifier) {
        Map<String, String> kortingsWordWithpartAfterKortingWordSubstrings = new HashMap<>();

        for(String kortingsWord : kortingsWordsOnPage) {
            String bodyCopy = bodyText;

            bodyCopy = bodyCopy.substring(bodyCopy.toLowerCase().indexOf(kortingsWord.toLowerCase()) +
                    kortingsWord.length(), bodyCopy.length());
            kortingsWordWithpartAfterKortingWordSubstrings.put(kortingsWord, bodyCopy);
        }

        Map<String, String> kortingWordLastPostTimes = new HashMap<>();

        for (Map.Entry<String, String> entry : kortingsWordWithpartAfterKortingWordSubstrings.entrySet()) {
            String partAfterKortingWord = entry.getValue();

            if(partAfterKortingWord.contains(timeHtmlIdentifier) &&
                    partAfterKortingWord.indexOf(timeHtmlIdentifier) + 70 < partAfterKortingWord.length()) {
                String kortingTimeForWord = partAfterKortingWord.substring(
                        partAfterKortingWord.indexOf(timeHtmlIdentifier),
                        partAfterKortingWord.indexOf(timeHtmlIdentifier) + 70);

                if(kortingTimeForWord.contains("span>") && kortingTimeForWord.contains("</span")
                        && kortingTimeForWord.indexOf("span>") < kortingTimeForWord.indexOf("</span")) {
                    kortingTimeForWord = kortingTimeForWord.substring(
                            kortingTimeForWord.indexOf("span>") + 5, kortingTimeForWord.indexOf("</span"));
                    kortingWordLastPostTimes.put(entry.getKey(), kortingTimeForWord);
                }
            }
        }

        return kortingWordLastPostTimes;
    }

    private class LastPostTimeComparator implements Comparator<String> {
        @Override
        public int compare(String lastPostTime1, String lastPostTime2) {
            String lastPostTime1Period = getPeriodFromPostTimeString(lastPostTime1);
            String lastPostTime2Period = getPeriodFromPostTimeString(lastPostTime2);

            int toReturn;

            if(lastPostTime1Period.equals(lastPostTime2Period)) {
                int lastPostTime1Integer = Integer.valueOf(lastPostTime1.substring(0, lastPostTime1.indexOf(" ")));
                int lastPostTime2Integer = Integer.valueOf(lastPostTime2.substring(0, lastPostTime1.indexOf(" ")));

                if(lastPostTime1Integer < lastPostTime2Integer) {
                    toReturn = -1;
                } else if(lastPostTime1Integer == lastPostTime2Integer) {
                    toReturn = 0;
                } else {
                    toReturn = 1;
                }
            } else {
                if(lastPostTime1Period.equals("minute")) {
                    toReturn = -1;
                } else if(lastPostTime1Period.equals("hour")) {
                    if(lastPostTime2Period.equals("minute")) {
                        toReturn = 1;
                    } else {
                        toReturn = -1;
                    }
                } else if(lastPostTime1Period.equals("day")) {
                    if(lastPostTime2Period.equals("minute") || lastPostTime2Period.equals("hour")) {
                        toReturn = 1;
                    } else {
                        toReturn = -1;
                    }
                } else if(lastPostTime1Period.equals("week")) {
                    if(lastPostTime2Period.equals("minute") || lastPostTime2Period.equals("hour") ||
                            lastPostTime2Period.equals("day")) {
                        toReturn = 1;
                    } else {
                        toReturn = -1;
                    }
                } else if(lastPostTime1Period.equals("month")) {
                    if(lastPostTime2Period.equals("minute") || lastPostTime2Period.equals("hour") ||
                            lastPostTime2Period.equals("day") || lastPostTime2Period.equals("week")) {
                        toReturn = 1;
                    } else {
                        toReturn = -1;
                    }
                } else {
                    toReturn = 1;
                }
            }

            return toReturn;
        }

        private String getPeriodFromPostTimeString(String postTimeString) {
            String period = "unknown";

            if(postTimeString.contains("minute")) {
                period = "minute";
            } else if(postTimeString.contains("hour")) {
                period = "hour";
            } else if(postTimeString.contains("day")) {
                period = "day";
            } else if(postTimeString.contains("week")) {
                period = "week";
            } else if(postTimeString.contains("month")) {
                period = "month";
            } else if(postTimeString.contains("year")) {
                period = "year";
            }

            return period;
        }
    }

    private String addSubscriptToPostTimeString(String timeString) {
        String timeStringWithSubscript;

        if(timeString.contains("minute")) {
            timeStringWithSubscript = "1) " + timeString;
        } else if(timeString.contains("hour")) {
            timeStringWithSubscript = "2) " + timeString;
        } else if(timeString.contains("day")) {
            timeStringWithSubscript = "3) " + timeString;
        } else if(timeString.contains("week")) {
            timeStringWithSubscript = "4) " + timeString;
        } else if(timeString.contains("month")) {
            timeStringWithSubscript = "5) " + timeString;
        } else if(timeString.contains("year")) {
            timeStringWithSubscript = "6) " + timeString;
        } else {
            timeStringWithSubscript = "7-weird) " + timeString;
        }

        return timeStringWithSubscript;
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

    private String getFullKortingPostText(String fullHtml, String kortingsWord, String startPostHtmlIndicator,
                                          String endPostHtmlIndicator) throws Exception {
        kortingsWord = kortingsWord.toLowerCase();
        fullHtml = fullHtml.toLowerCase();

        String partOfHtmlBeforeKortingsWord = fullHtml.substring(0, fullHtml.indexOf(kortingsWord));
        String partOfHtmlAfterKortingsWord = fullHtml.substring(fullHtml.indexOf(kortingsWord), fullHtml.length());

        String firstHalfOfKortingPostText = partOfHtmlBeforeKortingsWord.substring
                (partOfHtmlBeforeKortingsWord.lastIndexOf(startPostHtmlIndicator) + 5, partOfHtmlBeforeKortingsWord.length());

        String secondHalfOfKortingPostText = partOfHtmlAfterKortingsWord.substring
                (0, partOfHtmlAfterKortingsWord.indexOf(endPostHtmlIndicator));

        String fullKortingPostText = firstHalfOfKortingPostText + secondHalfOfKortingPostText;

        fullKortingPostText = "storn " + fullKortingPostText;

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
