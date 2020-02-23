package com.lennart.model.funda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LennartMac on 15/02/2020.
 */
public class SimilarHousesFinder {

    //zelfde postcode 3831 Dx ....
    //zelfde m2 75% 125%
    //gemiddelde vraagprijs van die groep en gooi outliers eruit (die niet binnen 75% 125% van gemiddelde vallen)

    private Connection con;

    public static void main(String[] args) throws Exception {
        new SimilarHousesFinder().getSimilarHouses("3832 C");
    }

    public List<House> getSimilarHouses(String postCode) throws Exception {
        List<House> allHouses = getAllHousesOfPostCode(postCode);

        double averagePrice = getAveragePriceOrM2OfHouseList(allHouses, false);
        double averageM2 = getAveragePriceOrM2OfHouseList(allHouses, true);

        allHouses = removeOutliers(allHouses, averageM2, true);
        allHouses = removeOutliers(allHouses, averagePrice, false);

        return allHouses;

    }

    public List<House> getAllHousesOfPostCode(String postCode) throws Exception {
        List<House> allHousesOfPostCode = new ArrayList<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda3 WHERE postcode LIKE '%" + postCode + "%'");

        while(rs.next()) {
            House house = new House();

            house.setAddress(rs.getString("adres"));
            house.setPostCode(rs.getString("postCode"));
            house.setCity(rs.getString("plaats"));
            house.setOppervlakte(rs.getDouble("oppervlakte"));
            house.setMakelaar(rs.getString("makelaar"));
            house.setPrice(rs.getDouble("prijs"));
            house.setDateAtPage(rs.getString("datum_op_pagina"));
            house.setCurrentDate(rs.getString("datum_van_storage_db"));
            house.setPriceM2(rs.getDouble("prijs_m2"));
            house.setNumberOfRooms(rs.getDouble("kamers"));

            allHousesOfPostCode.add(house);
        }

        rs.close();
        st.close();

        closeDbConnection();

        return allHousesOfPostCode;
    }

    public double getAveragePriceOrM2OfHouseList(List<House> houseList, boolean m2) {
        double total = 0;
        double totalCounter = 0;

        for(House house : houseList) {
            if(m2) {
                total = total + house.getOppervlakte();
            } else {
                total = total + house.getPrice();
            }

            totalCounter++;
        }

        return total / totalCounter;
    }

    private List<House> removeOutliers(List<House> houseList, double average, boolean m2) {
        List<House> houseListToReturn = new ArrayList<>();

        double upperBoundry = average * 1.25;
        double lowerBoundry = average * 0.75;

        for(House house : houseList) {
            if(m2) {
                if(house.getOppervlakte() > lowerBoundry && house.getOppervlakte() < upperBoundry) {
                    houseListToReturn.add(house);
                }
            } else {
                if(house.getPrice() > lowerBoundry && house.getPrice() < upperBoundry) {
                    houseListToReturn.add(house);
                }
            }
        }

        return houseListToReturn;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
