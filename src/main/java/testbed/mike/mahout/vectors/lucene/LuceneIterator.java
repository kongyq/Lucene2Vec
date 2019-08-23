package testbed.mike.mahout.vectors.lucene;

import com.google.common.base.Preconditions;
import org.apache.lucene.index.*;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.util.BytesRef;
import org.apache.mahout.math.NamedVector;
import org.apache.mahout.math.RandomAccessSparseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.utils.vectors.TermInfo;
import org.apache.mahout.utils.vectors.lucene.AbstractLuceneIterator;
import org.apache.mahout.vectorizer.Weight;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

public class LuceneIterator extends AbstractLuceneIterator {

    protected final Set<String> idFieldSelector;
    protected final String idField;

    private TermsEnum termsEnum;
    private int numDocs;
    private PostingsEnum postingsEnum;

    private CachedDocInfo docInfo;


    /**
     * Produce a LuceneIterable that can create the Vector plus normalize it.
     *
     * @param indexReader {@link IndexReader} to read the documents from.
     * @param idField     field containing the id. May be null.
     * @param field       field to use for the Vector
     * @param termInfo    termInfo
     * @param weight      weight
     * @param normPower   the normalization value. Must be non-negative, or {@link org.apache.mahout.utils.vectors.lucene.LuceneIterable#NO_NORMALIZING}
     */
    public LuceneIterator(IndexReader indexReader, String idField, String field, TermInfo termInfo, Weight weight,
                          double normPower) {
        this(indexReader, idField, field, termInfo, weight, normPower, 0.0);
    }

    /**
     * @param indexReader {@link IndexReader} to read the documents from.
     * @param idField    field containing the id. May be null.
     * @param field      field to use for the Vector
     * @param termInfo   termInfo
     * @param weight     weight
     * @param normPower  the normalization value. Must be non-negative, or {@link org.apache.mahout.utils.vectors.lucene.LuceneIterable#NO_NORMALIZING}
     * @param maxPercentErrorDocs most documents that will be tolerated without a term freq vector. In [0,1].
     * @see #LuceneIterator(org.apache.lucene.index.IndexReader, String, String, org.apache.mahout.utils.vectors.TermInfo,
     * org.apache.mahout.vectorizer.Weight, double)
     */
    public LuceneIterator(IndexReader indexReader,
                          String idField,
                          String field,
                          TermInfo termInfo,
                          Weight weight,
                          double normPower,
                          double maxPercentErrorDocs) {
        super(termInfo, normPower, indexReader, weight, maxPercentErrorDocs, field);
        // term docs(null) is a better way of iterating all the docs in Lucene
        Preconditions.checkArgument(normPower == LuceneIterable.NO_NORMALIZING || normPower >= 0,
                "normPower must be non-negative or -1, but normPower = " + normPower);
        Preconditions.checkArgument(maxPercentErrorDocs >= 0.0 && maxPercentErrorDocs <= 1.0,
                "Must be: 0.0 <= maxPercentErrorDocs <= 1.0");
        this.idField = idField;
        if (idField != null) {
            idFieldSelector = new TreeSet<>();
            idFieldSelector.add(idField);
        } else {
      /*The field in the index  containing the index. If null, then the Lucene internal doc id is used
      which is prone to error if the underlying index changes*/
            idFieldSelector = null;
        }

//        this.termEntryIterator = termInfo.getAllEntries();
        try {
            System.out.println("create term list!");
            this.createTermList(indexReader, field);
            this.numDocs = indexReader.numDocs();
            this.docInfo = new CachedDocInfo(indexReader, field);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * Useless dummy method
     * @param documentIndex
     * @return
     * @throws IOException
     */
    @Override
    protected String getVectorName(int documentIndex) throws IOException {
        String name;
        if (idField != null) {
            name = indexReader.document(documentIndex, idFieldSelector).get(idField);
        } else {
            name = String.valueOf(documentIndex);
        }
        return name;
    }



    private void createTermList(IndexReader indexReader, String field) throws IOException {
        Terms terms = MultiFields.getTerms(indexReader, field);
        this.termsEnum = terms.iterator();
    }

    @Override
    protected Vector computeNext() {
        try {
            BytesRef term = this.termsEnum.next();

            while (term != null && !term.utf8ToString().matches("[a-zA-Z]+")){
                term = this.termsEnum.next();
            }
            if(term == null) return endOfData();

            String name = term.utf8ToString();
            System.out.println(name);

            int df = this.termsEnum.docFreq();

            this.postingsEnum = this.termsEnum.postings(this.postingsEnum);

            Vector result = new RandomAccessSparseVector(numDocs);

            int docid;

            while((docid = postingsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS){
//                Terms currDocTerms = indexReader.getTermVector(docid, field);
//                if(currDocTerms == null) {
//                    System.out.println("Empty Term Vector on " + docid);
//                    return null;
//                }

                int docLen = this.docInfo.getDocLength(docid);
//                long docLen = currDocTerms.size();
                int tf = postingsEnum.freq();

                double score = weight.calculate(tf, df, docLen, numDocs);
//                System.out.print(" -- " + score);
//                result.setQuick(docid, 0.0);
                result.setQuick(docid, score);
            }

            if (normPower == LuceneIterable.NO_NORMALIZING) {
                result = new NamedVector(result, name);
            } else {
                result = new NamedVector(result.normalize(normPower), name);
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}