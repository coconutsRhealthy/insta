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

    private void companySorting() {
        List<List<String>> existingBatches = populateBatches();

        List<String> newBatch = new ArrayList<>();

        newBatch.add("zz");
        newBatch.add("zz");

        sortNewBatch(newBatch, existingBatches);

        //sortNewBatchBasedOnLevenshtein(existingBatches, newBatch);

        // The sorted order is now in the newBatch list
        //for (String word : newBatch) {
        //    System.out.println(word);
        //}
    }

    //other approach of relative position of all batches


    private void sortNewBatch(List<String> newBatch, List<List<String>> existingBatches) {
        Map<String, Double> averagePositions = new HashMap<>();

        // Calculate average positions of words across all batches
        for (String word : newBatch) {
            double totalPosition = 0.0;
            int wordCount = 0;

            // Iterate through all batches to find the position of the word
            for (List<String> batch : existingBatches) {
                int position = batch.indexOf(word);
                if (position != -1) {
                    totalPosition += position;
                    wordCount++;
                }
            }

            // Calculate average position of the word
            if (wordCount > 0) {
                double averagePosition = totalPosition / wordCount;
                averagePositions.put(word, averagePosition);
            }
        }

        // Sort the new batch based on average positions
        Collections.sort(newBatch, Comparator.comparingDouble(word -> averagePositions.getOrDefault(word, Double.MAX_VALUE)));

        newBatch.forEach(company -> System.out.println(company));

        //System.out.println("wacht");
    }

    private void sampleBatchFileMethod() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/Users/lennartmac/Desktop/sample_batches2.txt"));

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("\"")) {
                    line = line.substring(1);
                }

                int index = line.indexOf(" (");
                if (index != -1) {
                    line = line.substring(0, index);
                }

                int commaIndex = line.indexOf(',');

                if (commaIndex != -1) {
                    line = line.substring(0, commaIndex);
                }

                System.out.println(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<List<String>> populateBatches() {
        String filename = "/Users/lennartmac/Desktop/sample_batches2.txt"; // Change this to your file path

        List<List<String>> batches = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            List<String> currentBatch = new ArrayList<>();
            Set<String> wordSet = new HashSet<>(); // To keep track of words within each batch

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    // Empty line detected, add the current batch to the batches list
                    if(currentBatch.size() >= 20) {
                        batches.add(currentBatch);
                    }

                    currentBatch = new ArrayList<>(); // Reset currentBatch for the next batch
                    wordSet.clear(); // Clear the word set for the next batch
                } else {
                    String word = line.trim();
                    if (!wordSet.contains(word)) {
                        currentBatch.add(word); // Add word to the current batch if it's not already present
                        wordSet.add(word); // Add word to the word set
                    }
                }
            }

            // Add the last batch if it's not empty
            if (!currentBatch.isEmpty() && currentBatch.size() >= 20) {
                batches.add(currentBatch);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return batches;
    }

    //"/Users/lennartmac/Documents/Projects/diski/src/app/data/data.directive.ts";

    private List<String> readNewDataFromDataDirective() {
        String filePath = "/Users/lennartmac/Documents/Projects/diski/src/app/data/data.directive.ts";
        List<String> newData = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            boolean inArray = false;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("static dataArray")) {
                    inArray = true;
                    continue;
                }

                if (inArray && !line.trim().isEmpty()) {
                    newData.add(line.trim());
                }

                if (inArray && (line.trim().isEmpty() || line.contains("];"))) {
                    break;
                }
            }

            bufferedReader.close();
            return newData;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void testMethode() {
        List<List<String>> dataDirective = readDataFromDataDirective("/Users/lennartmac/Documents/Projects/diski/src/app/data/data.directive.ts");
        List<List<String>> archiveDataDirective = readDataFromDataDirective("/Users/lennartmac/Documents/Projects/diski/src/app/data/archivedata.directive.ts");
        List<List<String>> archive = readDataFromArchive("/Users/lennartmac/Documents/Projects/diski/src/app/data/archive2.txt");

        List<List<String>> dataDirectiveCompaniesOnly = retainOnlyCompanyInDataLines(dataDirective);
        List<List<String>> archiveDataDirectiveCompaniesOnly = retainOnlyCompanyInDataLines(archiveDataDirective);
        List<List<String>> archiveCompaniesOnly = retainOnlyCompanyInDataLines(archive);

        List<String> newData = dataDirectiveCompaniesOnly.get(0);
        List<List<String>> trainingData = new ArrayList<>();

        trainingData.addAll(dataDirectiveCompaniesOnly.subList(1, dataDirectiveCompaniesOnly.size()));
        trainingData.addAll(archiveDataDirectiveCompaniesOnly);
        trainingData.addAll(archiveCompaniesOnly.subList(0, Math.min(120, archiveCompaniesOnly.size())));

        trainingData = trainingData.stream()
                .filter(list -> list.size() >= 20)
                .collect(Collectors.toList());

        sortNewBatch(newData, trainingData);
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
