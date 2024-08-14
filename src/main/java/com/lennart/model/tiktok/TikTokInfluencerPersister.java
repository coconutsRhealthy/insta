package com.lennart.model.tiktok;

import java.sql.*;

public class TikTokInfluencerPersister {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new TikTokInfluencerPersister().setDefaultDateAddedInDb();
    }

    public void addOrUpdateTiktokUser(String username, int followers, String country,
                                  String gaveDiscount, String addedToDbDate) throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();

        ResultSet rs = st.executeQuery("SELECT * FROM tiktok_influencers where name = '" + username + "';");

        if(!rs.next()) {
            addTiktokUserToDb(username, followers, country, gaveDiscount, addedToDbDate);
            System.out.println("Added: " + username);
        } else {
            updateTiktokUserInDb(username, country);
            System.out.println("Already in db: " + username);
        }

        st.close();
        closeDbConnection();
    }

    private void addTiktokUserToDb(String username, int followers, String country,
                                    String gaveDiscount, String addedToDbDate) throws Exception {
        initializeDbConnection();
        Statement st = con.createStatement();

        st.executeUpdate("INSERT INTO tiktok_influencers (" +
                "name, " +
                "followers, " +
                "country, " +
                "gave_discount, " +
                "date_added) " +
                "VALUES ('" +
                username + "', '" +
                followers + "', '" +
                country + "', '" +
                gaveDiscount + "', '" +
                Date.valueOf(addedToDbDate) + "'" +
                ")");

        st.close();
        closeDbConnection();
    }

    private void updateTiktokUserInDb(String username, String country) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("UPDATE tiktok_influencers SET " +
                "country = '" + country + "' " +
                "WHERE name = '" + username + "'");
        st.close();

        closeDbConnection();
    }

    private void setDefaultDateAddedInDb() throws Exception {
        String dateString = "2024-05-19";
        Date dateForDb = Date.valueOf(dateString);

        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("UPDATE tiktok_influencers SET date_added = '" + dateForDb + "';");
        st.close();

        closeDbConnection();
    }

    public void setGaveDiscount(String influencer, String gaveDiscount) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        st.executeUpdate("UPDATE tiktok_influencers SET gave_discount = '" + gaveDiscount + "' WHERE name = '" + influencer + "';");
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
