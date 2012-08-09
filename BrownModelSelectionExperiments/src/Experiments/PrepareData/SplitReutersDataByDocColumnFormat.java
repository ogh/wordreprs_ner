package Experiments.PrepareData;

import java.util.Vector;

import IO.OutFile;
import LBJ2.nlp.*;

class SplitReutersDataByDocColumnFormat extends ColumnFormat {

	public static void main(String[] args){
		SplitReutersDataByDocColumnFormat splitTrain=new SplitReutersDataByDocColumnFormat("Data/GoldData/Reuters/OriginalFormat/BIO.train");
		splitTrain.splitData("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train");
		SplitReutersDataByDocColumnFormat splitDev=new SplitReutersDataByDocColumnFormat("Data/GoldData/Reuters/OriginalFormat/BIO.testa");
		splitDev.splitData("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev");
		SplitReutersDataByDocColumnFormat splitTest=new SplitReutersDataByDocColumnFormat("Data/GoldData/Reuters/OriginalFormat/BIO.testb");
		splitTest.splitData("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test");
		System.out.println("Done!!!");
	}
	
	String filename = null;

	public SplitReutersDataByDocColumnFormat(String file) {
		super(file);
		filename = file;
	}

	//fake method
	public Object next() {
		return null;
	}

	public void splitData(String outPutDir) {
		int docId = 0;
		String[] line = (String[]) super.next();
		while (true) {
			Vector<String> outDoc=new Vector<String>();
			while (line != null && (line.length<4||!line[4].equals("-X-")))
				line = (String[]) super.next();
			if (line == null)
				return;

			String lineOut = line[0];
			for (int i = 1; i < line.length; i++)
				lineOut += "\t" + line[i];
			outDoc.addElement(lineOut);
			line = (String[]) super.next();
			
			while (line != null && (line.length==0||!line[4].equals("-X-"))) {
				lineOut = "";
				if(line.length>0)
					lineOut=line[0];
				for (int i = 1; i < line.length; i++)
					lineOut += "\t" + line[i];
				outDoc.addElement(lineOut);
				line = (String[]) super.next();
			}
			if(outDoc.size()>0){
				String filename=String.valueOf(docId);
				while(filename.length()<4)
					filename="0"+filename;
				OutFile out = new OutFile(outPutDir + "/" + filename+".txt");
				for(int i=0;i<outDoc.size();i++)
					out.println(outDoc.elementAt(i));
				out.close();
			}
			docId++;

		}
	}
}
