package InferenceMethods;


import java.util.*;

import LBJ2.learn.*;
import LBJ2.parse.*;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;
import StringStatisticsUtils.CharacteristicObjects;
import StringStatisticsUtils.CharacteristicWords;


class BeamsearchDecoding
{
	/*
	 * This is the beamsearch variation of inference
	 */
	public static void annotateBeamsearch(Vector<LinkedVector> data,SparseNetworkLearner tagger,int inferenceLayer) throws Exception{
		if(inferenceLayer!=1&&inferenceLayer!=2){
			System.out.println("Terrible error- nonexisting inference layer");
			System.exit(0);
		}		
		CharacteristicObjects beam=new CharacteristicObjects(ParametersForLbjCode.beamSize);
		CharacteristicObjects nextBeam=new CharacteristicObjects(ParametersForLbjCode.beamSize);
		for(int k=0;k<data.size();k++){
			//System.out.println(k+ " sentences processed, size of sentence "+k+" is: "+data.elementAt(k).size());
			for(int i = 0; i < data.elementAt(k).size() ; ++i){
				//printBeamDebug(beam);
				nextBeam=new CharacteristicObjects(ParametersForLbjCode.beamSize);
				if(beam.topObjects.size()>0){
					for(int j=0;j<beam.topObjects.size();j++){
						BeamSearchNode node=(BeamSearchNode)beam.topObjects.elementAt(j);
						NEWord w=(NEWord)data.elementAt(k).get(i);
						if(w.previousIgnoreSentenceBoundary!=null&&w.previousIgnoreSentenceBoundary!=node.w){
							System.out.println("Error- mismatching words!!!");
							throw new Exception("Error- mismatching words!!! Prev word: "+w.previousIgnoreSentenceBoundary+" prev word according to the beam: "+node.w);
						}

						//resetting the hashed fields from the beam
						BeamSearchNode tempNode=node;
						int count=0;
						while(tempNode!=null&&(count++<=1000))
						{
							if(inferenceLayer==1)
								tempNode.w.neTypeLevel1 = tempNode.label;
							else
								tempNode.w.neTypeLevel2 = tempNode.label;
							tempNode=tempNode.prev;
						}

						CharacteristicWords predictions=null;
						Decoder.nullifyTaggerCachedFields(tagger);

						if(inferenceLayer==1)
							predictions=PredictionsToProbabilities.getPredictionLogConfidences(tagger,w);
						else
							predictions=PredictionsToProbabilities.getPredictionLogConfidences(tagger,w);

						for(int l=0;l<predictions.topWords.size();l++){
							BeamSearchNode next=new BeamSearchNode();
							next.w=w;
							next.prev=node;
							next.idx=node.idx+1;
							next.label=(String)predictions.topWords.elementAt(l);
							next.logProbCurrentPrediction=predictions.topScores.elementAt(l);
							next.logProbSequencePrediction=node.logProbSequencePrediction+predictions.topScores.elementAt(l);
							String prevPrediction=next.prev.w.neTypeLevel1;
							if(inferenceLayer==2)
								prevPrediction=next.prev.w.neTypeLevel2;
							String nowPrediction=next.w.neTypeLevel1;
							if(inferenceLayer==2)
								nowPrediction=next.w.neTypeLevel2;
							if(TransitionsConstraints.isLegalTransition(prevPrediction, nowPrediction))
								nextBeam.addElement(next, next.logProbSequencePrediction);
						}
					}
				}
				else{
					NEWord w=(NEWord)data.elementAt(k).get(i);
					CharacteristicWords predictions=null;
					if(inferenceLayer==1)
						predictions=PredictionsToProbabilities.getPredictionLogConfidences(tagger,w);
					else
						predictions=PredictionsToProbabilities.getPredictionLogConfidences(tagger,w);
					for(int l=0;l<predictions.topWords.size();l++){
						BeamSearchNode next=new BeamSearchNode();
						next.w=w;
						next.idx=0;
						next.prev=null;
						next.label=(String)predictions.topWords.elementAt(l);
						next.logProbCurrentPrediction=predictions.topScores.elementAt(l);
						next.logProbSequencePrediction=next.logProbCurrentPrediction;
						nextBeam.addElement(next, next.logProbSequencePrediction);
					}
				}
				beam=nextBeam;
			}
		}
		BeamSearchNode lastNode=(BeamSearchNode)beam.getMax();
		if(ParametersForLbjCode.logging)
			ParametersForLbjCode.loggingFile.println("Data log-likelyhood = "+lastNode.logProbSequencePrediction);
		while(lastNode!=null){
			if(inferenceLayer==1)
				lastNode.w.neTypeLevel1 =  lastNode.label;	 
			else
				lastNode.w.neTypeLevel2 =  lastNode.label;
			lastNode=lastNode.prev;
		}
		System.out.println("Data logprob = "+((BeamSearchNode)beam.getMax()).logProbSequencePrediction);
	}

	/*
	private static void printBeamDebug(CharacteristicObjects beam){
		if(beam.topObjects.size()==0)
			return;
		BeamSearchNode lastNode=(BeamSearchNode)beam.topObjects.elementAt(beam.topObjects.size()-1);
		if(lastNode.idx>=1){
			System.out.println("Content of the beam:");
			for(int i=0;i<beam.topObjects.size();i++){
				lastNode=(BeamSearchNode)beam.topObjects.elementAt(i);
				System.out.println("Rank: "+i+"; score: "+ lastNode.logProbSequencePrediction+"; last prediction logprob= "+lastNode.logProbCurrentPrediction+"; Last pred: "+lastNode.label+"; labels: ");
				while(lastNode!=null){
					System.out.print(lastNode.w.form+"/"+lastNode.label+" ");
					lastNode=lastNode.prev;
				}
				System.out.println();
			}
			System.out.println("Top element: ");
			lastNode=(BeamSearchNode)beam.getMax();
			while(lastNode!=null){
				System.out.print(lastNode.w.form+"/"+lastNode.label+" ");
				lastNode=lastNode.prev;
			}
			System.out.println();
			System.exit(0);
		}
	}
	*/
}

