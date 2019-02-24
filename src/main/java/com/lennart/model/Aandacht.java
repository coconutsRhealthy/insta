package com.lennart.model.noswords;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class Aandacht {

    public static void main(String[] args) throws Exception {
        Aandacht aandacht = new Aandacht();

        List<String> users = aandacht.fillUserList();

        Map<String, Map<String, Double>> allDataForAllUsers = new HashMap<>();

        for(String user : users) {
            Map<String, Double> userData = aandacht.getDataForUser(user);
            System.out.println(".");
            allDataForAllUsers.put(user, userData);
        }

        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
            System.out.println(entry.getKey());
        }

        System.out.println();
        System.out.println();

        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
            Map<String, Double> dataForUser = entry.getValue();
            String toPrint = String.valueOf(dataForUser.get("followers"));
            toPrint = toPrint.replace(".", ",");
            System.out.println(toPrint);
        }

        System.out.println();
        System.out.println();

//        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
//            Map<String, Double> dataForUser = entry.getValue();
//            String toPrint = String.valueOf(dataForUser.get("following"));
//            toPrint = toPrint.replace(".", ",");
//            System.out.println(toPrint);
//        }
//
//        System.out.println();
//        System.out.println();
//
//        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
//            Map<String, Double> dataForUser = entry.getValue();
//            String toPrint = String.valueOf(dataForUser.get("numberOfPosts"));
//            toPrint = toPrint.replace(".", ",");
//            System.out.println(toPrint);
//        }
//
//        System.out.println();
//        System.out.println();

//        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
//            Map<String, Double> dataForUser = entry.getValue();
//            String toPrint = String.valueOf(dataForUser.get("avNoOfLikesPerPost"));
//            toPrint = toPrint.replace(".", ",");
//            System.out.println(toPrint);
//        }
//
//        System.out.println();
//        System.out.println();

        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
            Map<String, Double> dataForUser = entry.getValue();
            String toPrint = String.valueOf(dataForUser.get("avNoOfPostsPerDay"));
            toPrint = toPrint.replace(".", ",");
            System.out.println(toPrint);
        }

        System.out.println();
        System.out.println();

        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
            Map<String, Double> dataForUser = entry.getValue();
            String toPrint = String.valueOf(dataForUser.get("likesToFollower"));
            toPrint = toPrint.replace(".", ",");
            System.out.println(toPrint);
        }

        System.out.println();
        System.out.println();

        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
            Map<String, Double> dataForUser = entry.getValue();
            String toPrint = String.valueOf(dataForUser.get("commentsToFollowers"));
            toPrint = toPrint.replace(".", ",");
            System.out.println(toPrint);
        }
//
//        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
//            Map<String, Double> dataForUser = entry.getValue();
//            String toPrint = String.valueOf(dataForUser.get("followersToFollowing"));
//            toPrint = toPrint.replace(".", ",");
//            System.out.println(toPrint);
//        }
//
//        System.out.println();
//        System.out.println();
//
//        for (Map.Entry<String, Map<String, Double>> entry : allDataForAllUsers.entrySet()) {
//            Map<String, Double> dataForUser = entry.getValue();
//            String toPrint = String.valueOf(dataForUser.get("followersToPostRatio"));
//            toPrint = toPrint.replace(".", ",");
//            System.out.println(toPrint);
//        }
//
//        System.out.println();
//        System.out.println();




//        for(String user : users) {
//            String script = aandacht.getRelevantScriptAsString(user);
//            List<Integer> likes = aandacht.getDataOfLastPosts(script, "edge_liked_by", 12);
//            List<Integer> timeStamps = aandacht.getDataOfLastPosts(script, "taken_at_timestamp", 12);
//
//            double averageNumberOfLikesPerPost = aandacht.getAverageNumberOfLikesPerPost(likes);
//            double averageNumberOfPostsPerDay = aandacht.averageNumberOfPostsPerDay(timeStamps);
//
//            double followers = aandacht.getFollowers(script);
//            double following = aandacht.getFollowing(script);
//            double numberOfPosts = aandacht.getNumberOfPosts(script);
//
//            System.out.println("user: " + user);
//            System.out.println("followers: " + followers);
//            System.out.println("average amount of likes per post: " + averageNumberOfLikesPerPost);
//            System.out.println("average number of posts per day: " + averageNumberOfPostsPerDay);
//
//            System.out.println("likes per follower: " + averageNumberOfLikesPerPost / followers);
//            System.out.println("follower to following ratio: " + followers / following);
//            System.out.println("followers to post ratio: " + followers / numberOfPosts);
//
//            System.out.println();
//            System.out.println();
//            System.out.println("*********");
//            System.out.println();
//            System.out.println();
//        }








//        List<String> users = aandacht.fillUserList();
//        Map<String, Integer> usersAndFollowers = new HashMap<>();
//
//        for(String user : users) {
//            String metaTag = aandacht.getRelevantMetaTag(user);
//            String followersString = aandacht.getFollowersStringFromMetaTag(metaTag);
//            int followers = aandacht.convertInstaNumberStringToInt(followersString);
//
//            usersAndFollowers.put(user, followers);
//
//            System.out.println(".");
//        }
//
//        System.out.println();
//        System.out.println();
//
//        usersAndFollowers = aandacht.sortByValue(usersAndFollowers);
//
//        for (Map.Entry<String, Integer> entry : usersAndFollowers.entrySet()) {
//            System.out.println(entry.getKey());
//        }
//
//        for (Map.Entry<String, Integer> entry : usersAndFollowers.entrySet()) {
//            System.out.println(entry.getValue());
//        }
    }

    private Map<String, Double> getDataForUser(String username) throws Exception {
        String script = getRelevantScriptAsString(username);
        List<Integer> likes = getDataOfLastPosts(script, "edge_liked_by", 11);
        List<Integer> timeStamps = getDataOfLastPosts(script, "taken_at_timestamp", 12);
        List<Integer> comments = getDataOfLastPosts(script, "edge_media_to_comment", 11);

        double followers = getFollowers(script);
        double following = getFollowing(script);
        double numberOfPosts = getNumberOfPosts(script);

        double averageNumberOfLikesPerPost = getAverageNumberOfLikesOrCommentsPerPost(likes);
        double averageNumberOfCommentsPerPost = getAverageNumberOfLikesOrCommentsPerPost(comments);
        double averageNumberOfPostsPerDay = averageNumberOfPostsPerDay(timeStamps);
        double likesToFollowerRatio = (averageNumberOfLikesPerPost / followers) * 100;
        double followersToFollowingRatio = followers / following;
        double followersToPostRatio = followers / numberOfPosts;
        double commentsToFollowerRatio = (averageNumberOfCommentsPerPost / followers) * 100;

        Map<String, Double> dataForUser = new HashMap<>();
        dataForUser.put("followers", followers);
        dataForUser.put("following", following);
        dataForUser.put("numberOfPosts", numberOfPosts);
        dataForUser.put("avNoOfLikesPerPost", averageNumberOfLikesPerPost);
        dataForUser.put("avNoOfPostsPerDay", averageNumberOfPostsPerDay);
        dataForUser.put("likesToFollower", likesToFollowerRatio);
        dataForUser.put("followersToFollowing", followersToFollowingRatio);
        dataForUser.put("followersToPostRatio", followersToPostRatio);
        dataForUser.put("commentsToFollowers", commentsToFollowerRatio);

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

    private double averageNumberOfPostsPerDay(List<Integer> timestampsOfLast12posts) {
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
    }

    private double getAverageNumberOfLikesOrCommentsPerPost(List<Integer> likesOrCommentsOfLast11posts) {
        int total = 0;

        for(Integer i : likesOrCommentsOfLast11posts) {
            total = total + i;
        }

        double average = total / 11;

        return average;
    }

    private String getRelevantMetaTag(String userName) throws Exception {
        String relevantMetaTag = "";

        Document document = Jsoup.connect("https://www.instagram.com/" + userName).get();
        Elements metaTags = document.getElementsByTag("meta");

        for(Element metaTag : metaTags) {
            String metaTagText = metaTag.toString();

            if(metaTagText.contains("Followers")) {
                relevantMetaTag = metaTagText;
            }

        }

        return relevantMetaTag;
    }

    private String getFollowersStringFromMetaTag(String metaTag) {
        String followers = metaTag.substring(metaTag.indexOf("Followers") - 10, metaTag.indexOf("Followers") + 10);
        followers = followers.substring(followers.indexOf("\"") + 1, followers.indexOf(" "));
        return followers;
    }

    private int convertInstaNumberStringToInt(String numberString) {
        //numberString = "20k";

        int number = 0;

        if(numberString.contains("k")) {
            int thousandPart;
            int hundredPart;

            if(numberString.contains(".")) {
                String[] parts = numberString.split("\\.");

                //if(parts[1].length() > 1) {
                    hundredPart = Integer.valueOf(parts[1].substring(0, 1));
                //} else {
                thousandPart = Integer.valueOf(parts[0]);
                   // hundredPart = 0;
                //}
            } else {
                numberString = numberString.replace("k", "");
                thousandPart = Integer.valueOf(numberString);
                hundredPart = 0;
            }



            number = (thousandPart * 1000) + (hundredPart * 100);
        } else {
            numberString = numberString.replace(",", "");
            number = Integer.valueOf(numberString);
        }

        return number;
    }

    private double getFollowingFromPageHtml(String pageHtml) {
        return 0;
    }

    private double getNumberOfPostsFromPageHtml(String pageHtml) {
        return 0;
    }

    private List<String> fillUserList() {
        List<String> users = new ArrayList<>();

        users.add("louiselutkes");
        users.add("anouskaband");
        users.add("nikkimarinus");
        users.add("amakahamelijnck");
        users.add("bibikleinenberg");
        users.add("daniellecamille");
        users.add("claartjerose");
        users.add("bentheliem");
        users.add("elaisaya");
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
        users.add("ingewildenberg");
        users.add("carmenleenen");
        users.add("jamiecrafoord");

        return users;
    }

    public String removeAllNonNumericCharacters(String string) {
        String stringToReturn = string.replaceAll("[^\\d.]", "");

        if(stringToReturn.startsWith(".")) {
            stringToReturn = "0" + stringToReturn;
        }

        return stringToReturn;
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
}
