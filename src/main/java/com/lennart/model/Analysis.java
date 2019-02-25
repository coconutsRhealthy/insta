package com.lennart.model;

import java.sql.*;
import java.util.*;

public class Analysis {

    private Connection con;

    public static void main(String[] args) throws Exception {
        Analysis analysis = new Analysis();

        analysis.doDiffAnalysisForStat("absoluteNumberOfPosts", 1);
    }

    private void doDiffAnalysisForStat(String stat, int daysDifference) throws Exception {
        List<String> allUsers = new Aandacht().fillUserList();

        Map<String, Double> analysisMap = new HashMap<>();

        for(String user : allUsers) {
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
            } else if(stat.equals("absoluteLikesToFollowerRatio")) {
                double absoluteLikesToFollowerDiff = getAbsoluteDifference("likesToFollowerRatio", allDataOfUser, daysDifference);
                analysisMap.put(user, absoluteLikesToFollowerDiff);
            }
        }

        analysisMap = Aandacht.sortByValue(analysisMap);

        for (Map.Entry<String, Double> entry : analysisMap.entrySet()) {
            System.out.println(entry.getKey() + "     " + entry.getValue());
        }
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
            dataPerEntry.put("likesToFollowerRatio", rs.getDouble("likesToFollowerRatio"));

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

        double current = (double) currentDateData.get(stat);
        double target = (double) targetDateData.get(stat);

        return current - target;
    }

    private double getRelativeDifference(String stat, Map<Integer, Map<String, Object>> allDataOfUser, int daysDifference) {
        List<Map<String, Object>> dataOfCurrentAndTargetDate = getDataOfCurrentAndTargetDateForUser(allDataOfUser, daysDifference);

        Map<String, Object> currentDateData = dataOfCurrentAndTargetDate.get(0);
        Map<String, Object> targetDateData = dataOfCurrentAndTargetDate.get(1);

        double current = (double) currentDateData.get(stat);
        double target = (double) targetDateData.get(stat);
        double difference = current - target;

        return (difference / target) * 100;
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
