package com.lennart.model.tiktok;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class TikTokAnalysis {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new TikTokAnalysis().getCompaniesThatGaveDiscount();
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

        dutchTiktokUsers = dutchTiktokUsers.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));

        dutchTiktokUsers.keySet().forEach(key -> System.out.println("\"" + key + "\","));

        return dutchTiktokUsers;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
