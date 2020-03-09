package com.lennart.model.funda;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 04/02/2020.
 */
public class PostCodeInfoRetriever {

    private Connection con;

    private PostCode postCode = new PostCode();

    public PostCode getPostCodeData(String postCodeString, String searchPeriod) throws Exception {
        setAllDataForPostCode(postCodeString, searchPeriod);
        return postCode;
    }

    private void setAllDataForPostCode(String postCodeString, String searchPeriod) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(getQuery(postCodeString, searchPeriod));

        Double numberOfHousesCounter = 0.0;
        Double numberOfHousesM2Counter = 0.0;
        double totalPriceCounter = 0;
        double totalPriceM2Counter = 0;
        List<String> allMakelaars = new ArrayList<>();

        while(rs.next()) {
            allMakelaars.add(rs.getString("makelaar"));

            if(postCode.getCity() == null) {
                postCode.setCity(rs.getString("plaats"));
            }

            numberOfHousesCounter++;
            totalPriceCounter = totalPriceCounter + rs.getDouble("prijs");

            double priceM2 = rs.getDouble("prijs_m2");

            if(priceM2 != -1) {
                totalPriceM2Counter = totalPriceM2Counter + rs.getDouble("prijs_m2");
                numberOfHousesM2Counter++;
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        postCode.setAverageHousePrice(convertPriceToCorrectStringFormat(totalPriceCounter / numberOfHousesCounter));
        postCode.setAverageHousePricePerM2(convertPriceToCorrectStringFormat(totalPriceM2Counter / numberOfHousesM2Counter));
        postCode.setNumberOfHousesSold(numberOfHousesCounter.intValue());
        postCode.setMostUsedMakelaar(getMostFrequentWordFromList(allMakelaars));
        postCode.setPostCodeString(postCodeString);
    }

    String getQuery(String postCodeString, String searchPeriod) {
        String query = "SELECT * FROM funda3 WHERE postcode LIKE '%" + postCodeString + "%' AND prijs > 0;";

        if(searchPeriod.equals("6months")) {
            query = query + " AND datum_op_pagina != 'Langer dan 6 maanden'";
        }

        query = query + ";";

        return query;
    }

    private String getMostFrequentWordFromList(List<String> list) {
        String mostRepeatedWord;

        try {
            mostRepeatedWord
                = list.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .get()
                .getKey();
        } catch (NoSuchElementException e) {
            mostRepeatedWord = "Not Available";
        }

        return mostRepeatedWord;
    }

    private String convertPriceToCorrectStringFormat(Double price) {
        int priceInt = price.intValue();
        String priceString = String.format("%,d", priceInt);
        priceString = priceString.replaceAll(",", ".");
        priceString = "€ " + priceString;
        return priceString;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
