package testbed.mike.mahout;

import org.junit.Test;
import testbed.mike.mahout.utils.ESLParser;
import testbed.mike.mahout.utils.SATParser;

import java.io.IOException;
import java.util.HashSet;

import static org.junit.Assert.*;

public class CompressedSensingTest {

    private final static String sparseSeqFile = "/home/mike/Documents/Index/vector/wikipedia_2/orig.txt";
//    private final static String outputFile = "/home/mike/Documents/Index/vector/compressed/orig.txt";
    private final static String outputFile = "/home/mike/Documents/Index/vector/compressed/orig_3.txt";

    @Test
    public void loadSparseSeqFile() {

    }

    @Test
    public void compress() throws IOException {
        CompressedSensing cs = new CompressedSensing(5982049,150);
        cs.loadSparseSeqFile(sparseSeqFile);
//        cs.compress(outputFile);

        HashSet<String> wordList = new HashSet<>();

        ESLParser eslParser = new ESLParser();
        SATParser satParser = new SATParser();

        wordList.addAll(eslParser.getWordList());
        wordList.addAll(satParser.getWordList());

        cs.compressTemp(outputFile, wordList);
    }
}