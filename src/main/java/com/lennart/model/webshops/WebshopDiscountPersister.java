package com.lennart.model.webshops;

import java.net.URI;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class WebshopDiscountPersister {

    private Connection conn;

    private WebshopDiscountFinder webshopDiscountFinder = new WebshopDiscountFinder();

    public static void main(String[] args) throws Exception {
        new WebshopDiscountPersister().persistDiscountSnippets();
    }

    public void persistDiscountSnippets() throws Exception {
        List<String> shopUrls = new ArrayList<>(getShops().values());

        initializeDbConnection();

        int counter = 1;

        for (String shopUrl : shopUrls) {
            System.out.println(counter++ + ") " + shopUrl);
            int shopId = getOrCreateShop(conn, shopUrl);

            Map<String, List<String>> discountSnippetsForShop = webshopDiscountFinder.extractDiscountSnippetsFromShop(shopUrl);

            for (Map.Entry<String, List<String>> entry : discountSnippetsForShop.entrySet()) {
                String searchTerm = entry.getKey();
                List<String> snippets = entry.getValue();

                for (String snippet : snippets) {
                    persistSnippet(conn, shopId, snippet, searchTerm);
                }
            }
        }

        closeDbConnection();
    }

    private int getOrCreateShop(Connection conn, String shopUrl) throws SQLException {
        String selectShopQuery = "SELECT id FROM shops WHERE url = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectShopQuery)) {
            ps.setString(1, shopUrl);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            } else {
                String insertShopQuery = "INSERT INTO shops (url, name) VALUES (?, ?)";
                try (PreparedStatement insertPs = conn.prepareStatement(insertShopQuery, Statement.RETURN_GENERATED_KEYS)) {
                    insertPs.setString(1, shopUrl);
                    insertPs.setString(2, extractWebshopName(shopUrl));
                    insertPs.executeUpdate();

                    ResultSet generatedKeys = insertPs.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to insert shop and get ID.");
                    }
                }
            }
        }
    }

    private void persistSnippet(Connection conn, int shopId, String snippet, String searchTerm) throws SQLException {
        Date scanDate = new Date(System.currentTimeMillis());
        String insertSnippetQuery = "INSERT IGNORE INTO discount_snippets (shop_id, search_term, snippet, scan_date) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(insertSnippetQuery)) {
            ps.setInt(1, shopId);
            ps.setString(2, searchTerm);
            ps.setString(3, snippet);
            ps.setDate(4, scanDate);
            ps.executeUpdate();
        }
    }

    private Map<String, String> getShops() {
        Map<String, String> webshops = new LinkedHashMap<>();

        webshops.put("about you", "https://en.aboutyou.nl/your-shop");
        webshops.put("accentil.com", "https://accentil.com/");
        webshops.put("achateshop.com", "https://achate.com/");
        webshops.put("aelfriceden.com", "https://www.aelfriceden.com/");
        webshops.put("agradi.nl", "https://agradi.nl/");
        webshops.put("aimnsportswear", "https://www.aimnsportswear.nl/");
        webshops.put("airalo.com", "https://www.airalo.com/");
        webshops.put("airup", "https://shop.air-up.com/nl/nl");
        webshops.put("alectobaby.nl", "https://www.alectobaby.nl/");
        webshops.put("alicehairstyling", "https://alicehairstyling.com/");
        webshops.put("aliexpress", "https://best.aliexpress.com/");
        webshops.put("allmatters.com", "https://nl.allmatters.com/");
        webshops.put("amicicosmetics.com", "https://amicicosmetics.com/");
        webshops.put("angeljuicers.eu", "https://www.angeljuicers.eu/nl/");
        webshops.put("animal-event.nl", "https://animal-event.nl/");
        webshops.put("arket", "https://www.arket.com/en-nl/");
        webshops.put("armedangels", "https://www.armedangels.com/nl-en");
        webshops.put("aromadiffusing.nl", "https://aromadiffusing.nl/");
        webshops.put("artystworld.com", "https://artystworld.com/");
        webshops.put("asos", "https://www.asos.com/nl/dames/");
        webshops.put("athleticbees.com", "https://athleticbees.com/");
        webshops.put("atmooz.com", "https://atmooz.com/");
        webshops.put("atstyles.nl", "https://atstyles.nl/");
        webshops.put("aybl", "https://nl.aybl.com/");

        return webshops;
    }

    private String extractWebshopName(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();

            String[] domainParts = host.split("\\.");

            if (domainParts.length > 2) {
                String firstPart = domainParts[0].toLowerCase();
                if (firstPart.matches("^(www|en|nl|de|fr|es|it|pt)$")) {
                    return domainParts[1];
                }
            }

            return domainParts[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/webshops_discounts?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        conn.close();
    }
}

