package com.lennart.model;

import java.sql.*;
import java.util.*;

public class SphAnalysis {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new SphAnalysis().printCompaniesPerInfluencer();
    }

    private void printCompaniesPerInfluencer() throws Exception {
        Map<String, Integer> recentDutchInfluencers = new InstaAccountFinder().getRecentInfluencersFromCountry("Netherlands");

        //recentDutchInfluencers.forEach((key, value) -> System.out.println(value));

        Map<String, List<String>> companiesPerInfluencer = new LinkedHashMap<>();

        recentDutchInfluencers.keySet().forEach(key -> companiesPerInfluencer.put(key, new ArrayList<>()));

        initializeDbConnection();

        int counter = 0;

        for (Map.Entry<String, List<String>> entry : companiesPerInfluencer.entrySet()) {
            String influener = entry.getKey();
            System.out.println(counter++);
            Set<String> companies = new HashSet<>();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE date >= '2024-01-01' AND influencer = '" + influener + "';");

            while(rs.next()) {
                companies.add(rs.getString("company"));
            }

            entry.getValue().addAll(companies);

            rs.close();
            st.close();
        }

        closeDbConnection();

        companiesPerInfluencer.values().forEach(list -> {
                    for(int i = 0; i < list.size(); i++) {
                        if(i == list.size() -1) {
                            System.out.print(list.get(i));
                        } else {
                            System.out.print(list.get(i) + ", ");
                        }
                    }

                    System.out.println();
                }
        );
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
