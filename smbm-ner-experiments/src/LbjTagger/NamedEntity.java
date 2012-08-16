package LbjTagger;

import java.util.Vector;

import LBJ2.parse.LinkedVector;


/*
 *	This class was created to support confidence-based predictions   
 */
public class NamedEntity {
	public static String[] possibleLabels=null;
	NEWord firstWord=null;
	NEWord lastWord=null;
	public Vector<NEWord> tokens=null; //this will link to a token span within data.
	public int startTokenSentenceId=-1;//the sentence number of the first token in the data; 
	public int startTokenWordInSentenceId=-1;//the offset of the word within the sentence. 
	public int endTokenSentenceId=-1;//the sentence number of the first token in the data; 
	public int endTokenWordInSentenceId=-1;//the offset of the word within the sentence. 
	public double[] confidences=null;//confidences[i] is the confidence of predicting label possibleLabels[i];
	
	public NamedEntity(Vector<LinkedVector> data,int _startTokenSentenceId,int _startTokenWordInSentenceId,int _endTokenSentenceId,int _endTokenWordInSentenceId){
		startTokenSentenceId=_startTokenSentenceId;
		startTokenWordInSentenceId=_startTokenWordInSentenceId;
		endTokenSentenceId=_endTokenSentenceId;
		endTokenWordInSentenceId=_endTokenWordInSentenceId;
		
		int i=startTokenSentenceId;
		int j=startTokenWordInSentenceId;
		
		firstWord=(NEWord)data.elementAt(startTokenSentenceId).get(startTokenWordInSentenceId);
		lastWord=(NEWord)data.elementAt(endTokenSentenceId).get(endTokenWordInSentenceId);

		tokens=new Vector<NEWord>();
		while(i<=endTokenSentenceId&&(i<endTokenSentenceId||j<=endTokenWordInSentenceId)){
			LinkedVector sentence=data.elementAt(i);
			if(i<endTokenSentenceId)
				while(j<sentence.size()){
					tokens.addElement((NEWord)sentence.get(j));
					((NEWord)sentence.get(j)).predictedEntity=this;
					j++;
				}
			else
				while(j<=endTokenWordInSentenceId){
					tokens.addElement((NEWord)sentence.get(j));
					((NEWord)sentence.get(j)).predictedEntity=this;
					j++;
				}
			i++;
		}
	}
}
