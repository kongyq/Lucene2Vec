package testbed.mike.mahout;


import org.apache.lucene.index.Terms;
import testbed.mike.mahout.vectors.lucene.Driver;

//import org.apache.mahout.utils.vectors.lucene.Driver;

import java.io.IOException;

import static java.lang.System.exit;

public class ProjectRunner {
    public static void main(String[] args) throws IOException {

        final String dir = "/home/mike/Documents/Index/wikipedia_tv_7.6.0";
        final String idField = "docid";
        final String output = "/home/mike/Documents/Index/vector/wikipedia_2/orig.txt";
        final String field = "body";
        final String dictOut = "/home/mike/Documents/Index/vector/wikipedia_2/dict.txt";
        final String weight = "TFIDF";

        final long max = 50;
        final double maxPercentErrorDocs = 0.0002;

        Driver driver = new Driver();

        driver.setLuceneDir(dir);
        driver.setIdField(idField);
        driver.setField(field);
        driver.setOutFile(output);
        driver.setDictOut(dictOut);

        driver.setWeightType(weight);
//        driver.setMaxDocs(max);
        driver.setMaxPercentErrorDocs(maxPercentErrorDocs);

        driver.setDelimiter("\t");

        System.out.println("Starting Generating of Term Vectors " + output);
        long start = System.currentTimeMillis();

        try {
            driver.dumpVectors();
        } catch (IOException e) {
            e.printStackTrace();
            long end = System.currentTimeMillis();
            System.out.println("Generating " + dir + " vectors took " + (end-start)/1000 + "s");
        }

        long end = System.currentTimeMillis();
        System.out.println("Generating " + dir + " vectors took " + (end-start)/1000 + "s");
    }
}
