package Experiments;

import java.util.Vector;

import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import LBJ2.classify.Classifier;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import LbjTagger.Data;
import LbjTagger.Parameters;
import LbjTagger.ParametersForLbjCode;

public class TestExperimentsCoNLLDevTuningGivenConfig {
	public static Vector<Data> readOutOfDomainTestData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		//Data data=new Data("../Data/GoldData/Arts/godby.columns.gold","art", "-c",new String[]{},new String[]{});
		Data data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.training.sentences.columns.gold", "muc7train","-c",new String[]{"MISC"},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.dryrun.sentences.columns.gold", "muc7dry","-c",new String[]{"MISC"},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.formalrun.sentences.columns.gold","muc7formal", "-c",new String[]{"MISC"},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		data=new Data("../Data/GoldData/WebpagesColumns/","web", "-c",new String[]{},new String[]{"MISC","PER","LOC","ORG"});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
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
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev","conll03dev", "-c",new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;		
	}

	public static void main(String[] args) throws Exception{
		String configFile=args[0];
		Parameters.readConfigAndLoadExternalData(configFile);
		Vector<Data> inDomainTest=readCoNLLTestData();
		Vector<Data> inDomainDev=readCoNLLDevData();
		Vector<Data> outOfDomainData=readOutOfDomainTestData();
		//tuning on the in-domain data (CoNLL test)
		String originalModelPath=ParametersForLbjCode.pathToModelFile;
		String originalLogFile=ParametersForLbjCode.debuggingLogPath;
		
		ParametersForLbjCode.pathToModelFile=originalModelPath+".CoNLLDevTuning";
		ParametersForLbjCode.debuggingLogPath=originalLogFile+".CoNLLDevTuning";
		System.out.println("-------------------------------------------------------------");
		System.out.println("---- PERFORMANCE WHEN TUNING ON THE CoNLL Dev DATA  ------");
		System.out.println("-------------------------------------------------------------");
		NETaggerLevel1 taggerLevel1 = (NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2 = (NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainTest,taggerLevel1,taggerLevel2,true);	
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainDev,taggerLevel1,taggerLevel2,true);
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(outOfDomainData,taggerLevel1,taggerLevel2,true);
		LbjTagger.NETesterMultiDataset.printAllTestResultsAsOneDataset(outOfDomainData,taggerLevel1,taggerLevel2,true); 
	}
}
