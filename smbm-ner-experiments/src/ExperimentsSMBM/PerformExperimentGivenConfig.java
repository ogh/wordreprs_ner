package ExperimentsSMBM;

import java.util.Vector;

import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import LBJ2.classify.Classifier;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import LbjTagger.Data;
import LbjTagger.Parameters;
import LbjTagger.ParametersForLbjCode;

public class PerformExperimentGivenConfig {

    public static Vector<Data> readData(String path,String nickname) throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data(path, nickname,"-c",new String[]{},new String[]{});
		ExpressiveFeaturesAnnotator.annotate(data.sentences);
		res.addElement(data);
		return res;		
	}

	public static void main(String[] args) throws Exception{
		String configFile=args[0];
		Parameters.readConfigAndLoadExternalData(configFile);
		String testDataPath = Parameters.pathToTrainDevTest + "Test";
		String trainDataPath = Parameters.pathToTrainDevTest + "Train";
		String devDataPath = Parameters.pathToTrainDevTest + "Dev";
		
		Vector<Data> testData=readData(testDataPath,"testSplit");
        Vector<Data> devData=readData(devDataPath, "devSplit");
		Vector<Data> trainData=readData(trainDataPath,"trainSplit");
		//tuning on the in-domain data (CoNLL test)
		String originalModelPath=ParametersForLbjCode.pathToModelFile;
		String originalLogFile=ParametersForLbjCode.debuggingLogPath;
		
		ParametersForLbjCode.pathToModelFile=originalModelPath+".smbm-contra-experiments.";
		ParametersForLbjCode.debuggingLogPath=originalLogFile+".smbm-contra-experiments.";
		System.out.println("-------------------------------------------------------------");
		System.out.println("------Training with tuning on CoNLL Development dataset       ------");
		System.out.println("-------------------------------------------------------------");
		LbjTagger.LearningCurveMultiDataset.getLearningCurve(trainData,devData);
		System.out.println("-------------------------------------------------------------");
		System.out.println("------PERFORMANCE WHEN TUNING ON THE CONLL DEVELOPMENT SET    ------");
		System.out.println("-------------------------------------------------------------");
		NETaggerLevel1 taggerLevel1=new NETaggerLevel1();
		taggerLevel1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		NETaggerLevel2 taggerLevel2=new NETaggerLevel2();
		taggerLevel2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(testData,taggerLevel1,taggerLevel2,true);	
		LbjTagger.NETesterMultiDataset.printTestResultsByDataset(devData,taggerLevel1,taggerLevel2,true);
	}
}
