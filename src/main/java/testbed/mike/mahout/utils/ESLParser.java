package testbed.mike.mahout.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;

public class ESLParser {
    private final File ESLFILE = new File("/home/mike/Documents/corpus/ESL/esl.txt");
    private final HashSet<String> wordList;
    private final ArrayList<ArrayList<String>> questionList;

    public ESLParser() throws IOException {
        this.wordList = new HashSet<>();
        this.questionList = new ArrayList<>();
        for (String line : Files.readAllLines(ESLFILE.toPath())) {
            if(!line.startsWith("#") && !line.startsWith(" ")){
                ArrayList<String> newLine = new ArrayList<>();
                for(String word : line.split(" ")){
                    if(word.matches("[a-zA-Z]+")){
                        this.wordList.add(word);
                        newLine.add(word.trim());
                    }
                }
                this.questionList.add(newLine);
//                this.wordList.addAll(Arrays.asList(line.split(" \\| ")));
            }
        };
    }

    public HashSet<String> getWordList(){
        return this.wordList;
    }

    public ArrayList<ArrayList<String>> getQuestionList(){
        return this.questionList;
    }
}
