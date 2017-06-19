package com.lennart.model;

import java.util.*;

/**
 * Created by LPO21630 on 19-6-2017.
 */
public class NewOwnApproach {

    public static void main(String[] args) {
        NewOwnApproach newOwnApproach = new NewOwnApproach();
        newOwnApproach.myNewOwnCompareLast();

    }

    private void myNewOwnCompareLast() {
        String a = "kevin truong accidental gay parents share unexpected journey to fatherhood";
        String b = "koepkas journey now includes a us open title";
        String c = "a filipino expatriates journey against cancer";
        String d = "cricket mohammed amirs journey from nadir to zenith";
        String e = "an eventful 40year journey for gaokao";
//        String f = "not enough fighter jets is akin to playing cricket with just 7 players air ";
//        String g = "three sukhoi jets deployed to tarakan to shoo fleeing maute terrorists";
//        String h = "russia threatens to target us jets after syria warplane downed";
//        String i = "qatar airways firms up order for 20 737 jets further options dependent on ita";
//        String j = "lockheed martin about to close 37bn deal for f35 jets";

        List<String> originalHeadlines = new ArrayList<>();
        originalHeadlines.add(a);
        originalHeadlines.add(b);
        originalHeadlines.add(c);
        originalHeadlines.add(d);
        originalHeadlines.add(e);
//        originalHeadlines.add(f);
//        originalHeadlines.add(g);
//        originalHeadlines.add(h);
//        originalHeadlines.add(i);
//        originalHeadlines.add(j);

        Set<String> setA = new HashSet();
        Set<String> setB = new HashSet();
        Set<String> setC = new HashSet();
        Set<String> setD = new HashSet();
        Set<String> setE = new HashSet();
//        Set<String> setF = new HashSet();
//        Set<String> setG = new HashSet();
//        Set<String> setH = new HashSet();
//        Set<String> setI = new HashSet();
//        Set<String> setJ = new HashSet();

        setA.addAll(Arrays.asList(a.split(" ")));
        setB.addAll(Arrays.asList(b.split(" ")));
        setC.addAll(Arrays.asList(c.split(" ")));
        setD.addAll(Arrays.asList(d.split(" ")));
        setE.addAll(Arrays.asList(e.split(" ")));
//        setF.addAll(Arrays.asList(f.split(" ")));
//        setG.addAll(Arrays.asList(g.split(" ")));
//        setH.addAll(Arrays.asList(h.split(" ")));
//        setI.addAll(Arrays.asList(i.split(" ")));
//        setJ.addAll(Arrays.asList(j.split(" ")));

        List<String> allWordsCombined = new ArrayList<>();
        allWordsCombined.addAll(setA);
        allWordsCombined.addAll(setB);
        allWordsCombined.addAll(setC);
        allWordsCombined.addAll(setD);
        allWordsCombined.addAll(setE);
//        allWordsCombined.addAll(setF);
//        allWordsCombined.addAll(setG);
//        allWordsCombined.addAll(setH);
//        allWordsCombined.addAll(setI);
//        allWordsCombined.addAll(setJ);

        allWordsCombined = removeBlackListWords(allWordsCombined);
        allWordsCombined = removeTheKeyword(allWordsCombined, "journey");

        Map<String, Integer> wordsRankedByOccurenceAll = new HashMap<>();
        Map<String, Integer> wordsRankedByOccurenceTwoOrMore = new HashMap<>();

        for(String word : allWordsCombined) {
            if(wordsRankedByOccurenceAll.get(word) == null) {
                int frequency = Collections.frequency(allWordsCombined, word);
                wordsRankedByOccurenceAll.put(word, frequency);
            }
        }

        for (Map.Entry<String, Integer> entry : wordsRankedByOccurenceAll.entrySet()) {
            if(entry.getValue() > 1) {
                wordsRankedByOccurenceTwoOrMore.put(entry.getKey(), entry.getValue());
            }
        }

        List<String> headlinesToRemove = new ArrayList<>();
        loop: for(String headline : originalHeadlines) {
            for (Map.Entry<String, Integer> entry : wordsRankedByOccurenceTwoOrMore.entrySet()) {
                if(headline.contains(entry.getKey())) {
                    continue loop;
                }
            }
            headlinesToRemove.add(headline);
        }

        originalHeadlines.removeAll(headlinesToRemove);

        System.out.println("wacht");


    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
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

    private List<String> removeBlackListWords(List<String> allWords) {
        List<String> blackListWords = new ArrayList<>();

        blackListWords.add("the");
        blackListWords.add("to");
        blackListWords.add("in");
        blackListWords.add("of");
        blackListWords.add("a");
        blackListWords.add("and");
        blackListWords.add("for");
        blackListWords.add("on");
        blackListWords.add("is");
        blackListWords.add("2017");
        blackListWords.add("by");
        blackListWords.add("at");
        blackListWords.add("us");
        blackListWords.add("as");
        blackListWords.add("from");
        blackListWords.add("after");
        blackListWords.add("are");
        blackListWords.add("it");
        blackListWords.add("that");
        blackListWords.add("this");
        blackListWords.add("be");
        blackListWords.add("you");
        blackListWords.add("an");
        blackListWords.add("his");
        blackListWords.add("will");
        blackListWords.add("has");
        blackListWords.add("was");
        blackListWords.add("have");
        blackListWords.add("your");
        blackListWords.add("how");
        blackListWords.add("who");
        blackListWords.add("not");
        blackListWords.add("but");
        blackListWords.add("its");
        blackListWords.add("what");
        blackListWords.add("he");
        blackListWords.add("their");
        blackListWords.add("man");
        blackListWords.add("her");
        blackListWords.add("get");
        blackListWords.add("no");

        allWords.removeAll(blackListWords);

        return allWords;
    }

    private List<String> removeTheKeyword(List<String> allWords, String keyWord) {
        allWords.removeAll(Collections.singleton(keyWord));
        return allWords;
    }
}
