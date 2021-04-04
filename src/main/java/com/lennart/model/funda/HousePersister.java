package com.lennart.model.funda;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by LennartMac on 23/01/2020.
 */
public class HousePersister {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        new HousePersister().addNewHousesToDb();
//    }

    private void compareShit() throws Exception {
        double beforePriceTotal = 0;
        double beforePriceM2Total = 0;
        double beforePriceTotalCounter = 0;
        double beforePriceM2TotalCounter = 0;
        double afterPriceTotal = 0;
        double afterPriceM2Total = 0;
        double afterPriceTotalCounter = 0;
        double afterPriceM2TotalCounter = 0;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda3;");

        while(rs.next()) {
            String datumOpPagina = rs.getString("datum_op_pagina");

            if(datumOpPagina.contains("donderdag 23 januari 2020") && rs.getDouble("prijs") < 5_000_000) {
                beforePriceTotal = beforePriceTotal + rs.getDouble("prijs");
                beforePriceM2Total = beforePriceM2Total + rs.getDouble("prijs_m2");

                beforePriceTotalCounter++;
                beforePriceM2TotalCounter++;
            }
        }

        rs.close();
        st.close();

        Statement st2 = con.createStatement();
        ResultSet rs2 = st2.executeQuery("SELECT * FROM eije;");

        while(rs2.next()) {
            String datumOpPagina = rs2.getString("datum_op_pagina");

            if(datumOpPagina.equals("donderdag 9 april 2020") && rs2.getDouble("prijs") < 5_000_000) {
                afterPriceTotal = afterPriceTotal + rs2.getDouble("prijs");
                afterPriceM2Total = afterPriceM2Total + rs2.getDouble("prijs_m2");

                afterPriceTotalCounter++;
                afterPriceM2TotalCounter++;
            }

        }

        rs2.close();
        st2.close();

        closeDbConnection();

//        System.out.println("before gemiddelde: " + (beforePriceTotal / beforeCounter));
//        System.out.println("after gemiddelde: " + (afterPriceTotal / afterCounter));

        System.out.println("before prijs gemiddelde: " + (beforePriceTotal / beforePriceTotalCounter));
        System.out.println("before prijs_m2 gemiddelde: " + (beforePriceM2Total / beforePriceM2TotalCounter));
        System.out.println("total: " + beforePriceTotalCounter);

        System.out.println();

        System.out.println("after prijs gemiddelde: " + (afterPriceTotal / afterPriceTotalCounter));
        System.out.println("after prijs_m2 gemiddelde: " + (afterPriceM2Total / afterPriceM2TotalCounter));
        System.out.println("total: " + afterPriceTotalCounter);
    }

    private void addNewHousesToDb() throws Exception {
        List<File> allNewHtmlFiles = getAllHtmlFilesFromDir("/Users/LennartMac/Documents/huizenstuff/update_3_mei");

        DataFromPageRetriever dataFromPageRetriever = new DataFromPageRetriever();
        int counter = 0;

        List<House> newHouseData = new ArrayList<>();

        for(File input : allNewHtmlFiles) {
            newHouseData.addAll(dataFromPageRetriever.gatherHouseData(input, true));
            System.out.println("html file: " + counter++);
        }

        System.out.println("aantal huizen: " + newHouseData.size());
        initializeDbConnection();

        int total = newHouseData.size();
        counter = 0;

        for(House house : newHouseData) {
            //if(houseIsNotPresentInDb(house)) {
                storeHouseInDb(house);
            //}

            System.out.println(counter++ + "      " + total);
        }

        closeDbConnection();
    }

    private boolean houseIsNotPresentInDb(House house) throws Exception {
        boolean houseIsNotPresentInDb = false;

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda3 WHERE adres = '" + house.getAddress() +
                "' AND postcode = '" + house.getPostCode() + "' AND prijs = '" + house.getPrice() + "';");

        if(!rs.next()) {
            houseIsNotPresentInDb = true;
            System.out.println("House not present in db!");
        } else {
            System.out.println("new house already present in db");
            System.out.println("Adres: " + house.getAddress());
            System.out.println("Postcode: " + house.getPostCode());
            System.out.println("Woonplaats: " + house.getCity());
            System.out.println("Prijs: " + house.getPrice());
            System.out.println("Makelaar: " + house.getMakelaar());
            System.out.println("Aantal kamers: " + house.getNumberOfRooms());
        }

        rs.close();
        st.close();

        return houseIsNotPresentInDb;
    }

    private void fillDbFromFiles() throws Exception {
        List<File> allHtmlFiles = getAllHtmlFilesFromDir("/Users/LennartMac/Documents/huizenstuff/update_april");

        DataFromPageRetriever dataFromPageRetriever = new DataFromPageRetriever();
        int counter = 0;

        List<House> houseData = new ArrayList<>();

        for(File input : allHtmlFiles) {
            houseData.addAll(dataFromPageRetriever.gatherHouseData(input, true));
            System.out.println("A " + counter++);
        }

        Set<House> houseDataSet = new HashSet<>();
        houseDataSet.addAll(houseData);
        List<House> houseDataCleaned = new ArrayList<>();
        houseDataCleaned.addAll(houseDataSet);

        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("old size: " + houseData.size());
        System.out.println("new, cleaned size: " + houseDataCleaned.size());
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

        counter = 0;

        initializeDbConnection();

        for(House house : houseDataCleaned) {
            storeHouseInDb(house);

            counter++;
            if(counter % 15 == 0) {
                System.out.println("B " + counter);
            }
        }

        closeDbConnection();
    }

    private void storeHouseInDb(House house) throws Exception {
        Statement st = con.createStatement();

        try {
            st.executeUpdate("INSERT INTO funda3 (" +
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
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.print("house already present in db: ");
            System.out.print(house.getAddress() + " ");
            System.out.print(house.getPostCode() + " ");
            System.out.print(house.getCity() + " ");
            System.out.print(house.getPrice() + " ");
            System.out.println(house.getMakelaar() + " ");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("wacht");
        }

        st.close();
    }

    public List<File> getAllHtmlFilesFromDir(String path) throws Exception {
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
