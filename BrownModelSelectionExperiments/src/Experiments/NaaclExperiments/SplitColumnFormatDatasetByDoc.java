package Experiments.NaaclExperiments;


import java.util.Vector;

import IO.OutFile;
import LBJ2.nlp.*;
import LBJ2.parse.LinkedVector;
import LBJ2.parse.Parser;
import LbjTagger.*;

public class SplitColumnFormatDatasetByDoc extends ColumnFormat
{
  /**
    * Constructs this parser to parse the given file.
    *
    * @param file The name of the file to parse.
   **/
	
	String filename=null;
	String outPath=null;
	public SplitColumnFormatDatasetByDoc(String _infile,String _outPath) { super(_infile);filename=_infile;outPath=_outPath; }
  
  public Object next()
  {
	  //empty mystery item
	  return null;
  }

  private static void printLine(String[] line,OutFile out){
	  if(line.length==0)
		  out.println("");
	  else{
			for  (int i=0;i<line.length-1;i++)
				out.print(line[i]+"\t");
			out.println(line[line.length-1]);
	  }
  }
  
 public void split()
 {	 
	 int fileId=0;
	 OutFile out=null;
	 String[] line = (String[]) super.next();
	 while(true){
	   while (line != null && line.length>4 &&line[4].equals("-X-")){
		   if(out!=null){
			   out.close();
		   }
		   fileId++;
		   out=new OutFile(outPath+"/"+fileId+".txt");
		   printLine(line, out);
		   line = (String[]) super.next();
	   }
	   while (line != null && (!(line.length>4&&line[4].equals("-X-")))){
		   printLine(line, out);
		   line = (String[]) super.next();
	   }
	   if(line==null){
		   out.close();
		   return ;
	   }
	 }
 }
 
 	public static void main(String[] args){
 		SplitColumnFormatDatasetByDoc s1=new SplitColumnFormatDatasetByDoc("Data/GoldData/Reuters/BIO.testa","Data/NaaclData/documentsTesta");
 		s1.split();
 		SplitColumnFormatDatasetByDoc s2=new SplitColumnFormatDatasetByDoc("Data/GoldData/Reuters/BIO.testb","Data/NaaclData/documentsTestb");
 		s2.split();
 	}
}

