package InferenceMethods;

import java.util.*;

import LBJ2.classify.*;
import LBJ2.learn.*;
import LBJ2.nlp.Word;
import LBJ2.parse.*;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;
import StringStatisticsUtils.CharacteristicWords;
import StringStatisticsUtils.MemoryEfficientHashtable;

class ViterbiDecoding
{
		
	public static void annotateViterbiSecondOrder(Vector<LinkedVector> data,
			SparseNetworkLearner tagger,int inferenceLayer)throws Exception
	{
		NEWord temp=new NEWord(new Word("lala"),null,"O");
		temp.parts=new String[0];
		Score[] sampleScores=tagger.scores(temp).toArray();
		String[] possibleLabels=new String[sampleScores.length];
		for(int i=0;i<possibleLabels.length;i++)
			possibleLabels[i]=sampleScores[i].value;
		int numStates=possibleLabels.length;
		
		Vector<MemoryEfficientHashtable<String,Double>> pathScores=new Vector<MemoryEfficientHashtable<String,Double>>(400);
		Vector<MemoryEfficientHashtable<String,String>> pathPaths=new Vector<MemoryEfficientHashtable<String,String>>(400);
		MemoryEfficientHashtable<String, Double> scores=new MemoryEfficientHashtable<String, Double>(400);
		scores.put("O O", 0.0);
		MemoryEfficientHashtable<String, String> paths=new MemoryEfficientHashtable<String, String>(400);
		paths.put("O O","O");
		pathPaths.addElement(paths);
		pathScores.addElement(scores);
		
		Vector<NEWord> allWords=new Vector<NEWord>();
		for(int sentenceId=0 ;sentenceId<data.size();sentenceId++)
		{
			System.out.println("Sentence "+sentenceId+" of "+data.size());
			LinkedVector sentence=data.elementAt(sentenceId);

			// 	fill the dynamic programming table
			for (int t = 0; t < sentence.size() ; t++){
				NEWord w=(NEWord)sentence.get(t);
				allWords.addElement(w);
				paths=new MemoryEfficientHashtable<String, String>(400);
				scores=new MemoryEfficientHashtable<String, Double>(400);

				for(int prev2State=0;prev2State<numStates;prev2State++){
					for(int prev1State=0;prev1State<numStates;prev1State++){						
						String prevTransition=possibleLabels[prev2State]+" "+possibleLabels[prev1State];
						if(pathScores.elementAt(pathScores.size()-1).containsKey(prevTransition)&&
								TransitionsConstraints.isLegalTransition(prev2State,prev1State,possibleLabels))
						{
							double logProbTillNow=pathScores.elementAt(pathScores.size()-1).get(prevTransition);
							refreshCashedFields(w, prevTransition, inferenceLayer);
							Decoder.nullifyTaggerCachedFields(tagger);
							CharacteristicWords conf= PredictionsToProbabilities.getPredictionLogConfidences(tagger, w);
							for(int currentState = 0; currentState<conf.topWords.size(); currentState ++){
								if(TransitionsConstraints.isLegalTransition(prev1State,currentState,possibleLabels)){
									String currentTransition=possibleLabels[prev1State]+" "+possibleLabels[currentState];
									double currentScore=logProbTillNow+conf.topScores.elementAt(currentState);
									
									if(scores.containsKey(currentTransition)){
										double bestScore=scores.get(currentTransition);
										if(currentScore>bestScore){
											paths.remove(currentTransition);
											scores.remove(currentTransition);
											paths.put(currentTransition,possibleLabels[prev2State]);
											scores.put(currentTransition, currentScore);
										}
									}
									else{
										paths.put(currentTransition,possibleLabels[prev2State]);
										scores.put(currentTransition, currentScore);
									}
								}//if legal transition
							} 	//currentState
						} //if the prev state has the transition 
					}//prev1state								
				}//prev2state
				pathPaths.addElement(paths);
				pathScores.addElement(scores);
				//System.out.println("Best at time "+t+" :"+ getBest(pathScores.elementAt(t)));
			} //t  //finished processing the sentence
		}

		//tracing back the sentence label assignment
		String bestLast=getBest(pathScores.elementAt(pathScores.size()-1));
		if(ParametersForLbjCode.logging)
			ParametersForLbjCode.loggingFile.println("Data log-likelyhhod = "+pathScores.elementAt(pathScores.size()-1).get(bestLast));
		//in the for loop below, we don't look at the first element on purpose-
		// it's an artificially inserted "startSentnece" token with "O O" transition,
		// "O" previous best state and 0 log probability... 
		for(int i=pathScores.size()-1;i>0;i--){
			StringTokenizer st=new StringTokenizer(bestLast);
			String prev=st.nextToken();
			String current=st.nextToken();
			NEWord w=allWords.elementAt(i-1);//the pathscores is one larger than the word sequence
			if(inferenceLayer==1)
				w.neTypeLevel1=current;
			else
				w.neTypeLevel2=current;
			bestLast=pathPaths.elementAt(i).get(bestLast)+" "+prev;
		}
	}

	private static String getBest(MemoryEfficientHashtable<String,Double> scores){
		String bestLast="null";
		double bestScore=Double.NEGATIVE_INFINITY;
		for(int i=0;i<scores.keys.size();i++){
			String s=scores.keys.elementAt(i);
			if(scores.get(s)>bestScore){
				bestLast=s;
				bestScore=scores.values.elementAt(i);
			}
		}
		return bestLast;
	}

	private static void refreshCashedFields(NEWord w,String prevValuesString,int inferenceLayer){
		NEWord temp=w;
		for(int count=0;count<1000&&temp!=null;count++){
			if(inferenceLayer==1)
				temp.neTypeLevel1=null;
			else
				temp.neTypeLevel2=null;
			temp=temp.nextIgnoreSentenceBoundary;
		}
		temp=w;
		//initialize the cached fields in the previous words
		StringTokenizer st=new StringTokenizer(prevValuesString);
		String[] prevValues=new String[2];
		//we have to invert the order here
		prevValues[1]=st.nextToken();
		prevValues[0]=st.nextToken();
		temp=w.previousIgnoreSentenceBoundary;
		for(int i=0;i<prevValues.length&&temp!=null;i++){
			if(inferenceLayer==1)
				temp.neTypeLevel1=prevValues[i];
			else	
				temp.neTypeLevel2=prevValues[i];
			temp=temp.previousIgnoreSentenceBoundary;
		}
	}
}

