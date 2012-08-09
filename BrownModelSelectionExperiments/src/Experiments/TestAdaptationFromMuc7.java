package Experiments;

import java.util.Vector;


import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import InferenceMethods.Decoder;
import LBJ2.classify.Classifier;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import LbjTagger.Data;
import LbjTagger.NEWord;
import LbjTagger.Parameters;

public class TestAdaptationFromMuc7 {
	
	public static void main(String[] args) throws Exception{
		//this is with Brown clusters
		System.out.println("-----------------------------------------------------");
		System.out.println("With Brown clusters");
		System.out.println("-----------------------------------------------------");
		getPerformanceWithoutAdaptation("Config/allBrownFreq5pruneAfterInductionMuc7Adaptation.config","Config/Muc7allBrownFreq5pruneAfterInduction.config");
		getPerformanceWithAdaptation("Config/allBrownFreq5pruneAfterInductionMuc7Adaptation.config","Config/Muc7allBrownFreq5pruneAfterInduction.config");		
		//this is without Brown clusters
		System.out.println("-----------------------------------------------------");
		System.out.println("Without Brown clusters");
		System.out.println("-----------------------------------------------------");
		getPerformanceWithoutAdaptation("Config/Muc7baselineAdaptation.config","Config/Muc7baseline.config");
		getPerformanceWithAdaptation("Config/Muc7baselineAdaptation.config","Config/Muc7baseline.config");		
	}
	
	public static Vector<Data> readCoNLLTestData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test", "conll03test","-c",new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;		
	}
	public static Vector<Data> readCoNLLDevData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev", "conll03dev","-c",new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;		
	}
	public static Vector<Data> readCoNLLTrainData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train","conll03train", "-c",new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;		
	}

	public static void annotateDataWithMuc7Tagger(Vector<Data> testData,Vector<Data> devData,String adaptationModelConfig) throws Exception{
		Parameters.readConfigAndLoadExternalData(adaptationModelConfig);
		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		Decoder.annotateDataBIO(testData.elementAt(0).sentences, taggerLevel1, taggerLevel2);
		MucTagsToFeatures(testData);
		Decoder.annotateDataBIO(devData.elementAt(0).sentences, taggerLevel1, taggerLevel2);
		MucTagsToFeatures(devData);
	}
	
	public static void MucTagsToFeatures(Vector<Data> data){
		for(int i=0;i<data.size();i++)
			for(int j=0;j<data.elementAt(i).sentences.size();j++)
				for(int k=0;k<data.elementAt(i).sentences.elementAt(j).size();k++){
					NEWord w=(NEWord)data.elementAt(i).sentences.elementAt(j).get(k);
					NEWord.DiscreteFeature f=new NEWord.DiscreteFeature();
					f.featureGroupName="Muc7Labels";
					f.featureValue=w.form+w.neTypeLevel2;
					f.useWithinTokenWindow=true;
					w.generatedDiscreteFeaturesConjunctive.addElement(f);
				}
	}
	
	public static void getPerformanceWithoutAdaptation(String currentConfig,String adaptationModelConfig) throws Exception{
		System.out.println("************************************************************");
		System.out.println("\t\t WITHOUT ADAPTATION");
		System.out.println("************************************************************");
		Parameters.readConfigAndLoadExternalData(currentConfig);
		Vector<Data> inDomainTest=readCoNLLTestData();
		Vector<Data> inDomainDev=readCoNLLDevData();
		Parameters.readConfigAndLoadExternalData(currentConfig);
		LbjTagger.LearningCurveMultiDataset.getLearningCurve(inDomainDev,inDomainTest);
		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainTest,taggerLevel1,taggerLevel2,true);			
	}
	
	public static void getPerformanceWithAdaptation(String currentConfig,String adaptationModelConfig) throws Exception{
		System.out.println("************************************************************");
		System.out.println("\t\t WITH ADAPTATION");
		System.out.println("************************************************************");
		Parameters.readConfigAndLoadExternalData(currentConfig);
		Vector<Data> inDomainTest=readCoNLLTestData();
		Vector<Data> inDomainDev=readCoNLLDevData();
		annotateDataWithMuc7Tagger(inDomainTest,inDomainDev,adaptationModelConfig);
		Parameters.readConfigAndLoadExternalData(currentConfig);
		LbjTagger.LearningCurveMultiDataset.getLearningCurve(inDomainDev,inDomainTest);
		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainTest,taggerLevel1,taggerLevel2,true);	
	}
}
