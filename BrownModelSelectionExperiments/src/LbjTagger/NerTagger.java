package LbjTagger;

import java.util.StringTokenizer;
import java.util.Vector;

import LbjFeatures.*;

import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import IO.Keyboard;
import IO.OutFile;
import InferenceMethods.Decoder;
import LBJ2.classify.Classifier;
import LBJ2.classify.FeatureVector;
import LBJ2.parse.LinkedVector;
import ParsingProcessingData.PlainTextReader;


public class NerTagger {
	public static void main(String[] args){
		try{
			Parameters.readConfigAndLoadExternalData(args[args.length-1]);
			
			ParametersForLbjCode.loggingFile=new OutFile(ParametersForLbjCode.debuggingLogPath);
			
			if(args[0].equalsIgnoreCase("-annotate"))
				NETagPlain.tagData(args[1], args[2],false);
			if(args[0].equalsIgnoreCase("-annotateWithConf"))
				NETagPlain.tagData(args[1], args[2],true);
			if(args[0].equalsIgnoreCase("-demo")){
		        NETaggerLevel1 tagger1 = new NETaggerLevel1();
		        tagger1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		        NETaggerLevel2 tagger2 = new NETaggerLevel2();
		        tagger2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
				String input="";
				while(!input.equalsIgnoreCase("quit")){
					input=Keyboard.readLine();
					if(input.equalsIgnoreCase("quit"))
						System.exit(0);
					String res=NETagPlain.tagLine(input,tagger1,tagger2);
					res=NETagPlain.insertHtmlColors(res);
					int len=0;
					StringTokenizer st=new StringTokenizer(res);
					StringBuffer output=new StringBuffer();
					while(st.hasMoreTokens()){
						String s=st.nextToken();
						output.append(" "+s);
						len+=s.length();
					}					
					System.out.println(output.toString());
				}
			}
			if(args[0].equalsIgnoreCase("-test"))
				NETesterMultiDataset.test(args[1], args[2],true);
			if(args[0].equalsIgnoreCase("-dumpFeatures"))
				NETesterMultiDataset.dumpFeaturesLabeledData(args[1], args[2], args[3]);
			if(args[0].equalsIgnoreCase("-train"))
				LearningCurveMultiDataset.getLearningCurve(args[1],args[3],args[4]);
		}catch(Exception e){
			System.out.println("Exception caught: ");
			e.printStackTrace();
			System.out.println("The problem might be the usage: use one of the below:");
			System.out.println("*)java -classpath $LBJ2.jar:LBJ2Library.jar:bin -Xmx1000m -train <traingFile> -test <testFile> <-b/-r> <pathToConfigFile>");
			System.out.println("\tThis command will learn the classifier and print the training curve, the last parameter specifies the file " +
					"format; use -b for brackets and -r for raw (plain) text; ");
			System.out.println("*)java -classpath $LBJ2.jar:LBJ2Library.jar:bin -Xmx1000m -annotate <rawInputFile> <outFile>  <pathToConfigFile>");
			System.out.println("\tThis one takes a plain text, tags it, and outputs the the specified file in brackets format");
			System.out.println("*)java -classpath $LBJ2.jar:LBJ2Library.jar:bin -Xmx1000m -test <goldFile> <format(-c/-r)>  <pathToConfigFile>");
			System.out.println("\tWill output phrase-level F1 score on the file (recall that I love other measures for comparing taggers, I want to use this primary as sanity check)");
		}
		ParametersForLbjCode.loggingFile.close();
	}
}
