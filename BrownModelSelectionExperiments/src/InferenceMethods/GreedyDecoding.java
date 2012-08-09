package InferenceMethods;


import java.util.*;

import LBJ2.learn.*;
import LBJ2.parse.*;
import LbjTagger.NEWord;
import StringStatisticsUtils.CharacteristicWords;



class GreedyDecoding
{
	/*
	 * This is the simplest greedy left-to right annotation.
	 */
	public static void annotateGreedy(Vector<LinkedVector> data,SparseNetworkLearner tagger,int inferenceLayer) throws Exception{
		if(inferenceLayer!=1&&inferenceLayer!=2){
			Exception e=new Exception("Terrible error- nonexisting inference layer");
			throw e;
		}		
		for (int k=0;k<data.size();k++){
			for (int i = 0; i < data.elementAt(k).size() ; ++i)
			{
				NEWord w=(NEWord)data.elementAt(k).get(i);
				if(inferenceLayer==1)
					w.neTypeLevel1 =  tagger.discreteValue(w);	 
				else
					w.neTypeLevel2 =  tagger.discreteValue(w);	 
			}
		}			    
	}
	/*
	 * This is the simplest greedy left-to right annotation.
	 */
	public static Vector<Vector<CharacteristicWords>> annotateGreedyWithConfidenceScores(Vector<LinkedVector> data,SparseNetworkLearner tagger,int inferenceLayer) throws Exception{
		if(inferenceLayer!=1&&inferenceLayer!=2){
			Exception e=new Exception("Terrible error- nonexisting inference layer");
			throw e;
		}
		Vector<Vector<CharacteristicWords>> res=new Vector<Vector<CharacteristicWords>>();
		for (int k=0;k<data.size();k++){
			Vector<CharacteristicWords> sentenceConf=new Vector<CharacteristicWords>();
			for (int i = 0; i < data.elementAt(k).size() ; ++i)
			{
				NEWord w=(NEWord)data.elementAt(k).get(i);
				sentenceConf.addElement(PredictionsToProbabilities.getPredictionConfidences(tagger, w));
				if(inferenceLayer==1)
					w.neTypeLevel1 =  tagger.discreteValue(w);	 
				else
					w.neTypeLevel2 =  tagger.discreteValue(w);	 
			}
			res.addElement(sentenceConf);
		}
		return res;
	}
}

