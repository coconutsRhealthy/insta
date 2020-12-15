package com.lennart.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 18/11/2020.
 */
public class Korting {

    private void printDiscountAccounts() throws Exception {
        List<String> users = new Aandacht().fillUserList(false);

        int counter = 0;

        for(String user : users) {
            if(givesDiscount(user)) {
                System.out.println(user);
            }
            System.out.println(counter++);
        }
    }

    private boolean givesDiscount(String username) throws Exception {
        boolean givesDiscount = false;

        try {
            Document document = Jsoup.connect("https://www.instagram.com/" + username).get();

            String bodyText = document.body().html();

            if(StringUtils.containsIgnoreCase(bodyText, "korting")) {
                givesDiscount = true;
            } else if(StringUtils.containsIgnoreCase(bodyText, "discount")) {
                givesDiscount = true;
            } else if(StringUtils.containsIgnoreCase(bodyText, "% off")) {
                givesDiscount = true;
            } else if(StringUtils.containsIgnoreCase(bodyText, "%off")) {
                givesDiscount = true;
            } else if(StringUtils.containsIgnoreCase(bodyText, "my code")) {
                givesDiscount = true;
            }
        } catch (Exception e) {
            System.out.println("%% - Exception: " + username);
        }

        return givesDiscount;
    }

    //korting op de eerste drie boxen. #HelloFreshNL #partner"}}]},"edge_media_to_comment":{"count":67},"comments_disabled":false,"taken_at_timestamp":1605367285,"edge_liked_by":{"count":9001}

//    private void getTimeStampForKorting(String longKortingString) {
//        String testString = "korting op de eerste drie boxen. #HelloFreshNL #partner\"}}]},\"edge_media_to_comment\":{"count":67},"comments_disabled":false,"taken_at_timestamp":1605367285,"edge_liked_by":{"count":9001}
//
//
//    }

//    public static void main(String[] args) throws Exception {
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
////        System.out.println("hoitje");
////        TimeUnit.SECONDS.sleep(3);
//
//
//        new Korting().testConnectingWithProxy();
//    }

//    private void testConnectingWithProxy() throws Exception {
//
////        URL url = new URL("https://www.google.com/");
////        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("47.91.88.100", 1080)); // or whatever your proxy is
////        HttpURLConnection uc = (HttpURLConnection)url.openConnection(proxy);
////
////        uc.connect();
////
////        String line = null;
////        StringBuffer tmp = new StringBuffer();
////        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
////        while ((line = in.readLine()) != null) {
////            tmp.append(line);
////        }
////
////        Document doc = Jsoup.parse(String.valueOf(tmp));
//
//
//
////        System.setProperty("http.proxyHost", "51.254.69.243");
////        System.setProperty("http.proxyPort", "3128");
//        //System.setProperty("https.proxyHost", "54.34.56.223");
//        //System.setProperty("https.proxyPort", "8080");
////        System.setProperty("java.net.useSystemProxies", "true");
////
////        System.getProperties().put("https.proxyHost", "eije");
////        System.getProperties().put("https.proxyPort", "hmm");
//
//        //System.setProperty("http.proxyHost", "51.154.69.243");
////        //System.setProperty("http.proxyPort", "3126");
////        System.setProperty("http.proxyHost","");
////        System.setProperty("http.proxyPort","");
////        System.setProperty("https.proxyHost","");
////        System.setProperty("https.proxyPort","");
//
//        //System.setProperty("http.proxyHost","eije");
//        //System.out.println(System.getProperties().getProperty("https.proxyHost"));
//
//        //System.out.println(System.getProperty("java.net.useSystemProxies"));
//
////        System.setProperty("http.proxyHost", "aaa");
////        System.setProperty("http.proxyPort", "bbb");
////        System.setProperty("java.net.useSystemProxies", "true");
////        Document doc = Jsoup.connect("https://www.google.com").get();
//
//        Document doc = Jsoup //
//                .connect("https://www.nu.nl") //
//                //.proxy("22.22", 8080) // sets a HTTP proxy
//                .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2") //
//                .header("Content-Language", "en-US") //
//                .get();
//
//        System.out.println(doc.html());
//
//        //System.setProperty("http.proxyHost", null);
//        //System.setProperty("http.proxyPort", null);
//    }

//    public static void main(String[] args) throws Exception {
//        //System.out.println("ja hoooiii!");
//
//        Korting korting = new Korting();
////
////        //for(int i = 0; i < 100; i++) {
////
//////        for(int i = 0; i < 10; i++) {
//////            System.out.println("aap");
//////        }
////
////            //try {
////                String script = korting.getRelevantScriptAsString("annanooshin");
////                double followers = korting.getFollowers(script);
////                System.out.println("followertjesz: " + followers);
//                //TimeUnit.SECONDS.sleep(56);
//            //} catch (Exception e) {
//            //    System.out.println("EXCEPTION!");
//            //    e.printStackTrace();
//                //TimeUnit.SECONDS.sleep(56);
//            //}
//        //}
//    }

//    public static void main(String[] args) throws Exception {
//        new Korting().letsSeePicuki();
//    }

    private void letsSeePicuki() throws Exception {
        Document document = Jsoup.connect("https://www.picuki.com/profile/juliamekkes").get();

        System.out.println(document.html());
    }

    private String getRelevantScriptAsString(String username) throws Exception {
        String scriptToReturn = null;

        Document document = Jsoup.connect("https://www.instagram.com/" + username).
                header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.67 Safari/537.36")
                .get();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        System.out.println(document.html());

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();


        //                .userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2") //
//                .header("Content-Language", "en-US") //

        //System.out.println(document.html());

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

    public String removeAllNonNumericCharacters(String string) {
        String stringToReturn = string.replaceAll("[^\\d.]", "");

        if(stringToReturn.startsWith(".")) {
            stringToReturn = "0" + stringToReturn;
        }

        return stringToReturn;
    }


    private void overallMethod() throws Exception {
        Map<String, List<String>> kortingWordsUsed = identifyKortingWordsUsed("naomivanasofficial");

        List<String> kortingTimeStamps = new ArrayList<>();

        for(Map.Entry<String, List<String>> entry : kortingWordsUsed.entrySet()) {
            kortingTimeStamps.addAll(getKortingTimeStamps(entry.getKey(), entry.getValue()));
        }

        kortingTimeStamps.stream().forEach(timeStampString -> System.out.println(convertTimeStampToDate(timeStampString)));
    }

    private Map<String, List<String>> identifyKortingWordsUsed(String username) throws Exception {
        Document document = Jsoup.connect("https://www.instagram.com/" + username)
                .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.67 Safari/537.36")
                .get();

        List<String> kortingsWordsPresentOnPage = new ArrayList<>();

        String bodyText = document.body().html();

        if(StringUtils.containsIgnoreCase(bodyText, "korting")) {
            kortingsWordsPresentOnPage.add("korting");
        } else if(StringUtils.containsIgnoreCase(bodyText, "discount")) {
            kortingsWordsPresentOnPage.add("discount");
        } else if(StringUtils.containsIgnoreCase(bodyText, "% off")) {
            kortingsWordsPresentOnPage.add("% off");
        } else if(StringUtils.containsIgnoreCase(bodyText, "%off")) {
            kortingsWordsPresentOnPage.add("%off");
        } else if(StringUtils.containsIgnoreCase(bodyText, "my code")) {
            kortingsWordsPresentOnPage.add("my code");
        }

        Map<String, List<String>> bodyWithKortingWords = new HashMap<>();
        bodyWithKortingWords.put(bodyText, kortingsWordsPresentOnPage);

        return bodyWithKortingWords;
    }

    private List<String> getKortingTimeStamps(String bodyText, List<String> kortingsWordsOnPage) {
        List<String> partAfterKortingWordSubstrings = new ArrayList<>();

        for(String kortingsWord : kortingsWordsOnPage) {
            String bodyCopy = bodyText;

            while(StringUtils.containsIgnoreCase(bodyCopy, kortingsWord)) {
                bodyCopy = bodyCopy.substring(bodyCopy.indexOf(kortingsWord) + kortingsWord.length(), bodyCopy.length());
                partAfterKortingWordSubstrings.add(bodyCopy);
            }
        }

        List<String> timeStamps = new ArrayList<>();

        for(String partAfterKortingWord : partAfterKortingWordSubstrings) {
            if(partAfterKortingWord.contains("taken_at_timestamp")) {
                String timeStampPart = partAfterKortingWord.substring(partAfterKortingWord.indexOf
                        ("taken_at_timestamp"), partAfterKortingWord.length());

                int firstIndexOfDoublePoint = timeStampPart.indexOf(":") + 1;
                int firstIndexOfComma = timeStampPart.indexOf(",");

                if(firstIndexOfDoublePoint > 0 && firstIndexOfComma > 0 && firstIndexOfComma > firstIndexOfDoublePoint) {
                    String timeStamp = timeStampPart.substring(firstIndexOfDoublePoint, firstIndexOfComma);
                    timeStamps.add(timeStamp);
                }
            }
        }

        return timeStamps;
    }

    private String convertTimeStampToDate(String timeStampString) {
        long timeStamp = Long.parseLong(timeStampString);
        timeStamp = timeStamp * 1000;
        Date dateOfKorting = new Date(timeStamp);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(dateOfKorting);
    }

    public List<String> fillKortingUsers() {
        List<String> users = new ArrayList<>();

        users.add("lyannemeijer");
        users.add("glam_by_eefje");
        users.add("fennleonoor");
        users.add("prisloelaa");
        users.add("melikebeauty");
        users.add("xlisadam");
        users.add("louiselutkes");
        users.add("lotteverlaat");
        users.add("martinemaureen");
        users.add("fleurverwey");
        users.add("lizzyperridon");
        users.add("nathalieejw");
        users.add("notsosadgrl");
        users.add("saarkoningsberger");
        users.add("larissavanmeerten");
        users.add("anoukmaasofficial");
        users.add("lotje_ig");
        users.add("clairecampman");
        users.add("xdyonnex");
        users.add("miloutjioe");
        users.add("annemerelcom");
        users.add("disfordazzle");
        users.add("miss_k_510");
        users.add("milouhendriks");
        users.add("fitwithmarit");
        users.add("laurabrijde");
        users.add("lizekorpie");
        users.add("frederiqueligtvoet");
        users.add("sarahchronis");
        users.add("kymorasade");
        users.add("sylvanadejong");
        users.add("sherpieksma");
        users.add("renategerschtanowitz");
        users.add("mylenefw");
        users.add("lynnvandevorst");
        users.add("femke.vermaas");
        users.add("marlyvd");
        users.add("yara_michels");
        users.add("lizasips");
        users.add("jolielot");
        users.add("lizzyvdligt");
        users.add("chantalbles");
        users.add("naomiavrahami");
        users.add("hijabhills");
        users.add("bobbieden");
        users.add("xellycvk");
        users.add("arieboomsmainstagram");
        users.add("lauraponticorvo");
        users.add("pippellens");
        users.add("loisbeekhuizen");
        users.add("moderosaofficial");
        users.add("kimkotter");
        users.add("moniquesmit_insta");
        users.add("noualiah");
        users.add("ninaschotpoort");
        users.add("lisevanwijk");
        users.add("naomivanasofficial");
        users.add("beaupotman");
        users.add("latifahladiva");
        users.add("manontilstra");
        users.add("anoukhoogendijk");
        users.add("mrskeizer");
        users.add("juliamekkes");
        users.add("kiya.vanrossum");
        users.add("shellysterk");
        users.add("onnedi");
        users.add("jippheldoorn");
        users.add("estellehagen");
        users.add("elaisaya");
        users.add("nikkie_official");
        users.add("jessiejazzvuijk");
        users.add("ginasingels.official");
        users.add("sennablond");
        users.add("sterrekoning");
        users.add("sophiemilzink");
        users.add("bibibreijman");
        users.add("liekemartens");
        users.add("k2im");
        users.add("queenofjetlags");
        users.add("ninawarink");
        users.add("katjaschuurman");
        users.add("monicageuze");
        users.add("annanooshin");
        users.add("summerdesnooo");
        users.add("dee");
        users.add("juultjetieleman");
        users.add("rianne.meijer");
        users.add("stephsa");
        users.add("nochtlii");
        users.add("sylviemeis");
        users.add("danadijk");
        users.add("lizzygerrits");
        users.add("beautygloss");
        users.add("vita.cleo");
        users.add("dionne_stax");
        users.add("x.raabs.ss");
        users.add("qtrustfull");
        users.add("chloegardenier");
        users.add("jilllvd");
        users.add("nisavanbaelen");
        users.add("lizzykooger");
        users.add("irismarkerink");
        users.add("lynnmachielsen");
        users.add("demiteeuwissen");
        users.add("natalievijfhuizen");
        users.add("olcay");
        users.add("mariekeelsinga");
        users.add("jacky.delang");
        users.add("bysheidaalhoei");
        users.add("shirleyvisser");
        users.add("shifrajumelet");
        users.add("yara.hendriks");
        users.add("famkelouise_");
        users.add("danthescholtenx");
        users.add("bridgetmaasland");
        users.add("hilanoorzai");
        users.add("fienvermeulen");
        users.add("lisamberr");
        users.add("romeestrijd");
        users.add("larissaverbon");
        users.add("sophie_hol");
        users.add("elisejoanne_nl");
        users.add("stacydriesen");
        users.add("carolienspoor");
        users.add("nienkeplas");
        users.add("suzanenfreek");
        users.add("lisaterhorstt");
        users.add("yolanthecabau");
        users.add("krstnaaa");
        users.add("jessie_maya_");
        users.add("teskedeschepper");
        users.add("miljuschka");
        users.add("geraldine_kemper");
        users.add("antje.a");
        users.add("pattybrard");
        users.add("brittscholte");
        users.add("michellekamies");
        users.add("kelly_weekers");
        users.add("joshveldhuizen");
        users.add("tabithamusik");
        users.add("brittmink_");
        users.add("missmontreal_");
        users.add("liefslotte");
        users.add("kimlianvdmeij");
        users.add("jacobienschumacher");
        users.add("kellycaresse");
        users.add("ikbensaske");
        users.add("liemeurs");
        users.add("florinecmh");
        users.add("sofiegraafland");
        users.add("nikkietutorials");
        users.add("abbeyhoes");
        users.add("mariereinders");
        users.add("eliseboers");
        users.add("rlreinders");
        users.add("iamjessicajoan");
        users.add("kimberlybosman");
        users.add("jillparisss");
        users.add("xkiimberly");
        users.add("feranavandalen");
        users.add("roxannekwant");
        users.add("lotkeckeis");
        users.add("nicolettekluijver_");
        users.add("bertriewierenga");
        users.add("naomivaneeren_");
        users.add("tassiejs");
        users.add("kaesutherland");
        users.add("natasjafroger");
        users.add("marijnkuipers");
        users.add("amandadekivit");
        users.add("anddominique");
        users.add("sorayavandermast");
        users.add("esmeemelissa");
        users.add("vivianhoorn");
        users.add("boncolor");
        users.add("rebeccadvg");
        users.add("charlottehoeboer");
        users.add("chimeneesmee");
        users.add("roxeannehazes");
        users.add("martienmeiland");
        users.add("pascaleverhoevenn");
        users.add("devriesroos");
        users.add("chantaaaala");
        users.add("roxyjaycee");
        users.add("chantaljanzen.official");
        users.add("nicolettevandam1");
        users.add("lotterooss");
        users.add("brittdekker92");
        users.add("daniquehogguer");
        users.add("roooom");
        users.add("irisschuurhuis");
        users.add("maximemeiland");
        users.add("therealsmahane");
        users.add("demivthuil");
        users.add("laviesanne");
        users.add("kimjacobs");
        users.add("marijezuurveld");
        users.add("taraverbon");
        users.add("lindamoerland_");
        users.add("robingaby");
        users.add("lunahoogwerf_");
        users.add("fennebraspenning");
        users.add("iriszeilstra");
        users.add("amijeroos");
        users.add("jennifermaryhoffman");
        users.add("tienvanwil");
        users.add("romyhoogers_");
        users.add("deniesmichelle");
        users.add("veracamilla");
        users.add("deniseannayoutube_");
        users.add("aimeevanderpijl");
        users.add("ninahouston");
        users.add("bovandenburg");
        users.add("victoria_vermeer");
        users.add("daniquebossers");
        users.add("michelle_bollen");
        users.add("channahkoerten");
        users.add("maan.de.st");
        users.add("evajinek");
        users.add("chiarahondersx");
        users.add("moisetrustfull");
        users.add("serenaverbon");
        users.add("saskiaweerstand");
        users.add("dichtbijdenise");
        users.add("sabrinaputrix");
        users.add("melaniiev");
        users.add("kusjescharlene_");
        users.add("samantha_de_jong29");
        users.add("juttaleerdam");
        users.add("renee.gunning");
        users.add("samantthxx");
        users.add("lonnekenooteboom");
        users.add("samanthasteenwijk");
        users.add("diesnaloomans");
        users.add("optimavita");
        users.add("cmkx");
        users.add("dehuismuts");
        users.add("wilvantien");
        users.add("daphne_deckers");
        users.add("robinsnelders");
        users.add("sarahrebeccanl");
        users.add("lies_zhara");
        users.add("sylvana");
        users.add("kuss.pas");
        users.add("romyfabiana_");
        users.add("sharontjehhh");
        users.add("marleen.hekkert");
        users.add("mariatailor");
        users.add("bowilkes");
        users.add("patricia_paay");
        users.add("zoegijzen_");
        users.add("daniqueschutjens");
        users.add("jessicakuijpers");
        users.add("lisettelubbers");
        users.add("kikamulderss");
        users.add("charelleschriek");
        users.add("elise_is_here");
        users.add("liv.janssen");
        users.add("jenniefromtheblog");
        users.add("clairelucia");
        users.add("bomink_");
        users.add("moniquenoell");
        users.add("leslie_keijzer");
        users.add("xisabel.kr");
        users.add("janinekho");
        users.add("jadeanna");
        users.add("merelwestrik");
        users.add("l11za");
        users.add("sanneliebrandd");

        users.add("amaka.hamelijnck");
        users.add("irisamber");
        users.add("irisenthoven");
        users.add("eva.rose");
        users.add("juliaheetman");
        users.add("bokado");
        users.add("bibikleinenberg");
        users.add("suuslothmann");
        users.add("sydneytros");
        users.add("girlys_blog");
        users.add("nolavanweel");
        users.add("jipzzaza");
        users.add("roxy.dekkerr");
        users.add("manoujuecardoso");
        users.add("fleurnijbacker");
        users.add("steffi_mercie");
        users.add("stefania_");
        users.add("maritbrugman");
        users.add("sophieousri");
        users.add("jadevw.official");
        users.add("rosalie");
        users.add("maytebellod");
        users.add("bibi.social_");
        users.add("isajolein");
        users.add("elise.bak");
        users.add("juliavanbergen");
        users.add("michellefleur");
        users.add("victoriawaldau");
        users.add("emmakeuven");
        users.add("nuravanvliet");
        users.add("femke.meines");
        users.add("fennahill");
        users.add("bentheliem");
        users.add("roosbolleboos333");
        users.add("eloisevanoranje");
        users.add("rubyv.rossum");
        users.add("clairerose");
        users.add("melanielatooy");
        users.add("didishanna");
        users.add("rosaliebeautyx");
        users.add("jadaborsato");
        users.add("juliahorsten");
        users.add("naomitraa");
        users.add("robinsingels");
        users.add("daphne.m.h");
        users.add("kesvandenbroek");
        users.add("louiselatooy");
        users.add("_isaluna");
        users.add("lunadubois");
        users.add("gioiaparijsofficial");

        users.add("janiceblok");
        users.add("mrsjamieli");
        users.add("elishflesch");
        users.add("dilansabah");
        users.add("rodanyakalsey");
        users.add("laviederosh");
        users.add("mylifeaselize");
        users.add("damiet");
        users.add("clairepronk");
        users.add("benteborst");
        users.add("vonnekebonneke");
        users.add("chicaswinnyalice");
        users.add("lian_vijfschaft");
        users.add("stijn_fransen");
        users.add("lindaa_slomp");

        users.add("ingewildenberg");

        //emmaheesters
        //fayaofficial
        //demiideboer
        //loesjerambaldo
        //lindehighler
        //stephanietency
        //noahcelik
        //kellyhamelijnck
        //melany
        //jaim_x
        //bodemunk_
        //amandabalk_
        //lisasamanthaa
        //douniarijkschroeff
        //rowenakonings_
        //kellyvanderminne
        //boyivandenhoek
        //jackiedeboer
        //maximedeboer
        //maikebeunk
        //michellegroen
        //annevedder
        //mirasoeteman
        //robindeboer_
        //indiasuy
        //rowantess
        //lotteereitsma
        //florianne.charlotte
        //mandyvanderwardt
        //djessyx
        //michellemarlene
        //estellecruijffofficial
        //manon_kool
        //jennifermua1
        //gaby.blaaser


        return users;
    }

    private void printCorretInfluLines() throws Exception {
        File textFile = new File("/Users/LennartMac/Documents/loglines/influencers.txt");

        List<String> textLines;
        try (Reader fileReader = new FileReader(textFile)) {
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = bufReader.readLine();

            textLines = new ArrayList<>();

            while (line != null) {
                textLines.add(line);
                line = bufReader.readLine();
            }

            bufReader.close();
            fileReader.close();
        }

        for(String influencer : textLines) {

            //users.add("ellen_hoog");
            System.out.println("users.add(\"" + influencer + "\");");
        }
    }
}
