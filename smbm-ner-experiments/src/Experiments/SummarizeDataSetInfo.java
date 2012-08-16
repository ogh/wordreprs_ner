package Experiments;

import LbjTagger.Data;
import LbjTagger.NEWord;
import LbjTagger.Parameters;
import LbjTagger.ParametersForLbjCode;

public class SummarizeDataSetInfo {
	
	public static void summData(Data data){
		int tokens=0;
		int sentneces=0;
		int per=0;
		int org=0;
		int loc=0;
		int misc=0;
		for(int i=0;i<data.sentences.size();i++){
			sentneces++;
			for(int j=0;j<data.sentences.elementAt(i).size();j++){
				NEWord w=(NEWord)data.sentences.elementAt(i).get(j);
				tokens++;
				if(w.neLabel.startsWith("B-PER")||w.neLabel.startsWith("U-PER"))
					per++;
				if(w.neLabel.startsWith("B-ORG")||w.neLabel.startsWith("U-ORG"))
					org++;
				if(w.neLabel.startsWith("B-LOC")||w.neLabel.startsWith("U-LOC"))
					loc++;
				if(w.neLabel.startsWith("B-MISC")||w.neLabel.startsWith("U-MISC"))
					misc++;
			}
		}
		System.out.println("Result:"+data.datasetPath+"&"+sentneces+"&"+tokens+"&"+(per+org+loc+misc)+"&"+per+"&"+org+"&"+loc+"&"+misc);		
	}
	
	public static void main(String[] args) throws Exception{
		Parameters.readConfigAndLoadExternalData("Config/bullshit.config");
		ParametersForLbjCode.logging=false;
		ParametersForLbjCode.debuggingLogPath="DebugLog/tempBullshitLog.txt";
		Data data=new Data("Data/GoldData/Arts/godby.columns.gold","art" ,"-c",new String[]{},new String[]{});
		summData(data);
		data=new Data("Data/GoldData/MUC7Columns/MUC7.NE.dryrun.sentences.columns.gold", "muc7dry" ,"-c",new String[]{"MISC"},new String[]{});
		summData(data);
		data=new Data("Data/GoldData/MUC7Columns/MUC7.NE.formalrun.sentences.columns.gold","muc7formal" ,"-c",new String[]{"MISC"},new String[]{});
		summData(data);
		data=new Data("Data/GoldData/WebpagesColumns/","web", "-c",new String[]{},new String[]{"MISC","PER","LOC","ORG"});
		summData(data);
		data=new Data("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test","conll03test", "-c",new String[]{},new String[]{});
		summData(data);
		data=new Data("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev", "conll03dev","-c",new String[]{},new String[]{});
		summData(data);
		data=new Data("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train","conll03train", "-c",new String[]{},new String[]{});
		summData(data);
	}
}
