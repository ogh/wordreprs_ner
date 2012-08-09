package ClassifiersAndUtils;

import java.util.Vector;

import IO.InFile;


/*
 * Much like the DocumentCollection class, except
 * it read the doc one by one. This is for the cases when the 
 * dataset is too large to fit in memeory and i'm using online learning
 */
public class DocumentReader {
	InFile in=null;
	StopWords stops=null;
	boolean discardFirstToken=false;
	
	public DocumentReader(String filename,StopWords _stops,boolean _discardFirstToken) {
		in=new InFile(filename);
		stops=_stops;
		discardFirstToken=_discardFirstToken;
	}
	
	/*
	 * This code assumes each line in a file contains a new document
	 */
	public Document nextDoc(int initClassID){
		Vector<String> words=in.readLineTokens();
		if((discardFirstToken)&&(words!=null)&&(words.size()>0))
			words.removeElementAt(0);
		if(stops!=null)
			words=stops.filterStopWords(words);
		while(words!=null)
		{
			if(words.size()>0)
				return new Document(words,initClassID);
			words=in.readLineTokens();
			if((discardFirstToken)&&(words!=null)&&(words.size()>0))
				words.removeElementAt(0);
			if(stops!=null)
				words=stops.filterStopWords(words);
		}
		return null;
	}
}
