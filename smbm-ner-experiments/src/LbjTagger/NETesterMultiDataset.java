package LbjTagger;


import java.io.File;
import java.util.*;

import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import IO.OutFile;
import InferenceMethods.Decoder;
import LBJ2.classify.*;
import LBJ2.nlp.Word;
import LBJ2.parse.*;
import LbjFeatures.FeaturesLevel1SharedWithLevel2;
import LbjFeatures.FeaturesLevel2;
import LbjFeatures.NELabel;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import LbjTagger.NEWord;
import ParsingProcessingData.TaggedDataReader;

public class NETesterMultiDataset
{

	public static void test(String testDatapath,String dataFormat,boolean verbose) throws Exception
	{		
		Data testData=new Data(testDatapath,testDatapath,dataFormat,new String[]{}, new String[]{});
		ExpressiveFeaturesAnnotator.annotate(testData.sentences);
		Vector<Data> data=new Vector<Data>();
		data.addElement(testData);

		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		//annotateBothLevels(testData, taggerLevel1, taggerLevel2);
		printTestResultsByDataset(data, taggerLevel1, taggerLevel2,true);
	}

	public static void dumpFeaturesLabeledData(String testDatapath,String outDatapath,String dataFormat) throws Exception
	{		
		FeaturesLevel1SharedWithLevel2 features1=new FeaturesLevel1SharedWithLevel2();
		FeaturesLevel2 features2=new FeaturesLevel2();
		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
    	File f=new File(testDatapath);
    	Vector<String> inFiles=new Vector<String>();
    	Vector<String> outFiles=new Vector<String>();
    	if(f.isDirectory()){
    		String[] files=f.list();
    		for(int i=0;i<files.length;i++)
    			if(!files[i].startsWith(".")){
    				inFiles.addElement(testDatapath+"/"+files[i]);
    				outFiles.addElement(outDatapath+"/"+files[i]);
    			}
    	}
    	else{
    		inFiles.addElement(testDatapath);
    		outFiles.addElement(outDatapath);
    	}
    	for(int fileId=0;fileId<inFiles.size();fileId++){
    		Data testData=new Data(inFiles.elementAt(fileId),inFiles.elementAt(fileId),dataFormat,new String[]{}, new String[]{});
    		ExpressiveFeaturesAnnotator.annotate(testData.sentences);
    		Decoder.annotateDataBIO(testData.sentences, taggerLevel1, taggerLevel2);
    		OutFile out=new OutFile(outFiles.elementAt(fileId));
    		for(int i=0;i<testData.sentences.size();i++){
    			for(int j=0;j<testData.sentences.elementAt(i).size();j++){
    				NEWord w=(NEWord)testData.sentences.elementAt(i).get(j);
    				out.print(w.neLabel+"\t"+w.form+"\t");
    				FeatureVector fv1=features1.classify(w);
    				FeatureVector fv2=features2.classify(w);
    				for(int k=0;k<fv1.size();k++){
    					String s=fv1.features.get(k).toString();
						out.print(" "+s.substring(s.indexOf(':')+1,s.length()));
    				}
    				for(int k=0;k<fv2.size();k++){
    					String s=fv2.features.get(k).toString();
    					out.print(" "+s.substring(s.indexOf(':')+1,s.length()));
    				}
    				out.println("");
    			}
    			out.println("");
    		}
    		out.close();
    	}
	}

	public static Vector<TestDiscrete[]> printTestResultsByDataset(Vector<Data> dataCollection,
			NETaggerLevel1 tagger1,
			NETaggerLevel2 tagger2,
			boolean verbose) throws Exception{
		for(int i=0;i<dataCollection.size();i++)
			Decoder.annotateDataBIO(dataCollection.elementAt(i).sentences, tagger1, tagger2);
		 return printTestResultsByDataset(dataCollection,verbose);
	}

	public static TestDiscrete[] printAllTestResultsAsOneDataset(Vector<Data> dataCollection,
			NETaggerLevel1 tagger1,
			NETaggerLevel2 tagger2,
			boolean verbose) throws Exception{
		for(int i=0;i<dataCollection.size();i++)
			Decoder.annotateDataBIO(dataCollection.elementAt(i).sentences, tagger1, tagger2);
		return printAllTestResultsAsOneDataset(dataCollection,verbose);
	}
	

	/*
	 *	assumes that the data has been annotated by both levels of taggers 
	 */
	public static Vector<TestDiscrete[]> printTestResultsByDataset(Vector<Data> dataCollection,boolean verbose){
		Vector<TestDiscrete[]> res=new Vector<TestDiscrete[]>();
		for(int dataSetId=0;dataSetId<dataCollection.size();dataSetId++){
			TestDiscrete resultsPhraseLevel1 = new TestDiscrete();
			resultsPhraseLevel1.addNull("O");
			TestDiscrete resultsTokenLevel1 = new TestDiscrete();
			resultsTokenLevel1.addNull("O");

			TestDiscrete resultsPhraseLevel2 = new TestDiscrete();
			resultsPhraseLevel2.addNull("O");
			TestDiscrete resultsTokenLevel2 = new TestDiscrete();
			resultsTokenLevel2.addNull("O");		

			TestDiscrete resultsByBILOU = new TestDiscrete();
			TestDiscrete resultsSegmentation = new TestDiscrete();
			resultsByBILOU.addNull("O");
			resultsSegmentation.addNull("O");

			reportPredictions(dataCollection.elementAt(dataSetId),
					resultsTokenLevel1,
					resultsTokenLevel2,
					resultsPhraseLevel1,
					resultsPhraseLevel2,
					resultsByBILOU,
					resultsSegmentation);
			TestDiscrete[] resCurrentData={resultsPhraseLevel1,resultsPhraseLevel2};
			res.addElement(resCurrentData);
			if(verbose){
				System.out.println("------------------------------------------------------------");
				System.out.println("******	Performance on dataset "+dataCollection.elementAt(dataSetId).datasetPath+"  **********");
				System.out.println("------------------------------------------------------------");
				System.out.println("Phrase-level Acc Level2:");
				resultsPhraseLevel2.printPerformance(System.out);
				System.out.println("Token-level Acc Level2:");
				resultsTokenLevel2.printPerformance(System.out);
				System.out.println("Level2 BILOU Accuracy, letter-by-letter:");
				resultsByBILOU.printPerformance(System.out);
				System.out.println("Level2 BILOU PHRASE/BOUNDARY DETECTION Accuracy");
				resultsSegmentation.printPerformance(System.out);
				System.out.println("Phrase-level Acc Level1:");
				resultsPhraseLevel1.printPerformance(System.out);
				System.out.println("Token-level Acc Level1:");
				resultsTokenLevel1.printPerformance(System.out);
				System.out.println("------------------------------------------------------------");
				System.out.println("****** (END)	Performance on dataset "+dataCollection.elementAt(dataSetId).datasetPath+"  **********");
				System.out.println("------------------------------------------------------------");
			}
			else{
				System.out.println(">>>>>>>>>	Phrase-level F1 on the dataset: "+dataCollection.elementAt(dataSetId).datasetPath);
				System.out.println("\t Level 1: "+resultsPhraseLevel1.getOverallStats()[2]);
				System.out.println("\t Level 2: "+resultsPhraseLevel2.getOverallStats()[2]);
			}
		}
		return res;
	}

	/*
	 *	assumes that the data has been annotated by both levels of taggers 
	 */
	public static TestDiscrete[] printAllTestResultsAsOneDataset(Vector<Data> dataCollection,boolean verbose)
	{	 
		TestDiscrete resultsPhraseLevel1 = new TestDiscrete();
		resultsPhraseLevel1.addNull("O");
		TestDiscrete resultsTokenLevel1 = new TestDiscrete();
		resultsTokenLevel1.addNull("O");

		TestDiscrete resultsPhraseLevel2 = new TestDiscrete();
		resultsPhraseLevel2.addNull("O");
		TestDiscrete resultsTokenLevel2 = new TestDiscrete();
		resultsTokenLevel2.addNull("O");		

		TestDiscrete resultsByBILOU = new TestDiscrete();
		TestDiscrete resultsSegmentation = new TestDiscrete();
		resultsByBILOU.addNull("O");
		resultsSegmentation.addNull("O");

		for(int dataSetId=0;dataSetId<dataCollection.size();dataSetId++)
			reportPredictions(dataCollection.elementAt(dataSetId),
						resultsTokenLevel1,
						resultsTokenLevel2,
						resultsPhraseLevel1,
						resultsPhraseLevel2,
						resultsByBILOU,
						resultsSegmentation);
		
		System.out.println("------------------------------------------------------------");
		System.out.println("******	Combined performance on all the datasets :");
		for(int i=0;i<dataCollection.size();i++)
			System.out.println("\t>>>\t"+dataCollection.elementAt(i).datasetPath);
		System.out.println("------------------------------------------------------------");
		if(verbose){
			System.out.println("Phrase-level Acc Level2:");
			resultsPhraseLevel2.printPerformance(System.out);
			System.out.println("Token-level Acc Level2:");
			resultsTokenLevel2.printPerformance(System.out);
			System.out.println("Level2 BILOU Accuracy, letter-by-letter:");
			resultsByBILOU.printPerformance(System.out);
			System.out.println("Level2 BILOU PHRASE/BOUNDARY DETECTION Accuracy");
			resultsSegmentation.printPerformance(System.out);
			System.out.println("Phrase-level Acc Level1:");
			resultsPhraseLevel1.printPerformance(System.out);
			System.out.println("Token-level Acc Level1:");
			resultsTokenLevel1.printPerformance(System.out);
		}else{
			System.out.println("\t Level 1: "+resultsPhraseLevel1.getOverallStats()[2]);
			System.out.println("\t Level 2: "+resultsPhraseLevel2.getOverallStats()[2]);			
		}
		System.out.println("------------------------------------------------------------");
		System.out.println("************************************************************");
		System.out.println("------------------------------------------------------------");

		TestDiscrete[] res={resultsPhraseLevel1,resultsPhraseLevel2};
		return res;
	}  

	public static void reportPredictions(Data dataSet, 
							TestDiscrete resultsTokenLevel1,
							TestDiscrete resultsTokenLevel2,
							TestDiscrete resultsPhraseLevel1,
							TestDiscrete resultsPhraseLevel2,
							TestDiscrete resultsByBILOU,
							TestDiscrete resultsSegmentation){
		NELabel labeler = new NELabel();
		Vector<LinkedVector> data=new Vector<LinkedVector>();
		for(int i=0;i<dataSet.sentences.size();i++){
			LinkedVector sentence=new LinkedVector();
			for(int j=0;j<dataSet.sentences.elementAt(i).size();j++){
				NEWord originalW=(NEWord)dataSet.sentences.elementAt(i).get(j);
				NEWord w=new NEWord(new Word(originalW.form),null,null);
				w.neLabel=originalW.neLabel;
				if(w.neLabel.indexOf('-')>-1&&
						dataSet.labelsToIgnoreForEvaluation.containsKey(w.neLabel.substring(2)))
					w.neLabel="O";
				w.neTypeLevel1=originalW.neTypeLevel1;
				if(w.neLabel.indexOf('-')>-1&&
						dataSet.labelsToAnonymizeForEvaluation.containsKey(w.neLabel.substring(2))){
					w.neLabel=w.neLabel.substring(0,2)+"ENTITY";
					//System.out.println("replace!!!");
				}
				w.neTypeLevel1=originalW.neTypeLevel1;
				if(w.neTypeLevel1.indexOf('-')>-1&&
						dataSet.labelsToIgnoreForEvaluation.containsKey(w.neTypeLevel1.substring(2)))
					w.neTypeLevel1="O";
				if(w.neTypeLevel1.indexOf('-')>-1&&
						dataSet.labelsToAnonymizeForEvaluation.containsKey(w.neTypeLevel1.substring(2)))
					w.neTypeLevel1=w.neTypeLevel1.substring(0,2)+"ENTITY";
				w.neTypeLevel2=originalW.neTypeLevel2;
				if(w.neTypeLevel2.indexOf('-')>-1&&
						dataSet.labelsToIgnoreForEvaluation.containsKey(w.neTypeLevel2.substring(2)))
					w.neTypeLevel2="O";
				if(w.neTypeLevel2.indexOf('-')>-1&&
						dataSet.labelsToAnonymizeForEvaluation.containsKey(w.neTypeLevel2.substring(2)))
					w.neTypeLevel2=w.neTypeLevel2.substring(0,2)+"ENTITY";
				sentence.add(w);
			}
			data.addElement(sentence);
		}
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
				resultsSegmentation.reportPrediction(bracketTypePrediction, bracketTypeLabel);
			}		
	}
}

