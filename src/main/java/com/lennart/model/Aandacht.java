package com.lennart.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Aandacht {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        Aandacht aandacht = new Aandacht();
//        aandacht.updateDb();
//    }

    private void updateDb() throws Exception {
        List<String> users = fillUserList();

        Map<String, Map<String, Double>> allDataForAllUsers = new HashMap<>();

        String date = getCurrentDate();

        for(String user : users) {
            TimeUnit.SECONDS.sleep(1);
            try {
                Map<String, Double> userData = getDataForUser(user);
                System.out.println(".");
                allDataForAllUsers.put(user, userData);
            } catch (Exception e) {
                System.out.println("zzzz error: " + user);
                e.printStackTrace();
            }
        }

        initializeDbConnection();

        Statement st = con.createStatement();

        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
            Map<String, Double> dataForUser = entry.getValue();

            String userName = entry.getKey();
            double followers = dataForUser.get("followers");
            double following = dataForUser.get("following");
            double numberOfPosts = dataForUser.get("numberOfPosts");
            double avNoOfLikesPerPost = dataForUser.get("avNoOfLikesPerPost");
            double avNoOfCommentsPerPost = dataForUser.get("avNoOfCommentsPerPost");
            double avNoOfPostsPerDay = dataForUser.get("avNoOfPostsPerDay");
            double engagement = dataForUser.get("engagement");
            double engagementLast24h = dataForUser.get("engagementLast24h");

            st.executeUpdate("INSERT INTO userdata (" +
                    "entry, " +
                    "date, " +
                    "username, " +
                    "followers, " +
                    "following, " +
                    "numberOfPosts, " +
                    "avNoOfLikesPerPost, " +
                    "avNoOfCommentsPerPost, " +
                    "avNoOfPostsPerDay, " +
                    "engagement, " +
                    "engagementLast24h) " +
                    "VALUES ('" +
                    (getHighestIntEntry("userdata") + 1) + "', '" +
                    date + "', '" +
                    userName + "', '" +
                    followers + "', '" +
                    following + "', '" +
                    numberOfPosts + "', '" +
                    avNoOfLikesPerPost + "', '" +
                    avNoOfCommentsPerPost + "', '" +
                    avNoOfPostsPerDay + "', '" +
                    engagement + "', '" +
                    engagementLast24h + "'" +
                    ")");
        }

        st.close();

        closeDbConnection();
    }

    public Map<String, Double> getDataForUser(String username) throws Exception {
        String script = getRelevantScriptAsString(username);
        List<Integer> likes = getDataOfLastPosts(script, "edge_liked_by", 11);
        List<Integer> timeStamps = getDataOfLastPosts(script, "taken_at_timestamp", 12);
        List<Integer> comments = getDataOfLastPosts(script, "edge_media_to_comment", 11);

        double followers = getFollowers(script);
        double following = getFollowing(script);
        double numberOfPosts = getNumberOfPosts(script);

        double averageNumberOfLikesPerPost = getAverageNumberOfLikesOrCommentsPerPost(likes);
        double averageNumberOfCommentsPerPost = getAverageNumberOfLikesOrCommentsPerPost(comments);
        double averageNumberOfPostsPerDay = averageNumberOfPostsPerDay(timeStamps, username);
        double engagement = ((averageNumberOfLikesPerPost + averageNumberOfCommentsPerPost) / followers);
        double engagementLast24h = getEngagementOfLast24hours(getNumberOfPostsInLast24hours(timeStamps), script, followers);

        Map<String, Double> dataForUser = new HashMap<>();
        dataForUser.put("followers", followers);
        dataForUser.put("following", following);
        dataForUser.put("numberOfPosts", numberOfPosts);
        dataForUser.put("avNoOfLikesPerPost", averageNumberOfLikesPerPost);
        dataForUser.put("avNoOfCommentsPerPost", averageNumberOfCommentsPerPost);
        dataForUser.put("avNoOfPostsPerDay", averageNumberOfPostsPerDay);
        dataForUser.put("engagement", engagement);
        dataForUser.put("engagementLast24h", engagementLast24h);

        return dataForUser;
    }

    private String getRelevantScriptAsString(String username) throws Exception {
        String scriptToReturn = null;

        Document document = Jsoup.connect("https://www.instagram.com/" + username).get();
        Elements scripts = document.getElementsByTag("script");

        for(Element script : scripts) {
            String scriptAsString = script.toString();

            if(scriptAsString.contains("edge_followed_by")) {
                scriptToReturn = scriptAsString;
            }
        }

        return scriptToReturn;
    }

    private double getFollowers(String script) {
        String followers = script.substring(script.indexOf("edge_followed_by"));
        followers = followers.substring(0, followers.indexOf(","));
        followers = removeAllNonNumericCharacters(followers);
        return Double.valueOf(followers);
    }

    private double getFollowing(String script) {
        String following = script.substring(script.indexOf("edge_follow\""));
        following = following.substring(0, following.indexOf(","));
        following = removeAllNonNumericCharacters(following);
        return Double.valueOf(following);
    }

    private double getNumberOfPosts(String script) {
        String following = script.substring(script.indexOf("edge_owner_to_timeline_media"));
        following = following.substring(0, following.indexOf(","));
        following = removeAllNonNumericCharacters(following);
        return Double.valueOf(following);
    }

    private List<Integer> getDataOfLastPosts(String script, String dataType, int numberOfPosts) {
        List<Integer> allData = new ArrayList<>();

        while(script.contains(dataType)) {
            script = script.substring(script.indexOf(dataType));
            script = script.replaceFirst(dataType, "");

            String dataAsString = script.substring(0, script.indexOf(","));
            dataAsString = removeAllNonNumericCharacters(dataAsString);
            allData.add(Integer.valueOf(dataAsString));
        }

        while(allData.size() > numberOfPosts) {
            allData.remove(0);
        }

        return allData;
    }

    private double averageNumberOfPostsPerDay(List<Integer> timestampsOfLast12posts, String user) {
        try {
            int timeStampOfLastPost = timestampsOfLast12posts.get(0);
            int timeStampOf12thPost = timestampsOfLast12posts.get(11);

            int difference = timeStampOfLastPost - timeStampOf12thPost;

            int differenceInMinutes = difference / 60;
            int differenceInHours = differenceInMinutes / 60;
            int differenceInDays = differenceInHours / 24;

            double differenceAsDouble = (double) differenceInDays;

            double postsPerDay = differenceAsDouble / 12;

            postsPerDay = 1 / postsPerDay;

            return postsPerDay;
        } catch (Exception e) {
            System.out.println("error: " + user);
            e.printStackTrace();
        }

        return -100;
    }

    private double getAverageNumberOfLikesOrCommentsPerPost(List<Integer> likesOrCommentsOfLast11posts) {
        int total = 0;

        for(Integer i : likesOrCommentsOfLast11posts) {
            total = total + i;
        }

        double average = total / 11;

        return average;
    }

    private double getNumberOfPostsInLast24hours(List<Integer> timeStamps) {
        long currentTimestamp = new Date().getTime();

        currentTimestamp = currentTimestamp / 1000;

        long timeStamp24ago = currentTimestamp - 86400;

        int counter = 0;

        for(Integer i : timeStamps) {
            if(i > timeStamp24ago) {
                counter++;
            }
        }

        return counter;
    }

    private double getEngagementOfLast24hours(double postsOfLast24hours, String script, double numberOfFollowers) {
        List<Integer> likes = getDataOfLastPosts(script, "edge_liked_by", 12);
        List<Integer> comments = getDataOfLastPosts(script, "edge_media_to_comment", 12);

        double engageCounter = 0;

        for(int i = 0; i < postsOfLast24hours; i++) {
            engageCounter = engageCounter + likes.get(i) + comments.get(i);
        }

        double engagementOfLast24h = engageCounter / numberOfFollowers;

        return engagementOfLast24h;
    }

    public List<String> fillUserList() {
        List<String> users = new ArrayList<>();

        users.add("glam_by_eefje");
        users.add("miss_k_510");
        users.add("melikebeauty");
        users.add("shrn__");
        users.add("soophjee");
        users.add("jessiejazzvuijk");
        users.add("mw_danique");
        users.add("manontilstra");
        users.add("bibibreijman");
        users.add("melanielatooy");
        users.add("jilllvd");
        users.add("jiami");
        users.add("teskedeschepper");
        users.add("disfordazzle");
        users.add("roosmarijnbraspenning");
        users.add("robin.balou");
        users.add("kickiedeklijn");
        users.add("valerierooyackers");
        users.add("lynnvandevorst");
        users.add("indiasuy");
        users.add("kikiboreel");
        users.add("juliettemesterom");
        users.add("elinevanhaasteren");
        users.add("meialinde");
        users.add("sylvanadejong");
        users.add("ishh");
        users.add("liekevdhoorn");
        users.add("lizachloe");
        users.add("esmeetrouw");
        users.add("kimberlyesmee");
        users.add("thefashionblognl");
        users.add("bemmay");
        users.add("xlisadam");
        users.add("germainepeels");
        users.add("marijezuurveld");
        users.add("saar");
        users.add("allaboutkimberly");
        users.add("cheyennehinrichs");
        users.add("dutchfashion_freak");
        users.add("milouhendriks");
        users.add("miloutjioe");
        users.add("naomivredevoort");
        users.add("ramijntje");
        users.add("inesgomesdefaria");
        users.add("remke");
        users.add("irisamberr");
        users.add("fennleonoor");
        users.add("brunabear");
        users.add("maximefelicia");
        users.add("esmeehuiden");
        users.add("sophiamolen");
        users.add("rebelrosey");
        users.add("elise.bak");
        users.add("xarisha");
        users.add("lisannede_bruijn");
        users.add("daphisticated");
        users.add("sophiemay");
        users.add("nathalieejw");
        users.add("queenofjetlags");
        users.add("stephsa");
        users.add("carmenmattijssen");
        users.add("vivianhoorn");
        users.add("babetteroosstyling");
        users.add("frederiqueligtvoet");
        users.add("noahfleur");
        users.add("martinemaureen");
        users.add("iriskristen");
        users.add("laurensophiemessack");
        users.add("lyannemeijer");
        users.add("danizijlstra");
        users.add("femke.vermaas");
        users.add("evaschaapp");
        users.add("louiselutkes");
        users.add("anouskaband");
        users.add("nikkimarinus");
        users.add("amakahamelijnck");
        users.add("daniellecamille");
        users.add("claartjerose");
        users.add("bentheliem");
        //users.add("elaisaya");
        users.add("naomiavrahami");
        users.add("lizzyperridon");
        users.add("victoriawaldau");
        users.add("rianne.meijer");
        users.add("clairecampman");
        users.add("esmeevanes");
        users.add("marlouisee");
        users.add("nataliadrzy");
        users.add("lauraponticorvo");
        users.add("clairepronk");
        users.add("sophiemilzink");
        users.add("daniquehogguer");
        users.add("amarenns");
        users.add("fabiennehekman");
        users.add("laurawilrycx");
        users.add("ophelievita");
        users.add("robinsingels");
        users.add("prishella");
        users.add("moderosaofficial");
        users.add("mendiewijker");
        users.add("ninawarink");
        users.add("laurelvuijk");
        users.add("daelostylo");
        users.add("laurenvansam");
        users.add("larissabruin");
        users.add("highonthoseheels");
        users.add("daniellevanginkel");
        users.add("mirthewillemsx");
        users.add("gabrieladegraaf");
        users.add("mailili.s");
        users.add("nickysmol");
        users.add("robineblickman");
        users.add("avji_");
        users.add("charelleschriek");
        users.add("noaismay");
        users.add("maudschellekens");
        users.add("daniellederidder");
        users.add("nadiaidder");
        users.add("romyydb");
        users.add("jolielot");
        users.add("s0phieramaekers");
        users.add("carmenleenen");
        users.add("jamiecrafoord");

        users.add("joshveldhuizen");
        users.add("ankeglamournl");
        users.add("noualiah");
        users.add("fabiola_volkers");
        users.add("daneejosephine");
        users.add("ninaschotpoort");
        users.add("estellehagen");
        users.add("sterrevanwoudenberg");
        users.add("lisevanwijk");
        users.add("hntm_cecilia");
        users.add("sanne");
        users.add("beautygloss");
        users.add("isadeejansen");
        users.add("sophieousri");
        users.add("louschulten");
        users.add("lotteyvette");
        users.add("lotteverlaat");
        users.add("xdyonnex");
        users.add("rachelleruwiel");
        users.add("thedutchbandit");
        users.add("mette_sterre");
        users.add("yuliandvd");

        users.add("albertdrosphotography");
        users.add("een_wasbeer");
        users.add("dutchtoy");

        return users;
    }

    public List<String> fillBnUserList(boolean includingInternational) {
        List<String> users = new ArrayList<>();

        if(includingInternational) {
            users.add("champagnepapi");
            users.add("instagram");
            users.add("gigihadid");
            users.add("jlo");
            users.add("therock");
            users.add("shawnmendes");
            users.add("khloekardashian");
            users.add("nickiminaj");
            users.add("mileycyrus");
            users.add("arianagrande");
            users.add("beyonce");
            users.add("cristiano");
            users.add("kimkardashian");
            users.add("kendalljenner");
            users.add("kyliejenner");
            users.add("justinbieber");
        }

        users.add("ferrisomogyi");
        users.add("viktor_brand");
        users.add("xdebuisonje");
        users.add("wolter_kroes");
        users.add("rene.froger");
        users.add("mariskabauer");
        users.add("fransbauer");
        users.add("halinareijn");
        users.add("patricklaureij");
        users.add("therealhansteeuwen");
        users.add("jesseklaver");
        users.add("sylvana");
        users.add("thijsromer");
        users.add("annadrijver");
        users.add("tim.haars");
        users.add("driesroelvink_official");
        users.add("patrickmartensofficial");
        users.add("marlijnweerdenburg");
        users.add("soundos_el_ahmadi");
        users.add("maximhartman");
        users.add("claudiadebreij");
        users.add("ilsewarringa");
        users.add("jelkavanhouten");
        users.add("henryvanloon");
        users.add("yara_michels");
        users.add("mrsjamieli");
        users.add("stormdennis");
        users.add("phalerieau");
        users.add("vkoblenko");
        users.add("brittdekker92");
        users.add("anoukhoogendijk");
        users.add("shellysterk");
        users.add("tessmilne_");
        users.add("sylvanasimons");
        users.add("leavecaricealone");
        users.add("irene_moors");
        users.add("pauldeleeuw");
        users.add("ryanmarciano");
        users.add("sunneryjames");
        users.add("renskroes");
        users.add("jetvannieuwkerk");
        users.add("frenna");
        users.add("martijn_krabbe");
        users.add("tonyjuniorofficial");
        users.add("markopinsta");
        users.add("eskobarz");
        users.add("boef");
        users.add("josylvio");
        users.add("lizekorpie");
        users.add("maradonnie");
        users.add("vjeze_fur");
        users.add("faberyayo");
        users.add("alexandernl");
        users.add("arminvanbuuren");
        users.add("tiesto");
        users.add("hanwe");
        users.add("eenhoornjoost");
        users.add("frankdane538");
        users.add("gio");
        users.add("barendvandeelen");
        users.add("milanknol");
        users.add("rutgervink");
        users.add("guus.meeuwis");
        users.add("kajvdree");
        users.add("dee");
        users.add("frank3fm");
        users.add("irmaknol");
        users.add("sanderhoogendrn");
        users.add("jochemvang");
        users.add("nickschilder");
        users.add("dylanhaegens");
        users.add("kraantjepappie");
        users.add("basvanteylingen");
        users.add("ruuddewild");
        users.add("freekvonk");
        users.add("debroervanroos");
        users.add("gwenvanpoorten");
        users.add("michielveenstra");
        users.add("arjenlubach");
        users.add("stefanstuktv");
        users.add("domien");
        users.add("jeroentjes");
        users.add("coenswijnenberg");
        users.add("gerardekdom");
        users.add("douwebob");
        users.add("giels");
        users.add("onnedi");
        users.add("ninaschotpoort");
        users.add("ninahouston_");
        users.add("bibi.social_");
        users.add("liekemartens");
        users.add("enzoknol");
        users.add("saskiaweerstand");
        users.add("sterrekoning");
        users.add("klaasvank");
        users.add("ronboszhardofficial");
        users.add("bijrobert");
        users.add("jamietrenite");
        users.add("evelien_de_bruijn");
        users.add("rickpaul");
        users.add("nielsonmusic");
        users.add("sinancan77");
        users.add("rik_van_de_westelaken");
        users.add("sarahchronis");
        users.add("merelwestrik");
        users.add("daphnebunskoek");
        users.add("renategerschtanowitz");
        users.add("evihanssen");
        users.add("dannydemunk");
        users.add("rubenvdmeer");
        users.add("xellycvk");
        users.add("tygo_gernandt");
        users.add("jvdboom");
        users.add("annemariemetstreepje");
        users.add("fatimademelo");
        users.add("winstongerschtanowitz");
        users.add("fayaofficial");
        users.add("sandra_schuurhof");
        users.add("babettevanveenofficial");
        users.add("danielleoerlemans");
        users.add("edsiliarombley");
        users.add("saarkoningsberger");
        users.add("susansmitinstagram");
        users.add("arieboomsmainstagram");
        users.add("maxverstappen1");
        users.add("samanthasteenwijk");
        users.add("jamailoman");
        users.add("official_waylon_music");
        users.add("jan_versteegh");
        users.add("lucywoesthoff_dromenjager");
        users.add("beauvaned");
        users.add("kimkotter");
        users.add("jansmitcom");
        users.add("jochemmyjer");
        users.add("leontineborsato");
        users.add("jennifermaryhoffman");
        users.add("tanjajess");
        users.add("jimbakkum");
        users.add("froukjedeboth");
        users.add("borsato");
        users.add("jandinoasporaat");
        users.add("roxeannehazes");
        users.add("birgit.schuurman");
        users.add("olcay");
        users.add("isahoes");
        users.add("dennisweeningofficial");
        users.add("bobbieden");
        users.add("mykillerbodymotivation");
        users.add("johnnydemolofficial");
        users.add("williamrutten");
        users.add("lecolook");
        users.add("bassmit");
        users.add("gordonheuckeroth");
        users.add("angelagroothuizen");
        users.add("kimlianvdmeij");
        users.add("missmontreal_");
        users.add("mariekeelsinga");
        users.add("miljuschka");
        users.add("nikkie_official");
        users.add("gerard_joling");
        users.add("heleenvanroyen");
        users.add("trijntjeoosterhuis");
        users.add("humbertotan");
        users.add("pattybrard");
        users.add("wendyvandijk3");
        users.add("liekevanlexmond");
        users.add("qtrustfull");
        users.add("gverbaan");
        users.add("nicolettevandam1");
        users.add("daphne_deckers");
        users.add("moniquenoell");
        users.add("nicolettekluijver_");
        users.add("bridgetmaasland");
        users.add("katjaschuurman");
        users.add("andrehazes");
        users.add("dondiablo");
        users.add("hardwell");
        users.add("martingarrix");
        users.add("sneijder10official");
        users.add("stefandevries95");
        users.add("bram.krikke");
        users.add("koenkardashian");
        users.add("geraldine_kemper");
        users.add("vivianhoorn");
        users.add("qucee");
        users.add("claartjerose");
        users.add("thooootje");
        users.add("lizzyvdligt");
        users.add("beautygloss");
        users.add("kalvijn");
        users.add("yolanthecabau");
        users.add("fredvanleer");
        users.add("k2im");
        users.add("bibibreijman");
        users.add("giel");
        users.add("donnyroelvink");
        users.add("bizzey");
        users.add("alibspec");
        users.add("ricoverhoeven");
        users.add("chantaljanzen.official");
        users.add("nochtlii");
        users.add("jiami");
        users.add("doutzen");
        users.add("queenofjetlags");
        users.add("famkelouise_");
        users.add("ronnieflex010");
        users.add("jessiejazzvuijk");
        users.add("ninawarink");
        users.add("sophiemilzink");
        users.add("juultjetieleman");
        users.add("nienkeplas");
        users.add("monicageuze");
        users.add("lilkleine");
        users.add("kajstypetjes");
        users.add("maan.de.st");
        users.add("annanooshin");

        return users;
    }

    public String removeAllNonNumericCharacters(String string) {
        String stringToReturn = string.replaceAll("[^\\d.]", "");

        if(stringToReturn.startsWith(".")) {
            stringToReturn = "0" + stringToReturn;
        }

        return stringToReturn;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
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

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueHighToLow(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>( map.entrySet() );
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue() ).compareTo( o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private String getCurrentDate() {
        java.util.Date date = new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }

    private int getHighestIntEntry(String table) throws Exception {
        int highestIntEntry = 0;

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM " + table + " ORDER BY entry DESC;");

        if(rs.next()) {
            highestIntEntry = rs.getInt("entry");
        }

        st.close();
        rs.close();

        closeDbConnection();

        return highestIntEntry;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
