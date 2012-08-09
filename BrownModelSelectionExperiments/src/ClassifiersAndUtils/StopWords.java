package ClassifiersAndUtils;

import java.util.*;

import IO.InFile;




public class StopWords {
	public Hashtable<String, Boolean> h=new Hashtable<String, Boolean>();
	
	public StopWords(String filename)
	{
		InFile in=new InFile(filename);
		Vector<String> words=in.readLineTokens();
		while(words!=null)
		{
			for(int i=0;i<words.size();i++)
				this.h.put(words.elementAt(i).toLowerCase(), true);
			words=in.readLineTokens();
		}
	}
	
	public Vector<String> filterStopWords(Vector<String> words)
	{
		if(words==null)
			return null;
		Vector<String> res=new Vector<String>();
		for(int i=0;i<words.size();i++)
			if(!h.containsKey(words.elementAt(i).toLowerCase()))
				res.addElement(words.elementAt(i));
		return res;
	}
	
	public boolean isStopWord(String s){
		return h.containsKey(s.toLowerCase());
	}
	
	public Vector<String> extractStopWords(Vector<String> words)
	{
		if(words==null)
			return null;
		Vector<String> res=new Vector<String>();
		for(int i=0;i<words.size();i++)
			if(h.containsKey(words.elementAt(i).toLowerCase()))
				res.addElement(words.elementAt(i));
		return res;
	}
	
}
