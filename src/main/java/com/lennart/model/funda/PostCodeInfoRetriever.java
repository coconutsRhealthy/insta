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

    public PostCode getPostCodeData(String postCodeString) throws Exception {
        setAllDataForPostCode(postCodeString);
        return postCode;
    }

    private void setAllDataForPostCode(String postCodeString) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(getQuery(postCodeString));

        Double numberOfHousesCounter_6months = 0.0;
        Double numberOfHousesCounter_12months = 0.0;
        Double numberOfHousesM2Counter_6months = 0.0;
        Double numberOfHousesM2Counter_12months = 0.0;
        double totalPriceCounter_6months = 0;
        double totalPriceCounter_12months = 0;
        double totalPriceM2Counter_6months = 0;
        double totalPriceM2Counter_12months = 0;
        List<String> allMakelaars = new ArrayList<>();

        while(rs.next()) {
            allMakelaars.add(rs.getString("makelaar"));

            if(postCode.getCity() == null) {
                postCode.setCity(rs.getString("plaats"));
            }

            numberOfHousesCounter_12months++;
            totalPriceCounter_12months = totalPriceCounter_12months + rs.getDouble("prijs");

            if(!rs.getString("datum_op_pagina").equals("Langer dan 6 maanden")) {
                numberOfHousesCounter_6months++;
                totalPriceCounter_6months = totalPriceCounter_6months + rs.getDouble("prijs");
            }

            double priceM2 = rs.getDouble("prijs_m2");

            if(priceM2 != -1) {
                totalPriceM2Counter_12months = totalPriceM2Counter_12months + rs.getDouble("prijs_m2");
                numberOfHousesM2Counter_12months++;

                if(!rs.getString("datum_op_pagina").equals("Langer dan 6 maanden")) {
                    numberOfHousesM2Counter_6months++;
                    totalPriceM2Counter_6months = totalPriceM2Counter_6months + rs.getDouble("prijs_m2");
                }
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        postCode.setAverageHousePrice_6months(convertPriceToCorrectStringFormat(totalPriceCounter_6months / numberOfHousesCounter_6months));
        postCode.setAverageHousePricePerM2_6months(convertPriceToCorrectStringFormat(totalPriceM2Counter_6months / numberOfHousesM2Counter_6months));
        postCode.setAverageHousePrice_12months(convertPriceToCorrectStringFormat(totalPriceCounter_12months / numberOfHousesCounter_12months));
        postCode.setAverageHousePricePerM2_12months(convertPriceToCorrectStringFormat(totalPriceM2Counter_12months / numberOfHousesM2Counter_12months));
        postCode.setNumberOfHousesSold_6months(numberOfHousesCounter_6months.intValue());
        postCode.setNumberOfHousesSold_12months(numberOfHousesCounter_12months.intValue());
        postCode.setMostUsedMakelaar(getMostFrequentWordFromList(allMakelaars));
        postCode.setPostCodeString(postCodeString);
    }

    String getQuery(String postCodeString) {
        String query = "SELECT * FROM funda3 WHERE postcode LIKE '%" + postCodeString + "%' AND prijs > 0;";
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
        priceString = "€" + priceString;
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
