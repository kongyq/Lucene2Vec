package testbed.mike.mahout.vectors.lucene;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import org.apache.lucene.index.*;
import org.apache.lucene.util.BytesRef;
import org.apache.mahout.utils.vectors.TermEntry;
import org.apache.mahout.utils.vectors.TermInfo;

import java.io.IOException;
import java.util.*;

public class CachedDocInfo implements TermInfo {
    private final Int2IntOpenHashMap docEntries = new Int2IntOpenHashMap();
    private final String field;
    private final Map<String, TermEntry> termEntries;

    public CachedDocInfo(IndexReader reader, String field) throws IOException {
        this(reader, field, null);
    }

    public CachedDocInfo(IndexReader reader, String field, HashSet<String> wordList) throws IOException {
        this.field = field;
        int numDocs = reader.numDocs();

        this.termEntries = new LinkedHashMap<>();

        if(wordList == null){
            Terms t = MultiFields.getTerms(reader, field);
            TermsEnum te = t.iterator();

            int count = 0;
            BytesRef text;
            while ((text = te.next()) != null) {
                int df = te.docFreq();
                TermEntry entry = new TermEntry(text.utf8ToString(), count++, df);
                this.termEntries.put(entry.getTerm(), entry);
            }
        }else{

            Terms t = new
        }

        for (int docid = 0; docid < numDocs; docid++) {
            if(docid % 10000 == 0) System.out.println(docid + " / " + numDocs + "loaded");
            Terms terms = reader.getTermVector(docid, field);
            if(terms == null){
                this.docEntries.put(docid, 0);
            }else{
                if(terms.size() == -1) {
                    System.out.println("Error! size is not in the codex!");
                    this.docEntries.put(docid, 0);
                }else{
                    this.docEntries.put(docid, (int)terms.size());
                }
            }
        }
    }

    public int getDocLength(int docid) {
        return this.docEntries.get(docid);
    }

    public int getNumDocs() {
        return this.numDocs;
    }

    @Override
    public int totalTerms(String field) {
        return this.termEntries.size();
    }

    @Override
    public TermEntry getTermEntry(String field, String term) {
        if (!this.field.equals(field)) {
            return null;
        }
        return termEntries.get(term);
    }

    @Override
    public Iterator<TermEntry> getAllEntries() {
        return termEntries.values().iterator();
    }
}
