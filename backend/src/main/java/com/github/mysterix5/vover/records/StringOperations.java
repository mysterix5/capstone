package com.github.mysterix5.vover.records;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringOperations {

    public static boolean isWord(String word){
        String wordRegex = "^[a-zA-Z]{1,30}$";
        return word.matches(wordRegex);
    }

    public static List<String> splitText(String text){
        List<String> wordList = new ArrayList<>();
        text = text.replaceAll("^\\s+", "");
        text = text.replaceAll("\\s+$", "");
        if(text.equals("")){
            return wordList;
        }
        wordList = Arrays.stream(text.split("\\s+")).toList();

        return wordList;
    }
}
