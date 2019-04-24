package com.lennart.model;

import java.sql.*;
import java.util.*;

public class Analysis {

    private Connection con;

//    public static void main(String[] args) throws Exception {
//        new Analysis().getBnList("absoluteFollowers", "2019-03-27", 1);
//    }

    private void printAllFollowerDataForUser(String username) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM userdatabn WHERE username = '" + username + "' ORDER BY date ASC;");

        while(rs.next()) {
            System.out.println(convertDoubleToPasteFriendly(rs.getDouble("followers")));
        }

        rs.close();
        st.close();

        closeDbConnection();
    }

    private double getFollowerDiff(String username) throws Exception {
        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM userdatabn WHERE username = '" + username + "' ORDER BY date DESC;");

        double lastValue = -1;
        double secondLastValue = -1;

        int counter = 0;

        while(rs.next()) {
            counter++;

            if(counter == 1) {
                lastValue = rs.getDouble("followers");
            } else if(counter == 2) {
                secondLastValue = rs.getDouble("followers");
            } else {
                break;
            }
        }

        rs.close();
        st.close();

        closeDbConnection();

        System.out.println(lastValue);
        System.out.println(secondLastValue);
        System.out.println(lastValue - secondLastValue);

        return 0;
    }

    public List<BnEr> getBnList(String stat, String date, int daysDifference, boolean bottom) throws Exception {
        Map<String, Double> bnMap = getTopOrBottomXofFullMap(doDiffAnalysisForStat(stat, daysDifference, date), 20, bottom);

        List<BnEr> bnErList = new ArrayList<>();

        for (Map.Entry<String, Double> entry : bnMap.entrySet()) {
            BnEr bnEr = new BnEr();
            bnEr.setName(entry.getKey());
            bnEr.setFollowerGrowth(entry.getValue());
            bnErList.add(bnEr);
        }

        return bnErList;
    }

    private void printFollowerDataForUser(List<String> users) throws Exception {
        initializeDbConnection();

        for(String user : users) {
            System.out.println("USER: " + user);
            System.out.println();

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM userdata WHERE username = '" + user + "' ORDER BY date ASC;");

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

    private Map<String, Double> getTopOrBottomXofFullMap(Map<String, Double> fullMap, int topLimit, boolean bottom) {
        if(bottom) {
            fullMap = Aandacht.sortByValueLowToHigh(fullMap);
        }

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

    private Map<String, Double> doDiffAnalysisForStat(String stat, int daysDifference, String date) throws Exception {
        List<String> allUsers = new Aandacht().fillUserList(false);

        Map<String, Double> analysisMap = new HashMap<>();

        for(String user : allUsers) {
            LinkedHashMap<String, Map<String, Object>> allDataOfUserDateKeys = getAllDataOfUser(user);

            if(stat.equals("absoluteFollowers")) {
                double absoluteFollowerDiff = getAbsoluteDifference("followers", allDataOfUserDateKeys, date, daysDifference);
                analysisMap.put(user, absoluteFollowerDiff);
            } else if(stat.equals("relativeFollowers")) {
                double relativeFollowerDiff = getRelativeDifference("followers", allDataOfUserDateKeys, date, daysDifference);
                analysisMap.put(user, relativeFollowerDiff);
            } else if(stat.equals("absoluteFollowing")) {
                double absoluteFollowingDiff = getAbsoluteDifference("following", allDataOfUserDateKeys, date, daysDifference);
                analysisMap.put(user, absoluteFollowingDiff);
            } else if(stat.equals("relativeFollowing")) {
                double relativeFollowingDiff = getRelativeDifference("following", allDataOfUserDateKeys, date, daysDifference);
                analysisMap.put(user, relativeFollowingDiff);
            } else if(stat.equals("absoluteNumberOfPosts")) {
                double absoluteNoOfPostsDiff = getAbsoluteDifference("numberOfPosts", allDataOfUserDateKeys, date, daysDifference);
                analysisMap.put(user, absoluteNoOfPostsDiff);
            } else if(stat.equals("absoluteAvNoOfLikesPerPost")) {
                double absoluteAvNoOfLikesPerPostDiff = getAbsoluteDifference("avNoOfLikesPerPost", allDataOfUserDateKeys, date, daysDifference);
                analysisMap.put(user, absoluteAvNoOfLikesPerPostDiff);
            } else if(stat.equals("absoluteAvNoOfPostsPerDay")) {
                double absoluteAvNoOfPostsPerDayDiff = getAbsoluteDifference("avNoOfPostsPerDay", allDataOfUserDateKeys, date, daysDifference);
                analysisMap.put(user, absoluteAvNoOfPostsPerDayDiff);
            } else if(stat.equals("absoluteEngagement")) {
                double absoluteEngagementDiff = getAbsoluteDifference("engagement", allDataOfUserDateKeys, date, daysDifference);
                analysisMap.put(user, absoluteEngagementDiff);
            }
        }

        analysisMap = Aandacht.sortByValueHighToLow(analysisMap);
        return analysisMap;
    }

    private Map<String, Double> getEngagementLast24hFromDb() throws Exception {
        List<String> allUsers = new Aandacht().fillUserList(false);

        Map<String, Double> analysisMap = new HashMap<>();

        initializeDbConnection();

        for(String user : allUsers) {
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM userdata WHERE username = '" + user + "' ORDER BY date DESC;");

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

    private LinkedHashMap<String, Map<String, Object>> getAllDataOfUser(String user) throws Exception {
        LinkedHashMap<String, Map<String, Object>> allDataOfUsers = new LinkedHashMap<>();

        initializeDbConnection();

        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM userdatabn WHERE username = '" + user + "' ORDER BY date DESC;");

        while(rs.next()) {
            Map<String, Object> dataPerEntry = new HashMap<>();

            String dateInDb = rs.getString("date");

            dataPerEntry.put("date", dateInDb);
            dataPerEntry.put("followers", rs.getDouble("followers"));
            dataPerEntry.put("following", rs.getDouble("following"));
            dataPerEntry.put("numberOfPosts", rs.getDouble("numberOfPosts"));
            dataPerEntry.put("avNoOfLikesPerPost", rs.getDouble("avNoOfLikesPerPost"));
            dataPerEntry.put("avNoOfCommentsPerPost", rs.getDouble("avNoOfCommentsPerPost"));
            dataPerEntry.put("avNoOfPostsPerDay", rs.getDouble("avNoOfPostsPerDay"));
            dataPerEntry.put("engagement", rs.getDouble("engagement"));

            String correctDateKey = dateInDb.substring(0, dateInDb.indexOf(" "));

            allDataOfUsers.put(correctDateKey, dataPerEntry);
        }

        rs.close();
        st.close();

        closeDbConnection();

        return allDataOfUsers;
    }

    private double getAbsoluteDifference(String stat, LinkedHashMap<String, Map<String, Object>> allDataOfUser, String date, int daysDifference) {
        List<Map<String, Object>> dataOfCurrentAndTargetDate = getDataOfTargetDateAndOfDaysDifferenceForUser(allDataOfUser, date, daysDifference);

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

    private double getRelativeDifference(String stat, LinkedHashMap<String, Map<String, Object>> allDataOfUser, String date, int daysDifference) {
        List<Map<String, Object>> dataOfCurrentAndTargetDate = getDataOfTargetDateAndOfDaysDifferenceForUser(allDataOfUser, date, daysDifference);

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

    private List<Map<String, Object>> getDataOfTargetDateAndOfDaysDifferenceForUser(Map<String, Map<String, Object>> allDataOfUser, String date, int daysDifference) {
        Map<String, Object> dataOfLastDate = allDataOfUser.get(date);

        Map<String, Object> dataOfComparisonDate = null;

        int counter = 0;

        for (Map.Entry<String, Map<String, Object>> entry : allDataOfUser.entrySet()) {
            if(counter == daysDifference) {
                dataOfComparisonDate = entry.getValue();
                break;
            }

            if(entry.getKey().equals(date)) {
                counter++;
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
