package DebuggingAndTesting;

import java.util.Vector;

import LbjFeatures.*;
import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import InferenceMethods.Decoder;
import LBJ2.classify.Classifier;
import LBJ2.parse.LinkedVector;
import LbjTagger.*;
import ParsingProcessingData.TaggedDataReader;
import ParsingProcessingData.TaggedDataWriter;

/*
 * We broke the original Reuters data to documents.
 * We wanna see that
 * 1) The performance stays the same if I read all the documents one by one 
 * into the same data structure I had for the original non-split vector of sentences
 * 2) If I disconnect the sentence boundaries between the documents (that is, before
 * I treated the whole corpus as one giant document, now I wanna break it to real
 * documents), the performance is still good. 
 */
public class TestFileFormattingAndSplittingIssues {
	
	public static void testColumnFormat() throws Exception{
		Parameters.forceNewSentenceOnLineBreaks=false;
		ParametersForLbjCode.sortLexicallyFilesInFolders=true;
		ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=true;
		Vector<LinkedVector> data1 = TaggedDataReader.readFolder("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test","-c");
		ExpressiveFeaturesAnnotator.annotate(data1);
		ParametersForLbjCode.sortLexicallyFilesInFolders=true;
		ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=false;
		Vector<LinkedVector> data2 = TaggedDataReader.readFolder("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test","-c");
		ExpressiveFeaturesAnnotator.annotate(data2);
		TaggedDataWriter.writeToFile("Data/Temp/ReutersTestData", data1, "-c", NEWord.LabelToLookAt.GoldLabel);
		Vector<LinkedVector> data3=TaggedDataReader.readFile("Data/Temp/ReutersTestData", "-c");
		ExpressiveFeaturesAnnotator.annotate(data3);
		
		NETaggerLevel1 tagger1 = new NETaggerLevel1();
		System.out.println("Reading model file : " + ParametersForLbjCode.pathToModelFile+".level1");
		tagger1=(NETaggerLevel1)Classifier.binaryRead(ParametersForLbjCode.pathToModelFile+".level1");
		NETaggerLevel2 tagger2 = new NETaggerLevel2();
		System.out.println("Reading model file : " + ParametersForLbjCode.pathToModelFile+".level2");
		tagger2=(NETaggerLevel2)Classifier.binaryRead(ParametersForLbjCode.pathToModelFile+".level2");

		System.out.println("**********************************************************");
		System.out.println("Performance measured with the old data parser, except data is saved after reading with the new parser..."); 
		System.out.println("**********************************************************");
		//NETester.annotateBothLevels(data3, tagger1, tagger2);
		Decoder.annotateDataBIO(data3, tagger1, tagger2);
		Vector<Data> v=new Vector<Data>();
		v.addElement(new Data(data3));
		NETesterMultiDataset.printTestResultsByDataset(v,true);
		System.out.println("**********************************************************");
		System.out.println("Performance when treating all the documents as one corpus");
		System.out.println("**********************************************************");
		//NETester.annotateBothLevels(data1, tagger1, tagger2);
		Decoder.annotateDataBIO(data1, tagger1, tagger2);
		v=new Vector<Data>();
		v.addElement(new Data(data1));
		NETesterMultiDataset.printTestResultsByDataset(v,true);
		System.out.println("**********************************************************");
		System.out.println("Performance when treating each document individually");
		System.out.println("**********************************************************");
		//NETester.annotateBothLevels(data2, tagger1, tagger2);
		Decoder.annotateDataBIO(data2, tagger1, tagger2);
		v=new Vector<Data>();
		v.addElement(new Data(data2));
		NETesterMultiDataset.printTestResultsByDataset(v,true);
	}
	
	public static void checkSentenceBoundaryMatch(Vector<LinkedVector> data1,Vector<LinkedVector> data2) throws Exception{
		if(data1.size()!=data2.size()){
			System.out.println("Data size mismatch 1 !!!");
			boolean mismatchFound=false;
			for(int i=0;i<Math.min(data1.size(), data2.size())&&!mismatchFound;i++){
				if(data1.elementAt(i).size()!=data2.elementAt(i).size()){
					System.out.println("*******sentence length mismatch******");
					mismatchFound=true;
					String s1="";
					for(int j=0;j<data1.elementAt(i).size();j++)
						s1+=" "+((NEWord)data1.elementAt(i).get(j)).form;
					String s2="";
					for(int j=0;j<data2.elementAt(i).size();j++)
						s2+=" "+((NEWord)data2.elementAt(i).get(j)).form;
					System.out.println("Data 1 sentence:\n\t"+s1);
					System.out.println("Data 2 sentence:\n\t"+s2);
				}
			}
			throw new Exception("Data size mismatch 1 !!!");
		}
		for(int i=0;i<data1.size();i++){
			LinkedVector v1=data1.elementAt(i);
			LinkedVector v3=data2.elementAt(i);
			if(v1.size()!=v3.size())
				throw new Exception("Data size mismatch 2 !!!");
			for(int j=0;j<v1.size();j++){
				NEWord w1=(NEWord)v1.get(j);
				NEWord w3=(NEWord)v3.get(j);
				boolean isOk=false;
				if(w1.next==null&&w3.next==null)
					isOk=true;
				else
					if(((NEWord)w1.next).form.equals(((NEWord)w3.next).form))
						isOk=true;
				if(!isOk)
					throw new Exception("next doesnt match");
				isOk=false;
				if(w1.nextIgnoreSentenceBoundary==null&&w3.nextIgnoreSentenceBoundary==null)
					isOk=true;
				else
					if(((NEWord)w1.nextIgnoreSentenceBoundary).form.equals(((NEWord)w3.nextIgnoreSentenceBoundary).form))
						isOk=true;
				if(!isOk)
					throw new Exception("next IgnoreSentenceBoundary doesnt match");
				isOk=false;
				if(w1.previous==null&&w3.previous==null)
					isOk=true;
				else
					if(((NEWord)w1.previous).form.equals(((NEWord)w3.previous).form))
						isOk=true;
				if(!isOk)
					throw new Exception("previous doesnt match");
				isOk=false;
				if(w1.previousIgnoreSentenceBoundary==null&&w3.previousIgnoreSentenceBoundary==null)
					isOk=true;
				else
					if(((NEWord)w1.previousIgnoreSentenceBoundary).form.equals(((NEWord)w3.previousIgnoreSentenceBoundary).form))
						isOk=true;
				if(!isOk)
					throw new Exception("previousIgnoreSentenceBoundary doesnt match");				
			}
		}
		System.out.println("**********************************************************");
		System.out.println("Passed all next/prev tests....");
		System.out.println("**********************************************************");
	}
	
	public static void testBracketsFromatOriginalTokenization() throws Exception{
		ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=true;
		ParametersForLbjCode.sortLexicallyFilesInFolders=true;
		ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=true;
		Vector<LinkedVector> data0 = TaggedDataReader.readFolder("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test","-c" );
		ParametersForLbjCode.sortLexicallyFilesInFolders=true;
		ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=true;
		Vector<LinkedVector> data = TaggedDataReader.readFolder("Data/GoldData/Reuters/BracketsFormatDocumentsSplitOriginalTokenization/Test","-r" );
		ExpressiveFeaturesAnnotator.annotate(data0);
		ExpressiveFeaturesAnnotator.annotate(data);
		checkSentenceBoundaryMatch(data0, data);
		NETaggerLevel1 tagger1 = new NETaggerLevel1();
		System.out.println("Reading model file : " + ParametersForLbjCode.pathToModelFile+".level1");
		tagger1=(NETaggerLevel1)Classifier.binaryRead(ParametersForLbjCode.pathToModelFile+".level1");
		NETaggerLevel2 tagger2 = new NETaggerLevel2();
		System.out.println("Reading model file : " + ParametersForLbjCode.pathToModelFile+".level2");
		tagger2=(NETaggerLevel2)Classifier.binaryRead(ParametersForLbjCode.pathToModelFile+".level2");

		System.out.println("**********************************************************");
		System.out.println("Performance measured on brackets files, original tokenization, all documents merged. Should be the same as column format (90.641?)"); 
		System.out.println("**********************************************************");
		//NETester.annotateBothLevels(data, tagger1, tagger2);
		Decoder.annotateDataBIO(data, tagger1, tagger2);
		Vector<Data> v=new Vector<Data>();
		v.addElement(new Data(data));
		NETesterMultiDataset.printTestResultsByDataset(v,true);
	}
		
	public static void testBracketsFromatMyTokenization() throws Exception{
		ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=false;
		ParametersForLbjCode.sortLexicallyFilesInFolders=true;
		ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=true;
		Vector<LinkedVector> data = TaggedDataReader.readFolder("Data/GoldData/Reuters/BracketsFormatDocumentsSplitMyTokenization/Test","-r");
		ExpressiveFeaturesAnnotator.annotate(data);
		NETaggerLevel1 tagger1 = new NETaggerLevel1();
		System.out.println("Reading model file : " + ParametersForLbjCode.pathToModelFile+".level1");
		tagger1=(NETaggerLevel1)Classifier.binaryRead(ParametersForLbjCode.pathToModelFile+".level1");
		NETaggerLevel2 tagger2 = new NETaggerLevel2();
		System.out.println("Reading model file : " + ParametersForLbjCode.pathToModelFile+".level2");
		tagger2=(NETaggerLevel2)Classifier.binaryRead(ParametersForLbjCode.pathToModelFile+".level2");

		System.out.println("**********************************************************");
		System.out.println("Performance measured on brackets files, my tokenization, all documents merged. Should be a bit lower than the column format"); 
		System.out.println("**********************************************************");
		//NETester.annotateBothLevels(data, tagger1, tagger2);
		Decoder.annotateDataBIO(data, tagger1, tagger2);
		Vector<Data> v=new Vector<Data>();
		v.addElement(new Data(data));
		NETesterMultiDataset.printTestResultsByDataset(v,true);
	}

	
	public static void main(String[] args) throws Exception{
		Parameters.readConfigAndLoadExternalData("ConfigForLbjRelease1.11/allFeaturesBigTrainingSet.config");
		testColumnFormat();
		testBracketsFromatOriginalTokenization();
		testBracketsFromatMyTokenization();
	}
}
