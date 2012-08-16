package LbjTagger;


import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import LBJ2.nlp.SentenceSplitter;
import LBJ2.nlp.Word;
import LBJ2.nlp.WordSplitter;
import LBJ2.parse.LinkedVector;
import LBJ2.parse.Parser;
import LbjTagger.ParametersForLbjCode.TokenizationScheme;
import StringStatisticsUtils.OccurrenceCounter;


public class NEWord extends Word
{

	public enum LabelToLookAt {PredictionLevel2Tagger,PredictionLevel1Tagger,GoldLabel};

	/** This field is used to store a computed named entity type tag. */
	public String neTypeLevel1;
	public String neTypeLevel2;
	public NamedEntity predictedEntity=null;//if non-null it keeps the named entity the tagger annotated this word with
	/** This field stores the named entity type tag found in labeled data. */
	public String neLabel;
	
	public int sentenceId=-1;
	public int tokenId=-1;
	public String datasetName=null;

	public String originalSpelling="";
	public String[] parts;

	public double shapePredPer=0;
	public double shapePredOrg=0;
	public double shapePredLoc=0;

	public Vector<DiscreteFeature> generatedDiscreteFeaturesNonConjunctive=new Vector<DiscreteFeature>();
	public Vector<DiscreteFeature> generatedDiscreteFeaturesConjunctive=new Vector<DiscreteFeature>();
	public Vector<RealFeature> generatedRealFeaturesNonConjunctive=new Vector<RealFeature>();
	public Vector<RealFeature> generatedRealFeaturesConjunctive=new Vector<RealFeature>();
	public Vector<RealFeature> hmmFeatures=new Vector<RealFeature>();

	public Vector<String> gazetteers; 
	public Vector<String> matchedMultiTokenGazEntries=new Vector<String>();
	public Vector<String> matchedMultiTokenGazEntryTypes=new Vector<String>();//this is the name of the dictionary rather than entity type!
	public Vector<String> matchedMultiTokenGazEntriesIgnoreCase=new Vector<String>();
	public Vector<String> matchedMultiTokenGazEntryTypesIgnoreCase=new Vector<String>();//this is the name of the dictionary rather than entity type!
	public OccurrenceCounter gazetteerMatchAggregationFeatures=new OccurrenceCounter();



	public Hashtable<String,Integer> nonLocalFeatures=new Hashtable<String,Integer>();
	private String[] nonLocFeatArray=null;
	public NEWord nextIgnoreSentenceBoundary=null;
	public NEWord previousIgnoreSentenceBoundary=null;
	public OccurrenceCounter mostFrequentLevel1Prediction=new OccurrenceCounter();
	public OccurrenceCounter mostFrequentLevel1PredictionType=new OccurrenceCounter();
	public OccurrenceCounter mostFrequentLevel1NotOutsidePrediction=new OccurrenceCounter();
	public OccurrenceCounter mostFrequentLevel1NotOutsidePredictionType=new OccurrenceCounter();

	public String entity;
	public String entityType;
	public OccurrenceCounter mostFrequentLevel1TokenInEntityType=new OccurrenceCounter();
	public OccurrenceCounter mostFrequentLevel1ExactEntityType=new OccurrenceCounter();
	public OccurrenceCounter mostFrequentLevel1SuperEntityType=new OccurrenceCounter();



	/**
	 * An <code>NEWord</code> can be constructed from a <code>Word</code>
	 * object representing the same word, an <code>NEWord</code> representing
	 * the previous word in the sentence, and the named entity type label found
	 * in the data.
	 *
	 * @param w    Represents the same word as the <code>NEWord</code> being
	 *             constructed.
	 * @param p    The previous word in the sentence.
	 * @param type The named entity type label for this word from the data.
	 **/
	public NEWord(Word w, NEWord p, String type)
	{
		super(w.form, w.partOfSpeech, w.lemma, w.wordSense, p, w.start, w.end);
		neLabel = type;
		neTypeLevel1=null;
	}


	/**
	 * Produces a simple <code>String</code> representation of this word in
	 * which the <code>neLabel</code> field appears followed by the word's part
	 * of speech and finally the form (i.e., spelling) of the word all
	 * surrounded by parentheses.
	 **/
	public String toString()
	{
		return "(" + neLabel + " " + partOfSpeech + " " + form + ")";
	}

	public String[] getAllNonlocalFeatures(){
		if(nonLocFeatArray==null){
			Vector<String> v=new Vector<String>();
			for(Iterator<String> i=nonLocalFeatures.keySet().iterator();i.hasNext();v.addElement(i.next()));
			nonLocFeatArray=new String[v.size()];
			for(int i=0;i<v.size();i++)
				nonLocFeatArray[i]=v.elementAt(i);
		}
		return nonLocFeatArray;
	}

	public int getNonLocFeatCount(String nonLocFeat)
	{
		return nonLocalFeatures.get(nonLocFeat).intValue();
	}


	public static void addTokenToSentence(LinkedVector sentence,String token,String tag){
		NEWord word=new NEWord(new Word(token),null,tag);
		Vector<NEWord> v=NEWord.splitWord(word);
		if(ParametersForLbjCode.tokenizationScheme.equals(TokenizationScheme.DualTokenizationScheme)){
			sentence.add(word);
			word.parts=new String[v.size()];
			for(int j=0;j<v.size();j++)
				word.parts[j]=v.elementAt(j).form;
		}
		else{
			if(ParametersForLbjCode.tokenizationScheme.equals(TokenizationScheme.LbjTokenizationScheme)){
				for(int j=0;j<v.size();j++)
					sentence.add(v.elementAt(j));
			}
			else{
				System.out.println("Fatal error in BracketFileManager.readAndAnnotate - unrecognized tokenization scheme: "+ParametersForLbjCode.tokenizationScheme);
				System.exit(0);
			}					  
		}		
	}
	
	
	/*
	 * Used for some tokenization schemes. 
	 */
	private static Vector<NEWord> splitWord(NEWord word){
		//System.out.println("------lala: "+word.form+" ");
		String[] sentence={word.form+" "};
		Parser parser = new WordSplitter(new SentenceSplitter(sentence));
		LinkedVector words=(LinkedVector) parser.next();
		Vector<NEWord> res=new Vector<NEWord>(); 
		String label=word.neLabel;
		for(int i=0;i<words.size();i++){
			if(label.indexOf("B-")>-1&&i>0)
				label="I-"+label.substring(2);
			NEWord w=new NEWord(new Word(((Word)words.get(i)).form),null,label);
			w.originalSpelling=word.form;
			res.addElement(w);
		}
		// for(int i=0;i<words.size();i++)
		//   System.out.println(((NEWord)res.elementAt(i)));
		return res;
	}

	public String getPrediction(LabelToLookAt labelType) {
		if (labelType==LabelToLookAt.GoldLabel) 
			return this.neLabel;
		if (labelType==LabelToLookAt.PredictionLevel1Tagger) 
			return this.neTypeLevel1;
		if (labelType==LabelToLookAt.PredictionLevel2Tagger) 
			return this.neTypeLevel2;
		return null;
	}
	

	public void setPrediction(String label,LabelToLookAt labelType) {
		if (labelType==LabelToLookAt.GoldLabel) 
			this.neLabel=label;
		if (labelType==LabelToLookAt.PredictionLevel1Tagger) 
			this.neTypeLevel1=label;
		if (labelType==LabelToLookAt.PredictionLevel2Tagger) 
			this.neTypeLevel2=label;
	}
	
	public static class DiscreteFeature{
		public String featureValue;
		public String featureGroupName;
		public boolean useWithinTokenWindow=false; //generate this feature for a window of +-2 tokens
	}
	
	public static class RealFeature{
		public double featureValue;
		public String featureGroupName;
		public boolean useWithinTokenWindow=false; //generate this feature for a window of +-2 tokens
	}
}

