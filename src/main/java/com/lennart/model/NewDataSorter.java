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

    private Map<String, String> sortNewBatch(Map<String, String> newBatchToSort, List<List<String>> existingBatches) {
        newBatchToSort.put("**new copmanies**", "**new companies**");
        Map<String, Double> averagePositions = new HashMap<>();

        for (String word : newBatchToSort.values()) {
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

            if (word.equals("temu") || word.equals("bylashbabe") || word.equals("idealofsweden") || word.equals("vitakruid") ||
                word.equals("desenio") || word.equals("pinkgellac")) {
                double randomHighAveragePosition = 4.0 + (new Random().nextDouble());
                averagePositions.put(word, randomHighAveragePosition);
            }

            if (word.equals("**new companies**")) {
                double artificialHighNumberForSorting = 5000.0;
                averagePositions.put(word, artificialHighNumberForSorting);
            }
        }

        List<Map.Entry<String, String>> mapAsList = new ArrayList<>(newBatchToSort.entrySet());

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

        Map<String, String> duplicates = getDuplicateCompanies(newDataFullLineAndCompanies);
        newDataFullLineAndCompanies.keySet().removeAll(duplicates.keySet());

        Map<String, String> sortedNewData = sortNewBatch(newDataFullLineAndCompanies, trainingData);

        Map<String, String> sortedNewDataDuplicatesAddedBack = addDuplicatesBackToMap(sortedNewData, duplicates);

        sortedNewDataDuplicatesAddedBack.keySet().forEach(System.out::println);
    }

    public List<List<String>> readDataFromDataDirective(String filePath) {
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

    public List<List<String>> readDataFromArchive(String filePath) {
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

    private Map<String, String> getDuplicateCompanies(Map<String, String> inputMap) {
        Map<String, String> result = new LinkedHashMap<>();
        Map<String, Integer> valueCounts = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : inputMap.entrySet()) {
            String value = entry.getValue();

            valueCounts.put(value, valueCounts.getOrDefault(value, 0) + 1);

            if (valueCounts.get(value) > 1) {
                result.put(entry.getKey(), value);
            }
        }

        return result;
    }

    private Map<String, String> addDuplicatesBackToMap(Map<String, String> sortedNewBatch, Map<String, String> duplicates) {
        //1 make a map newCompanies
        //2 make a map firstHalfSortedNewBatchExcludingNewCompanies
        //3 make a map secondHalfSortedNewBatchExcludingNewCompanies
        //4 make a new (empty) map secondHalfSortedPlusDuplicates
        //5 randomly put entries from secondHalfSortedNewBatchExcludingNewCompanies and duplicates into the new empty
        //secondHalfSortedPlusDuplicates map until both secondHalfSortedNewBatchExcludingNewCompanies and duplicates are empty
        //6 make a new empty map named toReturn
        //7 putall elements of firstHalfSortedNewBatchExcludingNewCompanies into this
        //putall elements of secondHalfSortedPlusDuplicates into this
        //putall elements of newCompanies into this
        //8 return toReturn;

        //1
        Map<String, String> newCompanies = new LinkedHashMap<>();
        boolean newCompaniesStarted = false;

        for(Map.Entry<String, String> entry : sortedNewBatch.entrySet()) {
            if(entry.getKey().equals("**new copmanies**")) {
                newCompaniesStarted = true;
            }

            if(newCompaniesStarted) {
                newCompanies.put(entry.getKey(), entry.getValue());
            }
        }

        //2
        Map<String, String> sortedNewBatchCopy = new LinkedHashMap<>(sortedNewBatch);
        sortedNewBatchCopy.keySet().removeAll(newCompanies.keySet());

        int size = sortedNewBatchCopy.size();
        int halfSize = size / 2;

        Map<String, String> firstHalfSortedNewBatchExcludingNewCompanies = sortedNewBatchCopy.entrySet().stream()
                .limit(halfSize)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        //3
        Map<String, String> secondHalfSortedNewBatchExcludingNewCompanies = new LinkedHashMap<>(sortedNewBatchCopy);
        secondHalfSortedNewBatchExcludingNewCompanies.keySet().removeAll(firstHalfSortedNewBatchExcludingNewCompanies.keySet());

        //4
        Map<String, String> secondHalfSortedPlusDuplicates = new LinkedHashMap<>();

        //5
        List<Map.Entry<String, String>> list1 = new ArrayList<>(secondHalfSortedNewBatchExcludingNewCompanies.entrySet());
        List<Map.Entry<String, String>> list2 = new ArrayList<>(duplicates.entrySet());

        Random random = new Random();
        while (!list1.isEmpty() || !list2.isEmpty()) {
            if (!list1.isEmpty() && (list2.isEmpty() || random.nextBoolean())) {
                Map.Entry<String, String> entry1 = list1.remove(0);
                secondHalfSortedPlusDuplicates.put(entry1.getKey(), entry1.getValue());
            } else {
                Map.Entry<String, String> entry2 = list2.remove(0);
                secondHalfSortedPlusDuplicates.put(entry2.getKey(), entry2.getValue());
            }
        }

        //6
        Map<String, String> toReturn = new LinkedHashMap<>();

        //7
        toReturn.putAll(firstHalfSortedNewBatchExcludingNewCompanies);
        toReturn.putAll(secondHalfSortedPlusDuplicates);
        toReturn.putAll(newCompanies);

        //8
        return toReturn;
    }
}
