package com.lennart.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class NewDataSorter {
    public static void main(String[] args) {
        new NewDataSorter().testMethode();
    }

    private Map<String, String> sortNewBatch(Map<String, String> mapToSort, List<List<String>> existingBatches) {
        Map<String, Double> averagePositions = new HashMap<>();

        for (String word : mapToSort.values()) {
            double totalPosition = 0.0;
            int wordCount = 0;

            for (List<String> batch : existingBatches) {
                int position = batch.indexOf(word);
                if (position != -1) {
                    totalPosition += position;
                    wordCount++;
                }
            }

            if (wordCount > 0) {
                double averagePosition = totalPosition / wordCount;
                averagePositions.put(word, averagePosition);
            }
        }

        List<Map.Entry<String, String>> mapAsList = new ArrayList<>(mapToSort.entrySet());

        Collections.sort(mapAsList, Comparator.comparingDouble(entry ->
                averagePositions.getOrDefault(entry.getValue(), Double.MAX_VALUE)));

        Map<String, String> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : mapAsList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    private void testMethode() {
        List<List<String>> dataDirective = readDataFromDataDirective("/Users/lennartmac/Documents/Projects/diski/src/app/data/data.directive.ts");
        List<List<String>> archiveDataDirective = readDataFromDataDirective("/Users/lennartmac/Documents/Projects/diski/src/app/data/archivedata.directive.ts");
        List<List<String>> archive = readDataFromArchive("/Users/lennartmac/Documents/Projects/diski/src/app/data/archive2.txt");

        List<List<String>> dataDirectiveCompaniesOnly = retainOnlyCompanyInDataLines(dataDirective);
        List<List<String>> archiveDataDirectiveCompaniesOnly = retainOnlyCompanyInDataLines(archiveDataDirective);
        List<List<String>> archiveCompaniesOnly = retainOnlyCompanyInDataLines(archive);

        Map<String, String> newDataFullLineAndCompanies = new LinkedHashMap<>();

        List<String> newDataCompaniesOnly = dataDirectiveCompaniesOnly.get(0);
        List<String> newDataFull = dataDirective.get(0);

        for(int i = 0; i < newDataFull.size(); i++) {
            newDataFullLineAndCompanies.put(newDataFull.get(i), newDataCompaniesOnly.get(i));
        }

        List<List<String>> trainingData = new ArrayList<>();

        trainingData.addAll(dataDirectiveCompaniesOnly.subList(1, dataDirectiveCompaniesOnly.size()));
        trainingData.addAll(archiveDataDirectiveCompaniesOnly);
        trainingData.addAll(archiveCompaniesOnly.subList(0, Math.min(120, archiveCompaniesOnly.size())));

        trainingData = trainingData.stream()
                .filter(list -> list.size() >= 20)
                .collect(Collectors.toList());

        Map<String, String> sortedNewData = sortNewBatch(newDataFullLineAndCompanies, trainingData);
        sortedNewData.keySet().forEach(System.out::println);
    }

    private List<List<String>> readDataFromDataDirective(String filePath) {
        List<List<String>> dataBatches = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            boolean inArray = false;
            List<String> tempArrayList = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("static dataArray")) {
                    inArray = true;
                    continue;
                }

                if (inArray && !line.trim().isEmpty() && !line.contains("];")) {
                    tempArrayList.add(line.trim());
                }

                if (inArray && (line.trim().isEmpty() || line.contains("];"))) {
                    dataBatches.add(new ArrayList<>(tempArrayList));
                    tempArrayList.clear();
                }
            }

            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataBatches;
    }

    private List<List<String>> readDataFromArchive(String filePath) {
        List<List<String>> dataBatches = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            List<String> tempArrayList = new ArrayList<>();

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    tempArrayList.add(line.trim());
                } else {
                    dataBatches.add(new ArrayList<>(tempArrayList));
                    tempArrayList.clear();
                }
            }

            bufferedReader.close();

            if (!tempArrayList.isEmpty()) {
                dataBatches.add(new ArrayList<>(tempArrayList));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataBatches;
    }

    public static List<List<String>> retainOnlyCompanyInDataLines(List<List<String>> inputList) {
        return inputList.stream()
                .map(innerList -> innerList.stream()
                        .map(str -> str.split(",")[0].trim()) // Split by comma and take the first part
                        .map(str -> str.replaceAll("\"", ""))
                        .map(str -> str.contains("(") ? str.substring(0, str.indexOf('(')) : str)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .collect(Collectors.toList());
    }
}
