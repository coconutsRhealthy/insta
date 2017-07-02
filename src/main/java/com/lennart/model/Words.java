package com.lennart.model;

import com.lennart.controller.Controller;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LennartMac on 05/06/17.
 */
public class Words {

    private Connection con;
    private double numberOfSites = 59.0;
//
//    private Map<String, Integer> occurrenceMapMultiple;
//    private Map<String, Integer> occurrenceMapSingle;

//    public static void main(String[] args) throws Exception {
//        Words words = new Words();
//
//        words.initializeDbConnection();
//
////        double a = words.retrieveAvNoOccurrencesFromDatabase("gunman");
////        double b = words.retrieveAvNoOccurrencesFromDatabaseNew("gunman");
////
////        double c = words.retrieveAvPercentageSitesFromDatabase("gunman");
////        double d = words.retrieveAvPercentageSitesFromDatabaseNew("gunman");
//
//        //words.getTop50HighestIncreaseSiteCount();
//
//        words.combine(words.getTop50HighestIncreaseWordCount(), words.getTop50HighestIncreaseSiteCount());
//
//        System.out.println("wacht");
//
//        words.closeDbConnection();
//
//        //words.updateDatabase();
//    }

    private void updateDatabase() throws Exception {
        Controller controller = new Controller();

        controller.initializeDocuments();

        Map<String, Integer> occurrenceMapMultiple = controller.testCompareMethodeWordMultiplePerSite();
        Map<String, Integer> occurrenceMapSingle = controller.testCompareMethodeWordOncePerSite();

        Map<String, List<Integer>> combinedMap = joinMaps(occurrenceMapMultiple, occurrenceMapSingle);

        for (Map.Entry<String, List<Integer>> entry : combinedMap.entrySet()) {
            initializeDbConnection();
            double oldMeasurementCount = (double) retrieveNumberOfMeasurements(entry.getKey());
            double avNoOccurrences = ((entry.getValue().get(0) / numberOfSites) + (oldMeasurementCount * retrieveAvNoOccurrencesFromDatabase(entry.getKey()))) / (oldMeasurementCount + 1);
            double avPercentageSites = ((entry.getValue().get(1) / numberOfSites) + (oldMeasurementCount * retrieveAvPercentageSitesFromDatabase(entry.getKey()))) / (oldMeasurementCount + 1);

            storeOrUpdateWordInDatabase("english_words_new", getHighestIntEntry("english_words_new") + 1, entry.getKey(), (int) oldMeasurementCount + 1, avNoOccurrences, avPercentageSites);
            closeDbConnection();
        }

    }

    private double retrieveAvNoOccurrencesFromDatabase(String word) throws SQLException {
        ResultSet rs = getResultSetFromQuery("SELECT * FROM english_words WHERE word = '" + word + "';");
        if(rs.next()) {
            return rs.getDouble("av_no_occurr_site");
        }
        return 0;
    }

    private double retrieveAvPercentageSitesFromDatabase(String word) throws SQLException {
        ResultSet rs = getResultSetFromQuery("SELECT * FROM english_words WHERE word = '" + word + "';");
        if(rs.next()) {
            return rs.getDouble("av_no_sites_present");
        }
        return 0;
    }

    private double retrieveAvNoOccurrencesFromDatabaseNew(String word) throws SQLException {
        ResultSet rs = getResultSetFromQuery("SELECT * FROM english_words_new WHERE word = '" + word + "';");
        if(rs.next()) {
            return rs.getDouble("av_no_occurr_site");
        }
        return 0;
    }

    private double retrieveAvPercentageSitesFromDatabaseNew(String word) throws SQLException {
        ResultSet rs = getResultSetFromQuery("SELECT * FROM english_words_new WHERE word = '" + word + "';");
        if(rs.next()) {
            return rs.getDouble("av_no_sites_present");
        }
        return 0;
    }

    private int retrieveNumberOfMeasurements(String word) throws SQLException {
        ResultSet rs = getResultSetFromQuery("SELECT * FROM english_words_new WHERE word = '" + word + "';");
        if(rs.next()) {
            return rs.getInt("measurement_count");
        }
        return 0;
    }

    private void testRetrieve() throws Exception {
        initializeDbConnection();
        ResultSet rs = getResultSetFromQuery("SELECT * FROM english_words_new WHERE word = 'you';");

        rs.next();
        double x = rs.getDouble("av_no_occurr_site");

        System.out.println("wacht");
        closeDbConnection();
    }

    private void storeOrUpdateWordInDatabase(String database, int entry, String word, int measurementCount, double avNoOccurrences, double avNoSites) throws SQLException {
        if(isWordInDatabase(word)) {
            updateWordInDatabase(database, word, measurementCount, avNoOccurrences, avNoSites);
        } else {
            storeWordInDatabase(database, entry, word, measurementCount, avNoOccurrences, avNoSites);
        }
    }

    public void storeWordInDatabase(String database, int entry, String word, int measurementCount, double avNoOccurrences, double avNoSites) throws SQLException {
        Statement st = con.createStatement();
        st.executeUpdate("INSERT INTO " + database + " (entry, word, measurement_count, av_no_occurr_site, av_no_sites_present) VALUES ('" + entry + "', '" + word + "', '" + measurementCount + "', '" + avNoOccurrences + "', '" + avNoSites + "')");
    }

    private boolean isWordInDatabase(String word) throws SQLException{
        ResultSet rs = getResultSetFromQuery("SELECT * FROM english_words_new WHERE word = '" + word + "';");
        return rs.next();
    }

    private void updateWordInDatabase(String database, String word, int measurementCount, double avNoOccurrences, double avNoSites) throws SQLException {
        Statement st = con.createStatement();
        st.executeUpdate("UPDATE " + database + " SET measurement_count = '" + measurementCount + "', av_no_occurr_site = '" + avNoOccurrences + "', av_no_sites_present = '" + avNoSites + "' WHERE word = '" + word + "'");
    }



    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/words", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }

    private ResultSet getResultSetFromQuery(String query) throws SQLException {
        Statement st = con.createStatement();
        return st.executeQuery(query);
    }

    private int getHighestIntEntry(String database) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY entry DESC;");
        ResultSet rs = st.executeQuery(sql);

        if(rs.next()) {
            return rs.getInt("entry");
        }
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

    private Map<String, Double> getTop50HighestIncreaseWordCount() throws Exception {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM english_words;");
        ResultSet rs2 = getResultSetFromQuery("SELECT * FROM english_words_new;");

        Map<String, Double> map1 = new HashMap<>();
        Map<String, Double> map2 = new HashMap<>();

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("av_no_occurr_site"));
        }

        while(rs2.next()) {
            map2.put(rs2.getString("word"), rs2.getDouble("av_no_occurr_site"));
        }

        for (Map.Entry<String, Double> entry : map1.entrySet()) {
            String word = entry.getKey();

            if(map2.get(word) != null) {
                double oldWordPerSite = entry.getValue();
                double newWordPerSite = map2.get(word);

                if(oldWordPerSite >= 0.44 || newWordPerSite >= 0.44) {
                    wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
                }
            }
        }

        Map<String, Double> filteredMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordIncreaseMap.entrySet()) {
            if(entry.getValue() >= 1.1 || entry.getValue() <= 0.9) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return HeadlineAnalyst.sortByValue(filteredMap);
        //System.out.println("wacht");
    }

    private Map<String, Double> getTop50HighestIncreaseSiteCount() throws SQLException {
        Map<String, Double> wordIncreaseMap = new HashMap<>();

        ResultSet rs = getResultSetFromQuery("SELECT * FROM english_words;");
        ResultSet rs2 = getResultSetFromQuery("SELECT * FROM english_words_new;");

        Map<String, Double> map1 = new HashMap<>();
        Map<String, Double> map2 = new HashMap<>();

        while(rs.next()) {
            map1.put(rs.getString("word"), rs.getDouble("av_no_sites_present"));
        }

        while(rs2.next()) {
            map2.put(rs2.getString("word"), rs2.getDouble("av_no_sites_present"));
        }

        for (Map.Entry<String, Double> entry : map1.entrySet()) {
            String word = entry.getKey();

            if(map2.get(word) != null) {
                double oldWordPerSite = entry.getValue();
                double newWordPerSite = map2.get(word);

                if(oldWordPerSite >= 0.04 || newWordPerSite >= 0.04) {
                    wordIncreaseMap.put(word, newWordPerSite / oldWordPerSite);
                }
            }
        }

        Map<String, Double> filteredMap = new HashMap<>();

        for (Map.Entry<String, Double> entry : wordIncreaseMap.entrySet()) {
            if(entry.getValue() >= 1.1 || entry.getValue() <= 0.9) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }

        return HeadlineAnalyst.sortByValue(filteredMap);
        //System.out.println("wacht");

    }

    private void combine(Map<String, Double> wordMap, Map<String, Double> siteMap) {
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

        combined = HeadlineAnalyst.sortByValue(combined);
        System.out.println("wacht");

    }
}
