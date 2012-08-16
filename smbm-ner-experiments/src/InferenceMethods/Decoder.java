package InferenceMethods;
import LbjFeatures.*;

import java.util.*;

import ExpressiveFeatures.GlobalFeatures;
import LBJ2.learn.SparseNetworkLearner;
import LBJ2.nlp.Word;
import LBJ2.parse.*;
import LbjTagger.NEWord;
import LbjTagger.NamedEntity;
import LbjTagger.ParametersForLbjCode;
import LbjTagger.TextChunkRepresentationManager;
import StringStatisticsUtils.CharacteristicWords;


public class Decoder
{
		
	/*
	 * 
	 * If you don't wanna use some of the classifiers - pass null parameters. 
	 * 
	 */
	public static void annotateDataBIO(Vector<LinkedVector> data,
			NETaggerLevel1 taggerLevel1,
			NETaggerLevel2 taggerLevel2) throws Exception{
		long time =System.currentTimeMillis();
		Decoder.annotateAllLevelsWithTaggers(data,taggerLevel1, taggerLevel2);
		System.out.println("Inference time: "+(System.currentTimeMillis()-time)+" milliseconds");
	}

	
	public static Vector<NamedEntity>  annotateDataBIOWithConfidenceScores(Vector<LinkedVector> data,
			NETaggerLevel1 taggerLevel1,
			NETaggerLevel2 taggerLevel2) throws Exception{
		if(!ParametersForLbjCode.inferenceMethod.equals(ParametersForLbjCode.InferenceMethods.GREEDY)){
			System.out.println("Error: Only the greedy inference supports confidence-based predictions right now.");
			throw new Exception("Error: Only the greedy inference supports confidence-based predictions right now. The encoding scheme used is: "+ParametersForLbjCode.taggingEncodingScheme);
		}
		if(!ParametersForLbjCode.taggingEncodingScheme.equals(TextChunkRepresentationManager.EncodingScheme.BILOU)){
			System.out.println("Error: Only the BILOU encoding scheme supports confidence-based predictions right now.");
			throw new Exception("Error: Only the BILOU encoding scheme supports confidence-based predictions right now. The encoding scheme used is: "+ParametersForLbjCode.taggingEncodingScheme);
		}

		Vector<Vector<CharacteristicWords>> confidences=Decoder.annotateGreedyAllLevelsWithTaggersWithConfidenceScores(data, taggerLevel1, taggerLevel2);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				data, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				ParametersForLbjCode.taggingEncodingScheme, 
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				data, NEWord.LabelToLookAt.PredictionLevel2Tagger);

		Vector<NamedEntity> markedEntities=Decoder.getMarkedEntities(data);
		Decoder.markPredictionConfidences(data,markedEntities,confidences);
		
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				data, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				data, NEWord.LabelToLookAt.PredictionLevel2Tagger);
		return markedEntities;
	}

	
	private static void markPredictionConfidences(Vector<LinkedVector> data,
			Vector<NamedEntity> markedEntities,
			Vector<Vector<CharacteristicWords>> predictionConfidenceScores) throws Exception {
		for(int i=0;i<markedEntities.size();i++){
			NamedEntity entity=markedEntities.elementAt(i);
			NamedEntity.possibleLabels=new String[ParametersForLbjCode.labelTypes.length+1];
			for(int j=0;j<ParametersForLbjCode.labelTypes.length;j++)
				NamedEntity.possibleLabels[j]=ParametersForLbjCode.labelTypes[j];
			NamedEntity.possibleLabels[NamedEntity.possibleLabels.length-1]="O";

			entity.confidences=new double[NamedEntity.possibleLabels.length];
			for(int k=0;k<entity.confidences.length;k++)
				entity.confidences[k]=0.0;
			if(entity.startTokenSentenceId==entity.startTokenSentenceId&&
					entity.startTokenWordInSentenceId==entity.endTokenWordInSentenceId){				
				CharacteristicWords predictionsConf=predictionConfidenceScores.elementAt(entity.startTokenSentenceId).elementAt(entity.startTokenWordInSentenceId);
				for(int k=0;k<NamedEntity.possibleLabels.length;k++)
					for(int j=0;j<predictionsConf.topWords.size();j++){
						if(predictionsConf.topWords.elementAt(j).equals("U-"+NamedEntity.possibleLabels[k]))
							entity.confidences[k]=predictionsConf.topScores.elementAt(j);//should be the square root of the square, so this cancels out
						if(predictionsConf.topWords.elementAt(j).equals("O"))
							entity.confidences[NamedEntity.possibleLabels.length-1]=predictionsConf.topScores.elementAt(j);//should be the square root of the square, so this cancels out
					}
			}
			else{
				CharacteristicWords predictionsConfStart=predictionConfidenceScores.elementAt(entity.startTokenSentenceId).elementAt(entity.startTokenWordInSentenceId);
				for(int k=0;k<NamedEntity.possibleLabels.length;k++)
					for(int j=0;j<predictionsConfStart.topWords.size();j++){
						if(predictionsConfStart.topWords.elementAt(j).equals("B-"+NamedEntity.possibleLabels[k]))
							entity.confidences[k]=predictionsConfStart.topScores.elementAt(j);
						if(predictionsConfStart.topWords.elementAt(j).equals("O"))
							entity.confidences[NamedEntity.possibleLabels.length-1]=predictionsConfStart.topScores.elementAt(j);
					}				
				CharacteristicWords predictionsConfEnd=predictionConfidenceScores.elementAt(entity.endTokenSentenceId).elementAt(entity.endTokenWordInSentenceId);
				for(int k=0;k<NamedEntity.possibleLabels.length;k++)
					for(int j=0;j<predictionsConfEnd.topWords.size();j++){
						if(predictionsConfEnd.topWords.elementAt(j).equals("L-"+NamedEntity.possibleLabels[k]))
							entity.confidences[k]*=predictionsConfEnd.topScores.elementAt(j);
						if(predictionsConfEnd.topWords.elementAt(j).equals("O"))
							entity.confidences[NamedEntity.possibleLabels.length-1]*=predictionsConfEnd.topScores.elementAt(j);
					}				
				for(int k=0;k<entity.confidences.length;k++)
					entity.confidences[k]=Math.sqrt(entity.confidences[k]);
				double sum=0;
				for(int k=0;k<entity.confidences.length;k++)
					sum+=entity.confidences[k];
				if(sum>0)
					for(int k=0;k<entity.confidences.length;k++)
						entity.confidences[k]/=sum;
			}			
		}
	}

	private static Vector<NamedEntity> getMarkedEntities(
			Vector<LinkedVector> taggedData) throws Exception {
		Vector<NamedEntity> res=new Vector<NamedEntity>();
		int startSentence=-1;
		int startToken=-1;
		for(int i=0;i<taggedData.size();i++)
			for(int j=0;j<taggedData.elementAt(i).size();j++){
				NEWord w=(NEWord)taggedData.elementAt(i).get(j);
				if(startSentence==-1&&w.neTypeLevel2.startsWith("B-")){
					startSentence=i;
					startToken=j;
				}
				if(startSentence==-1&&w.neTypeLevel2.startsWith("U-")){
					NamedEntity e=new NamedEntity(taggedData,i,j,i,j);
					res.addElement(e);
					startSentence=startToken=-1;
				}
				if(startSentence==-1&&w.neTypeLevel2.startsWith("L-")){	
					throw new Exception("Entity end spotted, but the entity start was missing!");
				}
				if(startSentence!=-1&&w.neTypeLevel2.startsWith("L-")){
					NamedEntity e=new NamedEntity(taggedData,startSentence,startToken,i,j);
					res.addElement(e);
					startSentence=startToken=-1;
				}
			}
		return res;
	}
	
	
	/*
	 * 
	 *  use taggerLevel2=null if you want to use only one level of inference
	 * 
	 */
	protected static Vector<Vector<CharacteristicWords>>  annotateGreedyAllLevelsWithTaggersWithConfidenceScores(Vector<LinkedVector> data,
			NETaggerLevel1 taggerLevel1,
			NETaggerLevel2 taggerLevel2) throws Exception {
		Vector<Vector<CharacteristicWords>> confidences=null;

		System.out.println("Annontating data with the models tagger, the inference algoritm is: "+ParametersForLbjCode.InferenceMethods.GREEDY.toString());
		clearPredictions(data);
		NETaggerLevel1.isTraining=false;
		NETaggerLevel2.isTraining=false;		
		
		
		confidences=GreedyDecoding.annotateGreedyWithConfidenceScores(data,taggerLevel1,1);

		if(taggerLevel2!=null&&(ParametersForLbjCode.featuresToUse.containsKey("PatternFeatures")||ParametersForLbjCode.featuresToUse.containsKey("PredictionsLevel1"))){
			//annotate with patterns
			if(ParametersForLbjCode.featuresToUse.containsKey("PredictionsLevel1")){
				GlobalFeatures.aggregateLevel1Predictions(data);
				GlobalFeatures.aggregateEntityLevelPredictions(data);
			}
			if(ParametersForLbjCode.inferenceMethod==ParametersForLbjCode.InferenceMethods.GREEDY)
				confidences=GreedyDecoding.annotateGreedyWithConfidenceScores(data,taggerLevel2,2);
		}
		else
		{
			for (int k=0;k<data.size();k++)
				for (int i = 0; i < data.elementAt(k).size(); i++){
					NEWord w=(NEWord)data.elementAt(k).get(i);
					w.neTypeLevel2=w.neTypeLevel1;
				}
		}

		TextChunkRepresentationManager.changeChunkRepresentation(
				ParametersForLbjCode.taggingEncodingScheme, 
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				data, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				ParametersForLbjCode.taggingEncodingScheme, 
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				data, NEWord.LabelToLookAt.PredictionLevel2Tagger);

		System.out.println("Done Annontating data with the models tagger, the inference algoritm is: "+ParametersForLbjCode.InferenceMethods.GREEDY.toString());
		return confidences;
	}
	

	/*
	 * 
	 *  use taggerLevel2=null if you want to use only one level of inference
	 * 
	 */
	protected static void annotateAllLevelsWithTaggers(Vector<LinkedVector> data,
			NETaggerLevel1 taggerLevel1,
			NETaggerLevel2 taggerLevel2) throws Exception {
		System.out.println("Annontating data with the models tagger, the inference algoritm is: "+ParametersForLbjCode.inferenceMethod.toString());
		clearPredictions(data);
		NETaggerLevel1.isTraining=false;
		NETaggerLevel2.isTraining=false;		
		
		
		if(ParametersForLbjCode.inferenceMethod==ParametersForLbjCode.InferenceMethods.GREEDY)
			GreedyDecoding.annotateGreedy(data,taggerLevel1,1);
		if(ParametersForLbjCode.inferenceMethod==ParametersForLbjCode.InferenceMethods.BEAMSEARCH)
			BeamsearchDecoding.annotateBeamsearch(data,taggerLevel1,1);
		if(ParametersForLbjCode.inferenceMethod==ParametersForLbjCode.InferenceMethods.VITERBI)
			ViterbiDecoding.annotateViterbiSecondOrder(data,taggerLevel1,1);

		if(taggerLevel2!=null&&(ParametersForLbjCode.featuresToUse.containsKey("PatternFeatures")||ParametersForLbjCode.featuresToUse.containsKey("PredictionsLevel1"))){
			//annotate with patterns
			if(ParametersForLbjCode.featuresToUse.containsKey("PredictionsLevel1")){
				GlobalFeatures.aggregateLevel1Predictions(data);
				GlobalFeatures.aggregateEntityLevelPredictions(data);
			}
			if(ParametersForLbjCode.inferenceMethod==ParametersForLbjCode.InferenceMethods.GREEDY)
				GreedyDecoding.annotateGreedy(data,taggerLevel2,2);
			if(ParametersForLbjCode.inferenceMethod==ParametersForLbjCode.InferenceMethods.BEAMSEARCH)
				BeamsearchDecoding.annotateBeamsearch(data,taggerLevel2,2);
			if(ParametersForLbjCode.inferenceMethod==ParametersForLbjCode.InferenceMethods.VITERBI)
				ViterbiDecoding.annotateViterbiSecondOrder(data,taggerLevel2,2);
		}
		else
		{
			for (int k=0;k<data.size();k++)
				for (int i = 0; i < data.elementAt(k).size(); i++){
					NEWord w=(NEWord)data.elementAt(k).get(i);
					w.neTypeLevel2=w.neTypeLevel1;
				}
		}

		TextChunkRepresentationManager.changeChunkRepresentation(
				ParametersForLbjCode.taggingEncodingScheme, 
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				data, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				ParametersForLbjCode.taggingEncodingScheme, 
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				data, NEWord.LabelToLookAt.PredictionLevel2Tagger);

		System.out.println("Done Annontating data with the models tagger, the inference algoritm is: "+ParametersForLbjCode.inferenceMethod.toString());
	}
	
	/*
	 * Lbj does some pretty annoying caching. We need this method for the beamsearch and the viterbi. 
	 */
	public static void nullifyTaggerCachedFields(SparseNetworkLearner tagger){
		NEWord w=new NEWord(new Word("lala1"),null,"O");
		w.parts=new String[0];
		NEWord[] words={new NEWord(w,null,"O"),new NEWord(w,null,"O"),new NEWord(w,null,"O"),new NEWord(w,null,"O"),new NEWord(w,null,"O"),new NEWord(w,null,"O"),new NEWord(w,null,"O")};
		for(int i=1;i<words.length;i++){
			words[i].parts=new String[0];
			words[i].previous=words[i-1];
			words[i].previousIgnoreSentenceBoundary=words[i-1];
			words[i-1].next=words[i];
			words[i-1].nextIgnoreSentenceBoundary=words[i];
		}
		for(int i=0;i<words.length;i++)
			words[i].neTypeLevel1=words[i].neTypeLevel2="O";
		tagger.classify(words[3]);
	}

	public static void clearPredictions(Vector<LinkedVector> data){
		for (int k=0;k<data.size();k++){
			for(int i=0;i<data.elementAt(k).size();i++){
				((NEWord)data.elementAt(k).get(i)).neTypeLevel1=null;
				((NEWord)data.elementAt(k).get(i)).neTypeLevel2=null;
			}
		}		
	}

}

