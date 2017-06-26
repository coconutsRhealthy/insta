package com.lennart.model;

import com.lennart.controller.Controller;

import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by LennartMac on 05/06/17.
 */
public class Words30 {

    private Connection con;
    private double numberOfSites = 59.0;

    public static void main(String[] args) throws Exception {

        //while(true) {
            Words30 words30 = new Words30();
            Map<String, Map<String, List<String>>> dataForAllBuzzWords = words30.compareCurrentWithLastDbEntry();

            if(dataForAllBuzzWords != null) {
                System.out.println("Size of dataForAllBuzzWords; " + dataForAllBuzzWords.size());
                new StoreBuzzwords().storeBuzzwordsInDb(dataForAllBuzzWords);
            }
        //}

////
//        List<Double> list1 = words30.getWordLastNoOWordsAndSitesFromJsoup("bird");
//        List<Double> list2 = words30.getWordLastNoOfWordsAndSitesFromDb("bird");
//
//        System.out.println("wacht");

//        words30.initializeDbConnection();
//        words30.getBuzzWords(words30.getTop50HighestIncreaseWordCountCurrent(), words30.getTop50HighestIncreaseSiteCountCurrent());
//        words30.closeDbConnection();


        //words30.updateDatabase();

        //words30.copyValueOfColumnsToLeft();
    }

    private void updateDatabase() throws Exception {
        Controller controller = new Controller();

        controller.initializeDocuments();

        Map<String, Integer> occurrenceMapMultiple = controller.testCompareMethodeWordMultiplePerSite();
        Map<String, Integer> occurrenceMapSingle = controller.testCompareMethodeWordOncePerSite();

        Map<String, List<Integer>> combinedMap = joinMaps(occurrenceMapMultiple, occurrenceMapSingle);

        clearTable();

        for (Map.Entry<String, List<Integer>> entry : combinedMap.entrySet()) {
            double avNoOccurrences = entry.getValue().get(0) / numberOfSites;
            double avPercentageSites = entry.getValue().get(1) / numberOfSites;

            initializeDbConnection();
            storeOrUpdateWordInDatabase("news_words", entry.getKey(), avNoOccurrences, avPercentageSites);
            closeDbConnection();
        }
    }

    private void clearTable() throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();
        st.executeUpdate("DELETE FROM news_words");
        st.close();
        closeDbConnection();
    }

    private void storeOrUpdateWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        if(isWordInDatabase(database, word)) {
            updateWordInDatabase(database, word, avNoOccurrences, avNoSites);
        } else {
            storeWordInDatabase(database, word, avNoOccurrences, avNoSites);
        }
    }

    public void storeWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO " + database + " (entry, word, av_no_occurr_site, av_no_sites) VALUES ('" + (getHighestIntEntry(database) + 1) + "', '" + word + "', '" + avNoOccurrences + "', '" + avNoSites + "')");
        st.close();
    }

    private void storeBuzzWordInDatabase(String database, String word) throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO " + database + " (entry, date, word) VALUES ('" + (getHighestIntEntry(database) + 1) + "', '" + getCurrentDateTime() + "', '" + word + "')");
        st.close();
    }

    private String getCurrentDateTime() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    private boolean isWordInDatabase(String database, String word) throws Exception{
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + database + " WHERE word = '" + word + "';");

        if(rs.next()) {
            rs.close();
            st.close();
            return true;
        }
        rs.close();
        st.close();
        return false;
    }

    private void updateWordInDatabase(String database, String word, double avNoOccurrences, double avNoSites) throws Exception {
        Statement st = con.createStatement();
        st.executeUpdate("UPDATE " + database + " SET av_no_occurr_site = '" + avNoOccurrences + "', av_no_sites = '" + avNoSites + "' WHERE word = '" + word + "'");
        st.close();
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    private int getHighestIntEntry(String database) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY entry DESC;");
        ResultSet rs = st.executeQuery(sql);

        if(rs.next()) {
            int highestIntEntry = rs.getInt("entry");
            st.close();
            rs.close();
            return highestIntEntry;
        }
        st.close();
        rs.close();
        return 0;
    }

    private Map<String, List<Integer>> joinMaps(Map<String, Integer> noOccurrences, Map<String, Integer> noSites) {
        Map<String, List<Integer>> joinedMaps = new HashMap<>();

        for (Map.Entry<String, Integer> entry : noOccurrences.entrySet()) {
            List<Integer> listOccurrencesAndSites = new ArrayList<>();

            if(noSites.get(entry.getKey()) != null) {
                listOccurrencesAndSites.add(entry.getValue());
                listOccurrencesAndSites.add(noSites.get(entry.getKey()));
            }

            if(listOccurrencesAndSites.get(0) == null || listOccurrencesAndSites.get(1) == null) {
                System.out.println("wacht");
            }

            joinedMaps.put(entry.getKey(), listOccurrencesAndSites);
        }
        return joinedMaps;
    }

    private Map<String, Double> getBuzzWords(Map<String, Double> wordMap, Map<String, Double> siteMap) {
        Map<String, Double> combined = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordMap.entrySet()) {
            if(siteMap.get(entry.getKey()) != null) {
                combined.put(entry.getKey(), entry.getValue());
            }
        }

        for (Map.Entry<String, Double> entry : siteMap.entrySet()) {
            if(combined.get(entry.getKey()) == null) {
                if(wordMap.get(entry.getKey()) != null) {
                    combined.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return HeadlineAnalyst.sortByValue(combined);
    }

    private ResultSet getResultSetFromQuery(String query) throws SQLException {
        Statement st = con.createStatement();
        return st.executeQuery(query);
    }

    private Map<String, Map<String, List<String>>> compareCurrentWithLastDbEntry() throws Exception {
        Controller controller = new Controller();

        try {
            controller.initializeDocuments();
        } catch (Exception e) {
            return null;
        }

        initializeDbConnection();

        Map<String, Double> buzzWords = getBuzzWords(getTop50HighestIncreaseWordCountCurrent(controller), getTop50HighestIncreaseSiteCountCurrent(controller));
        System.out.println("Size buzzwords: "+ buzzWords.size());

        Map<String, Map<String, List<String>>> dataForAllBuzzWords = new NewOwnApproach().getDataForAllBuzzWords(buzzWords, controller);
        closeDbConnection();

        return dataForAllBuzzWords;
    }

    private Map<String, Double> getTop50HighestIncreaseWordCountCurrent(Controller controller) throws Exception {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM news_words;");

        Map<String, Double> map1 = new HashMap<>();
        Map<String, Double> map2 = convertIntegerMapToDoubleWithSiteDivide(controller.testCompareMethodeWordMultiplePerSite());

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("av_no_occurr_site"));
        }

        for (Map.Entry<String, Double> entry : map2.entrySet()) {
            String word = entry.getKey();

            double oldWordPerSite;

            if(map1.get(entry.getKey()) != null) {
                oldWordPerSite = map1.get(entry.getKey());
            } else {
                oldWordPerSite = 0;
            }

            double newWordPerSite = entry.getValue();

            if(oldWordPerSite >= 0.033 || newWordPerSite >= 0.033) {
                wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
            }
        }

        Map<String, Double> filteredMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordIncreaseMap.entrySet()) {
            if(entry.getValue() >= 1.3) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return HeadlineAnalyst.sortByValue(filteredMap);
    }

    private Map<String, Double> getTop50HighestIncreaseSiteCountCurrent(Controller controller) throws Exception {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM news_words;");

        Map<String, Double> map1 = new HashMap<>();
        Map<String, Double> map2 = convertIntegerMapToDoubleWithSiteDivide(controller.testCompareMethodeWordOncePerSite());

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("av_no_sites"));
        }

        for (Map.Entry<String, Double> entry : map2.entrySet()) {
            String word = entry.getKey();

            double oldWordPerSite;

            if(map1.get(entry.getKey()) != null) {
                oldWordPerSite = map1.get(entry.getKey());
            } else {
                oldWordPerSite = 0;
            }

            double newWordPerSite = entry.getValue();

            if(newWordPerSite >= 0.033) {
                wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
            }
        }

        Map<String, Double> filteredMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordIncreaseMap.entrySet()) {
            if(entry.getValue() >= 1.3) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return HeadlineAnalyst.sortByValue(filteredMap);
        //System.out.println("wacht");

    }

    private Map<String, Double> convertIntegerMapToDoubleWithSiteDivide(Map<String, Integer> integerMap) {
        Map<String, Double> doubleMap = new HashMap<>();

        for (Map.Entry<String, Integer> entry : integerMap.entrySet()) {
            double d = (double) entry.getValue();
            doubleMap.put(entry.getKey(), d / numberOfSites);
        }
        return doubleMap;
    }

    private List<Double> getWordLastNoOfWordsAndSitesFromDb(String word) throws Exception {
        List<Double> numbers = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM news_words WHERE word = '" + word + "';");

        rs.next();

        numbers.add(rs.getDouble("30_av_no_occurr_site"));
        numbers.add(rs.getDouble("30_av_no_sites"));

        return numbers;
    }

    private List<Double> getWordLastNoOWordsAndSitesFromJsoup(String word) throws Exception {
        List<Double> numbers = new ArrayList<>();

        Controller controller = new Controller();
        controller.initializeDocuments();

        Map<String, Integer> occurrenceMapMultiple = controller.testCompareMethodeWordMultiplePerSite();
        Map<String, Integer> occurrenceMapSingle = controller.testCompareMethodeWordOncePerSite();

        for (Map.Entry<String, Integer> entry : occurrenceMapMultiple.entrySet()) {
            if(entry.getKey().equals(word)) {
                numbers.add((double) entry.getValue());
            }
        }

        for (Map.Entry<String, Integer> entry : occurrenceMapSingle.entrySet()) {
            if(entry.getKey().equals(word)) {
                numbers.add((double) entry.getValue());
            }
        }
        return numbers;
    }

    private void storeBuzzWordsInDatabase(Map<String, Double> buzzWords, String database) throws Exception {
        for (Map.Entry<String, Double> entry : buzzWords.entrySet()) {
            if(!isWordInDatabase(database, entry.getKey())) {
                storeBuzzWordInDatabase(database, entry.getKey());
            }
        }
    }
}