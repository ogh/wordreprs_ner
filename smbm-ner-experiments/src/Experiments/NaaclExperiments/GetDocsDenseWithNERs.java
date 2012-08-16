package Experiments.NaaclExperiments;

import java.io.File;
import java.util.Vector;

import IO.OutFile;
import StringStatisticsUtils.CharacteristicObjects;

public class GetDocsDenseWithNERs{

	public static void selectDensest(String inDir,String outDir,int numOfDocsToKeep){
		Vector<ReadSingleDocInSingleFile> v=new Vector<ReadSingleDocInSingleFile>();
		
		String[] files=(new File(inDir)).list();
		for(int i=0;i<files.length;i++)
			v.addElement(new ReadSingleDocInSingleFile(inDir+"/"+files[i]));
	
		CharacteristicObjects res=new CharacteristicObjects(numOfDocsToKeep);
		for(int i=0;i<v.size();i++){
			ReadSingleDocInSingleFile doc=v.elementAt(i);
			if(doc.lines.size()>=200)
				res.addElement(doc, countEntities(doc)/doc.lines.size());
		}
		
		for(int i=0;i<res.topObjects.size();i++){
			ReadSingleDocInSingleFile doc=(ReadSingleDocInSingleFile)res.topObjects.elementAt(i);
			OutFile out=new OutFile(outDir+"/"+doc.filename.substring(doc.filename.lastIndexOf("/"),doc.filename.length()));
			for(int j=0;j<doc.lines.size();j++)
				ReadSingleDocInSingleFile.printLine(doc.lines.elementAt(j), out);
			out.close();
		}
		
	}
	
	private static double countEntities(ReadSingleDocInSingleFile doc) {
		double res=0;
		for(int i=0;i<doc.lines.size();i++){
			String[] line=doc.lines.elementAt(i);
			if(line.length>0&&line[0].startsWith("B-"))
				res++;
		}
		return res;
	}
	
	public static void main(String[] args){
		selectDensest("Data/NaaclData/documentsTesta", "Data/NaaclData/DenseDocumentsTesta", 20);
		selectDensest("Data/NaaclData/documentsTestb", "Data/NaaclData/DenseDocumentsTestb", 20);
	}
	
}
