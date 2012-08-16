package LbjTagger;

import java.util.HashMap;
import java.util.Vector;

import LBJ2.parse.LinkedVector;
import ParsingProcessingData.PlainTextReader;
import ParsingProcessingData.TaggedDataReader;

public class Data {
	public String nickname;//this will be used to save the model to know on what dataset we have tuned....
	public String pathToData;
	public String datasetPath;
	public Vector<LinkedVector> sentences=null;
	HashMap<String,Boolean> labelsToIgnoreForEvaluation=new HashMap<String, Boolean>();
	HashMap<String,Boolean> labelsToAnonymizeForEvaluation=new HashMap<String, Boolean>();
	
	public Data(String rawText){
		datasetPath="missing";
		nickname="missing";
		this.sentences=PlainTextReader.parseText(rawText);
	}
	public Data(Vector<LinkedVector> _sentences){
		datasetPath="missing";
		nickname="missing";
		this.sentences=_sentences;
	}
	
	public Data(String pathToData,String nickname,String dataFormat,Vector<String> labelsToIgnoreForEvaluation,Vector<String> labelsToAnonymizeForEvaluation) throws Exception{
		this.datasetPath=pathToData;
		this.nickname=nickname;
		this.pathToData=pathToData;
		sentences=TaggedDataReader.readData(pathToData, dataFormat);
		setLabelsToIgnore(labelsToIgnoreForEvaluation);
		setLabelsToAnonymize(labelsToAnonymizeForEvaluation);
	}

	public Data(String pathToData,String nickname,String dataFormat,String[] labelsToIgnoreForEvaluation,String[] labelsToAnonymizeForEvaluation) throws Exception{
		this.datasetPath=pathToData;
		this.nickname=nickname;
		sentences=TaggedDataReader.readData(pathToData, dataFormat);
		setLabelsToIgnore(labelsToIgnoreForEvaluation);
		setLabelsToAnonymize(labelsToAnonymizeForEvaluation);
	}

	public void setLabelsToIgnore(Vector<String> labelsToIgnoreForEvaluation){
		this.labelsToIgnoreForEvaluation=new HashMap<String, Boolean>();
		if(labelsToIgnoreForEvaluation!=null)
			for(int i=0;i<labelsToIgnoreForEvaluation.size();i++)
				this.labelsToIgnoreForEvaluation.put(labelsToIgnoreForEvaluation.elementAt(i), true);		
	}
	public void setLabelsToAnonymize(Vector<String> labelsToAnonymizeForEvaluation){
		this.labelsToAnonymizeForEvaluation=new HashMap<String, Boolean>();
		if(labelsToAnonymizeForEvaluation!=null)
			for(int i=0;i<labelsToAnonymizeForEvaluation.size();i++)
				this.labelsToAnonymizeForEvaluation.put(labelsToAnonymizeForEvaluation.elementAt(i), true);		
	}
	public void setLabelsToIgnore(String[] labelsToIgnoreForEvaluation){
		this.labelsToIgnoreForEvaluation=new HashMap<String, Boolean>();
		if(labelsToIgnoreForEvaluation!=null)
			for(int i=0;i<labelsToIgnoreForEvaluation.length;i++)
				this.labelsToIgnoreForEvaluation.put(labelsToIgnoreForEvaluation[i], true);		
	}
	public void setLabelsToAnonymize(String[] labelsToAnonymizeForEvaluation){
		this.labelsToAnonymizeForEvaluation=new HashMap<String, Boolean>();
		if(labelsToAnonymizeForEvaluation!=null)
			for(int i=0;i<labelsToAnonymizeForEvaluation.length;i++)
				this.labelsToAnonymizeForEvaluation.put(labelsToAnonymizeForEvaluation[i], true);		
	}
}
