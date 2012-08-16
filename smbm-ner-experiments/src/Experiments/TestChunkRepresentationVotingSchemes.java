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

public class TestChunkRepresentationVotingSchemes {
	
	public static void main(String[] args) throws Exception{
		getPerformanceBaseline();
	}
	
	
	public static Vector<Data> readOutOfDomainTestData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		//Data data=new Data("../Data/GoldData/Arts/godby.columns.gold","art", "-c",new String[]{},new String[]{});
		Data data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.training.sentences.columns.gold", "muc7train","-c",new String[]{"MISC"},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.dryrun.sentences.columns.gold","muc7dry", "-c",new String[]{"MISC"},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.formalrun.sentences.columns.gold","muc7formal", "-c",new String[]{"MISC"},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		data=new Data("../Data/GoldData/WebpagesColumns/","web", "-c",new String[]{},new String[]{"MISC","PER","LOC","ORG"});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;
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

	public static void annotateDataWithDifferentChunkRepresentations(Vector<Data> data,String[] extraConfigFiles,String[] chunkEncogingSchemeNames,String[] extraModelSuffixes) throws Exception{
		for(int i=0;i<extraConfigFiles.length;i++){
			Parameters.readConfigAndLoadExternalData(extraConfigFiles[i]);
			System.out.println("The model path: "+Parameters.pathToModelFile+extraModelSuffixes[i]+".level1");
			NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
			taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+extraModelSuffixes[i]+".level1");
			NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
			taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+extraModelSuffixes[i]+".level2");
			for(int j=0;j<data.size();j++){
				Decoder.annotateDataBIO(data.elementAt(j).sentences, taggerLevel1, taggerLevel2);
				ChunkTagsToFeatures(data.elementAt(j),chunkEncogingSchemeNames[i]);				
			}
		}
	}
	
	public static void ChunkTagsToFeatures(Data data,String chunkEncogingSchemeName){
		for(int j=0;j<data.sentences.size();j++)
			for(int k=0;k<data.sentences.elementAt(j).size();k++){
				NEWord w=(NEWord)data.sentences.elementAt(j).get(k);
				NEWord.DiscreteFeature f=new NEWord.DiscreteFeature();
				f.featureGroupName=chunkEncogingSchemeName;
				f.featureValue=w.neTypeLevel2;
				f.useWithinTokenWindow=true;
				w.generatedDiscreteFeaturesNonConjunctive.addElement(f);
			}
	}
		
	public static void getPerformanceBaseline() throws Exception{
		Parameters.readConfigAndLoadExternalData("Config/baselineWeighted.config");
		Vector<Data> train=readCoNLLTrainData();
		Vector<Data> test=readCoNLLTestData();
		Vector<Data> dev=readCoNLLDevData();
		//
		// During the training we annotate the data with models trained on subsampled train
		//
		String[] extraConfigFiles=new String[]{"Config/baselineSubsampled.config",
			"Config/baselineBIOSubsampled.config",
			"Config/baselineIOB1Subsampled.config",
			"Config/baselineIOE1Subsampled.config",
			"Config/baselineIOE2Subsampled.config"};
		String[] chunkEncogingSchemeNames=new String[]{"BILOU","BIO","IOB1","IOE1","IOE2"};
		String[] extraModelSuffixes=new String[]{"","","","",""};
		annotateDataWithDifferentChunkRepresentations(train, extraConfigFiles, chunkEncogingSchemeNames,extraModelSuffixes);
		annotateDataWithDifferentChunkRepresentations(test, extraConfigFiles, chunkEncogingSchemeNames,extraModelSuffixes);
		annotateDataWithDifferentChunkRepresentations(dev, extraConfigFiles, chunkEncogingSchemeNames,extraModelSuffixes);
		Parameters.readConfigAndLoadExternalData("Config/baselineWeighted.config");
		LbjTagger.LearningCurveMultiDataset.getLearningCurve(train,dev);

		/*
		 * During the testing we annotate the data with models trained on complete train
		 */
		Parameters.readConfigAndLoadExternalData("Config/baselineWeighted.config");
		train = readCoNLLTrainData();
		test = readCoNLLTestData();
		dev = readCoNLLDevData();
		Vector<Data> ood = readOutOfDomainTestData();

		extraConfigFiles = new String[]{"Config/baseline.config",
				"Config/baselineBIO.config",
				"Config/baselineIOB1.config",
				"Config/baselineIOE1.config",
				"Config/baselineIOE2.config"};
		chunkEncogingSchemeNames = new String[]{"BILOU","BIO","IOB1","IOE1","IOE2"};
		extraModelSuffixes = new String[]{".CoNLLDevTuning",".CoNLLDevTuning",".CoNLLDevTuning",".CoNLLDevTuning",".CoNLLDevTuning"};
		annotateDataWithDifferentChunkRepresentations(train, extraConfigFiles, chunkEncogingSchemeNames,extraModelSuffixes);
		annotateDataWithDifferentChunkRepresentations(test, extraConfigFiles, chunkEncogingSchemeNames,extraModelSuffixes);
		annotateDataWithDifferentChunkRepresentations(dev, extraConfigFiles, chunkEncogingSchemeNames,extraModelSuffixes);
		annotateDataWithDifferentChunkRepresentations(ood, extraConfigFiles, chunkEncogingSchemeNames,extraModelSuffixes);

		Parameters.readConfigAndLoadExternalData("Config/baselineWeighted.config");
		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		System.out.println("Performance on test:");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(test,taggerLevel1,taggerLevel2,true);	
		System.out.println("Performance on dev:");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(dev,taggerLevel1,taggerLevel2,true);
		System.out.println("Performance on OOD:");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(ood,taggerLevel1,taggerLevel2,true);
		

	}
}
