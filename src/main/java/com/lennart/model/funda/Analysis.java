package com.lennart.model.funda;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LennartMac on 23/01/2020.
 */
public class Analysis {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        new Analysis().analyse();
//    }

    //print hoeveel van totaal goedkoper zijn qua prijs/m2


    private void analyse() throws Exception {
        String postCode = "1097";
        double m2 = 44;
        double price = 400_000;

        double priceM2consideration = price / m2;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM funda WHERE postcode LIKE '%" + postCode + "%';");

        List<Double> allPricesM2s = new ArrayList<>();

        while(rs.next()) {
            double priceRs = rs.getDouble("prijs");
            double m2Rs = rs.getDouble("oppervlakte");

            if(priceRs > 0 && m2Rs > 0) {
                allPricesM2s.add(priceRs / m2Rs);
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

        //System.out.println("Roughly " + ratioString.substring(0, 4) + "% of the houses are cheaper in this postcode");



//        SELECT * FROM batches
//        WHERE FilePath LIKE '%Parker\_Apple\_Ben\_20-10-1830\\\\%'


    }


    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
