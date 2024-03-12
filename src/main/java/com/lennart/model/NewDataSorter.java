package com.lennart.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class NewDataSorter {
    public static void main(String[] args) {
        new NewDataSorter().companySorting();
    }

    private void companySorting() {
        List<List<String>> existingBatches = populateBatches();

        List<String> newBatch = new ArrayList<>();

        newBatch.add("zz");
        newBatch.add("zz");

        newBatch = shuffleAndRemoveDuplicates(newBatch);

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

        System.out.println("wacht");
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

    private List<String> shuffleAndRemoveDuplicates(List<String> inputList) {
        // Shuffle the ArrayList
        Collections.shuffle(inputList);

        // Remove duplicates
        ArrayList<String> uniqueList = new ArrayList<>();
        for (String element : inputList) {
            if (!uniqueList.contains(element)) {
                uniqueList.add(element);
            }
        }

        return uniqueList;
    }
}
