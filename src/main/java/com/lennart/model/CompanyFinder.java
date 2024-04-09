package com.lennart.model;

import java.sql.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CompanyFinder {

    private Connection con;

    public static void main(String[] args) throws Exception {
        //new CompanyFinder().getCodesForCompany("aybl", "2019-01-01");
        new CompanyFinder().getCompanyFrequencyMap("2024-01-01");
    }

    private Map<String, Integer> getCompanyFrequencyMap(String dateLimit) throws Exception {
        Map<String, Integer> companyFrequencyMap = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE date >= '" + dateLimit + "';");

        while(rs.next()) {
            String company = rs.getString("company");
            companyFrequencyMap.put(company, companyFrequencyMap.getOrDefault(company, 0) + 1);
        }

        rs.close();
        st.close();

        closeDbConnection();

        companyFrequencyMap = companyFrequencyMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return companyFrequencyMap;
    }

    private Map<String, Date> getCodesForCompany(String company, String dateLimit) throws Exception {
        Map<String, Date> codesForCompany = new HashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM discounts WHERE company = '" + company + "' AND date >= '" + dateLimit + "';");

        while(rs.next()) {
            String discountCode = rs.getString("discount_code");
            Date date = rs.getDate("date");

            if(codesForCompany.containsKey(discountCode)) {
                Date existingDate = codesForCompany.get(discountCode);
                if(date.after(existingDate)) {
                    codesForCompany.put(discountCode, date);
                }
            } else {
                codesForCompany.put(discountCode, date);
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        codesForCompany = codesForCompany.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        return codesForCompany;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
