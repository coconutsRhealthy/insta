package com.lennart.model;

import com.lennart.controller.Controller;
import org.apache.airavata.samples.LevenshteinDistanceService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class FurtherWordAnalysis {

    public static void main(String[] args) throws Exception {
        FurtherWordAnalysis furtherWordAnalysis = new FurtherWordAnalysis();

        //furtherWordAnalysis.myOwnCompare();

        //furtherWordAnalysis.getAllElementsPerWord();


//        String headline = "this is the fuckingmonkey";
//
//        String word = " fuckingmonkey ";
//
//        String wordRemoveTrailingSpace = word.replace(" ", "");
//        wordRemoveTrailingSpace = " " + wordRemoveTrailingSpace;
//
//        int length = wordRemoveTrailingSpace.length();
//        String lastPart = headline.substring(headline.length() - length, headline.length());
//
//        System.out.println("wacht");

    }

    public List<Element> getAllElementsPerWord(String word, Controller controller) throws Exception {
//        Controller controller = new Controller();
//        controller.initializeDocuments();

        List<Document> allDocuments = controller.getListOfAllDocuments();
        List<Element> elementsPerWord = new ArrayList<>();

        for(Document document : allDocuments) {
            Elements elements = document.select("a:contains(" + word + ")");
            if(elements.size() != 0) {
                elementsPerWord.add(elements.get(0));
            }
        }
        return elementsPerWord;
    }

    public List<String> getHeadlinesPerWord(List<Element> elementsPerWord, String word) {
        List<String> headlinesPerWord = getUncorrectedHeadlinesPerWord(elementsPerWord);
        headlinesPerWord = trimHeadlinesToMax77Characters(headlinesPerWord);
        headlinesPerWord = removeWrongContainsHeadlines(headlinesPerWord, " " + word + " ");
        return headlinesPerWord;
    }

    public List<String> getRawHeadlinesPerWord(List<Element> elementsPerWord, String word) {
        List<String> headlinesPerWord = getUncorrectedHeadlinesPerWord(elementsPerWord);
        headlinesPerWord = trimHeadlinesToMax77Characters(headlinesPerWord);
        headlinesPerWord = removeWrongContainsHeadlinesForRaw(headlinesPerWord, " " + word + " ");
        return headlinesPerWord;
    }

    public List<String> getHrefHeadlinesPerWord(List<Element> elementsPerWord, String word) {
        List<String> headlinesPerWord = getUncorrectedHeadlinesPerWord(elementsPerWord);
        headlinesPerWord = trimHeadlinesToMax77Characters(headlinesPerWord);
        headlinesPerWord = removeWrongContainsHeadlinesForHref(headlinesPerWord, " " + word + " ", elementsPerWord);
        return headlinesPerWord;
    }

    private List<String> getUncorrectedHeadlinesPerWord(List<Element> elementsList) {
        List<String> headlines = new ArrayList<>();

        for(Element e : elementsList) {
            headlines.add(e.text());
        }

        return headlines;
    }

    public List<String> getInitialHrefsAll(List<Element> elementsList) {
        List<String> hrefs = new ArrayList<>();

        for(Element e : elementsList) {
            hrefs.add(e.attr("abs:href"));
        }
        return hrefs;
    }

    public List<String> removeWrongContainsHeadlinesForHref(List<String> headlines, String word, List<Element> elementsPerWord) {
        List<String> correctHeadlines = new ArrayList<>();
        List<String> correctHrefHeadlines = new ArrayList<>();

        List<String> allHrefs = getInitialHrefsAll(elementsPerWord);

        List<String> lowerCaseReplacedHeadlines = new ArrayList<>();

        for(String headline : headlines) {
            String replacedHeadline = headline.replaceAll("[^A-Za-z0-9 ]", "");
            replacedHeadline = replacedHeadline.toLowerCase();
            lowerCaseReplacedHeadlines.add(replacedHeadline);
        }

        for(int i = 0; i < lowerCaseReplacedHeadlines.size(); i++) {
            if(lowerCaseReplacedHeadlines.get(i).contains(word)) {
                correctHeadlines.add(lowerCaseReplacedHeadlines.get(i));
                correctHrefHeadlines.add(allHrefs.get(i));
            }
        }

        String wordRemoveTrailingSpace = word.replace(" ", "");
        wordRemoveTrailingSpace = " " + wordRemoveTrailingSpace;

        for(int i = 0; i < lowerCaseReplacedHeadlines.size(); i++) {
            String headline = lowerCaseReplacedHeadlines.get(i);
            int length = wordRemoveTrailingSpace.length();

            String lastPart = "";

            if(headline.length() > length) {
                lastPart = headline.substring(headline.length() - length, headline.length());
            }

            if(lastPart.equals(wordRemoveTrailingSpace)) {
                correctHeadlines.add(headline);
                correctHrefHeadlines.add(allHrefs.get(i));
            }
        }

        return correctHrefHeadlines;
    }

    public List<String> removeWrongContainsHeadlinesForRaw(List<String> headlines, String word) {
        List<String> correctHeadlines = new ArrayList<>();
        List<String> correctRawHeadlines = new ArrayList<>();

        List<String> originalHeadlinesCopy = new ArrayList<>();
        originalHeadlinesCopy.addAll(headlines);

        List<String> lowerCaseReplacedHeadlines = new ArrayList<>();

        for(String headline : headlines) {
            String replacedHeadline = headline.replaceAll("[^A-Za-z0-9 ]", "");
            replacedHeadline = replacedHeadline.toLowerCase();
            lowerCaseReplacedHeadlines.add(replacedHeadline);
        }

        for(int i = 0; i < lowerCaseReplacedHeadlines.size(); i++) {
            if(lowerCaseReplacedHeadlines.get(i).contains(word)) {
                correctHeadlines.add(lowerCaseReplacedHeadlines.get(i));
                correctRawHeadlines.add(originalHeadlinesCopy.get(i));
            }
        }

        String wordRemoveTrailingSpace = word.replace(" ", "");
        wordRemoveTrailingSpace = " " + wordRemoveTrailingSpace;

        for(int i = 0; i < lowerCaseReplacedHeadlines.size(); i++) {
            String headline = lowerCaseReplacedHeadlines.get(i);
            int length = wordRemoveTrailingSpace.length();

            String lastPart = "";

            if(headline.length() > length) {
                lastPart = headline.substring(headline.length() - length, headline.length());
            }

            if(lastPart.equals(wordRemoveTrailingSpace)) {
                correctHeadlines.add(headline);
                correctRawHeadlines.add(originalHeadlinesCopy.get(i));
            }
        }

        if(correctHeadlines.size() != correctRawHeadlines.size()) {
            System.out.println("wacht");
        }

        return correctRawHeadlines;
    }

    private List<String> removeWrongContainsHeadlines(List<String> headlines, String word) {
        List<String> correctHeadlines = new ArrayList<>();

        List<String> lowerCaseReplacedHeadlines = new ArrayList<>();

        for(String headline : headlines) {
            String replacedHeadline = headline.replaceAll("[^A-Za-z0-9 ]", "");
            replacedHeadline = replacedHeadline.toLowerCase();
            lowerCaseReplacedHeadlines.add(replacedHeadline);
        }

        for(String headline : lowerCaseReplacedHeadlines) {
            if(headline.contains(word)) {
                correctHeadlines.add(headline);
            }
        }

        String wordRemoveTrailingSpace = word.replace(" ", "");
        wordRemoveTrailingSpace = " " + wordRemoveTrailingSpace;
        for(String headline : lowerCaseReplacedHeadlines) {
            int length = wordRemoveTrailingSpace.length();

            String lastPart = "";

            if(headline.length() > length) {
                lastPart = headline.substring(headline.length() - length, headline.length());
            }

            if(lastPart.equals(wordRemoveTrailingSpace)) {
                correctHeadlines.add(headline);
            }
        }

        return correctHeadlines;
    }

    private double getAverageLevenshteinDistance(List<String> headlines) {
        double total = 0;
        double counter = 0;

        LevenshteinDistanceService levenshteinDistanceService = new LevenshteinDistanceService();

        for(String headline : headlines) {
            for(int i = 0; i < headlines.size() - 1; i++) {
                int distance = levenshteinDistanceService.computeDistance(headline, headlines.get(i));

                total = total + (double) distance;
                counter++;
            }
        }

        return total / counter;
    }

    private List<String> trimHeadlinesToMax77Characters(List<String> headlines) {
        List<String> trimmedHeadlines = new ArrayList<>();

        for(String headline : headlines) {
            if(headline.length() >= 78) {
                String trimmedHeadline = headline.substring(0, 78);
                trimmedHeadlines.add(trimmedHeadline);
            } else {
                trimmedHeadlines.add(headline);
            }
        }
        return trimmedHeadlines;
    }

    private void myOwnCompare() {

        //je hebt 3 Strings

        //2 daarvan horen bij elkaar, een is totaal iets anders

        //identificeer dit




        //maak van elke string een array van de woorden

        //check hoe



        String a = "this dad is the ultimate helicopter parent and hes flying his son around th";

        String b = "syria russia to target planes from usled coalition flying west of euphrates";

        String c = "war of words russia vows to destroy all flying objects over syria  includin";

        String d = "152 beat the traffic airbus showcase how their flying taxis will work";

        String e = "no departure cards for those flying abroad";

        String f = "no departure cards for indians flying abroad from july 1";

        String g = "soon no departure cards for indians flying abroad";

        String h = "hkfp lens the ruins of taiwans abandoned flying saucer holiday homes";

        String i = "dutch firm aims to deliver first flying car in 2018";

        List<String> aa = Arrays.asList(a.split(" "));
        List<String> bb = Arrays.asList(b.split(" "));
        List<String> cc = Arrays.asList(c.split(" "));
        List<String> dd = Arrays.asList(d.split(" "));
        List<String> ee = Arrays.asList(e.split(" "));
        List<String> ff = Arrays.asList(f.split(" "));
        List<String> gg = Arrays.asList(g.split(" "));
        List<String> hh = Arrays.asList(h.split(" "));
        List<String> ii = Arrays.asList(i.split(" "));

        //vergelijk elk woord van een string met alle andere woorden van een andere string, en neem de laagste score

        //tel de score van alle woorden op en deel door totaal woorden van string, dan heb je gemiddelde van de zin


        LevenshteinDistanceService levenshteinDistanceService = new LevenshteinDistanceService();

//        for(String word : bb) {
//            List<Integer> scores = new ArrayList<>();
//
//            for(String word2 : aa) {
//                scores.add(levenshteinDistanceService.computeDistance(word, word2));
//            }
//
//            Collections.sort(scores);
//            int lowestScore = scores.get(0);
//
//            lowestScores.add(lowestScore);
//        }
//
//        System.out.println(lowestScores);





        List<List<String>> wordArraysPerSentence = new ArrayList<>();
        wordArraysPerSentence.add(aa);
        wordArraysPerSentence.add(bb);
        wordArraysPerSentence.add(cc);
        wordArraysPerSentence.add(dd);
        wordArraysPerSentence.add(ee);
        wordArraysPerSentence.add(ff);
        wordArraysPerSentence.add(gg);
        wordArraysPerSentence.add(hh);
        wordArraysPerSentence.add(ii);

        Map<List<String>, Double> scoresPerSentence = new HashMap<>();

        for(List<String> wordArray : wordArraysPerSentence) {
            List<Integer> lowestScores = new ArrayList<>();

            for(List<String> wordArray2 : wordArraysPerSentence) {
                if(!wordArray.equals(wordArray2)) {
                    for(String word : wordArray) {
                        List<Integer> scores = new ArrayList<>();

                        for(String word2 : wordArray2) {
                            scores.add(levenshteinDistanceService.computeDistance(word, word2));
                        }

                        Collections.sort(scores);
                        int lowestScore = scores.get(0);

                        lowestScores.add(lowestScore);

                    }

                }


            }

            //bereken hier gemiddelde van lowestScores, en voeg toe aan map met wordArray als key
            scoresPerSentence.put(wordArray, calculateAverage(lowestScores));
        }


        System.out.println("wacht");




        //maak per zin een map tov de andere zinnen met scores.

        //
    }

    public double calculateAverage(List<Integer> lowestScores) {
        double total = 0;

        for(Integer i : lowestScores) {
            total = total + (double) i;
        }
        return total / (double) lowestScores.size();
    }
}

