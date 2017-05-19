package com.lennart.controller;

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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Configuration
@EnableAutoConfiguration
@RestController
public class Controller extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Controller.class);
    }

    public static void main(String[] args) throws Exception {
//        while(true) {
//            Map<String, String> headLines = retrieveHeadLinesFromNuNl();
//            addHeadLinesToDataBase(headLines);
//            System.out.print(".");
//            TimeUnit.MINUTES.sleep(5);
//        }

        SpringApplication.run(Controller.class, args);
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
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_lp", "root", "");

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
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);

        for (Map.Entry<String, String> entry : headLinesAndHrefs.entrySet()) {
            if(!isHeadLineAlreadyInDatabase(entry.getKey())) {
                java.sql.Connection con;

                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test_lp", "root", "");
                Statement st = con.createStatement();

                st.executeUpdate("INSERT INTO nu_nl_headlines (date, headline, link) VALUES ('" + dateString + "', '" + entry.getKey() + "', '" + entry.getValue() + "')");

                con.close();
            }
        }
    }
}
