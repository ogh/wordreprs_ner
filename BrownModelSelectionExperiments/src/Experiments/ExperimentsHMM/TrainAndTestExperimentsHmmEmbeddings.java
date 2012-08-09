package Experiments.ExperimentsHMM;

import java.util.Vector;

import ExpressiveFeatures.HmmEmbeddings;
import LBJ2.classify.Classifier;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import LbjTagger.Data;
import LbjTagger.Parameters;
import LbjTagger.ParametersForLbjCode;

public class TrainAndTestExperimentsHmmEmbeddings {
	public static Vector<Data> readCoNLLTestData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test","conll03test", "-c",new String[]{},new String[]{});
		res.addElement(data);
		return res;		
	}
	public static Vector<Data> readCoNLLDevData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev","conll03dev","-c",new String[]{},new String[]{});
		res.addElement(data);
		return res;		
	}
	public static Vector<Data> readCoNLLTrainData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train","conll03train", "-c",new String[]{},new String[]{});
		res.addElement(data);
		return res;		
	}

	public static void main(String[] args) throws Exception{
		Parameters.readConfigAndLoadExternalData(args[0]);
		Vector<Data> inDomainTest=readCoNLLTestData();
		Vector<Data> inDomainDev=readCoNLLDevData();
		Vector<Data> inDomainTrain=readCoNLLTrainData();
		HmmEmbeddings.addDataset("../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/TrainIndexed/", inDomainTrain.elementAt(0).sentences, "CoNllTrain");
		HmmEmbeddings.addDataset("../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/TestIndexed/", inDomainTest.elementAt(0).sentences, "CoNllTest");
		HmmEmbeddings.addDataset("../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/DevIndexed/", inDomainDev.elementAt(0).sentences, "CoNllDev");
		
		//tuning on the in-domain data (CoNLL test)
		String originalModelPath=ParametersForLbjCode.pathToModelFile;
		String originalLogFile=ParametersForLbjCode.debuggingLogPath;
		
		ParametersForLbjCode.pathToModelFile=originalModelPath+".HmmEmbeddingsCoNLLTestTuning";
		ParametersForLbjCode.debuggingLogPath=originalLogFile+".HmmEmbeddingsCoNLLTestTuning";
		System.out.println("-------------------------------------------------------------");
		System.out.println("------Training with tuning on CoNLL test dataset       ------");
		System.out.println("-------------------------------------------------------------");
		LbjTagger.LearningCurveMultiDataset.getLearningCurve(inDomainTrain,inDomainTest);
		System.out.println("-------------------------------------------------------------");
		System.out.println("------PERFORMANCE WHEN TUNING ON THE CONLL TEST SET    ------");
		System.out.println("-------------------------------------------------------------");
		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainTest,taggerLevel1,taggerLevel2,true);	
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(inDomainDev,taggerLevel1,taggerLevel2,true);


	}
}
