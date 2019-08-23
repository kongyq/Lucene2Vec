package testbed.mike.mahout.utils;

import com.sun.tools.javac.util.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SATParser {

    private final File SATFile = new File("/home/mike/Documents/corpus/SAT/SAT-package-V3.txt");
    private final HashSet<String> wordList;
    private final List<Question> questionList;

    public class Question{
        private String answer;
        private List<String> words;

        private Pair<String, String> question;
        private List<Pair<String, String>> options;

        public Question(List<String> lines){
            this.options = new ArrayList<>(5);
            this.words = new ArrayList<>();
            for (int i = 1; i < lines.size(); i++) {
                if(i == 1) {
                    this.question = getPair(lines.get(i));
                    this.words.add(this.question.fst);
                    this.words.add(this.question.snd);
                }
                else if(i != 7) {
                    this.options.add(getPair(lines.get(i)));
                    this.words.add(getPair(lines.get(i)).fst);
                    this.words.add(getPair(lines.get(i)).snd);
                }
                else answer = lines.get(i).substring(0, 1);
            }
        }

        private Pair<String, String> getPair(String line){
            return new Pair<>(line.split(" ")[0], line.split(" ")[1]);
        }

        public String getAnswer() {
            return this.answer;
        }

        public List<Pair<String, String>> getOptions(){
            return this.options;
        }

        public Pair<String, String> getQuestion(){
            return this.question;
        }

        public List<String> getWords(){
            return this.words;
        }
    }

    public SATParser() throws IOException {
        this.wordList = new HashSet<>();
        this.questionList = new ArrayList<>();
        int lineCount = 0;
        List<String> lines = new ArrayList<>(8);
        for (String line : Files.readAllLines(SATFile.toPath())) {
//            System.out.println(line);
            if(line.startsWith("#")) continue;

            if(lineCount == 0){
                lineCount++;
                continue;
            }else if(lineCount < 8){
                lines.add(line);
            }else{
                lines.add(line);
                this.questionList.add(new Question(lines));
                lines = new ArrayList<>();
                lineCount = 0;
                continue;
            }

            lineCount++;
        }

        for (Question q : this.questionList) {
            System.out.println(q.words);
            this.wordList.addAll(q.getWords());
        }
    }

    public HashSet<String> getWordList(){
        return this.wordList;
    }

    public List<Question> getQuestionList(){
        return this.questionList;
    }
}
