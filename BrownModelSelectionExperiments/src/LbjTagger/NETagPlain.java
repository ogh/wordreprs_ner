package LbjTagger;

import java.io.File;
import java.util.Vector;

import LbjFeatures.*;
import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import IO.OutFile;
import InferenceMethods.Decoder;
import LBJ2.parse.*;
import LBJ2.classify.Classifier;
import ParsingProcessingData.PlainTextReader;

public class NETagPlain
{  
    public static void tagData(String inputPath,String outputPath,boolean displayConfidenceScores) throws Exception
    {
    	File f=new File(inputPath);
    	Vector<String> inFiles=new Vector<String>();
    	Vector<String> outFiles=new Vector<String>();
    	if(f.isDirectory()){
    		String[] files=f.list();
    		for(int i=0;i<files.length;i++)
    			if(!files[i].startsWith(".")){
    				inFiles.addElement(inputPath+"/"+files[i]);
    				outFiles.addElement(outputPath+"/"+files[i]);
    			}
    	}
    	else{
    		inFiles.addElement(inputPath);
    		outFiles.addElement(outputPath);
    	}
    	for(int fileId=0;fileId<inFiles.size();fileId++){
        	System.out.println("Tagging file: "+inFiles.elementAt(fileId));
        	Vector<LinkedVector> data=PlainTextReader.parsePlainTextFile(inFiles.elementAt(fileId));
        	ExpressiveFeaturesAnnotator.annotate(data);
            NETaggerLevel1 tagger1 = new NETaggerLevel1();
            tagger1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
            NETaggerLevel2 tagger2 = new NETaggerLevel2();
            tagger2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");
            String tagged=null;
            if(displayConfidenceScores)
            	tagged=tagDataWithPredictionsConf(data, tagger1, tagger2);
            else
            	tagged=tagData(data, tagger1, tagger2);
            OutFile out=new OutFile(outFiles.elementAt(fileId));
            out.println(tagged);
            out.close();    	    		
    	}
    } 
    
    public static String tagLine(String line,NETaggerLevel1 tagger1,NETaggerLevel2 tagger2) throws Exception{
    	return tagData(PlainTextReader.parseText(line),tagger1,tagger2);
    }
    
    public static String tagData(Vector<LinkedVector> data,NETaggerLevel1 tagger1,NETaggerLevel2 tagger2) throws Exception
    {
    	ExpressiveFeaturesAnnotator.annotate(data);
    	//NETester.annotateBothLevels(data,tagger1,tagger2);
    	Decoder.annotateDataBIO(data,tagger1,tagger2);
        StringBuffer res=new StringBuffer();
        for(int i=0;i<data.size();i++){
            LinkedVector vector = data.elementAt(i);
            boolean open=false;
            String[] predictions=new String[vector.size()];
            String[] words=new String[vector.size()];
            for(int j=0;j<vector.size();j++){
            	predictions[j] = ((NEWord)vector.get(j)).neTypeLevel2;
            	words[j]=((NEWord)vector.get(j)).form;
            }
            for(int j=0;j<vector.size();j++)
            { 
            	if (predictions[j].startsWith("B-")
            			|| 
            			(j>0&&predictions[j].startsWith("I-") && (!predictions[j-1].endsWith(predictions[j].substring(2))))){
            		res.append("[" + predictions[j].substring(2) + " ");
            		open=true;
            	}
            	res.append(words[j]+ " ");
            	if(open){
            		boolean close=false;
            		if(j==vector.size()-1){
            			close=true;
            		}
            		else
            		{
            			if(predictions[j+1].startsWith("B-"))
            				close=true;
            			if(predictions[j+1].equals("O"))
            				close=true;
            			if(predictions[j+1].indexOf('-')>-1&&(!predictions[j].endsWith(predictions[j+1].substring(2))))
            				close=true;
            		}
            		if(close){
            			res.append(" ] ");
            			open=false;
            		}
            	}
            }
        }
        return res.toString();
    }

    public static String tagLineWithPredictionsConf(String line,NETaggerLevel1 tagger1,NETaggerLevel2 tagger2) throws Exception
    {
    	return tagDataWithPredictionsConf(PlainTextReader.parseText(line),tagger1,tagger2);    	
    }
    public static String tagDataWithPredictionsConf(Vector<LinkedVector> data,NETaggerLevel1 tagger1,NETaggerLevel2 tagger2) throws Exception
    {
    	ExpressiveFeaturesAnnotator.annotate(data);
    	Decoder.annotateDataBIOWithConfidenceScores(data,tagger1,tagger2);
        StringBuffer res=new StringBuffer();
        for(int i=0;i<data.size();i++){
            LinkedVector sentence = data.elementAt(i);
            for(int j=0;j<sentence.size();j++)
            { 
            	NEWord w=(NEWord)sentence.get(j);
            	if(w.predictedEntity!=null&&w.predictedEntity.firstWord==w){
            		res.append(" [");
            		for(int k=0;k<NamedEntity.possibleLabels.length;k++)
            			res.append(NamedEntity.possibleLabels[k]+":"+w.predictedEntity.confidences[k]+"; ");
            	}
            	res.append(w.form+" ");
            	if(w.predictedEntity!=null&&w.predictedEntity.lastWord==w)
            		res.append(" ] ");
            }
        }
        return res.toString();
    }
    
    
    public static String insertHtmlColors(String annotatedText){
    	String res=annotatedText.replace("[PER", "<font style=\"color:red\">[PER");
    	res=res.replace("[LOC", "<font style=\"color:blue\">[LOC");
    	res=res.replace("[ORG", "<font style=\"color:green\">[ORG");
    	res=res.replace("[MISC", "<font style=\"color:brown\">[MISC");
    	
    	res=res.replace("]", "]</font>");
    	return res;
    }

}

