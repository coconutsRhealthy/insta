package com.lennart.model.funda;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 04/02/2020.
 */
public class PostCodeInfoRetriever {

    private Connection con;
    private String city = null;
    private double averagePrice;
    private double averagePriceM2;
    private String mostUsedMakelaar;
    private int numberOfHousesSold;

    public PostCode getPostCodeData(String postCodeString) throws Exception {
        setAllDataForPostCode(postCodeString);

        PostCode postCode = new PostCode();

        postCode.setCity(city);
        postCode.setNumberOfHousesSold(numberOfHousesSold);
        postCode.setAverageHousePrice(averagePrice);
        postCode.setAverageHousePricePerM2(averagePriceM2);
        postCode.setMostUsedMakelaar(mostUsedMakelaar);

        return postCode;
    }

    private void setAllDataForPostCode(String postCode) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda2 WHERE postcode LIKE '%" + postCode + "%';");

        Double numberOfHousesCounter = 0.0;
        double totalPriceCounter = 0;
        double totalPriceM2Counter = 0;
        List<String> allMakelaars = new ArrayList<>();

        while(rs.next()) {
            allMakelaars.add(rs.getString("makelaar"));

            if(city == null) {
                city = rs.getString("plaats");
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

        averagePrice = totalPriceCounter / numberOfHousesCounter;
        averagePriceM2 = totalPriceM2Counter / numberOfHousesCounter;
        numberOfHousesSold = numberOfHousesCounter.intValue();
        mostUsedMakelaar = getMostFrequentWordFromList(allMakelaars);
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

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
