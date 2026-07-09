package com.lennart.model.tiktok;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class TikTokAnalysis {

    private Connection con;

    public static void main(String[] args) throws Exception {
        TikTokAnalysis tikTokAnalysis = new TikTokAnalysis();
        //tikTokAnalysis.getTiktokkersForNewApifyList();
        //tikTokAnalysis.getCompaniesThatGaveDiscount();
        //tikTokAnalysis.updateDbForUsersWhoGaveDiscount();
        //tikTokAnalysis.getTiktokkersForNewApifyList();
        tikTokAnalysis.getTiktokkersForNewApifyList();
        //tikTokAnalysis.printTiktokkersAddedOnDateForApify("2025-07-30");
    }

    private void getTiktokkersForNewApifyList() throws Exception {
        Map<String, Integer> tikTokkersFromDb1 = getTikTokkersFromDb("Netherlands", "true", "2024-05-19");
        Map<String, Integer> tikTokkersFromDb2 = getTikTokkersFromDb("Netherlands", "true", "2024-07-17");
        Map<String, Integer> tikTokkersFromDb3 = getTikTokkersFromDb("Netherlands", "true", "2024-07-18");
        Map<String, Integer> tikTokkersFromDb4 = getTikTokkersFromDb("Netherlands", "true", "2024-10-15");
        Map<String, Integer> tikTokkersFromDb5 = getTikTokkersFromDb("Netherlands", "true", "2025-01-07");
        Map<String, Integer> tikTokkersFromDb6 = getTikTokkersFromDb("Netherlands", "true", "2025-04-13");
        Map<String, Integer> tikTokkersFromDb7 = getTikTokkersFromDb("Netherlands", "true", "2025-07-30");
        Map<String, Integer> tikTokkersFromDb8 = getTikTokkersFromDb("Netherlands", "", "2026-04-20");

        int minimumFollowersForNewUsers = 1000;
        tikTokkersFromDb4.entrySet().removeIf(entry -> entry.getValue() < minimumFollowersForNewUsers);

        Map<String, Integer> combined = new HashMap<>();
        combined.putAll(tikTokkersFromDb1);
        combined.putAll(tikTokkersFromDb2);
        combined.putAll(tikTokkersFromDb3);
        combined.putAll(tikTokkersFromDb4);
        combined.putAll(tikTokkersFromDb5);
        combined.putAll(tikTokkersFromDb6);
        combined.putAll(tikTokkersFromDb7);
        tikTokkersFromDb8.entrySet().stream()
                .filter(e -> ThreadLocalRandom.current().nextDouble() < 0.37)
                .forEach(e -> combined.put(e.getKey(), e.getValue()));

        Map<String, Integer> sortedCombined = sortByValueHighToLow(combined);

        StringBuilder sb = new StringBuilder();
        sortedCombined.keySet().forEach(key -> sb.append(key).append("\n"));
        System.out.print(sb);
        StringSelection selection = new StringSelection(sb.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    private void updateDbForUsersWhoGaveDiscount() throws Exception {
        List<String> usersThatGaveDiscount = new ArrayList<>(getTiktokUsersThatGaveDiscount().keySet());

        TikTokInfluencerPersister tikTokInfluencerPersister = new TikTokInfluencerPersister();

        usersThatGaveDiscount.forEach(user -> {
            user = user.replace("_tiktok", "");

            try {
                tikTokInfluencerPersister.setGaveDiscount(user, "true");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private Map<String, List<String>> getTiktokUsersThatGaveDiscount() throws Exception {
        Map<String, List<String>> usersThatGaveDiscount = new TreeMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts;");

        while(rs.next()) {
            String influencer = rs.getString("influencer");

            if(influencer.contains("_tiktok")) {
                usersThatGaveDiscount.putIfAbsent(influencer, new ArrayList<>());
                usersThatGaveDiscount.get(influencer).add(rs.getString("company"));
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        return usersThatGaveDiscount;
    }

    private Map<String, Set<String>> getCompaniesThatGaveDiscount() throws Exception {
        Map<String, Set<String>> companiesThatGaveDiscount = new TreeMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts;");

        while(rs.next()) {
            String influencer = rs.getString("influencer");

            if(influencer.contains("_tiktok")) {
                String company = rs.getString("company");
                companiesThatGaveDiscount.putIfAbsent(company, new HashSet<>());
                companiesThatGaveDiscount.get(company).add(influencer);
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        companiesThatGaveDiscount.keySet().forEach(System.out::println);

        return companiesThatGaveDiscount;
    }

    private Map<String, Integer> getDutchTiktokUsers() throws Exception {
        Map<String, Integer> dutchTiktokUsers = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM tiktok_influencers WHERE country LIKE '%Netherlands%';");

        while(rs.next()) {
            dutchTiktokUsers.put(rs.getString("name"), rs.getInt("followers"));
        }

        rs.close();
        st.close();

        closeDbConnection();

        dutchTiktokUsers = sortByValueHighToLow(dutchTiktokUsers);

        dutchTiktokUsers.keySet().forEach(key -> System.out.println("\"" + key + "\","));

        return dutchTiktokUsers;
    }

    private Map<String, Integer> getTikTokkersFromDb(String country, String gaveDiscount, String dateAdded) throws Exception {
        Map<String, Integer> tikTokkers = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM tiktok_influencers WHERE country LIKE '%" + country +
                "%' AND gave_discount = '" + gaveDiscount + "' AND date_added = '" + Date.valueOf(dateAdded) + "';");

        while(rs.next()) {
            tikTokkers.put(rs.getString("name"), rs.getInt("followers"));
        }

        rs.close();
        st.close();

        closeDbConnection();

        tikTokkers = sortByValueHighToLow(tikTokkers);
        return tikTokkers;
    }

    private List<String> getNewTiktokkersThatGaveDiscount() throws Exception {
        List<String> newTiktokkers = new ArrayList<>(getTikTokkersFromDb("Netherlands", "", "2024-07-17").keySet());
        newTiktokkers.addAll(new ArrayList<>(getTikTokkersFromDb("Netherlands", "", "2024-07-18").keySet()));
        List<String> usersThatGaveDiscount = new ArrayList<>(getTiktokUsersThatGaveDiscount().keySet());
        usersThatGaveDiscount = usersThatGaveDiscount.stream().map(user -> user.replaceAll("_tiktok", "")).collect(Collectors.toList());
        newTiktokkers.retainAll(usersThatGaveDiscount);
        return newTiktokkers;
    }

    private Map<String, Integer> sortByValueHighToLow(Map<String, Integer> mapToSort) {
        return mapToSort.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    private void printTiktokkersAddedOnDateForApify(String dateAdded) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM tiktok_influencers WHERE date_added = '" + Date.valueOf(dateAdded) + "';");

        List<String> newTiktokkers = new ArrayList<>();

        while(rs.next()) {
            newTiktokkers.add(rs.getString("name"));
        }

        rs.close();
        st.close();

        closeDbConnection();

        Collections.sort(newTiktokkers);

        newTiktokkers.forEach(tiktokker -> {
            System.out.println("\"" + tiktokker + "\",");
        });
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
