package com.lennart.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.sql.*;
import java.util.List;

public class InfluencerPersister {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new InfluencerPersister().getAndPrintAllInfluencersFromDb();
    }

    private void initializeInfluencerDb() throws Exception {
        List<String> influencers = new InstaAccountFinder().getInfluencers("2019-01-01", "2025-12-31");

        initializeDbConnection();

        int counter = 0;

        for(String influencer : influencers) {
            int followers = getFollowersForUsernameFromJson(influencer);

            if(followers != -1) {
                Statement st = con.createStatement();

                st.executeUpdate("INSERT INTO influencers (" +
                        "name, " +
                        "followers) " +
                        "VALUES ('" +
                        influencer + "', '" +
                        followers + "'" +
                        ")");
                st.close();
            }

            System.out.println(counter++);
        }

        closeDbConnection();
    }

    private int getFollowersForUsernameFromJson(String username) throws Exception {
        JSONParser jsonParser = new JSONParser();

        JSONArray apifyData = (JSONArray) jsonParser.parse(
                new FileReader("/Users/lennartmac/Downloads/follower_count.json"));

        for(Object apifyDataElement : apifyData) {
            JSONObject followersJson = (JSONObject) apifyDataElement;
            String jsonUsername = (String) followersJson.get("userName");

            if(jsonUsername != null && jsonUsername.equals(username)) {
                Long followersCountLong = (Long) followersJson.get("followersCount");
                return Math.toIntExact(followersCountLong);
            }
        }

        return -1;
    }

    private void getAndPrintAllInfluencersFromDb() throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM influencers;");

        while(rs.next()) {
            System.out.println("\"https://www.instagram.com/" + rs.getString("name") + "\",");
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    public String getCountry(String influencer) throws Exception {
        String country = "";

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM influencers WHERE name = '" + influencer + "';");

        if(rs.next()) {
            country = rs.getString("country");
        }

        rs.close();
        st.close();

        closeDbConnection();

        return country;
    }

    public void setCountry(String username, String country) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();

        try {
            st.executeUpdate("UPDATE influencers SET country = '" + country + "' WHERE name = '" + username + "';");
        } catch (SQLSyntaxErrorException e) {
            e.printStackTrace();
            System.out.println("SQL syntax error! Query: ");
            System.out.println("UPDATE influencers SET country = '" + country + "' WHERE name = '" + username + "';");
        }

        st.close();

        closeDbConnection();
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
