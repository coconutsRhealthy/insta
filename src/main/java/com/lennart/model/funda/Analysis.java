package com.lennart.model.funda;

import java.io.File;
import java.sql.*;
import java.util.*;

/**
 * Created by LennartMac on 23/01/2020.
 */
public class Analysis {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        new Analysis().printDoubleHouses();
//    }

    //print hoeveel van totaal goedkoper zijn qua prijs/m2



    private void getMostExpensivePostCodeOfCity(String city) throws Exception {


        //get all entries of city

        //get all postcodes of city

        //do analysis per postcode




    }




    private void printDoubleHouses() throws Exception {
        List<File> allHtmlFiles = new HousePersister().getAllHtmlFilesFromDir("/Users/LennartMac/Documents/allehuizen");

        DataFromPageRetriever dataFromPageRetriever = new DataFromPageRetriever();

        List<House> houseData = new ArrayList<>();

        int counter = 0;
        int fileCounter = 0;

        for(File input : allHtmlFiles) {
            fileCounter++;

            if(fileCounter < 5000) {
                houseData.addAll(dataFromPageRetriever.gatherHouseData(input, true));
                counter++;

                if(counter % 100 == 0) {
                    System.out.println(counter);
                }
            } else {
                break;
            }
        }

//        for(House house1 : houseData) {
//            for(House house2 : houseData) {
//                if(house1.equals(house2)) {
//                    System.out.println("wacht");
//                }
//            }
//        }


        Set<House> houseSet = new HashSet<>();
        houseSet.addAll(houseData);

        List<House> houseListCheckDouble = new ArrayList<>();
        //houseListCheckDouble.addAll()
    }


    private void analyse() throws Exception {
        String postCode = "1097";
        double m2 = 44;
        double price = 400_000;

        double priceM2consideration = price / m2;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda2 WHERE postcode LIKE '%" + postCode + "%';");

        List<Double> allPricesM2s = new ArrayList<>();

        while(rs.next()) {
            double priceM2 = rs.getDouble("prijs_m2");

            if(priceM2 > 0) {
                allPricesM2s.add(priceM2);
            }
        }

        Collections.sort(allPricesM2s);

        rs.close();
        st.close();

        closeDbConnection();

        double cheaperCounter = 0;

        for(Double priceM2 : allPricesM2s) {
            if(priceM2 < priceM2consideration) {
                cheaperCounter++;
            }
        }

        double ratio = cheaperCounter / (double) allPricesM2s.size();
        ratio = ratio * 100;
        String ratioString = String.valueOf(ratio);

        System.out.println("" + cheaperCounter + " of the " + allPricesM2s.size() + " houses (" + ratioString.substring(0, 4) + "%) were cheaper in this postcode in the last year");
    }





//    public static void main(String[] args) throws Exception {
//        new Analysis().extendedTestMethod();
//    }

    private void printInfoForAddress() throws Exception {
        double vraagPrijs = 519_000;
        double oppervlakte = 122;
        String postCode = "3817";
        String city = "Amersfoort";

        double totalHouses = getTotalHouses(null, postCode, city, oppervlakte);
        double averagePrice = getAveragePrice(null, postCode, city, oppervlakte, "prijs");
        double averagePriceM2 = getAveragePrice(null, postCode, city, oppervlakte, "prijs_m2");

        System.out.println("Vraagprijs: " + vraagPrijs);
        System.out.println("Gemiddelde vraagprijs: " + averagePrice);
        System.out.println("Prijs m2: " + vraagPrijs / oppervlakte);
        System.out.println("Gemiddelde prijs m2: " + averagePriceM2);
        System.out.println("Observaties: " + totalHouses);
    }

    private void extendedTestMethod() throws Exception {
        File htmlFile = new File("/Users/LennartMac/Documents/testtekoop/view-source_https___www.funda.nl_koop_amsterdam_.html");
        List<House> newHouseList = new DataFromPageRetriever().gatherHouseData(htmlFile, true);

        for(House house : newHouseList) {
            String postCode = house.getPostCode().split(" ")[0];

//            double cheaperHouses = getCheaperHouses(house.getPrice(), house.getOppervlakte(), null, postCode, house.getCity());
            double totalHouses = getTotalHouses(null, postCode, house.getCity(), house.getOppervlakte());

            double averagePrice = getAveragePrice(null, postCode, house.getCity(), house.getOppervlakte(), "prijs");
            double averagePriceM2 = getAveragePrice(null, postCode, house.getCity(), house.getOppervlakte(), "prijs_m2");

            if(house.getPrice() < 0.8 * averagePrice || (house.getPrice() / house.getOppervlakte()) < 0.8 * averagePriceM2) {
                System.out.println(house.getAddress() + "     " + house.getCity());
                System.out.println("Vraagprijs: " + house.getPrice());
                System.out.println("Gemiddelde vraagprijs: " + averagePrice);
                System.out.println("Prijs m2: " + house.getPrice() / house.getOppervlakte());
                System.out.println("Gemiddelde prijs m2: " + averagePriceM2);
                System.out.println("Observaties: " + totalHouses);
                System.out.println();
                System.out.println();
            }

//            System.out.println(house.getAddress() + "     " + house.getCity());
//            System.out.println(cheaperHouses);
//            System.out.println(totalHouses);
//            System.out.println("aantal huizen goedkoper verkocht: " + cheaperHouses / totalHouses);
//            System.out.println();
//            System.out.println();
        }
    }




    private void testMethod() throws Exception {
        String street = null;
        String postCode = "1183";
        String city = "Amstelveen";

        double vraagPrijs = 500_000;
        double oppervlak = 110;

        double cheaperHouses = getCheaperHouses(vraagPrijs, oppervlak, street, postCode, city);
        double totalHouses = getTotalHouses(street, postCode, city, oppervlak);

        System.out.println(cheaperHouses);
        System.out.println(totalHouses);
        System.out.println("aantal huizen goedkoper verkocht: " + cheaperHouses / totalHouses);
    }

    private double getCheaperHouses(double vraagPrijs, double oppervlak, String street, String postCode, String city) throws Exception {
        String query = buildQuery(street, postCode, city, oppervlak);

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);

        double cheaperCounter = 0;

        double priceLimit;
        String priceTypeToGet;

        if(oppervlak == -1) {
            priceLimit = vraagPrijs;
            priceTypeToGet = "prijs";
        } else {
            priceLimit = vraagPrijs / oppervlak;
            priceTypeToGet = "prijs_M2";

//            priceLimit = vraagPrijs;
//            priceTypeToGet = "prijs";
        }

        while(rs.next()) {
            double priceTypeToGetValue = rs.getDouble(priceTypeToGet);

            if(priceTypeToGetValue != -1 && priceTypeToGetValue < priceLimit) {
                cheaperCounter++;
            }
        }

        return cheaperCounter;
    }

    private double getTotalHouses(String street, String postCode, String city, double oppervlakte) throws Exception {
        String query = buildQuery(street, postCode, city, oppervlakte);

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);

        int size = 0;
        if(rs != null) {
            rs.last();
            size = rs.getRow();
        }

        return size;
    }

    private double getAveragePrice(String street, String postCode, String city, double oppervlakte, String priceTypeToGet) throws Exception {
        String query = buildQuery(street, postCode, city, oppervlakte);

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(query);

        double numberOfHousesCounter = 0;
        double totalPriceCounter = 0;

        while(rs.next()) {
            numberOfHousesCounter++;
            totalPriceCounter = totalPriceCounter + rs.getDouble(priceTypeToGet);
        }

        return  totalPriceCounter / numberOfHousesCounter;
    }

    private String buildQuery(String street, String postCode, String city, double oppervlakte) {
        String query = "SELECT * FROM funda2 WHERE ";

        if(street != null) {
            query = query + "adres LIKE '%" + street + "%' AND ";
        }

        if(postCode != null) {
            query = query + "postcode LIKE '%" + postCode + "%' AND ";
        }

//        if(city != null) {
//            query = query + "plaats LIKE '%" + city + "%' AND ";
//        }

        if(city != null) {
            query = query + "plaats LIKE '%" + city + "%'";
        }

//        if(oppervlakte != -1) {
//            double lowerBoundry = oppervlakte * 0.92;
//            double upperBoundry = oppervlakte * 1.08;
//
//            query = query + "oppervlakte > " + lowerBoundry + " AND oppervlakte < " + upperBoundry;
//        }

        while(query.endsWith(" ")) {
            query = query.substring(0, query.length() - 1);
        }

        query = query + ";";

        return query;
    }


    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
