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

    private Connection con;

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Controller.class);
    }

    public static void main(String[] args) throws Exception {
        Controller controller = new Controller();

        controller.testCompareMethode();

        //SpringApplication.run(Controller.class, args);
    }

    @RequestMapping(value = "/startGame", method = RequestMethod.GET)
    public void startGame() throws Exception {
        while(true) {
            retrieveAndStoreNuNl();
            TimeUnit.SECONDS.sleep(5);
            retrieveAndStoreNos();
            TimeUnit.SECONDS.sleep(5);
            retrieveAndStoreTelegraaf();
            TimeUnit.SECONDS.sleep(5);
            retrieveAndStoreAd();
            TimeUnit.SECONDS.sleep(5);
            retrieveAndStoreVolkskrant();

            TimeUnit.MINUTES.sleep(10);
        }
    }

    private void retrieveAndStoreNuNl() {
        try {
            initializeDbConnection();
            Map<String, String> nuNlHeadlines = retrieveHeadLinesFromNuNl();
            addHeadLinesToDataBase("nu_nl_headlines", nuNlHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveAndStoreNos() {
        try {
            initializeDbConnection();
            Map<String, String> nosHeadlines = retrieveHeadLinesFromNos();
            addHeadLinesToDataBase("nos_headlines", nosHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveAndStoreTelegraaf() {
        try {
            initializeDbConnection();
            Map<String, String> telegraafHeadlines = retrieveHeadLinesFromTelegraaf();
            addHeadLinesToDataBase("telegraaf_headlines", telegraafHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveAndStoreAd() {
        try {
            initializeDbConnection();
            Map<String, String> adHeadlines = retrieveHeadLinesFromAd();
            addHeadLinesToDataBase("ad_headlines", adHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void retrieveAndStoreVolkskrant() {
        try {
            initializeDbConnection();
            Map<String, String> volkskrantHeadlines = retrieveHeadLinesFromVolkskrant();
            addHeadLinesToDataBase("volkskrant_headlines", volkskrantHeadlines);
            closeDbConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> retrieveHeadLinesFromNuNl() throws Exception {
        return retrieveHeadLinesFromSite("http://www.nu.nl/algemeen", "div#block-280723 div.block-content.clearfix span.title", "div#block-280723 div.block-content.clearfix a");
    }

    private Map<String, String> retrieveHeadLinesFromNos() throws Exception {
        return retrieveHeadLinesFromSite("http://www.nos.nl", "div#latest_news span.link-hover", "div#latest_news a");
    }

    private Map<String, String> retrieveHeadLinesFromTelegraaf() throws Exception {
        return retrieveHeadLinesFromSite("http://www.telegraaf.nl", "ul:nth-child(2) span.snelnieuws-tekst", "ul:nth-child(2) > li.item > a");
    }

    private Map<String, String> retrieveHeadLinesFromAd() throws Exception {
        Map<String, String> mapIncludingTime = retrieveHeadLinesFromSite("http://www.ad.nl/accept?url=http://www.ad.nl/nieuws", "div.widget.fjs-autoupdate-widget ol.articles-list.fjs-articles-list h3", "div.widget.fjs-autoupdate-widget ol.articles-list.fjs-articles-list a");
        return removeTimeFromAdHeadlines(mapIncludingTime);
    }

    private Map<String, String> retrieveHeadLinesFromVolkskrant() throws Exception {
        Map<String, String> mapIncludingSportAndOpinie = retrieveHeadLinesFromSite("http://www.volkskrant.nl/cookiewall/accept?url=http://www.volkskrant.nl/", "ol.latest-articles.latest-articles--most-recent p", "ol.latest-articles.latest-articles--most-recent a");
        return removeVolkskrantSportAndOpinieHeadlines(mapIncludingSportAndOpinie);
    }

    private Map<String, String> removeTimeFromAdHeadlines(Map<String, String> mapIncluding) {
        Map<String, String> mapExcluding = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : mapIncluding.entrySet()) {
            String headlineNoTime = entry.getKey().substring(5);
            mapExcluding.put(headlineNoTime, entry.getValue());
        }
        return mapExcluding;
    }

    private Map<String, String> removeVolkskrantSportAndOpinieHeadlines(Map<String, String> mapIncluding) {
        Map<String, String> mapExcluding = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : mapIncluding.entrySet()) {
            String fourthElementInLink = entry.getValue().split("/")[3];

            if(!fourthElementInLink.equals("sport") && !fourthElementInLink.equals("opinie")) {
                mapExcluding.put(entry.getKey(), entry.getValue());
            }
        }
        return mapExcluding;
    }

    private Map<String, String> retrieveHeadLinesFromSite(String url, String headlinesSelector,
                                                                 String hrefsSelector) throws Exception {
        Map<String, String> headLinesAndHrefs = new LinkedHashMap<>();

        List<String> headLines = new ArrayList<>();
        List<String> hRefs = new ArrayList<>();

        Document document = Jsoup.connect(url).get();
        Elements headLineTexts = document.select(headlinesSelector);

        for(Element e : headLineTexts) {
            headLines.add(e.text());
        }

        Elements headLineHrefs = document.select(hrefsSelector);
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

    private List<String> escapeSingleQuote(List<String> headLines) {
        List<String> headLinesSingleQuotesEscaped = new ArrayList<>();

        for(String headLine : headLines) {
            String correctHeadLine = headLine.replace("'", "''");
            correctHeadLine = correctHeadLine.replace("\"", "\\\"");
            headLinesSingleQuotesEscaped.add(correctHeadLine);
        }
        return headLinesSingleQuotesEscaped;
    }

    private boolean isHeadLineAlreadyInDatabase(String database, String headLine) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY date;");
        ResultSet rs = st.executeQuery(sql);
        while(rs.next()) {
            String retrievedString = rs.getString("headline").replace("'", "''");
            if(retrievedString.equals(headLine)) {
                return true;
            }
        }
        return false;
    }

    private void addHeadLinesToDataBase(String database, Map<String, String> headLinesAndHrefs) throws Exception {
        Date date = DateUtils.addHours(new Date(), 2);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);

        for (Map.Entry<String, String> entry : headLinesAndHrefs.entrySet()) {
            if(!isHeadLineAlreadyInDatabase(database, entry.getKey())) {
                int entryValue = getHighestIntEntry(database) + 1;
                Statement st = con.createStatement();
                st.executeUpdate("INSERT INTO " + database + " (entry, date, headline, link) VALUES ('" + entryValue + "', '" + dateString + "', '" + entry.getKey() + "', '" + entry.getValue() + "')");
            }
        }
    }

    private int getHighestIntEntry(String database) throws Exception {
        Statement st = con.createStatement();
        String sql = ("SELECT * FROM " + database + " ORDER BY entry DESC;");
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

    private void testCompareMethode() throws Exception {
        Document document = Jsoup.connect("http://www.cnn.com/").get();
        Document document2 = Jsoup.connect("http://www.nytimes.com/").get();
        Document document3 = Jsoup.connect("http://www.theguardian.com/").get();
        Document document4 = Jsoup.connect("http://www.washingtonpost.com").get();
        Document document5 = Jsoup.connect("http://bbc.co.uk/news").get();

        Document document6 = Jsoup.connect("http://www.indiatimes.com").get();
        Document document7 = Jsoup.connect("http://www.huffingtonpost.com").get();
        Document document8 = Jsoup.connect("http://www.foxnews.com").get();
        Document document9 = Jsoup.connect("http://www.bloomberg.com").get();
        Document document10 = Jsoup.connect("http://www.reuters.com").get();
        Document document11 = Jsoup.connect("http://www.usatoday.com").get();
        Document document12 = Jsoup.connect("http://www.cnbc.com").get();
        Document document13 = Jsoup.connect("http://www.nbcnews.com").get();
        Document document14 = Jsoup.connect("http://www.chinadaily.com.cn").get();
        Document document15 = Jsoup.connect("http://www.indianexpress.com").get();
        Document document16 = Jsoup.connect("http://www.latimes.com").get();
        Document document17 = Jsoup.connect("http://www.nypost.com").get();
        Document document18 = Jsoup.connect("http://www.news.com.au").get();
        Document document19 = Jsoup.connect("http://www.cbsnews.com").get();
        Document document20 = Jsoup.connect("http://www.abcnews.go.com").get();
        Document document21 = Jsoup.connect("http://www.dailymail.co.uk").get();
        Document document22 = Jsoup.connect("http://www.thesun.co.uk").get();
        Document document23 = Jsoup.connect("http://www.standard.co.uk").get();
        Document document24 = Jsoup.connect("http://www.mirror.co.uk").get();
        Document document25 = Jsoup.connect("http://www.telegraph.co.uk").get();
        Document document26 = Jsoup.connect("http://www.dailystar.co.uk").get();
        Document document27 = Jsoup.connect("http://www.ft.com").get();
        Document document28 = Jsoup.connect("http://www.independent.co.uk").get();

        Set<String> cnn = getSetOfWordsFromDocument(document);
        Set<String> nyTimes = getSetOfWordsFromDocument(document2);
        Set<String> theGuardian = getSetOfWordsFromDocument(document3);
        Set<String> washingtonPost = getSetOfWordsFromDocument(document4);
        Set<String> bbc = getSetOfWordsFromDocument(document5);

        Set<String> indiaTimes = getSetOfWordsFromDocument(document6);
        Set<String> huffingtonPost = getSetOfWordsFromDocument(document7);
        Set<String> foxNews = getSetOfWordsFromDocument(document8);
        Set<String> bloomBerg = getSetOfWordsFromDocument(document9);
        Set<String> reuters = getSetOfWordsFromDocument(document10);
        Set<String> usaToday = getSetOfWordsFromDocument(document11);
        Set<String> cnbc = getSetOfWordsFromDocument(document12);
        Set<String> nbcnews = getSetOfWordsFromDocument(document13);
        Set<String> chinaDaily = getSetOfWordsFromDocument(document14);
        Set<String> indianExpress = getSetOfWordsFromDocument(document15);
        Set<String> laTimes = getSetOfWordsFromDocument(document16);
        Set<String> nyPost = getSetOfWordsFromDocument(document17);
        Set<String> newsAu = getSetOfWordsFromDocument(document18);
        Set<String> cbsNews = getSetOfWordsFromDocument(document19);
        Set<String> abcNews = getSetOfWordsFromDocument(document20);
        Set<String> dailyMail = getSetOfWordsFromDocument(document21);
        Set<String> theSun = getSetOfWordsFromDocument(document22);
        Set<String> standard = getSetOfWordsFromDocument(document23);
        Set<String> mirror = getSetOfWordsFromDocument(document24);
        Set<String> telegraph = getSetOfWordsFromDocument(document25);
        Set<String> dailystar = getSetOfWordsFromDocument(document26);
        Set<String> ft = getSetOfWordsFromDocument(document27);
        Set<String> independent = getSetOfWordsFromDocument(document28);

        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(cnn);
        combinedList.addAll(nyTimes);
        combinedList.addAll(theGuardian);
        combinedList.addAll(washingtonPost);
        combinedList.addAll(bbc);

        combinedList.addAll(indiaTimes);
        combinedList.addAll(huffingtonPost);
        combinedList.addAll(foxNews);
        combinedList.addAll(bloomBerg);
        combinedList.addAll(reuters);
        combinedList.addAll(usaToday);
        combinedList.addAll(cnbc);
        combinedList.addAll(nbcnews);
        combinedList.addAll(chinaDaily);
        combinedList.addAll(indianExpress);
        combinedList.addAll(laTimes);
        combinedList.addAll(nyPost);
        combinedList.addAll(newsAu);
        combinedList.addAll(cbsNews);
        combinedList.addAll(abcNews);
        combinedList.addAll(dailyMail);
        combinedList.addAll(theSun);
        combinedList.addAll(standard);
        combinedList.addAll(mirror);
        combinedList.addAll(telegraph);
        combinedList.addAll(dailystar);
        combinedList.addAll(ft);
        combinedList.addAll(independent);

        Map<String, Integer> occurrenceMap = new HashMap<>();

        for(String word : combinedList) {
            if(occurrenceMap.get(word) == null) {
                int frequency = Collections.frequency(combinedList, word);
                occurrenceMap.put(word, frequency);
            }
        }

        occurrenceMap = sortByValue(occurrenceMap);

        System.out.println("wacht");
    }

    private List<String> getCommonWords() {
        List<String> commonWords = new ArrayList<>();

        commonWords.add("in");
        commonWords.add("van");
        commonWords.add("op");
        commonWords.add("voor");
        commonWords.add("en");
        commonWords.add("met");
        commonWords.add("de");
        commonWords.add("bij");
        commonWords.add("om");
        commonWords.add("door");
        commonWords.add("na");
        commonWords.add("is");
        commonWords.add("het");
        commonWords.add("nieuwe");
        commonWords.add("uit");
        commonWords.add("zich");
        commonWords.add("wil");
        commonWords.add("dood");
        commonWords.add("aan");
        commonWords.add("niet");
        commonWords.add("vs");
        commonWords.add("te");
        commonWords.add("cel");
        commonWords.add("jaar");
        commonWords.add("een");
        commonWords.add("voorkomen");
        commonWords.add("zaak");
        commonWords.add("man");
        commonWords.add("meer");
        commonWords.add("dat");
        commonWords.add("krijgt");
        commonWords.add("geen");
        commonWords.add("moet");
        commonWords.add("tegen");
        commonWords.add("als");
        commonWords.add("naar");
        commonWords.add("weer");
        commonWords.add("eigen");
        commonWords.add("dit");
        commonWords.add("nederlandse");
        commonWords.add("gaat");
        commonWords.add("maar");
        commonWords.add("gewonden");
        commonWords.add("doden");
        commonWords.add("over");
        commonWords.add("er");
        commonWords.add("af");
        commonWords.add("den");
        commonWords.add("opnieuw");
        commonWords.add("zonder");
        commonWords.add("gevonden");
        commonWords.add("witte");
        commonWords.add("deze");
        commonWords.add("zeggen");
        commonWords.add("ook");
        commonWords.add("nu");
        commonWords.add("nodig");
        commonWords.add("die");
        commonWords.add("wordt");
        commonWords.add("eerst");
        commonWords.add("grote");
        commonWords.add("alsnog");
        commonWords.add("kost");
        commonWords.add("wat");
        commonWords.add("schokt");
        commonWords.add("alle");
        commonWords.add("nog");
        commonWords.add("dode");
        commonWords.add("stapt");
        commonWords.add("tijd");
        commonWords.add("doet");
        commonWords.add("eist");
        commonWords.add("vrouw");
        commonWords.add("eis");
        commonWords.add("toch");
        commonWords.add("schept");
        commonWords.add("trekt");
        commonWords.add("dicht");
        commonWords.add("aantal");
        commonWords.add("vindt");
        commonWords.add("ze");
        commonWords.add("dan");
        commonWords.add("");
        commonWords.add("liveblog");
        commonWords.add("verdachte");
        commonWords.add("minder");
        commonWords.add("zijn");
        commonWords.add("zal");
        commonWords.add("onder");

        return commonWords;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue() ).compareTo( o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private Set<String> getSetOfWordsFromDocument(Document document) {
        String allText = document.text();
        allText = allText.replaceAll("[^A-Za-z0-9 ]", "");
        allText = allText.toLowerCase();

        List<String> listOfWordsTemp = Arrays.asList(allText.split(" "));
        List<String> listOfWords = new ArrayList<>();

        listOfWords.addAll(listOfWordsTemp);
        listOfWords.removeAll(getCommonWords());

        Set<String> setOfWords = new HashSet<>();
        setOfWords.addAll(listOfWords);

        return setOfWords;
    }
}
