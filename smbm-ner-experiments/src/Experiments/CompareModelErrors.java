package Experiments;

import java.util.Vector;

import IO.OutFile;
import InferenceMethods.Decoder;
import LBJ2.classify.Classifier;
import LBJ2.parse.LinkedVector;
import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;
import LbjTagger.NEWord;
import LbjTagger.Parameters;
import ParsingProcessingData.TaggedDataReader;

/*
 * This class gets two config files and a gold-annotated file. It'll analyze the errors by both models-
 * where one of the models makes a mistake, which model, and when both of them make a mistake
 */
public class CompareModelErrors {
	public static void comareModels(String configFile1,String configFile2,String pathToGoldDataFile,String goldDataFormat,String pathToOutputFile) throws Exception{
		Parameters.readConfigAndLoadExternalData(configFile1);
		System.out.println("loading the tagger for config 1");
        NETaggerLevel1 tagger1 = new NETaggerLevel1();
        tagger1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
        NETaggerLevel2 tagger2 = new NETaggerLevel2();
        tagger2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
    	System.out.println("Done- loading the tagger");
    	Vector<LinkedVector> data1=TaggedDataReader.readData(pathToGoldDataFile, goldDataFormat);
    	Decoder.annotateDataBIO(data1,tagger1,tagger2);
		Parameters.readConfigAndLoadExternalData(configFile2);
		System.out.println("loading the tagger for config 2");
        tagger1 = new NETaggerLevel1();
        tagger1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
        tagger2 = new NETaggerLevel2();
        tagger2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
    	System.out.println("Done- loading the tagger");
    	Vector<LinkedVector> data2=TaggedDataReader.readData(pathToGoldDataFile, goldDataFormat);
    	Decoder.annotateDataBIO(data2,tagger1,tagger2);
    	OutFile out=new OutFile(pathToOutputFile);
    	for(int i=0;i<data1.size();i++){
    		for(int j=0;j<data1.elementAt(i).size();j++){
    			NEWord w1=(NEWord)data1.elementAt(i).get(j);
    			NEWord w2=(NEWord)data2.elementAt(i).get(j);
    			if(w1.neTypeLevel2.equals(w1.neLabel)&&w2.neTypeLevel2.equals(w2.neLabel))
    				out.println("-VV-\t"+w1.form+"\t"+w1.neTypeLevel2+"\t"+w2.neTypeLevel2+"\t"+w1.neLabel);
    			if((!w1.neTypeLevel2.equals(w1.neLabel))&&w2.neTypeLevel2.equals(w2.neLabel))
    				out.println("-XV-\t"+w1.form+"\t"+w1.neTypeLevel2+"\t"+w2.neTypeLevel2+"\t"+w1.neLabel);
    			if(w1.neTypeLevel2.equals(w1.neLabel)&&(!w2.neTypeLevel2.equals(w2.neLabel)))
    				out.println("-VX-\t"+w1.form+"\t"+w1.neTypeLevel2+"\t"+w2.neTypeLevel2+"\t"+w1.neLabel);
    			if((!w1.neTypeLevel2.equals(w1.neLabel))&&(!w2.neTypeLevel2.equals(w2.neLabel)))
    				out.println("-XX-\t"+w1.form+"\t"+w1.neTypeLevel2+"\t"+w2.neTypeLevel2+"\t"+w1.neLabel);
    		}
    		out.println("");
    	}
    	out.close();
	}
	public static void main(String[] args) throws Exception{
		comareModels(args[0],args[1],args[2],args[3],args[4]);
	}

}
