package com.lennart.model.funda;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 03/05/2020.
 */
public class Simple {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        //new Simple().getUrls().stream().forEach(eije -> System.out.println(eije));
//        new Simple().store();
//    }

    private void scrape() throws Exception {
        List<String> urls = getUrls();

        TimeUnit.SECONDS.sleep(2);

        for(String url : urls) {
            StringSelection stringSelection = new StringSelection(url);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);

            TimeUnit.MILLISECONDS.sleep(500);

            //click url bar
            rightClick(801, 80);

            TimeUnit.MILLISECONDS.sleep(300);

            //click paste
            click(550, 237);

            TimeUnit.MILLISECONDS.sleep(4950);

            //right click
            rightClick(500, 275);
            TimeUnit.MILLISECONDS.sleep(150);

            //click save page as
            TimeUnit.MILLISECONDS.sleep(500);
            click(547, 349);

            //click save
            TimeUnit.MILLISECONDS.sleep(2000);
            click(807, 305);
            click(807, 305);
        }
    }

    private void store() throws Exception {
        Map<String, Integer> numbers = getNumberOfHousesForSale();
        //calculateAveragePrice(numbers);
        storeNumbersInDb(numbers);
    }

    private Map<String, Integer> getNumberOfHousesForSale() throws Exception {
        Map<String, Integer> numberOfHouses = new HashMap<>();

        List<File> allFiles = new HousePersister()
                .getAllHtmlFilesFromDir("/Users/LennartMac/Documents/huizenstuff/simple/4_mei");

        for(File input : allFiles) {
            Document document = Jsoup.parse(input, "UTF-8", "http://www.eije.com/");
            Elements allSearchResults = document.select("div.search-output-result-count");
            Integer number = Integer.valueOf(allSearchResults.first().text().split(" ")[0]);
            numberOfHouses.put(getCorrectKeyPriceString(document), number);
        }

        return numberOfHouses;
    }

    private String getCorrectKeyPriceString(Document document) {
        Elements selecteds = document.select("select option[selected]");

        List<String> values = new ArrayList<>();

        for(Element selected : selecteds) {
            if(selected.text().contains("€")) {
                String value = selected.text();

                value = value.replace("€", "");
                value = value.replace(" ", "");
                value = value.replace(".", "");

                values.add(value);
            }
        }

        List<Integer> asNumbers = values.stream().map(string -> Integer.valueOf(string)).collect(Collectors.toList());

        String toReturn;

        if(asNumbers.size() == 1) {
            toReturn = "_" + asNumbers.get(0) + "+";
        } else {
            Collections.sort(asNumbers);
            toReturn = "_" + asNumbers.get(0) + "-" + asNumbers.get(1);
        }

        return toReturn;
    }

    private void storeNumbersInDb(Map<String, Integer> numbers) throws Exception {
        int value_0_50 = -1;
        int value_50_75 = -1;
        int value_75_100 = -1;
        int value_100_125 = -1;
        int value_125_150 = -1;
        int value_150_175 = -1;
        int value_175_200 = -1;
        int value_200_225 = -1;
        int value_225_250 = -1;
        int value_250_275 = -1;
        int value_275_300 = -1;
        int value_300_325 = -1;
        int value_325_350 = -1;
        int value_350_375 = -1;
        int value_375_400 = -1;
        int value_400_450 = -1;
        int value_450_500 = -1;
        int value_500_550 = -1;
        int value_550_600 = -1;
        int value_600_650 = -1;
        int value_650_700 = -1;
        int value_700_750 = -1;
        int value_750_800 = -1;
        int value_800_900 = -1;
        int value_900_1000 = -1;
        int value_1000_1250 = -1;
        int value_1250_1500 = -1;
        int value_1500_2000 = -1;
        int value_2000_up = -1;

        for(Map.Entry<String, Integer> entry : numbers.entrySet()) {
            String key = entry.getKey();

            if(key.contains("_0-50000")) {
                value_0_50 = entry.getValue();
            } else if(key.contains("_50000-75000")) {
                value_50_75 = entry.getValue();
            } else if(key.contains("_75000-100000")) {
                value_75_100 = entry.getValue();
            } else if(key.contains("_100000-125000")) {
                value_100_125 = entry.getValue();
            } else if(key.contains("_125000-150000")) {
                value_125_150 = entry.getValue();
            } else if(key.contains("_150000-175000")) {
                value_150_175 = entry.getValue();
            } else if(key.contains("_175000-200000")) {
                value_175_200 = entry.getValue();
            } else if(key.contains("_200000-225000")) {
                value_200_225 = entry.getValue();
            } else if(key.contains("_225000-250000")) {
                value_225_250 = entry.getValue();
            } else if(key.contains("_250000-275000")) {
                value_250_275 = entry.getValue();
            } else if(key.contains("_275000-300000")) {
                value_275_300 = entry.getValue();
            } else if(key.contains("_300000-325000")) {
                value_300_325 = entry.getValue();
            } else if(key.contains("_325000-350000")) {
                value_325_350 = entry.getValue();
            } else if(key.contains("_350000-375000")) {
                value_350_375 = entry.getValue();
            } else if(key.contains("_375000-400000")) {
                value_375_400 = entry.getValue();
            } else if(key.contains("_400000-450000")) {
                value_400_450 = entry.getValue();
            } else if(key.contains("_450000-500000")) {
                value_450_500 = entry.getValue();
            } else if(key.contains("_500000-550000")) {
                value_500_550 = entry.getValue();
            } else if(key.contains("_550000-600000")) {
                value_550_600 = entry.getValue();
            } else if(key.contains("_600000-650000")) {
                value_600_650 = entry.getValue();
            } else if(key.contains("_650000-700000")) {
                value_650_700 = entry.getValue();
            } else if(key.contains("_700000-750000")) {
                value_700_750 = entry.getValue();
            } else if(key.contains("_750000-800000")) {
                value_750_800 = entry.getValue();
            } else if(key.contains("_800000-900000")) {
                value_800_900 = entry.getValue();
            } else if(key.contains("_900000-1000000")) {
                value_900_1000 = entry.getValue();
            } else if(key.contains("_1000000-1250000")) {
                value_1000_1250 = entry.getValue();
            } else if(key.contains("_1250000-1500000")) {
                value_1250_1500 = entry.getValue();
            } else if(key.contains("_1500000-2000000")) {
                value_1500_2000 = entry.getValue();
            } else if(key.contains("_2000000+")) {
                value_2000_up = entry.getValue();
            }
        }

        Map<Double, Double> totalAndAverage = calculateAveragePrice(numbers);

        double total = totalAndAverage.entrySet().iterator().next().getKey();
        double average = totalAndAverage.entrySet().iterator().next().getValue();

        initializeDbConnection();
        Statement st = con.createStatement();

        st.executeUpdate("INSERT INTO simple (" +
                "datum, " +
                "totaal_huizen, " +
                "gemiddelde, " +
                "0_50, " +
                "50_75, " +
                "75_100, " +
                "100_125, " +
                "125_150, " +
                "150_175, " +
                "175_200, " +
                "200_225, " +
                "225_250, " +
                "250_275, " +
                "275_300, " +
                "300_325, " +
                "325_350, " +
                "350_375, " +
                "375_400, " +
                "400_450, " +
                "450_500, " +
                "500_550, " +
                "550_600, " +
                "600_650, " +
                "650_700, " +
                "700_750, " +
                "750_800, " +
                "800_900, " +
                "900_1000, " +
                "1000_1250, " +
                "1250_1500, " +
                "1500_2000, " +
                "2000_plus) " +
                "VALUES ('" +
                getCurrentDate() + "', '" +
                total + "', '" +
                average + "', '" +
                value_0_50 + "', '" +
                value_50_75 + "', '" +
                value_75_100 + "', '" +
                value_100_125 + "', '" +
                value_125_150 + "', '" +
                value_150_175 + "', '" +
                value_175_200 + "', '" +
                value_200_225 + "', '" +
                value_225_250 + "', '" +
                value_250_275 + "', '" +
                value_275_300 + "', '" +
                value_300_325 + "', '" +
                value_325_350 + "', '" +
                value_350_375 + "', '" +
                value_375_400 + "', '" +
                value_400_450 + "', '" +
                value_450_500 + "', '" +
                value_500_550 + "', '" +
                value_550_600 + "', '" +
                value_600_650 + "', '" +
                value_650_700 + "', '" +
                value_700_750 + "', '" +
                value_750_800 + "', '" +
                value_800_900 + "', '" +
                value_900_1000 + "', '" +
                value_1000_1250 + "', '" +
                value_1250_1500 + "', '" +
                value_1500_2000 + "', '" +
                value_2000_up + "'" +
                ")");

        st.close();
        closeDbConnection();
    }

    private List<Integer> getPriceLimits() {
        List<Integer> priceLimits = new ArrayList<>();

        priceLimits.add(0);
        priceLimits.add(50_000);
        priceLimits.add(75_000);
        priceLimits.add(100_000);
        priceLimits.add(125_000);
        priceLimits.add(150_000);
        priceLimits.add(175_000);
        priceLimits.add(200_000);
        priceLimits.add(225_000);
        priceLimits.add(250_000);
        priceLimits.add(275_000);
        priceLimits.add(300_000);
        priceLimits.add(325_000);
        priceLimits.add(350_000);
        priceLimits.add(375_000);
        priceLimits.add(400_000);
        priceLimits.add(450_000);
        priceLimits.add(500_000);
        priceLimits.add(550_000);
        priceLimits.add(600_000);
        priceLimits.add(650_000);
        priceLimits.add(700_000);
        priceLimits.add(750_000);
        priceLimits.add(800_000);
        priceLimits.add(900_000);
        priceLimits.add(1_000_000);
        priceLimits.add(1_250_000);
        priceLimits.add(1_500_000);
        priceLimits.add(2_000_000);

        return priceLimits;
    }

    private Map<Double, Double> calculateAveragePrice(Map<String, Integer> numbers) throws Exception {
        Map<Double, Double> numbersToWorkWith = new TreeMap<>();

        numbersToWorkWith.put(25000.0, 0.0);
        numbersToWorkWith.put(62500.0, 0.0);
        numbersToWorkWith.put(87500.0, 0.0);
        numbersToWorkWith.put(112500.0, 0.0);
        numbersToWorkWith.put(137500.0, 0.0);
        numbersToWorkWith.put(162500.0, 0.0);
        numbersToWorkWith.put(187500.0, 0.0);
        numbersToWorkWith.put(212500.0, 0.0);
        numbersToWorkWith.put(237500.0, 0.0);
        numbersToWorkWith.put(262500.0, 0.0);
        numbersToWorkWith.put(287500.0, 0.0);
        numbersToWorkWith.put(312500.0, 0.0);
        numbersToWorkWith.put(337500.0, 0.0);
        numbersToWorkWith.put(362500.0, 0.0);
        numbersToWorkWith.put(387500.0, 0.0);
        numbersToWorkWith.put(425000.0, 0.0);
        numbersToWorkWith.put(475000.0, 0.0);
        numbersToWorkWith.put(525000.0, 0.0);
        numbersToWorkWith.put(575000.0, 0.0);
        numbersToWorkWith.put(625000.0, 0.0);
        numbersToWorkWith.put(675000.0, 0.0);
        numbersToWorkWith.put(725000.0, 0.0);
        numbersToWorkWith.put(775000.0, 0.0);
        numbersToWorkWith.put(850000.0, 0.0);
        numbersToWorkWith.put(950000.0, 0.0);
        numbersToWorkWith.put(1125000.0, 0.0);
        numbersToWorkWith.put(1375000.0, 0.0);
        numbersToWorkWith.put(1750000.0, 0.0);
        numbersToWorkWith.put(2500000.0, 0.0);

        for(Map.Entry<String, Integer> entry : numbers.entrySet()) {
            String key = entry.getKey();

            if(key.contains("_0-50000")) {
                //currentValue = numbersToWorkWith.get(25000.0);
                numbersToWorkWith.put(25000.0, entry.getValue() + 0.0);
            } else if(key.contains("_50000-75000")) {
                //currentValue = numbersToWorkWith.get(62500.0);
                numbersToWorkWith.put(62500.0, entry.getValue() + 0.0);
            } else if(key.contains("_75000-100000")) {
                //currentValue = numbersToWorkWith.get(87500.0);
                numbersToWorkWith.put(87500.0, entry.getValue() + 0.0);
            } else if(key.contains("_100000-125000")) {
                //currentValue = numbersToWorkWith.get(112500.0);
                numbersToWorkWith.put(112500.0, entry.getValue() + 0.0);
            } else if(key.contains("_125000-150000")) {
                //currentValue = numbersToWorkWith.get(137500.0);
                numbersToWorkWith.put(137500.0, entry.getValue() + 0.0);
            } else if(key.contains("_150000-175000")) {
                //currentValue = numbersToWorkWith.get(162500.0);
                numbersToWorkWith.put(162500.0, entry.getValue() + 0.0);
            } else if(key.contains("_175000-200000")) {
                //currentValue = numbersToWorkWith.get(187500.0);
                numbersToWorkWith.put(187500.0, entry.getValue() + 0.0);
            } else if(key.contains("_200000-225000")) {
                //currentValue = numbersToWorkWith.get(212500.0);
                numbersToWorkWith.put(212500.0, entry.getValue() + 0.0);
            } else if(key.contains("_225000-250000")) {
                //currentValue = numbersToWorkWith.get(237500.0);
                numbersToWorkWith.put(237500.0, entry.getValue() + 0.0);
            } else if(key.contains("_250000-275000")) {
                //currentValue = numbersToWorkWith.get(262500.0);
                numbersToWorkWith.put(262500.0, entry.getValue() + 0.0);
            } else if(key.contains("_275000-300000")) {
                //currentValue = numbersToWorkWith.get(287500.0);
                numbersToWorkWith.put(287500.0, entry.getValue() + 0.0);
            } else if(key.contains("_300000-325000")) {
                //currentValue = numbersToWorkWith.get(312500.0);
                numbersToWorkWith.put(312500.0, entry.getValue() + 0.0);
            } else if(key.contains("_325000-350000")) {
                //currentValue = numbersToWorkWith.get(337500.0);
                numbersToWorkWith.put(337500.0, entry.getValue() + 0.0);
            } else if(key.contains("_350000-375000")) {
                //currentValue = numbersToWorkWith.get(362500.0);
                numbersToWorkWith.put(362500.0, entry.getValue() + 0.0);
            } else if(key.contains("_375000-400000")) {
                //currentValue = numbersToWorkWith.get(387500.0);
                numbersToWorkWith.put(387500.0, entry.getValue() + 0.0);
            } else if(key.contains("_400000-450000")) {
                //currentValue = numbersToWorkWith.get(425000.0);
                numbersToWorkWith.put(425000.0, entry.getValue() + 0.0);
            } else if(key.contains("_450000-500000")) {
                //currentValue = numbersToWorkWith.get(475000.0);
                numbersToWorkWith.put(475000.0, entry.getValue() + 0.0);
            } else if(key.contains("_500000-550000")) {
                //currentValue = numbersToWorkWith.get(525000.0);
                numbersToWorkWith.put(525000.0, entry.getValue() + 0.0);
            } else if(key.contains("_550000-600000")) {
                //currentValue = numbersToWorkWith.get(575000.0);
                numbersToWorkWith.put(575000.0, entry.getValue() + 0.0);
            } else if(key.contains("_600000-650000")) {
                //currentValue = numbersToWorkWith.get(625000.0);
                numbersToWorkWith.put(625000.0, entry.getValue() + 0.0);
            } else if(key.contains("_650000-700000")) {
                //currentValue = numbersToWorkWith.get(675000.0);
                numbersToWorkWith.put(675000.0, entry.getValue() + 0.0);
            } else if(key.contains("_700000-750000")) {
                //currentValue = numbersToWorkWith.get(725000.0);
                numbersToWorkWith.put(725000.0, entry.getValue() + 0.0);
            } else if(key.contains("_750000-800000")) {
                //currentValue = numbersToWorkWith.get(775000.0);
                numbersToWorkWith.put(775000.0, entry.getValue() + 0.0);
            } else if(key.contains("_800000-900000")) {
                //currentValue = numbersToWorkWith.get(850000.0);
                numbersToWorkWith.put(850000.0, entry.getValue() + 0.0);
            } else if(key.contains("_900000-1000000")) {
                //currentValue = numbersToWorkWith.get(950000.0);
                numbersToWorkWith.put(950000.0, entry.getValue() + 0.0);
            } else if(key.contains("_1000000-1250000")) {
                //currentValue = numbersToWorkWith.get(1125000.0);
                numbersToWorkWith.put(1125000.0, entry.getValue() + 0.0);
            } else if(key.contains("_1250000-1500000")) {
                //currentValue = numbersToWorkWith.get(1375000.0);
                numbersToWorkWith.put(1375000.0, entry.getValue() + 0.0);
            } else if(key.contains("_1500000-2000000")) {
                //currentValue = numbersToWorkWith.get(1750000.0);
                numbersToWorkWith.put(1750000.0, entry.getValue() + 0.0);
            } else if(key.contains("_2000000+")) {
                //currentValue = numbersToWorkWith.get(2500000.0);
                numbersToWorkWith.put(2500000.0, entry.getValue() + 0.0);
            }
        }

        double totalPrice = 0;
        double totalNumberOfHouses = 0;

        for(Map.Entry<Double, Double> entry : numbersToWorkWith.entrySet()) {
            totalPrice = totalPrice + (entry.getKey() * entry.getValue());
            totalNumberOfHouses = totalNumberOfHouses + entry.getValue();
        }

        double average = totalPrice / totalNumberOfHouses;

        Map<Double, Double> mapToReturn = new HashMap<>();
        mapToReturn.put(totalNumberOfHouses, average);
        return mapToReturn;
    }

    private List<String> getUrls() {
        List<String> urls = new ArrayList<>();
        List<Integer> priceLimits = getPriceLimits();

        for(int i = 0; i < priceLimits.size() - 1; i++) {
            String url = "view-source:https://www.funda.nl/koop/heel-nederland/" + priceLimits.get(i) + "-" + priceLimits.get(i + 1)
                    + "/1-dag/" + Math.random();
            urls.add(url);
        }

        urls.add("view-source:https://www.funda.nl/koop/heel-nederland/2000000+/1-dag/");
        return urls;
    }

    private void click(int x, int y) {
        try {
            Robot bot = new Robot();
            bot.mouseMove(x, y);
            bot.mousePress(InputEvent.BUTTON1_MASK);
            bot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void rightClick(int x, int y) {
        try {
            Robot bot = new Robot();
            bot.mouseMove(x, y);
            bot.mousePress(InputEvent.BUTTON3_MASK);
            bot.mouseRelease(InputEvent.BUTTON3_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private String getCurrentDate() {
        java.util.Date date = new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
