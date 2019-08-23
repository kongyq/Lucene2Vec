package testbed.mike.mahout.vectors.lucene;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import org.apache.commons.io.Charsets;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.mahout.math.VectorWritable;
import org.apache.mahout.utils.vectors.TermEntry;
import org.apache.mahout.utils.vectors.TermInfo;
import org.apache.mahout.utils.vectors.io.DelimitedTermInfoWriter;
import org.apache.mahout.utils.vectors.io.SequenceFileVectorWriter;
import org.apache.mahout.utils.vectors.io.VectorWriter;
import org.apache.mahout.utils.vectors.lucene.CachedTermInfo;
import org.apache.mahout.vectorizer.TF;
import org.apache.mahout.vectorizer.TFIDF;
import org.apache.mahout.vectorizer.Weight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;

public class Driver {

    private static final Logger log = LoggerFactory.getLogger(org.apache.mahout.utils.vectors.lucene.Driver.class);

    private String luceneDir;
    private String outFile;
    private String field;
    private String idField;
    private String dictOut;
    private String seqDictOut = "";
    private String weightType = "tfidf";
    private String delimiter = "\t";
    private double norm = LuceneIterable.NO_NORMALIZING;
    private long maxDocs = Long.MAX_VALUE;
    private int minDf = 1;
    private int maxDFPercent = 99;
    private double maxPercentErrorDocs = 0.0;

    private HashSet<String> wordList;

    public Driver() {

    }

    public void dumpVectors() throws IOException {

        File file = new File(luceneDir);
        Preconditions.checkArgument(file.isDirectory(),
                "Lucene directory: " + file.getAbsolutePath()
                        + " does not exist or is not a directory");
        Preconditions.checkArgument(maxDocs >= 0, "maxDocs must be >= 0");
        Preconditions.checkArgument(minDf >= 1, "minDf must be >= 1");
        Preconditions.checkArgument(maxDFPercent <= 99, "maxDFPercent must be <= 99");

        Directory dir = FSDirectory.open(Paths.get(file.getAbsolutePath()));
        IndexReader reader = DirectoryReader.open(dir);


        Weight weight;
        if ("tf".equalsIgnoreCase(weightType)) {
            weight = new TF();
        } else if ("tfidf".equalsIgnoreCase(weightType)) {
            weight = new TFIDF();
        } else {
            throw new IllegalArgumentException("Weight type " + weightType + " is not supported");
        }

        TermInfo termInfo = new CachedTermInfo(reader, field, minDf, maxDFPercent);

//        CachedDocInfo docInfo = new CachedDocInfo(reader, field);

        LuceneIterable iterable;
        if (norm == LuceneIterable.NO_NORMALIZING) {
            iterable = new LuceneIterable(reader, idField, field, null, weight, LuceneIterable.NO_NORMALIZING,
                    maxPercentErrorDocs);
        } else {
            iterable = new LuceneIterable(reader, idField, field, null, weight, norm, maxPercentErrorDocs);
        }

        log.info("Output File: {}", outFile);

        try (VectorWriter vectorWriter = getSeqFileWriter(outFile)) {
            long numDocs = vectorWriter.write(iterable, maxDocs);
            log.info("Wrote: {} vectors", numDocs);
        }



//        File dictOutFile = new File(dictOut);
//        log.info("Dictionary Output file: {}", dictOutFile);
//        Writer writer = Files.newWriter(dictOutFile, Charsets.UTF_8);
//        try (DelimitedTermInfoWriter tiWriter = new DelimitedTermInfoWriter(writer, delimiter, field)) {
//            tiWriter.write(termInfo);
//        }

//        if (!"".equals(seqDictOut)) {
//            log.info("SequenceFile Dictionary Output file: {}", seqDictOut);
//
//            Path path = new Path(seqDictOut);
//            Configuration conf = new Configuration();
////            FileSystem fs = FileSystem.get(conf);
//            try(SequenceFile.Writer seqWriter = SequenceFile.createWriter(conf,
//                    SequenceFile.Writer.file(path),
//                    SequenceFile.Writer.keyClass(Text.class),
//                    SequenceFile.Writer.valueClass(IntWritable.class)))
//            {
//                Text term = new Text();
//                IntWritable termIndex = new IntWritable();
//                Iterator<TermEntry> termEntries = termInfo.getAllEntries();
//                while (termEntries.hasNext()) {
//                    TermEntry termEntry = termEntries.next();
//                    term.set(termEntry.getTerm());
//                    termIndex.set(termEntry.getTermIdx());
//                    seqWriter.append(term, termIndex);
//                }
//            }
//        }
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

    public void setLuceneDir(String luceneDir) {
        this.luceneDir = luceneDir;
    }

    public void setMaxDocs(long maxDocs) {
        this.maxDocs = maxDocs;
    }

    public void setWeightType(String weightType) {
        this.weightType = weightType;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setMinDf(int minDf) {
        this.minDf = minDf;
    }

    public void setMaxDFPercent(int maxDFPercent) {
        this.maxDFPercent = maxDFPercent;
    }

    public void setNorm(double norm) {
        this.norm = norm;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public void setOutFile(String outFile) {
        this.outFile = outFile;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void setDictOut(String dictOut) {
        this.dictOut = dictOut;
    }

    public void setSeqDictOut(String seqDictOut) {
        this.seqDictOut = seqDictOut;
    }

    public void setMaxPercentErrorDocs(double maxPercentErrorDocs) {
        this.maxPercentErrorDocs = maxPercentErrorDocs;
    }

    public void setWordList(HashSet<String> wordList) {
        this.wordList = wordList;
    }

}
