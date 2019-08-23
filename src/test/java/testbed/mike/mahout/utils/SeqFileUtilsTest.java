package testbed.mike.mahout.utils;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SeqFileUtilsTest {

    private final static String seqFile = "/home/mike/Documents/Index/vector/compressed/orig_3.txt";
    private final SequenceFile.Reader reader = SeqFileUtils.loadSeqFile(seqFile);

    public SeqFileUtilsTest() throws IOException {
    }

    @Test
    public void loadSeqFile() {
    }

    @Test
    public void displayNextVector() throws IOException {

        LongWritable key = new LongWritable();
        while (reader.next(key)) {
            SeqFileUtils.displayNextVector(reader);

        }


//        SeqFileUtils.displayNextVector(reader);
    }

    @Test
    public void dispVecName() throws IOException {
        SeqFileUtils.dispVecName(reader);
    }
}