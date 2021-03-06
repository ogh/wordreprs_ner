package StringStatisticsUtils;

import java.util.Hashtable;
import java.util.Iterator;



public class OccurrenceCounter {
	public Hashtable<String,Double> counts=null;
	public int uniqueTokens=0;
	public int totalTokens=0;
	
	public void addToken(String s)
	{
		if(counts==null)
			counts=new Hashtable<String, Double>();
		totalTokens++;
		if(counts.containsKey(s))
		{
			double v=counts.get(s).doubleValue();
			counts.remove(s);
			counts.put(s, v+1.0);
		}else{
			uniqueTokens++;
			counts.put(s, 1.0);
		}
	}
	
	public void addToken(String s,double d)
	{
		if(counts==null)
			counts=new Hashtable<String, Double>();
		totalTokens+=d;
		if(counts.containsKey(s))
		{
			double v=counts.get(s).doubleValue();
			counts.remove(s);
			counts.put(s, v+d);
		}else{
			uniqueTokens++;
			counts.put(s, d);
		}
	}
	
	public double getCount(String s)
	{
		if(counts==null)
			counts=new Hashtable<String, Double>();
		if(counts.containsKey(s))
			return counts.get(s).doubleValue();
		return 0;
	}
	
	public Iterator<String> getTokensIterator(){
		if(counts==null)
			counts=new Hashtable<String, Double>();
		return counts.keySet().iterator();
	}
	
	public String[] getTokens(){
		String[] res=new String[uniqueTokens];
		int i=0;
		for(Iterator<String> iter=getTokensIterator();iter.hasNext();res[i++]=iter.next());
		return res;
	}
	
	public CharacteristicWords getMostFrequentTokens(int numOfTokensToReturn){
		CharacteristicWords topFeatures=new CharacteristicWords(numOfTokensToReturn);
		Iterator<String> iter=this.getTokensIterator();
		while(iter.hasNext()){
			String s=iter.next();
			topFeatures.addElement(s,this.getCount(s));
		}
		return topFeatures;
	}
	
	public String toString(){
		String res="";
		for(Iterator<String> iter=getTokensIterator();iter.hasNext();){
			String s=iter.next();
			res+="\t"+s+"\t-\t"+getCount(s)+"\n";
		}
		return res;
	}
}
