package com.lennart.controller;

import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

        controller.testCompareMethodeWordOncePerSite();

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

    private void testCompareMethodeWordOncePerSite() throws Exception {
        //Canada
        Document document1 = Jsoup.connect("http://www.cbc.ca/news").get();
        Document document2 = Jsoup.connect("https://www.thestar.com").get();

        //US
        Document document3 = Jsoup.connect("https://www.nytimes.com").get();
        Document document4 = Jsoup.connect("https://www.washingtonpost.com").get();
        Document document5 = Jsoup.connect("http://www.huffingtonpost.com").get();
        Document document6 = Jsoup.connect("http://www.latimes.com").get();
        Document document7 = Jsoup.connect("http://www.cnn.com").get();
        Document document8 = Jsoup.connect("http://www.foxnews.com").get();
        Document document9 = Jsoup.connect("https://www.usatoday.com").get();
        Document document10 = Jsoup.connect("https://www.wsj.com").get();
        Document document11 = Jsoup.connect("http://www.cnbc.com").get();
        Document document12 = Jsoup.connect("http://www.nbcnews.com").get();

        //Mexico
        Document document13 = Jsoup.connect("http://www.theyucatantimes.com").get();
        Document document14 = Jsoup.connect("http://www.thenews.mx").get();

        //Brazil
        Document document15 = Jsoup.connect("http://riotimesonline.com").get();
        Document document16 = Jsoup.connect("http://www1.folha.uol.com.br/internacional/en").get();

        //Argentina
        Document document17 = Jsoup.connect("http://www.buenosairesherald.com/printed-edition").get();

        //UK
        Document document18 = Jsoup.connect("https://www.theguardian.com").get();
        Document document19 = Jsoup.connect("http://www.bbc.co.uk").get();
        Document document20 = Jsoup.connect("https://www.ft.com").get();
        Document document21 = Jsoup.connect("https://www.thetimes.co.uk").get();
        Document document22 = Jsoup.connect("https://www.thesun.co.uk").get();

        //Ireland
        Document document23 = Jsoup.connect("http://www.irishtimes.com").get();

        //France
        Document document24 = Jsoup.connect("https://www.thelocal.fr").get();
        Document document25 = Jsoup.connect("https://www.mediapart.fr/en/english").get();

        //Germany
        Document document26 = Jsoup.connect("http://www.spiegel.de/international").get();
        Document document27 = Jsoup.connect("https://www.thelocal.de").get();

        //Spain
        Document document28 = Jsoup.connect("http://elpais.com/elpais/inenglish.html").get();

        //Italy
        Document document29 = Jsoup.connect("http://www.ansa.it/english").get();

        //Russia
        Document document30 = Jsoup.connect("https://www.rt.com").get();
        Document document31 = Jsoup.connect("https://themoscowtimes.com").get();

        //South Africa
        Document document32 = Jsoup.connect("http://www.dailysun.co.za").get();
        Document document33 = Jsoup.connect("http://www.timeslive.co.za").get();

        //Nigeria
        Document document34 = Jsoup.connect("http://www.vanguardngr.com").get();

        //Dubai
        Document document35 = Jsoup.connect("http://gulfnews.com").get();

        //Turkey
        Document document36 = Jsoup.connect("https://www.dailysabah.com").get();

        //Iran
        Document document37 = Jsoup.connect("http://www.tehrantimes.com").get();

        //Israel
        Document document38 = Jsoup.connect("https://www.ynetnews.com").get();

        //Oman
        Document document39 = Jsoup.connect("http://timesofoman.com").get();

        //India
        Document document40 = Jsoup.connect("http://timesofindia.indiatimes.com/home/headlines").get();
        Document document41 = Jsoup.connect("http://indianexpress.com").get();

        //China
        Document document42 = Jsoup.connect("http://www.chinadaily.com.cn").get();
        Document document43 = Jsoup.connect("http://www.shanghaidaily.com").get();
        Document document44 = Jsoup.connect("http://www.xinhuanet.com/english").get();
        Document document45 = Jsoup.connect("http://www.globaltimes.cn").get();
        Document document46 = Jsoup.connect("http://www.scmp.com/frontpage/international").get();

        //Japan
        Document document47 = Jsoup.connect("http://www.japantimes.co.jp").get();
        Document document48 = Jsoup.connect("http://the-japan-news.com").get();
        Document document49 = Jsoup.connect("https://japantoday.com").get();

        //Hong Kong
        //Document document50 = Jsoup.connect("www.chinadaily.com.cn/hkedition/hk.html").get();
        Document document51 = Jsoup.connect("https://www.hongkongfp.com").get();

        //Thailand
        Document document52 = Jsoup.connect("http://www.bangkokpost.com").get();

        //Vietnam
        Document document53 = Jsoup.connect("http://vietnamnews.vn").get();

        //Indonesia
        Document document54 = Jsoup.connect("http://www.thejakartapost.com").get();

        //Australia
        Document document55 = Jsoup.connect("http://www.abc.net.au/news").get();
        Document document56 = Jsoup.connect("http://www.theaustralian.com.au").get();

        //New Zealand
        Document document57 = Jsoup.connect("http://www.nzherald.co.nz").get();

        //Other
        Document document58 = Jsoup.connect("http://www.aljazeera.com").get();
        Document document59 = Jsoup.connect("https://www.bloomberg.com").get();
        Document document60 = Jsoup.connect("http://www.reuters.com").get();

        Set<String> cbc = getSetOfWordsFromDocument(document1);
        Set<String> theStar = getSetOfWordsFromDocument(document2);
        Set<String> nyTimes = getSetOfWordsFromDocument(document3);
        Set<String> washingtonPost = getSetOfWordsFromDocument(document4);
        Set<String> huffingtonPost = getSetOfWordsFromDocument(document5);
        Set<String> laTimes = getSetOfWordsFromDocument(document6);
        Set<String> cnn = getSetOfWordsFromDocument(document7);
        Set<String> foxNews = getSetOfWordsFromDocument(document8);
        Set<String> usaToday = getSetOfWordsFromDocument(document9);
        Set<String> wsj = getSetOfWordsFromDocument(document10);
        Set<String> cnbc = getSetOfWordsFromDocument(document11);
        Set<String> nbc = getSetOfWordsFromDocument(document12);
        Set<String> theYucatanTimes = getSetOfWordsFromDocument(document13);
        Set<String> theNewsMx = getSetOfWordsFromDocument(document14);
        Set<String> rioTimesOnline = getSetOfWordsFromDocument(document15);
        Set<String> folha = getSetOfWordsFromDocument(document16);
        Set<String> buenosAiresHerald = getSetOfWordsFromDocument(document17);
        Set<String> theGuardian = getSetOfWordsFromDocument(document18);
        Set<String> bbc = getSetOfWordsFromDocument(document19);
        Set<String> ft = getSetOfWordsFromDocument(document20);
        Set<String> theTimes = getSetOfWordsFromDocument(document21);
        Set<String> theSun = getSetOfWordsFromDocument(document22);
        Set<String> irishTimes = getSetOfWordsFromDocument(document23);
        Set<String> theLocalFr = getSetOfWordsFromDocument(document24);
        Set<String> mediaPartFr = getSetOfWordsFromDocument(document25);
        Set<String> spiegel = getSetOfWordsFromDocument(document26);
        Set<String> theLocalDe = getSetOfWordsFromDocument(document27);
        Set<String> elPais = getSetOfWordsFromDocument(document28);
        Set<String> ansaIt = getSetOfWordsFromDocument(document29);
        Set<String> rt = getSetOfWordsFromDocument(document30);
        Set<String> theMoscowTimes = getSetOfWordsFromDocument(document31);
        Set<String> dailySun = getSetOfWordsFromDocument(document32);
        Set<String> timesLive = getSetOfWordsFromDocument(document33);
        Set<String> vanguardNgr = getSetOfWordsFromDocument(document34);
        Set<String> gulfNews = getSetOfWordsFromDocument(document35);
        Set<String> dailySabah = getSetOfWordsFromDocument(document36);
        Set<String> teheranTimes = getSetOfWordsFromDocument(document37);
        Set<String> ynetNews = getSetOfWordsFromDocument(document38);
        Set<String> timesOfOman = getSetOfWordsFromDocument(document39);
        Set<String> timesOfIndia = getSetOfWordsFromDocument(document40);
        Set<String> indianExpress = getSetOfWordsFromDocument(document41);
        Set<String> chinaDaily = getSetOfWordsFromDocument(document42);
        Set<String> shanghaiDaily = getSetOfWordsFromDocument(document43);
        Set<String> xinHuanet = getSetOfWordsFromDocument(document44);
        Set<String> globalTimesCn = getSetOfWordsFromDocument(document45);
        Set<String> scmp = getSetOfWordsFromDocument(document46);
        Set<String> japanTimes = getSetOfWordsFromDocument(document47);
        Set<String> japanNews = getSetOfWordsFromDocument(document48);
        Set<String> japanToday = getSetOfWordsFromDocument(document49);
        //List<String> chinaDailyHk = getSetOfWordsFromDocument(document50);
        Set<String> hongKongFp = getSetOfWordsFromDocument(document51);
        Set<String> bangKokPost = getSetOfWordsFromDocument(document52);
        Set<String> vietnamNews = getSetOfWordsFromDocument(document53);
        Set<String> jakartaPost = getSetOfWordsFromDocument(document54);
        Set<String> abcAu = getSetOfWordsFromDocument(document55);
        Set<String> theAustralian = getSetOfWordsFromDocument(document56);
        Set<String> nzHerald = getSetOfWordsFromDocument(document57);
        Set<String> alJazeera = getSetOfWordsFromDocument(document58);
        Set<String> bloomberg = getSetOfWordsFromDocument(document59);
        Set<String> reuters = getSetOfWordsFromDocument(document60);

        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(cbc);
        combinedList.addAll(theStar);
        combinedList.addAll(nyTimes);
        combinedList.addAll(washingtonPost);
        combinedList.addAll(huffingtonPost);
        combinedList.addAll(laTimes);
        combinedList.addAll(cnn);
        combinedList.addAll(foxNews);
        combinedList.addAll(usaToday);
        combinedList.addAll(wsj);
        combinedList.addAll(cnbc);
        combinedList.addAll(nbc);
        combinedList.addAll(theYucatanTimes);
        combinedList.addAll(theNewsMx);
        combinedList.addAll(rioTimesOnline);
        combinedList.addAll(folha);
        combinedList.addAll(buenosAiresHerald);
        combinedList.addAll(theGuardian);
        combinedList.addAll(bbc);
        combinedList.addAll(ft);
        combinedList.addAll(theTimes);
        combinedList.addAll(theSun);
        combinedList.addAll(irishTimes);
        combinedList.addAll(theLocalFr);
        combinedList.addAll(mediaPartFr);
        combinedList.addAll(spiegel);
        combinedList.addAll(theLocalDe);
        combinedList.addAll(elPais);
        combinedList.addAll(ansaIt);
        combinedList.addAll(rt);
        combinedList.addAll(theMoscowTimes);
        combinedList.addAll(dailySun);
        combinedList.addAll(timesLive);
        combinedList.addAll(vanguardNgr);
        combinedList.addAll(gulfNews);
        combinedList.addAll(dailySabah);
        combinedList.addAll(teheranTimes);
        combinedList.addAll(ynetNews);
        combinedList.addAll(timesOfOman);
        combinedList.addAll(timesOfIndia);
        combinedList.addAll(indianExpress);
        combinedList.addAll(chinaDaily);
        combinedList.addAll(shanghaiDaily);
        combinedList.addAll(xinHuanet);
        combinedList.addAll(globalTimesCn);
        combinedList.addAll(scmp);
        combinedList.addAll(japanTimes);
        combinedList.addAll(japanNews);
        combinedList.addAll(japanToday);
        //combinedList.addAll(chinaDailyHk);
        combinedList.addAll(hongKongFp);
        combinedList.addAll(bangKokPost);
        combinedList.addAll(vietnamNews);
        combinedList.addAll(jakartaPost);
        combinedList.addAll(abcAu);
        combinedList.addAll(theAustralian);
        combinedList.addAll(nzHerald);
        combinedList.addAll(alJazeera);
        combinedList.addAll(bloomberg);
        combinedList.addAll(reuters);

        Map<String, Integer> occurrenceMapAll = new HashMap<>();

        for(String word : combinedList) {
            if(occurrenceMapAll.get(word) == null) {
                int frequency = Collections.frequency(combinedList, word);
                occurrenceMapAll.put(word, frequency);
            }
        }

        Map<String, Integer> occurrenceMapOnlyTwoOrMore = new HashMap<>();

        for (Map.Entry<String, Integer> entry : occurrenceMapAll.entrySet()) {
            if(entry.getValue() >= 5) {
                occurrenceMapOnlyTwoOrMore.put(entry.getKey(), entry.getValue());
            }
        }

        occurrenceMapAll = sortByValue(occurrenceMapAll);
        occurrenceMapOnlyTwoOrMore = sortByValue(occurrenceMapOnlyTwoOrMore);

        System.out.println("wacht");
    }

    private void testCompareMethodeWordMultiplePerSite() throws Exception {
        //Canada
        Document document1 = Jsoup.connect("http://www.cbc.ca/news").get();
        Document document2 = Jsoup.connect("https://www.thestar.com").get();

        //US
        Document document3 = Jsoup.connect("https://www.nytimes.com").get();
        Document document4 = Jsoup.connect("https://www.washingtonpost.com").get();
        Document document5 = Jsoup.connect("http://www.huffingtonpost.com").get();
        Document document6 = Jsoup.connect("http://www.latimes.com").get();
        Document document7 = Jsoup.connect("http://www.cnn.com").get();
        Document document8 = Jsoup.connect("http://www.foxnews.com").get();
        Document document9 = Jsoup.connect("https://www.usatoday.com").get();
        Document document10 = Jsoup.connect("https://www.wsj.com").get();
        Document document11 = Jsoup.connect("http://www.cnbc.com").get();
        Document document12 = Jsoup.connect("http://www.nbcnews.com").get();

        //Mexico
        Document document13 = Jsoup.connect("http://www.theyucatantimes.com").get();
        Document document14 = Jsoup.connect("http://www.thenews.mx").get();

        //Brazil
        Document document15 = Jsoup.connect("http://riotimesonline.com").get();
        Document document16 = Jsoup.connect("http://www1.folha.uol.com.br/internacional/en").get();

        //Argentina
        Document document17 = Jsoup.connect("http://www.buenosairesherald.com/printed-edition").get();

        //UK
        Document document18 = Jsoup.connect("https://www.theguardian.com").get();
        Document document19 = Jsoup.connect("http://www.bbc.co.uk").get();
        Document document20 = Jsoup.connect("https://www.ft.com").get();
        Document document21 = Jsoup.connect("https://www.thetimes.co.uk").get();
        Document document22 = Jsoup.connect("https://www.thesun.co.uk").get();

        //Ireland
        Document document23 = Jsoup.connect("http://www.irishtimes.com").get();

        //France
        Document document24 = Jsoup.connect("https://www.thelocal.fr").get();
        Document document25 = Jsoup.connect("https://www.mediapart.fr/en/english").get();

        //Germany
        Document document26 = Jsoup.connect("http://www.spiegel.de/international").get();
        Document document27 = Jsoup.connect("https://www.thelocal.de").get();

        //Spain
        Document document28 = Jsoup.connect("http://elpais.com/elpais/inenglish.html").get();

        //Italy
        Document document29 = Jsoup.connect("http://www.ansa.it/english").get();

        //Russia
        Document document30 = Jsoup.connect("https://www.rt.com").get();
        Document document31 = Jsoup.connect("https://themoscowtimes.com").get();

        //South Africa
        Document document32 = Jsoup.connect("http://www.dailysun.co.za").get();
        Document document33 = Jsoup.connect("http://www.timeslive.co.za").get();

        //Nigeria
        Document document34 = Jsoup.connect("http://www.vanguardngr.com").get();

        //Dubai
        Document document35 = Jsoup.connect("http://gulfnews.com").get();

        //Turkey
        Document document36 = Jsoup.connect("https://www.dailysabah.com").get();

        //Iran
        Document document37 = Jsoup.connect("http://www.tehrantimes.com").get();

        //Israel
        Document document38 = Jsoup.connect("https://www.ynetnews.com").get();

        //Oman
        Document document39 = Jsoup.connect("http://timesofoman.com").get();

        //India
        Document document40 = Jsoup.connect("http://timesofindia.indiatimes.com/home/headlines").get();
        Document document41 = Jsoup.connect("http://indianexpress.com").get();

        //China
        Document document42 = Jsoup.connect("http://www.chinadaily.com.cn").get();
        Document document43 = Jsoup.connect("http://www.shanghaidaily.com").get();
        Document document44 = Jsoup.connect("http://www.xinhuanet.com/english").get();
        Document document45 = Jsoup.connect("http://www.globaltimes.cn").get();
        Document document46 = Jsoup.connect("http://www.scmp.com/frontpage/international").get();

        //Japan
        Document document47 = Jsoup.connect("http://www.japantimes.co.jp").get();
        Document document48 = Jsoup.connect("http://the-japan-news.com").get();
        Document document49 = Jsoup.connect("https://japantoday.com").get();

        //Hong Kong
        //Document document50 = Jsoup.connect("www.chinadaily.com.cn/hkedition/hk.html").get();
        Document document51 = Jsoup.connect("https://www.hongkongfp.com").get();

        //Thailand
        Document document52 = Jsoup.connect("http://www.bangkokpost.com").get();

        //Vietnam
        Document document53 = Jsoup.connect("http://vietnamnews.vn").get();

        //Indonesia
        Document document54 = Jsoup.connect("http://www.thejakartapost.com").get();

        //Australia
        Document document55 = Jsoup.connect("http://www.abc.net.au/news").get();
        Document document56 = Jsoup.connect("http://www.theaustralian.com.au").get();

        //New Zealand
        Document document57 = Jsoup.connect("http://www.nzherald.co.nz").get();

        //Other
        Document document58 = Jsoup.connect("http://www.aljazeera.com").get();
        Document document59 = Jsoup.connect("https://www.bloomberg.com").get();
        Document document60 = Jsoup.connect("http://www.reuters.com").get();

        List<String> cbc = getListOfWordsFromDocument(document1);
        List<String> theStar = getListOfWordsFromDocument(document2);
        List<String> nyTimes = getListOfWordsFromDocument(document3);
        List<String> washingtonPost = getListOfWordsFromDocument(document4);
        List<String> huffingtonPost = getListOfWordsFromDocument(document5);
        List<String> laTimes = getListOfWordsFromDocument(document6);
        List<String> cnn = getListOfWordsFromDocument(document7);
        List<String> foxNews = getListOfWordsFromDocument(document8);
        List<String> usaToday = getListOfWordsFromDocument(document9);
        List<String> wsj = getListOfWordsFromDocument(document10);
        List<String> cnbc = getListOfWordsFromDocument(document11);
        List<String> nbc = getListOfWordsFromDocument(document12);
        List<String> theYucatanTimes = getListOfWordsFromDocument(document13);
        List<String> theNewsMx = getListOfWordsFromDocument(document14);
        List<String> rioTimesOnline = getListOfWordsFromDocument(document15);
        List<String> folha = getListOfWordsFromDocument(document16);
        List<String> buenosAiresHerald = getListOfWordsFromDocument(document17);
        List<String> theGuardian = getListOfWordsFromDocument(document18);
        List<String> bbc = getListOfWordsFromDocument(document19);
        List<String> ft = getListOfWordsFromDocument(document20);
        List<String> theTimes = getListOfWordsFromDocument(document21);
        List<String> theSun = getListOfWordsFromDocument(document22);
        List<String> irishTimes = getListOfWordsFromDocument(document23);
        List<String> theLocalFr = getListOfWordsFromDocument(document24);
        List<String> mediaPartFr = getListOfWordsFromDocument(document25);
        List<String> spiegel = getListOfWordsFromDocument(document26);
        List<String> theLocalDe = getListOfWordsFromDocument(document27);
        List<String> elPais = getListOfWordsFromDocument(document28);
        List<String> ansaIt = getListOfWordsFromDocument(document29);
        List<String> rt = getListOfWordsFromDocument(document30);
        List<String> theMoscowTimes = getListOfWordsFromDocument(document31);
        List<String> dailySun = getListOfWordsFromDocument(document32);
        List<String> timesLive = getListOfWordsFromDocument(document33);
        List<String> vanguardNgr = getListOfWordsFromDocument(document34);
        List<String> gulfNews = getListOfWordsFromDocument(document35);
        List<String> dailySabah = getListOfWordsFromDocument(document36);
        List<String> teheranTimes = getListOfWordsFromDocument(document37);
        List<String> ynetNews = getListOfWordsFromDocument(document38);
        List<String> timesOfOman = getListOfWordsFromDocument(document39);
        List<String> timesOfIndia = getListOfWordsFromDocument(document40);
        List<String> indianExpress = getListOfWordsFromDocument(document41);
        List<String> chinaDaily = getListOfWordsFromDocument(document42);
        List<String> shanghaiDaily = getListOfWordsFromDocument(document43);
        List<String> xinHuanet = getListOfWordsFromDocument(document44);
        List<String> globalTimesCn = getListOfWordsFromDocument(document45);
        List<String> scmp = getListOfWordsFromDocument(document46);
        List<String> japanTimes = getListOfWordsFromDocument(document47);
        List<String> japanNews = getListOfWordsFromDocument(document48);
        List<String> japanToday = getListOfWordsFromDocument(document49);
        //List<String> chinaDailyHk = getListOfWordsFromDocument(document50);
        List<String> hongKongFp = getListOfWordsFromDocument(document51);
        List<String> bangKokPost = getListOfWordsFromDocument(document52);
        List<String> vietnamNews = getListOfWordsFromDocument(document53);
        List<String> jakartaPost = getListOfWordsFromDocument(document54);
        List<String> abcAu = getListOfWordsFromDocument(document55);
        List<String> theAustralian = getListOfWordsFromDocument(document56);
        List<String> nzHerald = getListOfWordsFromDocument(document57);
        List<String> alJazeera = getListOfWordsFromDocument(document58);
        List<String> bloomberg = getListOfWordsFromDocument(document59);
        List<String> reuters = getListOfWordsFromDocument(document60);

        List<String> combinedList = new ArrayList<>();
        combinedList.addAll(cbc);
        combinedList.addAll(theStar);
        combinedList.addAll(nyTimes);
        combinedList.addAll(washingtonPost);
        combinedList.addAll(huffingtonPost);
        combinedList.addAll(laTimes);
        combinedList.addAll(cnn);
        combinedList.addAll(foxNews);
        combinedList.addAll(usaToday);
        combinedList.addAll(wsj);
        combinedList.addAll(cnbc);
        combinedList.addAll(nbc);
        combinedList.addAll(theYucatanTimes);
        combinedList.addAll(theNewsMx);
        combinedList.addAll(rioTimesOnline);
        combinedList.addAll(folha);
        combinedList.addAll(buenosAiresHerald);
        combinedList.addAll(theGuardian);
        combinedList.addAll(bbc);
        combinedList.addAll(ft);
        combinedList.addAll(theTimes);
        combinedList.addAll(theSun);
        combinedList.addAll(irishTimes);
        combinedList.addAll(theLocalFr);
        combinedList.addAll(mediaPartFr);
        combinedList.addAll(spiegel);
        combinedList.addAll(theLocalDe);
        combinedList.addAll(elPais);
        combinedList.addAll(ansaIt);
        combinedList.addAll(rt);
        combinedList.addAll(theMoscowTimes);
        combinedList.addAll(dailySun);
        combinedList.addAll(timesLive);
        combinedList.addAll(vanguardNgr);
        combinedList.addAll(gulfNews);
        combinedList.addAll(dailySabah);
        combinedList.addAll(teheranTimes);
        combinedList.addAll(ynetNews);
        combinedList.addAll(timesOfOman);
        combinedList.addAll(timesOfIndia);
        combinedList.addAll(indianExpress);
        combinedList.addAll(chinaDaily);
        combinedList.addAll(shanghaiDaily);
        combinedList.addAll(xinHuanet);
        combinedList.addAll(globalTimesCn);
        combinedList.addAll(scmp);
        combinedList.addAll(japanTimes);
        combinedList.addAll(japanNews);
        combinedList.addAll(japanToday);
        //combinedList.addAll(chinaDailyHk);
        combinedList.addAll(hongKongFp);
        combinedList.addAll(bangKokPost);
        combinedList.addAll(vietnamNews);
        combinedList.addAll(jakartaPost);
        combinedList.addAll(abcAu);
        combinedList.addAll(theAustralian);
        combinedList.addAll(nzHerald);
        combinedList.addAll(alJazeera);
        combinedList.addAll(bloomberg);
        combinedList.addAll(reuters);

        Map<String, Integer> occurrenceMapAll = new HashMap<>();

        for(String word : combinedList) {
            if(occurrenceMapAll.get(word) == null) {
                int frequency = Collections.frequency(combinedList, word);
                occurrenceMapAll.put(word, frequency);
            }
        }

        Map<String, Integer> occurrenceMapOnlyTwoOrMore = new HashMap<>();

        for (Map.Entry<String, Integer> entry : occurrenceMapAll.entrySet()) {
            if(entry.getValue() >= 5) {
                occurrenceMapOnlyTwoOrMore.put(entry.getKey(), entry.getValue());
            }
        }

        occurrenceMapAll = sortByValue(occurrenceMapAll);
        occurrenceMapOnlyTwoOrMore = sortByValue(occurrenceMapOnlyTwoOrMore);

        System.out.println("wacht");
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

        Set<String> setOfWords = new HashSet<>();
        setOfWords.addAll(listOfWords);

        return setOfWords;
    }

    private List<String> getListOfWordsFromDocument(Document document) {
        String allText = document.text();
        allText = allText.replaceAll("[^A-Za-z0-9 ]", "");
        allText = allText.toLowerCase();

        List<String> listOfWordsTemp = Arrays.asList(allText.split(" "));
        List<String> listOfWords = new ArrayList<>();

        listOfWords.addAll(listOfWordsTemp);

        return listOfWords;
    }
}
