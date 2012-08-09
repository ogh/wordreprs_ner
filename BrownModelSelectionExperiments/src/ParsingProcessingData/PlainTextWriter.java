package ParsingProcessingData;

import java.util.Vector;

import IO.OutFile;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;

public class PlainTextWriter {
	public static void write(Vector<LinkedVector> data,String outFile){
		OutFile out=new OutFile(outFile);
		for(int i=0;i<data.size();i++){
			StringBuffer buf=new StringBuffer(2000);
			for(int j=0;j<data.elementAt(i).size();j++)
				buf.append(((NEWord)data.elementAt(i).get(j)).form+" ");
			out.println(buf.toString());
		}
		out.close();
	}
}
