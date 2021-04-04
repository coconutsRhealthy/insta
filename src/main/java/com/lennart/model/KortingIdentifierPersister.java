package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 01/12/2020.
 */
public class KortingIdentifierPersister {

    private Connection con;

    public static void main(String[] args) throws Exception {
        KortingIdentifierPersister kortingIdentifierPersister = new KortingIdentifierPersister();
        InstaAccounts instaAccounts = new InstaAccounts();

        while(true) {
            kortingIdentifierPersister.continuousRunKorting(instaAccounts);
        }
    }

    private void continuousRunKorting(InstaAccounts instaAccounts) throws Exception {
        List<String> users = instaAccounts.getAllInstaAccounts();

        int counter = 0;

        for(String user : users) {
            try {
                nightlyRunLogic(counter, user, "picuki", "<div class=\"time\">", "alt=", "\">");
            } catch (Exception z) {
                System.out.println("picuki error!");

                try {
                    nightlyRunLogic(counter, user, "instajust", "<div class=\"article_time\">", "<span>", "</span>");
                } catch (Exception y) {
                    System.out.println("ERROR complete!!!");
                    updateKortingDb("error", "error", "stipo error", user, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(5000)));
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
        String fullKortingsWordText = "stipo none";
        String dateOfPost = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(5000));
        String company = "dunno";
        String kortingsCode = "dunno";

        if(!lastPostTimesPerKortingsWord.isEmpty()) {
            String lastPostTimeWithoutSubscript = lastPostTimesPerKortingsWord.values().stream().collect(Collectors.toList()).get(0);
            kortingsWord = lastPostTimesPerKortingsWord.keySet().stream().collect(Collectors.toList()).get(0);
            lastPostTimeToUse = addSubscriptToPostTimeString(lastPostTimeWithoutSubscript);
            fullKortingsWordText = getFullKortingPostText(fullHtmlForUser, kortingsWord,
                    startKortingPostHtmlIndicator, endKortingPostHtmlIndicator);
            dateOfPost = getDateFromTimeString(lastPostTimeWithoutSubscript);
            fullKortingsWordText = cleanHtmlFromDiscountPosttext(fullKortingsWordText);
            company = identifyCompaniesThatGiveDiscount(fullKortingsWordText);
            kortingsCode = identifyDiscountCode(fullKortingsWordText);
        }

        updateKortingDb(kortingsWord, lastPostTimeToUse, fullKortingsWordText, user, dateOfPost);
        updateKortingDbAllKortingTable(user, kortingsWord, fullKortingsWordText, dateOfPost, company, kortingsCode);
    }

    private void updateKortingDb(String kortingsWord, String lastKortingPostTime, String kortingPostFullText,
                                 String username, String dateOfPost) throws Exception {
        kortingsWord = replaceUnwantedCharacters(kortingsWord);
        lastKortingPostTime = replaceUnwantedCharacters(lastKortingPostTime);
        kortingPostFullText = replaceUnwantedCharacters(kortingPostFullText);
        username = replaceUnwantedCharacters(username);
        dateOfPost = replaceUnwantedCharacters(dateOfPost);

        try {
            initializeDbConnection();

            Statement st = con.createStatement();
            st.executeUpdate("UPDATE followers2 SET " +
                    "last_korting = '" + lastKortingPostTime + "', " +
                    "kortingsword = '" + kortingsWord + "', " +
                    "kortingsword_post_fulltext = '" + kortingPostFullText + "', " +
                    "date_of_post = '" + dateOfPost + "' " +
                    "WHERE username = '" + username + "'");
            st.close();

            closeDbConnection();
        } catch (Exception e) {
            System.out.println("OLD DB ERROR!");
            e.printStackTrace();
        }
    }

    private void updateKortingDbAllKortingTable(String username, String kortingsWord, String kortingPostFullText,
                                                String dateOfPost, String company, String discountWord) throws Exception {
        username = replaceUnwantedCharacters(username);
        kortingsWord = replaceUnwantedCharacters(kortingsWord);
        kortingPostFullText = replaceUnwantedCharacters(kortingPostFullText);
        dateOfPost = replaceUnwantedCharacters(dateOfPost);
        company = replaceUnwantedCharacters(company);
        discountWord = replaceUnwantedCharacters(discountWord);

        try {
            initializeDbConnection();

            Statement st = con.createStatement();
            st.executeUpdate("INSERT INTO all_korting (" +
                    "username, kortingsword, kortingsword_post_fulltext, date_of_post, company, kortingscode) " +
                    "VALUES (" +
                    "'" + username + "', '"
                    + kortingsWord + "', '"
                    + kortingPostFullText + "', '"
                    + dateOfPost + "', '"
                    + company + "', '"
                    + discountWord + "') " +
                    "ON DUPLICATE KEY UPDATE username=username;");
            st.close();

            closeDbConnection();
        } catch (Exception e) {
            System.out.println("NEW DB ERROR!");
            e.printStackTrace();
        }
    }

    private String getFullHtmlForUsername(String siteName, String username) throws Exception {
        Document document = Jsoup.connect("https://www." + siteName + ".com/profile/" + username).get();
        return document.html();
    }

    private Set<String> identifyKortingWordsUsed(String fullHtml) throws Exception {
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

    private Map<String, String> getKortingWordLastPostTimes(String bodyText, Set<String> kortingsWordsOnPage,
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

        fullKortingPostText = "stipo " + fullKortingPostText;

        return fullKortingPostText;
    }

    private String getDateFromTimeString(String timeString) {
        int timeStringInteger = Integer.valueOf(timeString.substring(0, timeString.indexOf(" ")));

        Date dateOfPost;

        if(timeString.contains("minute")) {
            dateOfPost = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
                    - TimeUnit.MINUTES.toMillis(timeStringInteger));
        } else if(timeString.contains("hour")) {
            dateOfPost = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
                    - TimeUnit.HOURS.toMillis(timeStringInteger));
        } else if(timeString.contains("day")) {
            dateOfPost = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
                    - TimeUnit.DAYS.toMillis(timeStringInteger));
        } else if(timeString.contains("week")) {
            dateOfPost = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
                    - TimeUnit.DAYS.toMillis(timeStringInteger * 7));
        } else if(timeString.contains("month")) {
            dateOfPost = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
                    - TimeUnit.DAYS.toMillis(timeStringInteger * 30));
        } else if(timeString.contains("year")) {
            dateOfPost = new Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1)
                    - TimeUnit.DAYS.toMillis(timeStringInteger * 365));
        } else {
            dateOfPost = new Date(0);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(dateOfPost);
    }

    private String cleanHtmlFromDiscountPosttext(String fullDiscountPosttext) {
        String cleanedPostText = fullDiscountPosttext.replaceAll("<.*?>", "");
        return cleanedPostText;
    }

    private String identifyCompaniesThatGiveDiscount(String fullDiscountPosttext) {
        List<String> companies = new ArrayList<>();

        while(fullDiscountPosttext.contains("@") && fullDiscountPosttext.length() > fullDiscountPosttext.indexOf("@") + 1) {
            fullDiscountPosttext = fullDiscountPosttext.substring(fullDiscountPosttext.indexOf("@") + 1, fullDiscountPosttext.length());

            String company;

            if(fullDiscountPosttext.contains(" ")) {
                company = fullDiscountPosttext.substring(0, fullDiscountPosttext.indexOf(" "));
            } else {
                company = fullDiscountPosttext;
            }

            if(company.endsWith(".") || company.endsWith(",") || company.endsWith("!") || company.endsWith("?")) {
                company = company.substring(0, company.length() - 1);
            }


            companies.add(company);
        }

        String companiesDbString = "";

        if(!companies.isEmpty()) {
            if(companies.size() > 1) {
                for(String company : companies) {
                    companiesDbString = companiesDbString + ", " + company;
                }
            } else {
                companiesDbString = companies.get(0);
            }
        }

        if(companiesDbString.startsWith(", ")) {
            companiesDbString = companiesDbString.replaceFirst(", ", "");
        }

        if(companiesDbString.equals("")) {
            companiesDbString = "nothingFound";
        }

        return companiesDbString;
    }

    private String identifyDiscountCode(String fullDiscountPosttext) {
        String code = "nothingFound";

        if(fullDiscountPosttext.toLowerCase().contains("code")) {
            code = fullDiscountPosttext.substring(fullDiscountPosttext.indexOf("code") + 4, fullDiscountPosttext.length());

            if(code.startsWith(" ")) {
                code = code.replaceFirst(" ", "");
            }

            if(code.contains(" ")) {
                code = code.substring(0, code.indexOf(" "));
            }
        }

        return code;
    }

    private String replaceUnwantedCharacters(String baseString) {
        String replacedString = baseString;

        replacedString = replacedString.replace("&amp;", "en");
        replacedString = replacedString.replace("&", "en");
        replacedString = replacedString.replace("'", "");

        return replacedString;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/influencers", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
