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
        ResultSet rs = st.executeQuery("SELECT * FROM funda2 WHERE postcode LIKE '%" + postCodeString + "%';");

        Double numberOfHousesCounter = 0.0;
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
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        postCode.setAverageHousePrice(convertPriceToCorrectStringFormat(totalPriceCounter / numberOfHousesCounter));
        postCode.setAverageHousePricePerM2(convertPriceToCorrectStringFormat(totalPriceM2Counter / numberOfHousesCounter));
        postCode.setNumberOfHousesSold(numberOfHousesCounter.intValue());
        postCode.setMostUsedMakelaar(getMostFrequentWordFromList(allMakelaars));
    }

    private String getMostFrequentWordFromList(List<String> list) {
        String mostRepeatedWord
                = list.stream()
                .collect(Collectors.groupingBy(w -> w, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .get()
                .getKey();

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
