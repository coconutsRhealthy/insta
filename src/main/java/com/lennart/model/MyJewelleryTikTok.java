package com.lennart.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by LennartMac on 24/08/2022.
 */
public class MyJewelleryTikTok {

    public static void main(String[] args) throws Exception {
        List<String> potentialCodes = new MyJewelleryTikTok().getPotentialCodes(new MyJewelleryTikTok().getFullHtml());
        potentialCodes.forEach(code -> {
            System.out.println(code);
            System.out.println();
        });
    }


    private List<String> getPotentialCodes(String fullHtml) {
        Set<String> potentialCodes = new HashSet<>();
        List<String> codePatterns = getCodePatterns();

        for(String pattern : codePatterns) {
            String htmlCopy = fullHtml;

            while(htmlCopy.contains(pattern)) {
                String relevantPart = htmlCopy.substring(htmlCopy.indexOf(pattern) - 20, htmlCopy.indexOf(pattern) + 20);
                potentialCodes.add(relevantPart);

                int indexFromWhereToRemove = htmlCopy.indexOf(pattern) + 50;

                //System.out.println(indexFromWhereToRemove);

                htmlCopy = htmlCopy.substring(indexFromWhereToRemove);

                //htmlCopy = htmlCopy.replaceFirst(relevantPart, "removedRelevantPart");
            }
        }

        List<String> potentialCodesList = potentialCodes.stream().collect(Collectors.toList());
        //Collections.sort(potentialCodesList);
        return potentialCodesList;
    }

    private List<String> getCodePatterns() {
        List<String> codePatterns = new ArrayList<>();

        List<String> alphabet = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z");

        for(String letter : alphabet) {
            codePatterns.add(letter + "-15");
            codePatterns.add(letter + "15");
            codePatterns.add("15-" + letter);
            codePatterns.add("15" + letter);
        }

        return codePatterns;
    }

    private String getFullHtml() throws Exception {
        File file = new File("/Users/LennartMac/Desktop/testmj.rtf");

        String fileText = "";
        try (Reader fileReader = new FileReader(file)) {
            BufferedReader bufReader = new BufferedReader(fileReader);

            String line = bufReader.readLine();

            while (line != null) {
                fileText = fileText + line;
                line = bufReader.readLine();
            }

            bufReader.close();
            fileReader.close();
        }

        return fileText;
    }

}
