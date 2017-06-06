package com.lennart.model;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * Created by LennartMac on 31/05/17.
 */
public class HeadlineAnalyst {

    private Connection con;

    public Map<String, Integer> getWordsRankedByOccurrence(List<String> allHeadlines) {

        Map<String, Integer> wordsRankedByOccurence = new HashMap<>();

        List<String> allWords = new ArrayList<>();

        for(String headline : allHeadlines) {
            headline = headline.toLowerCase();
            allWords.addAll(Arrays.asList(headline.split(" ")));
        }

        for(String word : allWords) {
            if(wordsRankedByOccurence.get(word) == null) {
                int frequency = Collections.frequency(allWords, word);
                wordsRankedByOccurence.put(word, frequency);
            }
        }

        wordsRankedByOccurence = clearMapOfCommonWords(wordsRankedByOccurence);
        wordsRankedByOccurence = sortByValue(wordsRankedByOccurence);

        return null;
    }

    public Map<String, Integer> getTwoSubsequentWordsRanksByOccurence(List<String> allHeadlines) {
        Map<String, Integer> twoSubsequentWordsRankedByOccurence = new HashMap<>();

        List<String> allTwoSubsequentWords = new ArrayList<>();

        for(String headline : allHeadlines) {
            headline = headline.toLowerCase();

            List<String> headlineWords = Arrays.asList(headline.split(" "));

            for(int i = 0; i < headlineWords.size() - 1; i++) {
                String twoSubsequentWords = headlineWords.get(i) + " " + headlineWords.get(i + 1);
                allTwoSubsequentWords.add(twoSubsequentWords);
            }
        }

        for(String twoSubsequentWords : allTwoSubsequentWords) {
            if(twoSubsequentWordsRankedByOccurence.get(twoSubsequentWords) == null) {
                int frequency = Collections.frequency(allTwoSubsequentWords, twoSubsequentWords);
                twoSubsequentWordsRankedByOccurence.put(twoSubsequentWords, frequency);
            }
        }
        return sortByValue(twoSubsequentWordsRankedByOccurence);
    }

    public static void main(String[] args) throws Exception {
//        String a = "2017-06-01 12:35:18";
//        String b = "2017-06-01 13:00:00";
//
//        Date aa = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(a);
//        Date bb = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(b);
//
//        if(aa.before(bb)) {
//
//        }




        HeadlineAnalyst headlineAnalyst = new HeadlineAnalyst();

        headlineAnalyst.getNumberOfHeadlinesUntilHourForDate("2017-06-01");

        //headlineAnalyst.getDomesticForeignDistribution("2017-05-31");
//
//        headlineAnalyst.retrieveAllDateTimeFromDatabaseForDate("2017-06-01");
        //headlineAnalyst.getWordsRankedByOccurrence(headlineAnalyst.retrieveAllHeadlinesFromDatabaseForDate("2017-06-01"));
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue() ).compareTo( o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private List<String> retrieveAllHeadlinesFromDatabaseForDate(String date) throws Exception {
        List<String> allHeadlinesFromDatabase = new ArrayList<>();

        initializeDbConnection();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM nu_nl_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allHeadlinesFromDatabase.addAll(getSiteHeadlines(rs));

        rs = getResultSetFromQuery("SELECT * FROM nos_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allHeadlinesFromDatabase.addAll(getSiteHeadlines(rs));

        rs = getResultSetFromQuery("SELECT * FROM ad_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allHeadlinesFromDatabase.addAll(getSiteHeadlines(rs));

        rs = getResultSetFromQuery("SELECT * FROM telegraaf_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allHeadlinesFromDatabase.addAll(getSiteHeadlines(rs));

        rs = getResultSetFromQuery("SELECT * FROM volkskrant_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allHeadlinesFromDatabase.addAll(getSiteHeadlines(rs));

        closeDbConnection();

        return allHeadlinesFromDatabase;
    }

    private List<String> retrieveAllDateTimeFromDatabaseForDate(String date) throws Exception {
        List<String> allDateTimeFromDatabase = new ArrayList<>();

        initializeDbConnection();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM nu_nl_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allDateTimeFromDatabase.addAll(getSiteDateTimes(rs));

        rs = getResultSetFromQuery("SELECT * FROM nos_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allDateTimeFromDatabase.addAll(getSiteDateTimes(rs));

        rs = getResultSetFromQuery("SELECT * FROM ad_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allDateTimeFromDatabase.addAll(getSiteDateTimes(rs));

        rs = getResultSetFromQuery("SELECT * FROM telegraaf_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allDateTimeFromDatabase.addAll(getSiteDateTimes(rs));

        rs = getResultSetFromQuery("SELECT * FROM volkskrant_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allDateTimeFromDatabase.addAll(getSiteDateTimes(rs));

        closeDbConnection();

        return allDateTimeFromDatabase;
    }

    private List<String> retrieveAllLinksFromDatabaseForDate(String date) throws Exception {
        List<String> allLinksFromDatabase = new ArrayList<>();

        initializeDbConnection();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM nu_nl_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allLinksFromDatabase.addAll(getSiteLinks(rs));

        //no nos, doesn't have it in its links

        rs = getResultSetFromQuery("SELECT * FROM ad_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allLinksFromDatabase.addAll(getSiteLinks(rs));

        rs = getResultSetFromQuery("SELECT * FROM telegraaf_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allLinksFromDatabase.addAll(getSiteLinks(rs));

        rs = getResultSetFromQuery("SELECT * FROM volkskrant_headlines WHERE date LIKE '%" + date + "%' ORDER BY date;");
        allLinksFromDatabase.addAll(getSiteLinks(rs));

        closeDbConnection();

        return allLinksFromDatabase;
    }

    private List<String> getSiteHeadlines(ResultSet rs) throws SQLException {
        List<String> siteHeadlines = new ArrayList<>();
        while(rs.next()) {
            String retrievedString = rs.getString("headline").replace("'", "''");
            retrievedString = retrievedString.replaceAll("[^A-Za-z0-9 ]", "");
            siteHeadlines.add(retrievedString);
        }
        return siteHeadlines;
    }

    private List<String> getSiteDateTimes(ResultSet rs) throws SQLException {
        List<String> siteDateTimes = new ArrayList<>();
        while(rs.next()) {
            String retrievedString = rs.getString("date");
            siteDateTimes.add(retrievedString);
        }
        return siteDateTimes;
    }

    private List<String> getSiteLinks(ResultSet rs) throws SQLException {
        List<String> siteLinks = new ArrayList<>();
        while(rs.next()) {
            String retrievedString = rs.getString("link");
            siteLinks.add(retrievedString);
        }
        return siteLinks;
    }

    private Map<String, Integer> clearMapOfCommonWords(Map<String, Integer> unClearedMap) {
        Map<String, Integer> clearedMap = new HashMap<>();
        List<String> commonWords = new ArrayList<>();

        commonWords.add("in");
        commonWords.add("van");
        commonWords.add("op");
        commonWords.add("voor");
        commonWords.add("en");
        commonWords.add("met");
        commonWords.add("de");
        commonWords.add("bij");
        commonWords.add("om");
        commonWords.add("door");
        commonWords.add("na");
        commonWords.add("is");
        commonWords.add("het");
        commonWords.add("nieuwe");
        commonWords.add("uit");
        commonWords.add("zich");
        commonWords.add("wil");
        commonWords.add("dood");
        commonWords.add("aan");
        commonWords.add("niet");
        commonWords.add("vs");
        commonWords.add("te");
        commonWords.add("cel");
        commonWords.add("jaar");
        commonWords.add("een");
        commonWords.add("voorkomen");
        commonWords.add("zaak");
        commonWords.add("man");
        commonWords.add("meer");
        commonWords.add("dat");
        commonWords.add("krijgt");
        commonWords.add("geen");
        commonWords.add("moet");
        commonWords.add("tegen");
        commonWords.add("als");
        commonWords.add("naar");
        commonWords.add("weer");
        commonWords.add("eigen");
        commonWords.add("dit");
        commonWords.add("nederlandse");
        commonWords.add("gaat");
        commonWords.add("maar");
        commonWords.add("gewonden");
        commonWords.add("doden");
        commonWords.add("over");
        commonWords.add("er");
        commonWords.add("af");
        commonWords.add("den");
        commonWords.add("opnieuw");
        commonWords.add("zonder");
        commonWords.add("gevonden");
        commonWords.add("witte");
        commonWords.add("deze");
        commonWords.add("zeggen");
        commonWords.add("ook");
        commonWords.add("nu");
        commonWords.add("nodig");
        commonWords.add("die");
        commonWords.add("wordt");
        commonWords.add("eerst");
        commonWords.add("grote");
        commonWords.add("alsnog");
        commonWords.add("kost");
        commonWords.add("wat");
        commonWords.add("schokt");
        commonWords.add("alle");
        commonWords.add("nog");
        commonWords.add("dode");
        commonWords.add("stapt");
        commonWords.add("tijd");
        commonWords.add("doet");
        commonWords.add("eist");
        commonWords.add("vrouw");
        commonWords.add("eis");
        commonWords.add("toch");
        commonWords.add("schept");
        commonWords.add("trekt");
        commonWords.add("dicht");
        commonWords.add("aantal");
        commonWords.add("vindt");
        commonWords.add("ze");
        commonWords.add("dan");
        commonWords.add("");
        commonWords.add("liveblog");
        commonWords.add("verdachte");
        commonWords.add("minder");
        commonWords.add("zijn");
        commonWords.add("zal");
        commonWords.add("onder");

        boolean entryShouldBeRemoved = false;

        for (Map.Entry<String, Integer> entry : unClearedMap.entrySet()) {
            for(String commonWord : commonWords) {
                if(entry.getKey().equals(commonWord)) {
                    entryShouldBeRemoved = true;
                    break;
                }
            }

            if(!entryShouldBeRemoved) {
                clearedMap.put(entry.getKey(), entry.getValue());
            }

            entryShouldBeRemoved = false;
        }
        return clearedMap;
    }

    private Map<Integer, Integer> getNumberOfHeadlinesPerHourForDate(String date) throws Exception {
        Map<Integer, Integer> numberOfHeadlinesPerHour = new LinkedHashMap<>();

        for(int i = 0; i < 24; i++) {
            numberOfHeadlinesPerHour.put(i, 0);
        }

        List<String> allDateTimes = retrieveAllDateTimeFromDatabaseForDate(date);

        for(String dateTime : allDateTimes) {
            Date parsedDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);

            if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 01:00:00"))) {
                numberOfHeadlinesPerHour.put(0, numberOfHeadlinesPerHour.get(0) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 02:00:00"))) {
                numberOfHeadlinesPerHour.put(1, numberOfHeadlinesPerHour.get(1) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 03:00:00"))) {
                numberOfHeadlinesPerHour.put(2, numberOfHeadlinesPerHour.get(2) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 04:00:00"))) {
                numberOfHeadlinesPerHour.put(3, numberOfHeadlinesPerHour.get(3) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 05:00:00"))) {
                numberOfHeadlinesPerHour.put(4, numberOfHeadlinesPerHour.get(4) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 06:00:00"))) {
                numberOfHeadlinesPerHour.put(5, numberOfHeadlinesPerHour.get(5) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 07:00:00"))) {
                numberOfHeadlinesPerHour.put(6, numberOfHeadlinesPerHour.get(6) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 08:00:00"))) {
                numberOfHeadlinesPerHour.put(7, numberOfHeadlinesPerHour.get(7) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 09:00:00"))) {
                numberOfHeadlinesPerHour.put(8, numberOfHeadlinesPerHour.get(8) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 10:00:00"))) {
                numberOfHeadlinesPerHour.put(9, numberOfHeadlinesPerHour.get(9) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 11:00:00"))) {
                numberOfHeadlinesPerHour.put(10, numberOfHeadlinesPerHour.get(10) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 12:00:00"))) {
                numberOfHeadlinesPerHour.put(11, numberOfHeadlinesPerHour.get(11) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 13:00:00"))) {
                numberOfHeadlinesPerHour.put(12, numberOfHeadlinesPerHour.get(12) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 14:00:00"))) {
                numberOfHeadlinesPerHour.put(13, numberOfHeadlinesPerHour.get(13) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 15:00:00"))) {
                numberOfHeadlinesPerHour.put(14, numberOfHeadlinesPerHour.get(14) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 16:00:00"))) {
                numberOfHeadlinesPerHour.put(15, numberOfHeadlinesPerHour.get(15) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 17:00:00"))) {
                numberOfHeadlinesPerHour.put(16, numberOfHeadlinesPerHour.get(16) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 18:00:00"))) {
                numberOfHeadlinesPerHour.put(17, numberOfHeadlinesPerHour.get(17) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 19:00:00"))) {
                numberOfHeadlinesPerHour.put(18, numberOfHeadlinesPerHour.get(18) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 20:00:00"))) {
                numberOfHeadlinesPerHour.put(19, numberOfHeadlinesPerHour.get(19) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 21:00:00"))) {
                numberOfHeadlinesPerHour.put(20, numberOfHeadlinesPerHour.get(20) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 22:00:00"))) {
                numberOfHeadlinesPerHour.put(21, numberOfHeadlinesPerHour.get(21) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 23:00:00"))) {
                numberOfHeadlinesPerHour.put(22, numberOfHeadlinesPerHour.get(22) + 1);
            } else if(parsedDateTime.before(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date + " 23:59:59"))) {
                numberOfHeadlinesPerHour.put(23, numberOfHeadlinesPerHour.get(23) + 1);
            }
        }

        return numberOfHeadlinesPerHour;
    }

    public Map<Integer, Integer> getNumberOfHeadlinesUntilHourForDate(String date) throws Exception {
        Map<Integer, Integer> numberOfHeadlinesUntilHour = new LinkedHashMap<>();
        Map<Integer, Integer> numberOfHeadlinesPerHour = getNumberOfHeadlinesPerHourForDate(date);

        int cumulative = 0;

        for (Map.Entry<Integer, Integer> entry : numberOfHeadlinesPerHour.entrySet()) {
            cumulative = cumulative + entry.getValue();
            numberOfHeadlinesUntilHour.put(entry.getKey(), cumulative);
        }

        return numberOfHeadlinesUntilHour;
    }

    public Map<String, Integer> getDomesticForeignDistribution(String date) throws Exception {
        Map<String, Integer> domesticForeign = new HashMap<>();
        List<String> allLinks = retrieveAllLinksFromDatabaseForDate(date);

        domesticForeign.put("binnenland", 0);
        domesticForeign.put("buitenland", 0);
        domesticForeign.put("overig", 0);

        for(String link : allLinks) {
            if(link.contains("/binnenland/")) {
                domesticForeign.put("binnenland", domesticForeign.get("binnenland") + 1);
            } else if(link.contains("/buitenland/")) {
                domesticForeign.put("buitenland", domesticForeign.get("buitenland") + 1);
            } else {
                domesticForeign.put("overig", domesticForeign.get("overig") + 1);
            }
        }
        return domesticForeign;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/headlines?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    private ResultSet getResultSetFromQuery(String query) throws SQLException {
        Statement st = con.createStatement();
        return st.executeQuery(query);
    }
}
