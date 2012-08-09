package Experiments.ExperimentsNoConvergence;

import java.util.Vector;

import LBJ2.classify.Classifier;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import LbjTagger.Data;
import LbjTagger.Parameters;
import LbjTagger.ParametersForLbjCode;

public class TrainWithoutConvergenceConfig {
	public static Vector<Data> readTestData() throws Exception{
		Vector<Data> res=new Vector<Data>();
		Data data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.dryrun.sentences.columns.gold","muc7dry", "-c",new String[]{"MISC"},new String[]{});
		res.addElement(data);
		data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.formalrun.sentences.columns.gold","muc7formal", "-c",new String[]{"MISC"},new String[]{});
		res.addElement(data);
		data=new Data("../Data/GoldData/MUC7Columns/MUC7.NE.training.sentences.columns.gold","muc7train", "-c",new String[]{"MISC"},new String[]{});
		res.addElement(data);
		data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test","conll03test", "-c",new String[]{},new String[]{});
		res.addElement(data);
		data=new Data("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev","conll03dev", "-c",new String[]{},new String[]{});
		res.addElement(data);
		data=new Data("../Data/GoldData/WebpagesColumns/", "web","-c",new String[]{},new String[]{"MISC","PER","LOC","ORG"});
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
		String configFile=args[0];
		Parameters.readConfigAndLoadExternalData(configFile);
		Vector<Data> test=readTestData();
		Vector<Data> train=readCoNLLTrainData();
		//tuning on the in-domain data (CoNLL test)
		String originalModelPath=ParametersForLbjCode.pathToModelFile;
		String originalLogFile=ParametersForLbjCode.debuggingLogPath;
		

		ParametersForLbjCode.pathToModelFile=originalModelPath;
		ParametersForLbjCode.debuggingLogPath=originalLogFile;
		System.out.println("-------------------------------------------------------------");
		System.out.println("------Training with tuning on out of domain dataset    ------");
		System.out.println("-------------------------------------------------------------");
		LbjTagger.LearningCurveMultiDatasetNoConvergence.getLearningCurve(train,test,200);
		System.out.println("-------------------------------------------------------------");
		System.out.println("----PERFORMANCE WHEN TUNING ON THE OUT OF DOMAIN DATA  ------");
		System.out.println("-------------------------------------------------------------");
	}
}
