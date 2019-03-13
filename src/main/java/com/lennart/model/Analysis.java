package com.lennart.model;

import java.sql.*;
import java.util.*;

public class Analysis {

    private Connection con;

    public static void main(String[] args) throws Exception {
        Analysis analysis = new Analysis();

        List<String> users = new ArrayList<>();

        users.add("kimberlyesmee");
        users.add("carmenmattijssen");
        users.add("mette_sterre");
        users.add("cheyennehinrichs");
        users.add("amarenns");

        analysis.printFollowerDataForUser(users);
    }

    private void doTheDailyTop5Analysis() throws Exception {
        Map<String, Double> relFollowersMap = getTopXofFullMap(doDiffAnalysisForStat("relativeFollowers", 1), 20);

        for (Map.Entry<String, Double> entry : relFollowersMap.entrySet()) {
            System.out.println(entry.getKey());
        }
        for (Map.Entry<String, Double> entry : relFollowersMap.entrySet()) {
            System.out.println(convertDoubleToPasteFriendly(entry.getValue() / 100));
        }

        Map<String, Double> absFollowerDiffMap = new LinkedHashMap<>();
        Map<String, Double> engagementMap = new LinkedHashMap<>();

        for(Map.Entry<String, Double> entry : relFollowersMap.entrySet()) {
            String userName = entry.getKey();

            Map<Integer, Map<String, Object>> allDataOfUser = getAllDataOfUser(userName);
            double absoluteFollowerDiff = getAbsoluteDifference("followers", allDataOfUser, 1);

            absFollowerDiffMap.put(entry.getKey(), absoluteFollowerDiff);
        }

        for(Map.Entry<String, Double> entry : relFollowersMap.entrySet()) {
            String userName = entry.getKey();

            Map<String, Double> dataForUser = new Aandacht().getDataForUser(userName);

            double engagement = dataForUser.get("engagement");

            engagementMap.put(entry.getKey(), engagement);
        }

        for(Map.Entry<String, Double> entry : absFollowerDiffMap.entrySet()) {
            System.out.println(entry.getKey());
        }
        for(Map.Entry<String, Double> entry : absFollowerDiffMap.entrySet()) {
            System.out.println(convertDoubleToPasteFriendly(entry.getValue()));
        }

        for(Map.Entry<String, Double> entry : engagementMap.entrySet()) {
            System.out.println(entry.getKey());
        }
        for(Map.Entry<String, Double> entry : engagementMap.entrySet()) {
            System.out.println(convertDoubleToPasteFriendly(entry.getValue()));
        }
    }

    private void dailyTableTop20Analysis() throws Exception {
        Map<String, Double> relFollowerMap = getTopXofFullMap(doDiffAnalysisForStat("relativeFollowers", 1), 5);
        Map<String, Double> relFollowing = getTopXofFullMap(doDiffAnalysisForStat("relativeFollowing", 1), 5);
        Map<String, Double> absPosts = getTopXofFullMap(doDiffAnalysisForStat("absoluteNumberOfPosts", 1), 5);
        Map<String, Double> engagementLast24h = getTopXofFullMap(getEngagementLast24hFromDb(), 5);

        for (Map.Entry<String, Double> entry : relFollowerMap.entrySet()) {
            System.out.println(entry.getKey());
        }
        for (Map.Entry<String, Double> entry : relFollowerMap.entrySet()) {
            System.out.println(convertDoubleToPasteFriendly(entry.getValue()));
        }

        System.out.println();

        for (Map.Entry<String, Double> entry : relFollowing.entrySet()) {
            System.out.println(entry.getKey());
        }
        for (Map.Entry<String, Double> entry : relFollowing.entrySet()) {
            System.out.println(convertDoubleToPasteFriendly(entry.getValue()));
        }

        System.out.println();

        for (Map.Entry<String, Double> entry : absPosts.entrySet()) {
            System.out.println(entry.getKey());
        }
        for (Map.Entry<String, Double> entry : absPosts.entrySet()) {
            System.out.println(convertDoubleToPasteFriendly(entry.getValue()));
        }

        System.out.println();

        for (Map.Entry<String, Double> entry : engagementLast24h.entrySet()) {
            System.out.println(entry.getKey());
        }
        for (Map.Entry<String, Double> entry : engagementLast24h.entrySet()) {
            System.out.println(convertDoubleToPasteFriendly(entry.getValue()));
        }
    }

    private void printFollowerDataForUser(List<String> users) throws Exception {
        initializeDbConnection();

        for(String user : users) {
            System.out.println("USER: " + user);
            System.out.println();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM userdata WHERE username = '" + user + "' ORDER BY entry ASC;");

            while(rs.next()) {
                System.out.println(convertDoubleToPasteFriendly(rs.getDouble("followers")));
            }

            rs.close();
            st.close();

            System.out.println();
            System.out.println();
            System.out.println();
        }

        closeDbConnection();
    }

    private Map<String, Double> getTopXofFullMap(Map<String, Double> fullMap, int topLimit) {
        Map<String, Double> top5map = new HashMap<>();

        int counter = 0;

        for (Map.Entry<String, Double> entry : fullMap.entrySet()) {
            counter++;

            if(counter <= topLimit) {
                top5map.put(entry.getKey(), entry.getValue());
            } else {
                break;
            }
        }

        top5map = Aandacht.sortByValueHighToLow(top5map);

        return top5map;
    }

    private String convertDoubleToPasteFriendly(double d) {
        String pasteFriendly = String.valueOf(d);
        pasteFriendly = pasteFriendly.replace(".", ",");
        return pasteFriendly;
    }

    private Map<String, Double> doDiffAnalysisForStat(String stat, int daysDifference) throws Exception {
        List<String> allUsers = new Aandacht().fillUserList();

        Map<String, Double> analysisMap = new HashMap<>();

        for(String user : allUsers) {
            if(!user.equals("albertdrosphotography") && !user.equals("een_wasbeer") && !user.equals("dutchtoy")) {
                Map<Integer, Map<String, Object>> allDataOfUser = getAllDataOfUser(user);

                if(stat.equals("absoluteFollowers")) {
                    double absoluteFollowerDiff = getAbsoluteDifference("followers", allDataOfUser, daysDifference);
                    analysisMap.put(user, absoluteFollowerDiff);
                } else if(stat.equals("relativeFollowers")) {
                    double relativeFollowerDiff = getRelativeDifference("followers", allDataOfUser, daysDifference);
                    analysisMap.put(user, relativeFollowerDiff);
                } else if(stat.equals("absoluteFollowing")) {
                    double absoluteFollowingDiff = getAbsoluteDifference("following", allDataOfUser, daysDifference);
                    analysisMap.put(user, absoluteFollowingDiff);
                } else if(stat.equals("relativeFollowing")) {
                    double relativeFollowingDiff = getRelativeDifference("following", allDataOfUser, daysDifference);
                    analysisMap.put(user, relativeFollowingDiff);
                } else if(stat.equals("absoluteNumberOfPosts")) {
                    double absoluteNoOfPostsDiff = getAbsoluteDifference("numberOfPosts", allDataOfUser, daysDifference);
                    analysisMap.put(user, absoluteNoOfPostsDiff);
                } else if(stat.equals("absoluteAvNoOfLikesPerPost")) {
                    double absoluteAvNoOfLikesPerPostDiff = getAbsoluteDifference("avNoOfLikesPerPost", allDataOfUser, daysDifference);
                    analysisMap.put(user, absoluteAvNoOfLikesPerPostDiff);
                } else if(stat.equals("absoluteAvNoOfPostsPerDay")) {
                    double absoluteAvNoOfPostsPerDayDiff = getAbsoluteDifference("avNoOfPostsPerDay", allDataOfUser, daysDifference);
                    analysisMap.put(user, absoluteAvNoOfPostsPerDayDiff);
                } else if(stat.equals("absoluteEngagement")) {
                    double absoluteEngagementDiff = getAbsoluteDifference("engagement", allDataOfUser, daysDifference);
                    analysisMap.put(user, absoluteEngagementDiff);
                }
            }
        }

        analysisMap = Aandacht.sortByValueHighToLow(analysisMap);
        return analysisMap;
    }

    private Map<String, Double> getEngagementLast24hFromDb() throws Exception {
        List<String> allUsers = new Aandacht().fillUserList();

        Map<String, Double> analysisMap = new HashMap<>();

        initializeDbConnection();

        for(String user : allUsers) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM userdata WHERE username = '" + user + "' ORDER BY entry DESC;");

            if(rs.next()) {
                double engagementLast24h = rs.getDouble("engagementLast24h");
                analysisMap.put(user, engagementLast24h);
            }

            rs.close();
            st.close();
        }

        closeDbConnection();

        analysisMap = Aandacht.sortByValueHighToLow(analysisMap);
        return analysisMap;
    }

    private Map<Integer, Map<String, Object>> getAllDataOfUser(String user) throws Exception {
        Map<Integer, Map<String, Object>> allDataOfUsers = new TreeMap<>(Collections.reverseOrder());

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM userdata WHERE username = '" + user + "' ORDER BY entry DESC;");

        while(rs.next()) {
            Map<String, Object> dataPerEntry = new HashMap<>();

            dataPerEntry.put("date", rs.getString("date"));
            dataPerEntry.put("followers", rs.getDouble("followers"));
            dataPerEntry.put("following", rs.getDouble("following"));
            dataPerEntry.put("numberOfPosts", rs.getDouble("numberOfPosts"));
            dataPerEntry.put("avNoOfLikesPerPost", rs.getDouble("avNoOfLikesPerPost"));
            dataPerEntry.put("avNoOfCommentsPerPost", rs.getDouble("avNoOfCommentsPerPost"));
            dataPerEntry.put("avNoOfPostsPerDay", rs.getDouble("avNoOfPostsPerDay"));
            dataPerEntry.put("engagement", rs.getDouble("engagement"));

            allDataOfUsers.put(rs.getInt("entry"), dataPerEntry);
        }

        rs.close();
        st.close();

        closeDbConnection();

        return allDataOfUsers;
    }

    private double getAbsoluteDifference(String stat, Map<Integer, Map<String, Object>> allDataOfUser, int daysDifference) {
        List<Map<String, Object>> dataOfCurrentAndTargetDate = getDataOfCurrentAndTargetDateForUser(allDataOfUser, daysDifference);

        Map<String, Object> currentDateData = dataOfCurrentAndTargetDate.get(0);
        Map<String, Object> targetDateData = dataOfCurrentAndTargetDate.get(1);

        double valueToReturn;

        if(currentDateData != null && targetDateData != null) {
            double current = (double) currentDateData.get(stat);
            double target = (double) targetDateData.get(stat);

            valueToReturn = current - target;
        } else {
            valueToReturn = -5000;
        }

        return valueToReturn;
    }

    private double getRelativeDifference(String stat, Map<Integer, Map<String, Object>> allDataOfUser, int daysDifference) {
        List<Map<String, Object>> dataOfCurrentAndTargetDate = getDataOfCurrentAndTargetDateForUser(allDataOfUser, daysDifference);

        Map<String, Object> currentDateData = dataOfCurrentAndTargetDate.get(0);
        Map<String, Object> targetDateData = dataOfCurrentAndTargetDate.get(1);

        double valueToReturn;

        if(currentDateData != null && targetDateData != null) {
            double current = (double) currentDateData.get(stat);
            double target = (double) targetDateData.get(stat);
            double difference = current - target;

            valueToReturn = (difference / target) * 100;
        } else {
            valueToReturn = -5000;
        }

        return valueToReturn;
    }

    private List<Map<String, Object>> getDataOfCurrentAndTargetDateForUser(Map<Integer, Map<String, Object>> allDataOfUser, int daysDifference) {
        Map<String, Object> dataOfLastDate = allDataOfUser.entrySet().iterator().next().getValue();

        Map<String, Object> dataOfComparisonDate = null;

        int counter = -1;

        for (Map.Entry<Integer, Map<String, Object>> entry : allDataOfUser.entrySet()) {
            counter++;

            if(counter == daysDifference) {
                dataOfComparisonDate = entry.getValue();
                break;
            }
        }

        List<Map<String, Object>> dataOfCurrentAndTargetDate = new ArrayList<>();
        dataOfCurrentAndTargetDate.add(dataOfLastDate);
        dataOfCurrentAndTargetDate.add(dataOfComparisonDate);

        return dataOfCurrentAndTargetDate;
    }

    private void initializeDbConnection() throws Exception {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        con = DriverManager.getConnection("jdbc:mysql://localhost:3306/insta?&serverTimezone=UTC", "root", "");
    }

    private void closeDbConnection() throws SQLException {
        con.close();
    }
}
