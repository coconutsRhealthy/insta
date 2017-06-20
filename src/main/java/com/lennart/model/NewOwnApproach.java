package com.lennart.model;

import com.lennart.controller.Controller;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * Created by LPO21630 on 19-6-2017.
 */
public class NewOwnApproach {

    public static void main(String[] args) throws Exception {
        NewOwnApproach newOwnApproach = new NewOwnApproach();
        //newOwnApproach.myNewOwnCompareLast("carney");
    }

    public Map<String, List<String>> getHeadlinesPerBuzzWord(Map<String, Double> buzzWords, Controller controller) throws Exception {
        Map<String, List<String>> headlinesPerBuzzWord = new HashMap<>();

        for (Map.Entry<String, Double> entry : buzzWords.entrySet()) {
            List<String> headLinesForWord = myNewOwnCompareLast(entry.getKey(), controller);

            if(headLinesForWord.size() >= 3) {
                headlinesPerBuzzWord.put(entry.getKey(), headLinesForWord);
            }
        }
        return headlinesPerBuzzWord;
    }

    private List<String> myNewOwnCompareLast(String word, Controller controller) throws Exception {
        FurtherWordAnalysis furtherWordAnalysis = new FurtherWordAnalysis();

        List<Element> elementsPerWord = furtherWordAnalysis.getAllElementsPerWord(word, controller);
        List<String> headlinesForWord = furtherWordAnalysis.getHeadlinesPerWord(elementsPerWord, word);

        List<Set<String>> wordSetsPerHeadline = new ArrayList<>();

        for(int i = 0; i < headlinesForWord.size(); i++) {
            wordSetsPerHeadline.add(new HashSet<>());
            wordSetsPerHeadline.get(i).addAll(Arrays.asList(headlinesForWord.get(i).split(" ")));
        }

        List<String> allWordsCombined = new ArrayList<>();

        for(Set<String> set : wordSetsPerHeadline) {
            allWordsCombined.addAll(set);
        }

        allWordsCombined = removeBlackListWords(allWordsCombined);
        allWordsCombined = removeTheKeyword(allWordsCombined, word);

        Map<String, Integer> wordsRankedByOccurenceAll = new HashMap<>();
        Map<String, Integer> wordsRankedByOccurenceTwoOrMore = new HashMap<>();

        for(String s : allWordsCombined) {
            if(wordsRankedByOccurenceAll.get(s) == null) {
                int frequency = Collections.frequency(allWordsCombined, s);
                wordsRankedByOccurenceAll.put(s, frequency);
            }
        }

        for (Map.Entry<String, Integer> entry : wordsRankedByOccurenceAll.entrySet()) {
            if(entry.getValue() > 2) {
                wordsRankedByOccurenceTwoOrMore.put(entry.getKey(), entry.getValue());
            }
        }

        List<String> headlinesToRemove = new ArrayList<>();
        loop: for(String headline : headlinesForWord) {
            for (Map.Entry<String, Integer> entry : wordsRankedByOccurenceTwoOrMore.entrySet()) {
                if(headline.contains(entry.getKey())) {
                    for (Map.Entry<String, Integer> entry2 : wordsRankedByOccurenceTwoOrMore.entrySet()) {
                        if(!entry.getKey().equals(entry2.getKey()) && headline.contains(entry2.getKey())) {
                            continue loop;
                        }
                    }
                }
            }
            headlinesToRemove.add(headline);
        }

        headlinesForWord.removeAll(headlinesToRemove);
        return headlinesForWord;
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
        blackListWords.add("our");
        blackListWords.add("new");
        blackListWords.add("more");
        blackListWords.add("with");
        blackListWords.add("news");
        blackListWords.add("ago");
        blackListWords.add("about");
        blackListWords.add("over");
        blackListWords.add("up");
        blackListWords.add("out");
        blackListWords.add("all");

        allWords.removeAll(blackListWords);

        return allWords;
    }

    private List<String> removeTheKeyword(List<String> allWords, String keyWord) {
        allWords.removeAll(Collections.singleton(keyWord));
        return allWords;
    }
}
