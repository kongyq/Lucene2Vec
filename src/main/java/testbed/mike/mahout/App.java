package testbed.mike.mahout;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;

import java.io.File;
import java.io.IOException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World!" );

        final File indexFolder = new File("/home/mike/Documents/Index/wikipedia_tv_7.6.0");
        Directory dir = NIOFSDirectory.open(indexFolder.toPath());

        IndexReader reader = DirectoryReader.open(dir);

        String field = "body";

        int nullTVDoc = 0;
        int doc = 0;

        while (doc < reader.maxDoc()) {
            if (reader.getTermVector(doc, field) == null) {
                nullTVDoc++;
            }
            if(doc % 100000 == 0) System.out.println(doc);
            doc++;
        }
        System.out.println(nullTVDoc);

    }
}
