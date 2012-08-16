package LbjTagger;


import java.util.*;

import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import InferenceMethods.Decoder;
import LBJ2.classify.*;
import LBJ2.parse.*;
import LbjFeatures.NELabel;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import LbjTagger.NEWord;
import LbjTagger.Parameters;
import ParsingProcessingData.TaggedDataReader;

/*
 * This class has been depricated!!!
 */
public class NETester
{
/*
	public static void test(String testFilename,String fileFormat,boolean verbose) throws Exception
	{		
		Vector<LinkedVector> testData=TaggedDataReader.readData(testFilename, fileFormat);
		ExpressiveFeaturesAnnotator.annotate(testData);

		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		//annotateBothLevels(testData, taggerLevel1, taggerLevel2);
		Decoder.annotateDataBIO(testData, taggerLevel1, taggerLevel2);
		printTestResults(testData,verbose);
	}

	//
	//	assumes that the data has been annotated by both levels of taggers 
	//
	public static TestDiscrete[] printTestResults(Vector<LinkedVector> data,boolean verbose)
	{	 

		NELabel labeler = new NELabel();

		TestDiscrete resultsPhraseLevel1 = new TestDiscrete();
		resultsPhraseLevel1.addNull("O");
		TestDiscrete resultsTokenLevel1 = new TestDiscrete();
		resultsTokenLevel1.addNull("O");

		TestDiscrete resultsPhraseLevel2 = new TestDiscrete();
		resultsPhraseLevel2.addNull("O");
		TestDiscrete resultsTokenLevel2 = new TestDiscrete();
		resultsTokenLevel2.addNull("O");		

		for (int k=0;k<data.size();k++)
		{
			LinkedVector vector=data.elementAt(k);
			int N = vector.size();	
			String[] predictionsLevel1 = new String[N],predictionsLevel2 = new String[N], labels = new String[N];

			for (int i = 0; i < N; ++i)
			{
				predictionsLevel1[i] =  ((NEWord)vector.get(i)).neTypeLevel1;	 
				predictionsLevel2[i] =  ((NEWord)vector.get(i)).neTypeLevel2;	 
				labels[i] = labeler.discreteValue(vector.get(i));
				String pLevel1=predictionsLevel1[i];
				String pLevel2=predictionsLevel2[i];
				if(pLevel1.indexOf('-')>-1)
					pLevel1=pLevel1.substring(2);
				if(pLevel2.indexOf('-')>-1)
					pLevel2=pLevel2.substring(2);
				String l=labels[i];
				if(l.indexOf('-')>-1)
					l=l.substring(2);
				resultsTokenLevel1.reportPrediction(pLevel1, l);
				resultsTokenLevel2.reportPrediction(pLevel2, l);
			}


			//getting phrase level accuracy level1
			for (int i = 0; i < N; ++i)
			{
				String p = "O", l = "O";
				int pEnd = -1, lEnd = -1;

				if (predictionsLevel1[i].startsWith("B-")
						|| predictionsLevel1[i].startsWith("I-")
						&& (i == 0
								|| !predictionsLevel1[i - 1]
								                      .endsWith(predictionsLevel1[i].substring(2))))
				{
					p = predictionsLevel1[i].substring(2);
					pEnd = i;
					while (pEnd + 1 < N && predictionsLevel1[pEnd + 1].equals("I-" + p))
						++pEnd;
				}

				if (labels[i].startsWith("B-"))
				{
					l = labels[i].substring(2);
					lEnd = i;
					while (lEnd + 1 < N && labels[lEnd + 1].equals("I-" + l)) ++lEnd;
				}

				if (!p.equals("O") || !l.equals("O"))
				{
					if (pEnd == lEnd) resultsPhraseLevel1.reportPrediction(p, l);
					else
					{
						if (!p.equals("O")) resultsPhraseLevel1.reportPrediction(p, "O");
						if (!l.equals("O")) resultsPhraseLevel1.reportPrediction("O", l);
					}
				}
			}

			//getting phrase level accuracy level2
			for (int i = 0; i < N; ++i)
			{
				String p = "O", l = "O";
				int pEnd = -1, lEnd = -1;

				if (predictionsLevel2[i].startsWith("B-")
						|| predictionsLevel2[i].startsWith("I-")
						&& (i == 0
								|| !predictionsLevel2[i - 1]
								                      .endsWith(predictionsLevel2[i].substring(2))))
				{
					p = predictionsLevel2[i].substring(2);
					pEnd = i;
					while (pEnd + 1 < N && predictionsLevel2[pEnd + 1].equals("I-" + p))
						++pEnd;
				}

				if (labels[i].startsWith("B-"))
				{
					l = labels[i].substring(2);
					lEnd = i;
					while (lEnd + 1 < N && labels[lEnd + 1].equals("I-" + l)) ++lEnd;
				}

				if (!p.equals("O") || !l.equals("O"))
				{
					if (pEnd == lEnd) resultsPhraseLevel2.reportPrediction(p, l);
					else
					{
						if (!p.equals("O")) resultsPhraseLevel2.reportPrediction(p, "O");
						if (!l.equals("O")) resultsPhraseLevel2.reportPrediction("O", l);
					}
				}
			}
		}

		TestDiscrete resultsByBILOU = new TestDiscrete();
		TestDiscrete resultsByBrackets = new TestDiscrete();
		resultsByBILOU.addNull("O");
		resultsByBrackets.addNull("O");
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				data, NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				data, NEWord.LabelToLookAt.PredictionLevel2Tagger);
		for(int i=0;i<data.size();i++)
			for(int j=0;j<data.elementAt(i).size();j++){
				NEWord w=(NEWord)data.elementAt(i).get(j);
				String bracketTypePrediction=w.neTypeLevel2;
				if(bracketTypePrediction.indexOf('-')>0)
					bracketTypePrediction=bracketTypePrediction.substring(0,1);
				String bracketTypeLabel=w.neLabel;
				if(bracketTypeLabel.indexOf('-')>0)
					bracketTypeLabel=bracketTypeLabel.substring(0,1);
				resultsByBILOU.reportPrediction(w.neTypeLevel2, w.neLabel);
				resultsByBrackets.reportPrediction(bracketTypePrediction, bracketTypeLabel);
			}
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				data, NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				data, NEWord.LabelToLookAt.PredictionLevel2Tagger);
		
		System.out.println("Phrase-level Acc Level2:");
		resultsPhraseLevel2.printPerformance(System.out);
		if(verbose){
			System.out.println("Token-level Acc Level2:");
			resultsTokenLevel2.printPerformance(System.out);
			System.out.println("Level2 BILOU Accuracy, letter-by-letter:");
			resultsByBILOU.printPerformance(System.out);
			System.out.println("Level2 BILOU PHRASE/BOUNDARY DETECTION Accuracy");
			resultsByBrackets.printPerformance(System.out);
			System.out.println("Phrase-level Acc Level1:");
			resultsPhraseLevel1.printPerformance(System.out);
			System.out.println("Token-level Acc Level1:");
			resultsTokenLevel1.printPerformance(System.out);
		}

		TestDiscrete[] res={resultsPhraseLevel1,resultsPhraseLevel2};
		return res;
	}  
	*/
}

