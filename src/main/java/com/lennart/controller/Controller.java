package com.lennart.controller;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableAutoConfiguration
@RestController
public class Controller extends SpringBootServletInitializer {

    private static Connection con;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Controller.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Controller.class, args);
    }

    @RequestMapping(value = "/startGame", method = RequestMethod.GET)
    public void startGame() throws Exception {
        while(true) {
            initializeDbConnection();
            Map<String, String> headLines = retrieveHeadLinesFromNuNl();
            addHeadLinesToDataBase(headLines);
            closeDbConnection();
            TimeUnit.MINUTES.sleep(10);
        }
    }

    private static Map<String, String> retrieveHeadLinesFromNuNl() throws Exception {
        Map<String, String> headLinesAndHrefs = new LinkedHashMap<>();

        List<String> headLines = new ArrayList<>();
        List<String> hRefs = new ArrayList<>();

        Document document = Jsoup.connect("http://www.nu.nl").get();
        Elements headLineTexts = document.select("div#block-324321 span.title");
        for(Element e : headLineTexts) {
            headLines.add(e.text());
        }

        Elements headLineHrefs = document.select("div#block-324321 a");
        for(Element e : headLineHrefs) {
            hRefs.add(e.attr("href"));
        }

        headLines = escapeSingleQuote(headLines);
        hRefs = escapeSingleQuote(hRefs);

        for(int i = 0; i < headLines.size(); i++) {
            headLinesAndHrefs.put(headLines.get(i), hRefs.get(i));
        }

        return headLinesAndHrefs;
    }

    private static List<String> escapeSingleQuote(List<String> headLines) {
        List<String> headLinesSingleQuotesEscaped = new ArrayList<>();

        for(String headLine : headLines) {
            String correctHeadLine = headLine.replace("'", "''");
            correctHeadLine = correctHeadLine.replace("\"", "\\\"");
            headLinesSingleQuotesEscaped.add(correctHeadLine);
        }
        return headLinesSingleQuotesEscaped;
    }

    private static boolean isHeadLineAlreadyInDatabase(String headLine) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM nu_nl_headlines ORDER BY date;");
        ResultSet rs = st.executeQuery(sql);
        while(rs.next()) {
            String retrievedString = rs.getString("headline").replace("'", "''");
            if(retrievedString.equals(headLine)) {
                return true;
            }
        }
        return false;
    }

    private static void addHeadLinesToDataBase(Map<String, String> headLinesAndHrefs) throws Exception {
        Date date = DateUtils.addHours(new Date(), 2);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);

        for (Map.Entry<String, String> entry : headLinesAndHrefs.entrySet()) {
            if(!isHeadLineAlreadyInDatabase(entry.getKey())) {
                int entryValue = getHighestIntEntry() + 1;
                Statement st = con.createStatement();
                st.executeUpdate("INSERT INTO nu_nl_headlines (entry, date, headline, link) VALUES ('" + entryValue + "', '" + dateString + "', '" + entry.getKey() + "', '" + entry.getValue() + "')");
            }
        }
    }

    private static int getHighestIntEntry() throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM nu_nl_headlines ORDER BY entry DESC;");
        ResultSet rs = st.executeQuery(sql);

        if(rs.next()) {
            return rs.getInt("entry");
        }
        return 0;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/headlines", "root", "Vuurwerk00");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
