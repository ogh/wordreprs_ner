package LbjTagger;

import java.util.Vector;
import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import ExpressiveFeatures.GlobalFeatures;
import InferenceMethods.Decoder;
import LBJ2.classify.TestDiscrete;
import LBJ2.parse.*;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import ParsingProcessingData.TaggedDataReader;
/*
 * This class was depricated
 */
public class LearningCurve
{
	/*
	public static void getLearningCurve(String trainFilename,String testFilename,String filesFormat) throws Exception{
		Data trainData=new Data(trainFilename,trainFilename,filesFormat,new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(trainData.sentences);
		Data testData=new Data(testFilename,testFilename,filesFormat,new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(testData.sentences);
		getLearningCurve(trainData, testData);
	}
	
	public static void getLearningCurve(Data trainData,Data testData) throws Exception
	{	  
		NETaggerLevel1 tagger1 = new NETaggerLevel1();
		tagger1.forget();
		NETaggerLevel2 tagger2 = new NETaggerLevel2();
		tagger2.forget();
		double bestF1Level1 = -1;
		int bestRoundLevel1 = 0;
		double bestF1Level2 = -1;
		int bestRoundLevel2 = 0;


		for (int i = 0; i < 1000 && i-bestRoundLevel2<10; ++i)
		{
			ParametersForLbjCode.trainingIteration=i;
			System.out.println("Learning round "+i);
			Decoder.clearPredictions(trainData.sentences);
			NETaggerLevel1.isTraining=true;
			NETaggerLevel2.isTraining=true;

			TextChunkRepresentationManager.changeChunkRepresentation(
					TextChunkRepresentationManager.EncodingScheme.BIO, 
					ParametersForLbjCode.taggingEncodingScheme, 
					trainData.sentences, NEWord.LabelToLookAt.GoldLabel);
				
			if(Parameters.featuresToUse.containsKey("PredictionsLevel1")){
				GlobalFeatures.aggregateLevel1Predictions(trainData.sentences);
				GlobalFeatures.aggregateEntityLevelPredictions(trainData.sentences);
				//GlobalFeatures.displayLevel1AggregationData(trainData);
			}
			System.out.println("Sentences trained on (out of "+trainData.sentences.size()+") :");
			for (int k=0;k<trainData.sentences.size();k++){
				if(k%100==0)
					System.out.print(" "+k);
				for (int j=0;j<trainData.sentences.elementAt(k).size();j++){
					tagger1.learn(trainData.sentences.elementAt(k).get(j));
					if(Parameters.featuresToUse.containsKey("PatternFeatures")||Parameters.featuresToUse.containsKey("PredictionsLevel1"))
						tagger2.learn(trainData.sentences.elementAt(k).get(j));
				}
			}
			System.out.println();
			//after we're done training, go back to BIO. This will not cause
			//problems when testing because all the "pattern extraction" and
			//"prediction aggregation" will use the predicted tags and not the
			//gold labels.
			TextChunkRepresentationManager.changeChunkRepresentation(
					ParametersForLbjCode.taggingEncodingScheme, 
					TextChunkRepresentationManager.EncodingScheme.BIO, 
					trainData.sentences, NEWord.LabelToLookAt.GoldLabel);

			
			System.out.println("Testing round "+i);
			//NETester.annotateBothLevels(testData, tagger1, tagger2);
			Decoder.annotateDataBIO(testData.sentences, tagger1, tagger2);
			Vector<Data> v=new Vector<Data>();
			v.addElement(testData);
			TestDiscrete[] results = NETesterMultiDataset.printTestResultsByDataset(v,false).elementAt(0);
			double f1Level1 = results[0].getOverallStats()[2];
			System.out.println("Level1: "+(i + 1) + "\t" + f1Level1);
			double f1Level2 = results[1].getOverallStats()[2];
			System.out.println("Level2: "+(i + 1) + "\t" + f1Level2);

			if (f1Level1 > bestF1Level1)
			{
				bestF1Level1 = f1Level1;
				bestRoundLevel1 = i + 1;
				NETaggerLevel1.getInstance().binaryWrite(Parameters.pathToModelFile+".level1");
			}
			if (f1Level2 > bestF1Level2)
			{
				bestF1Level2 = f1Level2;
				bestRoundLevel2 = i + 1;
				NETaggerLevel2.getInstance().binaryWrite(Parameters.pathToModelFile+".level2");
			}

			if ((i + 1) % 5 == 0)
				System.err.println((i + 1) + " rounds.  Best so far: Level1(" + bestRoundLevel1 + ")=" + bestF1Level1+" Level2(" + bestRoundLevel2 + ") " + bestF1Level2);
		}
	}*/
}

