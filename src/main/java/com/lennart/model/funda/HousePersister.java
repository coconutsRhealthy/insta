package com.lennart.model.funda;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 23/01/2020.
 */
public class HousePersister {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        new HousePersister().fillDbFromFiles();
//    }

    private void fillDbFromFiles() throws Exception {
        List<File> allHtmlFiles = getAllHtmlFilesFromDir("/Users/LennartMac/Documents/huizen");

        DataFromPageRetriever dataFromPageRetriever = new DataFromPageRetriever();
        int counter = 0;

        initializeDbConnection();

        for(File input : allHtmlFiles) {
            List<House> houseData = dataFromPageRetriever.gatherHouseData(input);

            for(House house : houseData) {
                storeHouseInDb(house);
            }

            System.out.println(counter++);
        }

        closeDbConnection();
    }

    private void storeHouseInDb(House house) throws Exception {
        Statement st = con.createStatement();

        try {
            st.executeUpdate("INSERT INTO funda2 (" +
                    "adres, " +
                    "postcode, " +
                    "plaats, " +
                    "oppervlakte, " +
                    "kamers, " +
                    "prijs, " +
                    "prijs_m2, " +
                    "makelaar, " +
                    "datum_op_pagina, " +
                    "datum_van_storage_db) " +
                    "VALUES ('" +
                    house.getAddress() + "', '" +
                    house.getPostCode() + "', '" +
                    house.getCity() + "', '" +
                    house.getOppervlakte() + "', '" +
                    house.getNumberOfRooms() + "', '" +
                    house.getPrice() + "', '" +
                    house.getPriceM2() + "', '" +
                    house.getMakelaar() + "', '" +
                    house.getDateAtPage() + "', '" +
                    house.getCurrentDate() + "'" +
                    ")");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("wacht");
        }

        st.close();
    }


    private List<File> getAllHtmlFilesFromDir(String path) throws Exception {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        List<File> allPageHtmlFiles = new ArrayList<>();

        for (File file : listOfFiles) {
            if(file.getAbsolutePath().endsWith(".htm") || file.getAbsolutePath().endsWith(".html")) {
                allPageHtmlFiles.add(file);
            }
        }

        return allPageHtmlFiles;
    }

    public List<Integer> getAllM2Prices() throws Exception {
        List<Integer> allM2Prices = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda;");

        while(rs.next()) {
            if(rs.getDouble("prijs_m2") >= 0) {
                allM2Prices.add((int) rs.getDouble("prijs_m2"));
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        return allM2Prices;
    }

    public List<Integer> getAllPrices() throws Exception {
        List<Integer> allPrices = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda;");

        while(rs.next()) {
            if(rs.getDouble("prijs") >= 0) {
                allPrices.add((int) rs.getDouble("prijs"));
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        return allPrices;
    }

    public List<Integer> getAllPricesForCity(String city) throws Exception {
        List<Integer> allPrices = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda WHERE plaats LIKE '%" + city + "%';");

        while(rs.next()) {
            if(rs.getDouble("prijs") >= 0) {
                allPrices.add((int) rs.getDouble("prijs"));
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        return allPrices;
    }

    public List<Integer> getAllM2PricesForCity(String city) throws Exception {
        List<Integer> allPrices = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda WHERE plaats LIKE '%" + city + "%' AND prijs < 2000000 AND prijs_m2 < 12500;");

        while(rs.next()) {
            if(rs.getDouble("prijs_m2") >= 0) {
                allPrices.add((int) rs.getDouble("prijs_m2"));
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        return allPrices;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
