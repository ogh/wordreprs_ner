package ClassifiersAndUtils;
import java.util.Hashtable;
import java.util.Vector;

import IO.InFile;



public class UnigramStatistics {
	public Hashtable<String, Integer> wordCounts=new Hashtable<String, Integer>();
	boolean countRepsWithinDocs=false;
	public int totalWordCount=0;
	/*
	 * if countRepsWithinDoc is false, increase occurrence count
	 * only when the word appears in distince documents
	 */
	public  UnigramStatistics(String filename,FeatureMap map){
		InFile in=new InFile(filename);
		Vector<String> tokens=in.readLineTokens();
		while(tokens!=null){
			for(int i=0;i<tokens.size();i++)
				if(map.wordToFid.containsKey(tokens.elementAt(i)))
					addWord(tokens.elementAt(i));
			tokens=in.readLineTokens();
		}
		in.close();
	}

	public  UnigramStatistics(DocumentCollection docs,boolean _countRepsWithinDocs)
	{
		countRepsWithinDocs=_countRepsWithinDocs;
		System.out.println("Building unigram statistics");
		for(int i=0;i<docs.docs.size();i++)
		{
			addDoc(docs.docs.elementAt(i));
		}			
		System.out.println("Done building unigram statistics");
	}
	/*
	 * if countRepsWithinDoc is false, increase occurrence count
	 * only when the word appears in distince documents
	 */		
	public  UnigramStatistics(boolean _countRepsWithinDocs)
	{
		countRepsWithinDocs=_countRepsWithinDocs;
	}
	
	public void addDoc(Document doc)
	{
		Hashtable<String, Boolean> alreadyAppreared=new Hashtable<String, Boolean>();
		Vector<String> words=doc.words;
		for(int j=0;j<words.size();j++)
		{
			if(countRepsWithinDocs||(!alreadyAppreared.containsKey(words.elementAt(j))))
			{
				addWord(words.elementAt(j));
				alreadyAppreared.put(words.elementAt(j), true);
			}
		}		
	}
	
	public void addWord(String w){
		totalWordCount++;
		if(!wordCounts.containsKey(w))
		{
			wordCounts.put(w, 1);
		}
		else
		{
			int count=wordCounts.get(w);
			wordCounts.remove(w);
			wordCounts.put(w, count+1);
		}
	}
}
