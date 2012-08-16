package DebuggingAndTesting;

import java.util.Vector;

import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import IO.OutFile;
import LBJ2.parse.LinkedVector;
import LbjTagger.Data;
import LbjTagger.LearningCurve;
import LbjTagger.LearningCurveMultiDataset;
import LbjTagger.NEWord;
import LbjTagger.Parameters;
import LbjTagger.ParametersForLbjCode;
import ParsingProcessingData.TaggedDataReader;
import StringStatisticsUtils.MyString;

public class TestAdditionalFeatures {
	public static void initAdditionalFeaturesForms(Vector<LinkedVector> data){
		for(int i=0;i<data.size();i++)
			for(int j=0;j<data.elementAt(i).size();j++){
				NEWord w=(NEWord)data.elementAt(i).get(j);
				NEWord.DiscreteFeature f=new NEWord.DiscreteFeature();
				f.featureGroupName="forms";
				f.featureValue=w.form;
				f.useWithinTokenWindow=true;
				w.generatedDiscreteFeaturesConjunctive.addElement(f);
				f=new NEWord.DiscreteFeature();
				f.featureGroupName="forms";
				f.featureValue=MyString.normalizeDigitsForFeatureExtraction(w.form);
				f.useWithinTokenWindow=true;
				w.generatedDiscreteFeaturesConjunctive.addElement(f);
			}
	}
	
	public static void trainForms() throws Exception{
		Parameters.readConfigAndLoadExternalData("Config/baselineTestAdditionalFeatures.config");
		ParametersForLbjCode.loggingFile=new OutFile(ParametersForLbjCode.debuggingLogPath);
		Vector<LinkedVector> trainData=TaggedDataReader.readData("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train", "-c");
		ExpressiveFeaturesAnnotator.annotate(trainData);
		Vector<LinkedVector> testData=TaggedDataReader.readData("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev", "-c");
		ExpressiveFeaturesAnnotator.annotate(testData);
		initAdditionalFeaturesForms(trainData);
		initAdditionalFeaturesForms(testData);
		Vector<Data> train=new Vector<Data>();
		train.addElement(new Data(trainData));
		Vector<Data> test=new Vector<Data>();
		test.addElement(new Data(testData));
		LearningCurveMultiDataset.getLearningCurve(train, test);
	}
	public static void 	main(String[] args) throws Exception{
		trainForms();
	}
}
