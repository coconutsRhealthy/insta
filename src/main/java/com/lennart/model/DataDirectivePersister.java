package com.lennart.model;

import java.sql.*;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataDirectivePersister {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new DataDirectivePersister().enterSomeData();
    }

    private void enterSomeData() throws Exception {
        initializeDbConnection();

        List<String> dataLines = getDataDirectiveLines("/Users/lennartmac/Documents/Projects/diski/src/app/data/data.directive.ts");
        //List<String> dataLines = getArchiveLines("/Users/lennartmac/Documents/Projects/diski/src/app/data/archive2.txt");

        //addYearToDate(dataLines);

        for(String line : dataLines) {
            line = line.replaceAll("\"", "");
            line = line.substring(0, line.length() - 1);

            if(line.length() > 5) {
                Statement st = con.createStatement();

                st.executeUpdate("INSERT INTO discounts (" +
                    "company, " +
                    "discount_code, " +
                    "discount_percentage, " +
                    "influencer, " +
                    "date) " +
                    "VALUES ('" +
                    getCompanyFromLine(line) + "', '" +
                    getDiscountCodeFromLine(line) + "', '" +
                    getDiscountPercentageFromLine(line) + "', '" +
                    getInfluencerFromLine(line) + "', '" +
                    getDateFromLine(line) + "'" +
                    ")");

                st.close();
            }

        }

        closeDbConnection();
    }

    private String getCompanyFromLine(String line) {
        String[] parts = line.split(",");
        String company = parts[0].trim();

        if(company.startsWith("@")) {
            company = company.replaceFirst("@", "");
        }

        return company;
    }

    private String getDiscountCodeFromLine(String line) {
        String[] parts = line.split(",");
        return parts[1].trim();
    }

    private String getDiscountPercentageFromLine(String line) {
        String[] parts = line.split(",");
        return parts[2].trim();
    }

    private String getInfluencerFromLine(String line) {
        String[] parts = line.split(",");
        return parts[3].trim();
    }

    private Date getDateFromLine(String line) {
        String[] parts = line.split(",");
        String dateString = parts[4].trim();
        return Date.valueOf("2024-" + dateString);
    }

    private List<String> getDataDirectiveLines(String path) {
        return new NewDataSorter().readDataFromDataDirective(path).
                stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private List<String> getArchiveLines(String path) {
        return new NewDataSorter().readDataFromArchive(path).
                stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private List<String> addYearToDate(List<String> dataDirectives) {
        int currentYear = Year.now().getValue();
        List<String> dataDirectivesIncludingYear = new ArrayList<>();
        String lastMonth = null;
        for (int i = 0; i < dataDirectives.size(); i++) {
            String[] parts = dataDirectives.get(i).split(", ");

            if(parts.length >= 4) {
                int partsIndexToUse = (parts.length > 4) ? 4 : 3;

                String date = parts[partsIndexToUse];

                String[] dateParts = date.split("-");
                String month = dateParts[0];

                if (lastMonth != null && lastMonth.equals("01") && month.equals("12")) {
                    currentYear--;
                }

                lastMonth = month;

                parts[partsIndexToUse] = parts[partsIndexToUse] + "-" + currentYear;
                dataDirectivesIncludingYear.add(String.join(", ", parts));
            }
        }

        return dataDirectivesIncludingYear;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
