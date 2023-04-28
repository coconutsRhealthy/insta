package com.lennart.model;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProfilePicuki {

    public static void main(String[] args) throws Exception {
        new ProfilePicuki().printCodesOfAllFiles();
    }

    private void printCodesOfAllFiles() throws Exception {
        List<File> allFilesFromDir = getAllFilesFromDir("/Users/LennartMac/Documents/picuki");

        for(File file : allFilesFromDir) {
            if(file.isFile()) {
                try {
                    Map<String, Map<String, String>> data = new HashTagPicuki().getData(file);
                    printEverything(data.get("kortingPostsWithIdentifier"), data.get("times"), data.get("urls"), file.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readPage() throws Exception {
        String filePath = "/Users/LennartMac/Documents/picuki/femkezoe.html";
        Map<String, Map<String, String>> data = new HashTagPicuki().getData(new File("/Users/LennartMac/Documents/picuki/femkezoe.html"));
        printEverything(data.get("kortingPostsWithIdentifier"), data.get("times"), data.get("urls"), filePath);
    }

    private void printEverything(Map<String, String> kortingPostsWithIdentifier, Map<String, String> times,
                                 Map<String, String> urls, String filePath) {
        for(Map.Entry<String, String> entry : times.entrySet()) {
            String time = entry.getValue();

            if(timeIsOnOrBeforeBoundry(time, "2 days ago")) {
                String postText = kortingPostsWithIdentifier.get(entry.getKey());
                String url = urls.get(entry.getKey());

                System.out.print(time + "   ");
                System.out.println(postText);
                System.out.println(url);
                System.out.println(filePath);
                System.out.println();
                System.out.println();
            }
        }
    }

    private boolean timeIsOnOrBeforeBoundry(String time, String boundry) {
        int comparatorResult = new LastPostTimeComparator().compare(time, boundry);

        if(comparatorResult == -1 || comparatorResult == 0) {
            return true;
        }

        return false;
    }

    private List<File> getAllFilesFromDir(String dirPath) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();

        List<File> allFiles = Arrays.asList(files);

        return allFiles;
    }

}
