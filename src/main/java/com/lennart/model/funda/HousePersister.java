package com.lennart.model.funda;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 23/01/2020.
 */
public class HousePersister {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new HousePersister().fillDbFromFiles();
    }

    private void fillDbFromFiles() throws Exception {
        List<File> allHtmlFiles = getAllHtmlFilesFromDir("/Users/LennartMac/Documents/huizen");

        DataFromPageRetriever dataFromPageRetriever = new DataFromPageRetriever();
        int counter = 0;

        initializeDbConnection();

        for(File input : allHtmlFiles) {
            List<House> houseData = dataFromPageRetriever.gatherHouseData(input);

            for(House house : houseData) {
                storeHouseInDb(house);
                System.out.println(counter++);
            }
        }

        closeDbConnection();
    }

    private void storeHouseInDb(House house) throws Exception {
        Statement st = con.createStatement();

        try {
            st.executeUpdate("INSERT INTO funda (" +
                    "adres, " +
                    "postcode, " +
                    "plaats, " +
                    "oppervlakte, " +
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
                    house.getPrice() + "', '" +
                    house.getPriceM2() + "', '" +
                    house.getMakelaar() + "', '" +
                    house.getDateAtPage() + "', '" +
                    house.getCurrentDate() + "'" +
                    ")");
        } catch (Exception e) {
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
