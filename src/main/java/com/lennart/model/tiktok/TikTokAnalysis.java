package com.lennart.model.tiktok;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

public class TikTokAnalysis {

    private Connection con;

    public static void main(String[] args) throws Exception {
        TikTokAnalysis tikTokAnalysis = new TikTokAnalysis();
        tikTokAnalysis.getTiktokkersForNewApifyList();
    }

    private void getTiktokkersForNewApifyList() throws Exception {
        Map<String, Integer> tikTokkersFromDb1 = getTikTokkersFromDb("Netherlands", "true", "2024-05-19");
        Map<String, Integer> tikTokkersFromDb2 = getTikTokkersFromDb("Netherlands", "", "2024-07-17");
        Map<String, Integer> tikTokkersFromDb3 = getTikTokkersFromDb("Netherlands", "", "2024-07-18");

        Map<String, Integer> combined = new HashMap<>();
        combined.putAll(tikTokkersFromDb1);
        combined.putAll(tikTokkersFromDb2);
        combined.putAll(tikTokkersFromDb3);

        combined = sortByValueHighToLow(combined);
        combined.keySet().forEach(key -> System.out.println("\"" + key + "\","));
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
