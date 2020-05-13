import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final Map<Integer, Integer> AGES = new HashMap<>();

    //The program takes filename from command line
    public static void main(String[] args) throws IOException {

        //Filling the map with age values
        fillMap();

        //Reading the textfile and getting+printing stats
        String text = readFile(args[0]);
        System.out.println("java Main " + args[0] + "\nThe text is:\n" + text + "\n");
        int[] stats = countStatistics(text);
        printStatistics(stats);
        int words = stats[0];
        int sentences = stats[1];
        int characters = stats[2];
        int syllables = stats[3];
        int polysyllables = stats[4];

        //Choosing scores to calculate
        System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
        Scanner scanner = new Scanner(System.in);
        double age = 0;
        switch (scanner.nextLine()) {
            case "ARI":
                countARI(characters, words, sentences);
                break;
            case "FK" :
                countFK(words, sentences, syllables);
                break;
            case "SMOG" :
                countSMOK(polysyllables, sentences);
                break;
            case "CL" :
                countCL(characters, sentences, words);
                break;
            case "all" :
                callAllMethods(words, sentences, syllables, characters, polysyllables);
        }
    }


    private static String readFile(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        Files.readAllLines(Path.of(path)).forEach(builder::append);
        return builder.toString();
    }

    //AGES Map filling with values specified in Automated readability index Wikipedia article

    private static void fillMap() {
        AGES.put(1, 6);
        AGES.put(2, 7);
        int age = 9;
        for (int i = 3; i < 13; i++) {
            AGES.put(i, age++);
        }
        AGES.put(13, 24);
        AGES.put(14, 24);
    }

    //Counting text statistics

    private static int[] countStatistics(String text) {
        int[] stats = new int[5];
        String[] sentences = text.split("[!?.]\\s");
        int[] syllAndPolySyll = countSyllablesAndPolysyllables(sentences);
        stats[0] = countWords(sentences);                                    //wordsCount
        stats[1] = sentences.length;                                         //sentencesCount
        stats[2] = text.replaceAll("\\s+", "").length();     //charactersCount
        stats[3] = syllAndPolySyll[0];                                       //syllablesCount
        stats[4] = syllAndPolySyll[1];                                       //polysyllablesCount

        return stats;
    }

    //Printing statistics

    private static void printStatistics(int[] stats) {
        System.out.println("Words: " + stats[0]);
        System.out.println("Sentences: " + stats[1]);
        System.out.println("Characters: " + stats[2]);
        System.out.println("Syllables: " + stats[3]);
        System.out.println("Polysyllables: " + stats[4]);
    }

    //Counting words in sentences

    private static int countWords(String[] sentences) {
        int words = 0;
        for (String sentence : sentences) {
            words += sentence.split("\\s").length;
        }
        return words;
    }

    /*
    Counting syllables and polysyllables in words using
    4 rules, marked by comments.
     */
    private static int[] countSyllablesAndPolysyllables(String[] sentences) {
        int syllables = 0;
        int polysyllables = 0;
        for (String sentence : sentences) {
            for (String word : sentence.split("\\s")) {
                String temp = word.toLowerCase();
                int syllablesInWord = word.endsWith("e") ? -1 : 0;                  //Rule 3. If the last letter in the word is 'e' do not count it as a vowel (for example, "side" is 1 syllable)
                for (int i = 1; i <= temp.length(); i++) {
                    if (i == temp.length() && isVowel(temp.charAt(i-1))) {          //counting the last vowel
                        syllablesInWord++;
                    }
                    if (isVowel(temp.charAt(i - 1)) && i != temp.length()) {        //Rule 2. Do not count double-vowels (for example, "rain" has 2 vowels but is only 1 syllable)
                        if (!isVowel(temp.charAt(i))) {                             //Rule 1. Count the number of vowels in the word.
                            syllablesInWord++;
                        }
                    }
                }
                if (syllablesInWord <= 0) {syllablesInWord = 1;}                    //Rule 4. If at the end it turns out that the word contains 0 vowels, then consider this word as 1-syllable.
                syllables += syllablesInWord;
                if (syllablesInWord > 2) {                                          //counting polysyllables
                    polysyllables++;
                }
            }
        }
        return new int[]{syllables, polysyllables};
    }

    private static boolean isVowel(char letter) {
        return letter == 'a' || letter == 'e' || letter == 'y' || letter == 'o'
                || letter == 'i' ||  letter == 'u';
    }

    //Automated Readability Index
    private static int countARI(int characters, int words, int sentences) {
        double ari = 4.71 * ((double) characters / words) + 0.5 * ((double) words / sentences) - 21.43;
        int age = AGES.get((int) Math.round(ari));
        System.out.print("\nAutomated Readability Index: " + ari + " (about "
                + (ari > 14 ? age + "+" : age) + " years olds).");
        return age;
    }

    //Flesch–Kincaid readability tests
    private static int countFK(int words, int sentences, int syllables) {
        double fk = 0.39 * words / sentences + 11.8 * syllables / words - 15.59;
        int age = AGES.get((int) Math.round(fk));
        System.out.print("\nFlesch–Kincaid readability tests: " + fk + " (about "
                + (fk > 14 ? age + "+" : age) + " years olds).");
        return age;
    }

    //Simple Measure of Gobbledygook
    private static int countSMOK(int polysyllables, int sentences) {
        double smok = 1.043 * Math.sqrt(polysyllables * 30.0 / sentences) + 3.1291;
        int age = AGES.get((int) Math.round(smok));
        System.out.print("\nSimple Measure of Gobbledygook: " + smok + " (about "
                + (smok > 14 ? age + "+" : age) + " years old).");
        return age;
    }

    //Coleman–Liau index
    private static int countCL(int characters, int sentences, int words) {
        double cl = 0.0588 * characters / words * 100 - 0.296 * sentences / words * 100 - 15.8;
        int age = AGES.get((int) Math.round(cl));
        System.out.print("\nColeman–Liau index: " + cl + " (about "
                + (cl > 14 ? age + "+" : age) + " years old).");
        return age;
    }

    //all methods
    private static void callAllMethods(int words, int sentences, int syllables, int characters, int polysyllables) {
        double sum = 0;
        sum += countARI(characters, words, sentences);
        sum += countFK(words, sentences, syllables);
        sum += countSMOK(polysyllables, sentences);
        sum += countCL(characters, sentences, words);
        System.out.printf("\n\nThis text should be understood in average by %.2f year olds.", sum / 4);
    }
}