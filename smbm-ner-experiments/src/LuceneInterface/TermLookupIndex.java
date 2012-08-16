package LuceneInterface;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.NIOFSDirectory;

public class TermLookupIndex {
	
	protected String indexDirPath = null;
	private IndexWriter writer = null;
	private Searcher searcher = null;
	private IndexReader reader=null;

	public TermLookupIndex(String indexPath){
		indexDirPath=indexPath;
	}
	
	public void openIndexWriting() throws CorruptIndexException, LockObtainFailedException, IOException{
		writer = new IndexWriter(NIOFSDirectory.getDirectory(indexDirPath), new StandardAnalyzer(),IndexWriter.MaxFieldLength.LIMITED);
	}
	/*
	 * The key could either be the token form or a (sentence#,token#) key
	 */
	public void addToken(String key,String token,String representation) throws Exception{
		Document doc = new Document();
		doc.add(new Field("key",key,Field.Store.YES,Field.Index.NOT_ANALYZED));
		doc.add(new Field("token",token,Field.Store.YES,Field.Index.NOT_ANALYZED));
		doc.add(new Field("embedding",representation,Field.Store.YES,Field.Index.NOT_ANALYZED));
		writer.addDocument(doc);
	}
	
	/*
	 * once you're done with adding tokens and the corresponding representations- 
	 * close the index for writing and prepare it for retrieval 
	 */
	public void finalizeIndexWriting() throws CorruptIndexException, IOException{
		writer.setMergeFactor(2);
		writer.optimize();
		writer.close();
	}
	
	public void openIndexForRetrieval() throws CorruptIndexException, IOException{
		reader=IndexReader.open(NIOFSDirectory.getDirectory(indexDirPath),true);
		searcher= new IndexSearcher(reader);
	}
	
	public String getToken(String key) throws IOException{
		TopDocs hits=searcher.search(new TermQuery(new Term("key",key)),1);
		return searcher.doc(hits.scoreDocs[0].doc).get("token");
	}
	public double[] getEmbedding(String key) throws IOException{
		TopDocs hits=searcher.search(new TermQuery(new Term("key",key)),1);
		String embedding= searcher.doc(hits.scoreDocs[0].doc).get("embedding");
		Vector<Double> v=new Vector<Double>();
		StringTokenizer st=new StringTokenizer(embedding,"\t \n");
		while(st.hasMoreTokens())
			v.addElement(new Double(st.nextToken()));
		double[] res=new double[v.size()];
		for(int i=0;i<v.size();i++)
			res[i]=v.elementAt(i).doubleValue();
		return res;
	}	
}
