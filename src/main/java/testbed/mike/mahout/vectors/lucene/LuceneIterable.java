package testbed.mike.mahout.vectors.lucene;

import org.apache.lucene.index.IndexReader;
import org.apache.mahout.math.Vector;
import org.apache.mahout.utils.vectors.TermInfo;
import org.apache.mahout.vectorizer.Weight;

import java.util.Iterator;

public final class LuceneIterable implements Iterable<Vector> {

    public static final double NO_NORMALIZING = -1.0;

    private final IndexReader indexReader;
    private final String field;
    private final String idField;
    private final TermInfo terminfo;
    private final double normPower;
    private final double maxPercentErrorDocs;
    private final Weight weight;

    public LuceneIterable(IndexReader reader, String idField, String field, TermInfo terminfo, Weight weight) {
        this(reader, idField, field, terminfo, weight, NO_NORMALIZING);
    }

    public LuceneIterable(IndexReader indexReader, String idField, String field, TermInfo terminfo, Weight weight,
                          double normPower) {
        this(indexReader, idField, field, terminfo, weight, normPower, 0);
    }

    /**
     * Produce a LuceneIterable that can create the Vector plus normalize it.
     *
     * @param indexReader         {@link org.apache.lucene.index.IndexReader} to read the documents from.
     * @param idField             field containing the id. May be null.
     * @param field               field to use for the Vector
     * @param normPower           the normalization value. Must be nonnegative, or {@link #NO_NORMALIZING}
     * @param maxPercentErrorDocs the percentage of documents in the lucene index that can have a null term vector
     */
    public LuceneIterable(IndexReader indexReader,
                          String idField,
                          String field,
                          TermInfo terminfo,
                          Weight weight,
                          double normPower,
                          double maxPercentErrorDocs) {
        this.indexReader = indexReader;
        this.idField = idField;
        this.field = field;
        this.terminfo = terminfo;
        this.normPower = normPower;
        this.maxPercentErrorDocs = maxPercentErrorDocs;
        this.weight = weight;
    }

    @Override
    public Iterator<Vector> iterator() {
        return new LuceneIterator(indexReader, idField, field, terminfo, weight, normPower, maxPercentErrorDocs);
    }
}