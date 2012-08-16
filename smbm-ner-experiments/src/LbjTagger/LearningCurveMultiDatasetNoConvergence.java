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
 * This class takes a list of training and testing sets and the 
 * the number of iterations to run to convergence. There is no convergence
 * criterion. The training is going on for the prescribed number of iterations,
 * and the best model for each test set is stored in a separate model file
 * SO IMPORTANT- THIS CLASS WILL PRODUCE MULTIPLE MODELS!!!
 * 
 * This class solves the problem of the need to tune on multiple datasets
 * Note that I'll save the best model for each dataset separately, which
 * may cause problems when testing independently!
 * If you want to test these models, some file copy-pasting might be necessary
 */
public class LearningCurveMultiDatasetNoConvergence
{
	
	public static void getLearningCurve(Vector<Data> trainDataSet,Vector<Data> testDataSet,int maxIterations) throws Exception
	{	  
		NETaggerLevel1 tagger1 = new NETaggerLevel1();
		tagger1.forget();
		NETaggerLevel2 tagger2 = new NETaggerLevel2();
		tagger2.forget();
		double[] bestF1Level1 = new double[testDataSet.size()];
		for(int i=0;i<bestF1Level1.length;i++)
			bestF1Level1[i]=-1;		
		int[] bestRoundLevel1 = new int[testDataSet.size()];
		for(int i=0;i<testDataSet.size();i++)
			bestRoundLevel1[i]=0;
		double[] bestF1Level2 = new double[testDataSet.size()];
		for(int i=0;i<testDataSet.size();i++)
			bestF1Level2[i]=-1;
		int[] bestRoundLevel2 = new int[testDataSet.size()];
		for(int i=0;i<testDataSet.size();i++)
			bestRoundLevel2[i]=0;


		for (int i = 0; i < maxIterations; ++i)
		{
			ParametersForLbjCode.trainingIteration=i;
			System.out.println("Learning round "+i);
			for(int dataId=0;dataId<trainDataSet.size();dataId++){
				Vector<LinkedVector> trainData = trainDataSet.elementAt(dataId).sentences;
				Decoder.clearPredictions(trainData);
				NETaggerLevel1.isTraining=true;
				NETaggerLevel2.isTraining=true;

				TextChunkRepresentationManager.changeChunkRepresentation(
						TextChunkRepresentationManager.EncodingScheme.BIO, 
						ParametersForLbjCode.taggingEncodingScheme, 
						trainData, NEWord.LabelToLookAt.GoldLabel);
					
				if(Parameters.featuresToUse.containsKey("PredictionsLevel1")){
					GlobalFeatures.aggregateLevel1Predictions(trainData);
					GlobalFeatures.aggregateEntityLevelPredictions(trainData);
					//GlobalFeatures.displayLevel1AggregationData(trainData);
				}
				System.out.println("Sentences trained on (out of "+trainData.size()+") :");
				for (int k=0;k<trainData.size();k++){
					if(k%100==0)
						System.out.print(" "+k);
					for (int j=0;j<trainData.elementAt(k).size();j++){
						tagger1.learn(trainData.elementAt(k).get(j));
						if(Parameters.featuresToUse.containsKey("PatternFeatures")||Parameters.featuresToUse.containsKey("PredictionsLevel1"))
							tagger2.learn(trainData.elementAt(k).get(j));
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
						trainData, NEWord.LabelToLookAt.GoldLabel);
			}
			
			System.out.println("Testing round "+i);
			//NETester.annotateBothLevels(testData, tagger1, tagger2);
			Vector<TestDiscrete[]> results = NETesterMultiDataset.printTestResultsByDataset(testDataSet,tagger1,tagger2,false);
			for(int dataId=0;dataId<testDataSet.size();dataId++){
				Data testData=testDataSet.elementAt(dataId);
				Decoder.annotateDataBIO(testData.sentences, tagger1, tagger2);
				double f1Level1 = results.elementAt(dataId)[0].getOverallStats()[2];
				//System.out.println("Level1 performance, dataset: "+testData.nickname+"; round "+(i + 1) + "\t" + f1Level1);
				double f1Level2 = results.elementAt(dataId)[1].getOverallStats()[2];
				//System.out.println("Level2 performance, dataset: "+testData.nickname+"; round "+(i + 1) + "\t" + f1Level2);

				if (f1Level1 > bestF1Level1[dataId])
				{
					bestF1Level1[dataId] = f1Level1;
					bestRoundLevel1[dataId] = i + 1;
					NETaggerLevel1.getInstance().binaryWrite(Parameters.pathToModelFile+"."+testData.nickname+".level1");
					System.out.println("Saving the model : "+Parameters.pathToModelFile+"."+testData.nickname+".level1");
				}
				if (f1Level2 > bestF1Level2[dataId])
				{
					bestF1Level2[dataId] = f1Level2;
					bestRoundLevel2[dataId] = i + 1;
					NETaggerLevel2.getInstance().binaryWrite(Parameters.pathToModelFile+"."+testData.nickname+".level2");
					System.out.println("Saving the model : "+Parameters.pathToModelFile+"."+testData.nickname+".level2");
				}
			}
			
		}
	}
}

