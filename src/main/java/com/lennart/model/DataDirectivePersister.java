package com.lennart.model;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataDirectivePersister {

    private Connection con;

    public static void main(String[] args) throws Exception {
        new DataDirectivePersister().fillEmptyDb();
    }

    private void addNewDataDirectiveLinesToDb(String dateBoundry) throws Exception {
        //dateBoundry example: "03-29-2024";
        List<String> dataDirective = getDataDirectiveLines("/Users/lennartmac/Documents/Projects/diski/src/app/data/data.directive.ts");
        dataDirective = addYearToDate(dataDirective, 2024);
        dataDirective = removeDataBeforeDate(dataDirective, dateBoundry);
        addDataToDb(dataDirective);
    }

    private void fillEmptyDb() throws Exception {
        List<String> dataDirective = getDataDirectiveLines("/Users/lennartmac/Documents/Projects/diski/src/app/data/data.directive.ts");
        List<String> archiveDataDirective = getDataDirectiveLines("/Users/lennartmac/Documents/Projects/diski/src/app/data/archivedata.directive.ts");
        List<String> archive2 = getArchiveLines("/Users/lennartmac/Documents/Projects/diski/src/app/data/archive2.txt");
        List<String> archive = getArchiveLines("/Users/lennartmac/Documents/Projects/diski/src/app/data/archive.txt");

        dataDirective = addYearToDate(dataDirective, 2024);
        archiveDataDirective = addYearToDate(archiveDataDirective, 2024);
        archive2 = addYearToDate(archive2, 2024);
        archive = addYearToDate(archive, 2022);

        addDataToDb(dataDirective);
        addDataToDb(archiveDataDirective);
        addDataToDb(archive2);
        addDataToDb(archive);
    }

    private void addDataToDb(List<String> dataLines) throws Exception {
        initializeDbConnection();

        for(String line : dataLines) {
            line = removeQuotesAndLastCommaFromLine(line);
            line = addEmptyDiscountCodeToLineIfNeeded(line);

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
        String[] dateParts = dateString.split("-");

        String dateStringToUse;

        if(dateParts[0].length() == 2) {
            dateStringToUse = dateParts[2] + "-" + dateParts[0] + "-" + dateParts[1];
        } else {
            dateStringToUse = dateString;
        }

        return Date.valueOf(dateStringToUse);
    }

    private List<String> getDataDirectiveLines(String path) {
        return new NewDataSorter().readDataFromDataDirective(path).
                stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private List<String> getArchiveLines(String path) {
        return new NewDataSorter().readDataFromArchive(path).
                stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private List<String> addYearToDate(List<String> dataDirectives, int yearToStartWith) {
        List<String> dataDirectivesIncludingYear = new ArrayList<>();
        String lastMonth = null;
        for (int i = 0; i < dataDirectives.size(); i++) {
            String[] parts = dataDirectives.get(i).split(", ");

            if(parts.length >= 4) {
                int partsIndexToUse = (parts.length > 4) ? 4 : 3;
                String date = parts[partsIndexToUse];
                String[] dateParts = date.split("-");

                if(dateParts.length == 2) {
                    String month = dateParts[0];

                    if (lastMonth != null && lastMonth.equals("01") && month.equals("12")) {
                        yearToStartWith--;
                    }

                    lastMonth = month;

                    String dateToUse = date.substring(0, date.length() - 2);
                    dateToUse = dateToUse + "-" + yearToStartWith + "\",";

                    parts[partsIndexToUse] = dateToUse;
                }

                dataDirectivesIncludingYear.add(String.join(", ", parts));
            }
        }

        return dataDirectivesIncludingYear;
    }

    private String addEmptyDiscountCodeToLineIfNeeded(String line) {
        String lineToReturn = line;
        String[] parts = line.split(",");

        if (parts.length == 4) {
            lineToReturn = parts[0] + "," + parts[1] + ",  , " + parts[2] + "," + parts[3];
        }

        return lineToReturn;
    }

    private List<String> removeDataBeforeDate(List<String> dataDirective, String dateBoundryString) {
        List<String> dataDirectiveAfterDateBoundry = new ArrayList<>();

        for(String line : dataDirective) {
            if(line.length() > 5) {
                String lineToAnalyseDate = removeQuotesAndLastCommaFromLine(line);
                String[] parts = lineToAnalyseDate.split(",");
                String dateString = parts[4].trim();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
                LocalDate dateInDataDirective = LocalDate.parse(dateString, formatter);
                LocalDate dateBoundry = LocalDate.parse(dateBoundryString, formatter);

                if(!dateInDataDirective.isBefore(dateBoundry)) {
                    dataDirectiveAfterDateBoundry.add(line);
                }
            }
        }

        return dataDirectiveAfterDateBoundry;
    }

    private String removeQuotesAndLastCommaFromLine(String line) {
        line = line.replaceAll("\"", "");
        line = line.substring(0, line.length() - 1);
        return line;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/diski?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
