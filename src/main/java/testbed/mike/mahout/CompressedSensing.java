package testbed.mike.mahout;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.utils.vectors.io.SequenceFileVectorWriter;
import org.apache.mahout.utils.vectors.io.VectorWriter;
import org.jcodings.util.Hash;

import java.io.IOException;
import java.util.HashSet;
import java.util.function.Predicate;

import static java.lang.System.exit;
import static java.lang.System.out;

public class CompressedSensing {

    private RandomGaussian rgMatrix;
    private SequenceFile.Reader seqReader;

    private final int origDim;
    private final int targetDim;

    // for reproduction purpose
    private static final long RGSeed = 22708093L;

    public CompressedSensing(int origDim, int targetDim) {
        this.origDim = origDim;
        this.targetDim = targetDim;
        this.rgMatrix = new RandomGaussian(targetDim, origDim, RGSeed);
    }

    public void loadSparseSeqFile(String seqFile) throws IOException {
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);

        Path path = new Path(seqFile);

        this.seqReader = new SequenceFile.Reader(conf, SequenceFile.Reader.file(path));
        System.out.println("SequenceFile successfully loaded!");
    }

    public void compress(String output) throws IOException {
        compress(output, new HashSet<>());
    }

    public void compress(String output, HashSet<String> keyList) throws IOException {
        if(seqReader == null){
            System.out.println("Please load sequenceFile first!");
            exit(1);
        }

        LongWritable key = new LongWritable();
        VectorWritable value = new VectorWritable();

        VectorWriter writer = getSeqFileWriter(output);
        while (seqReader.next(key, value)) {
            NamedVector namedVector = (NamedVector)value.get();
            String name = namedVector.getName();
//            System.out.println(name);
            if(!keyList.isEmpty() && !keyList.contains(name)) continue;
            System.out.println(name);
//            namedVector.assign(this.rgMatrix.mvMultiply(namedVector));
//            Vector vector = this.rgMatrix.mvMultiply(namedVector);
            writer.write(new NamedVector(this.rgMatrix.mvMultiply(namedVector), name));
        }
        writer.close();
        seqReader.close();
    }

    public void compressTemp(String output, HashSet<String> keyList) throws IOException {
        HashSet<String> newKeyList = new HashSet<>(keyList);
        newKeyList.removeIf(s -> s.charAt(0) < 'y');
        out.println(newKeyList);
        this.compress(output, newKeyList);
    }

    private static VectorWriter getSeqFileWriter(String outFile) throws IOException {
        Path path = new Path(outFile);
        Configuration conf = new Configuration();
//        FileSystem fs = FileSystem.get(conf);
        // TODO: Make this parameter driven

        SequenceFile.Writer seqWriter = SequenceFile.createWriter(conf,
                SequenceFile.Writer.file(path),
                SequenceFile.Writer.keyClass(LongWritable.class),
                SequenceFile.Writer.valueClass(VectorWritable.class));

        return new SequenceFileVectorWriter(seqWriter);
    }
}
