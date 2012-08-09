package Experiments.NaaclExperiments;


import java.util.Vector;

import IO.OutFile;
import LBJ2.nlp.*;
import LBJ2.parse.LinkedVector;
import LBJ2.parse.Parser;
import LbjTagger.*;

public class ReadSingleDocInSingleFile extends ColumnFormat
{
  /**
    *This really assume the file contains a single document
    *
   **/
	
	String filename=null;
	Vector<String[]> lines=null;
	public ReadSingleDocInSingleFile(String infile) 
	{ 
		super(infile);
		filename=infile;
		lines=new Vector<String[]>();
		String[] line = (String[]) super.next();
		while(line!=null){
			lines.addElement(line);
			line = (String[]) super.next();
		}
	}
  
  public Object next()
  {
	  //empty mystery item
	  return null;
  }

  public static void printLine(String[] line,OutFile out){
	  if(line.length==0)
		  out.println("");
	  else{
			for  (int i=0;i<line.length-1;i++)
				out.print(line[i]+"\t");
			out.println(line[line.length-1]);
	  }
  }  
}

