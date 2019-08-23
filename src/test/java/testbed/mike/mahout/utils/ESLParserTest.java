package testbed.mike.mahout.utils;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ESLParserTest {

    @Test
    public void getWordList() throws IOException {
        ESLParser parser = new ESLParser();
        for (String word : parser.getWordList()) {
            System.out.println(word);
        }
    }
}