package testbed.mike.mahout.utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.math.*;

import java.io.EOFException;
import java.io.IOException;

public class SeqFileUtils {

    public static SequenceFile.Reader loadSeqFile(String seqFile) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        Path path = new Path(seqFile);

        return new SequenceFile.Reader(conf, SequenceFile.Reader.file(path));
    }

    public static void displayNextVector(SequenceFile.Reader reader) throws IOException {
        LongWritable key = new LongWritable();
        VectorWritable value = new VectorWritable();
        try {
            reader.next(key, value);
//            System.out.println(value.get().size());
            NamedVector namedVector = (NamedVector)value.get();
            Vector vect = namedVector.getDelegate();

            for(Vector.Element e : vect.nonZeroes()){
                System.out.println("Index: " + e.index() + ", value: " + e.get()) ;
            }
            System.out.println(key.get());
            System.out.println(namedVector.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void dispVecName(SequenceFile.Reader reader) throws IOException {
        LongWritable key = new LongWritable();
        VectorWritable value = new VectorWritable();
        int count = 0;
        while (reader.next(key, value)) {
            count++;
            NamedVector namedVector = (NamedVector) value.get();

            System.out.println(namedVector.getName());
        }
        System.out.println(count);
    }
}
