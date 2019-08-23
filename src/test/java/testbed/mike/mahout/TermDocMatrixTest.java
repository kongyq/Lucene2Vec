package testbed.mike.mahout;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.mahout.common.distance.CosineDistanceMeasure;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CosineSimilarity;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class TermDocMatrixTest {

    private static final File indexFolder = new File("/home/mike/Documents/Index/wikipedia_tv_7.6.0");
    @Test
    public void test() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        String vectorsPath = "/home/mike/Documents/Index/vector/wikipedia_2/orig.txt";
        Path path = new Path(vectorsPath);

        SequenceFile.Reader reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(path));

        LongWritable key = new LongWritable();
        VectorWritable value = new VectorWritable();
        while (reader.next(key, value)) {

            NamedVector namedVector = (NamedVector)value.get();
            RandomAccessSparseVector vect = (RandomAccessSparseVector)namedVector.getDelegate();

            for(Vector.Element e : vect.nonZeroes()){
                System.out.println("Token: " + e.index() + ", TF-IDF weight: " + e.get()) ;
            }
            System.out.println(key.get());
            System.out.println(namedVector.getName());
            break;
        }
        reader.close();
    }

    @Test
    public void seekSFposition() throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        String vectorsPath = "/home/mike/Documents/Index/vector/wikipedia/orig.txt";
        Path path = new Path(vectorsPath);

        SequenceFile.Reader reader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(path));
        System.out.println(reader.getPosition());
        LongWritable key = new LongWritable();
        VectorWritable value = new VectorWritable();

        reader.next(key);
        int docid = 2;
        while (key.get() != docid) {
            reader.next(key);
        }
        reader.getCurrentValue(value);

        NamedVector namedVector = (NamedVector)value.get();
        RandomAccessSparseVector vect = (RandomAccessSparseVector)namedVector.getDelegate();

        for(Vector.Element e : vect.nonZeroes()){
            System.out.println("Token: " + e.index() + ", TF-IDF weight: " + e.get()) ;
        }
        System.out.println(key.get());
        System.out.println(namedVector.getName());
    }

    @Test
    public void showTermVector() throws IOException {
        Directory dir = NIOFSDirectory.open(indexFolder.toPath());
        IndexReader reader = DirectoryReader.open(dir);

        String field = "body";

        Terms terms = MultiFields.getTerms(reader, field);
        TermsEnum termsEnum = terms.iterator();

        BytesRef bytesRef;

        //get total number of documents
        int docCount = reader.maxDoc();

        TFIDFSimilarity tfidfSim = new ClassicSimilarity();

        int n = 0;

        while((bytesRef = termsEnum.next()) != null){

            //get doc frequency of current term
            int docFreq = termsEnum.docFreq();

            PostingsEnum postingsEnum = null;
            postingsEnum = termsEnum.postings(postingsEnum);
            while(postingsEnum.nextDoc() != DocIdSetIterator.NO_MORE_DOCS){

                //get term frequency of current document
                int termFreq = postingsEnum.freq();
                System.out.print(bytesRef.utf8ToString() + "-->" + postingsEnum.docID() + "\t" + postingsEnum.freq() + "-->>");

                //compute TF-IDf score using tf, idf
                double tfidfScore = tfidfSim.tf(termFreq) * tfidfSim.idf(docFreq, docCount);
                System.out.println(tfidfScore);
                break;
            }
            n++;
            if(n >= 3) break;
            continue;
        }

        CosineSimilarity cosineSimilarity = new CosineSimilarity();
        CosineDistanceMeasure distanceMeasure = new CosineDistanceMeasure();
    }
}