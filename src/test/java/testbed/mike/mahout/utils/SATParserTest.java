package testbed.mike.mahout.utils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SATParserTest {

    @Test
    public void getWordList() throws IOException {

        SATParser parser = new SATParser();
        System.out.println(parser.getWordList());
    }

    @Test
    public void getQuestionList() {
    }
}