package Experiments.PrepareData;

import java.util.Vector;

import IO.InFile;
import IO.OutFile;

public class PrepareMucFiles {
	public static void main(String[] args) throws Exception {
		processDataset("Data/OriginalCorpora/MUC7/MUC7.NE.dryrun.eng","Data/GoldData/MUC7/BracketsFormat/Dev/","Data/RawData/MUC7/Dev/");
		processDataset("Data/OriginalCorpora/MUC7/MUC7.NE.formalrun.eng","Data/GoldData/MUC7/BracketsFormat/Test/","Data/RawData/MUC7/Test/");
		processDataset("Data/OriginalCorpora/MUC7/training.ne.eng.keys.980205","Data/GoldData/MUC7/BracketsFormat/Train/","Data/RawData/MUC7/Train/");
	}
	
	public static void processDataset(String xmlFile,String brackersOutDir,String rawOutDir) throws Exception {
		InFile in =new InFile(xmlFile);
		Vector<String> sentences=readNextDoc(in);
		int count=0;
		while(sentences!=null){
			String filename=String.valueOf(count);
			while(filename.length()<4)
				filename="0"+filename;
			OutFile out1=new OutFile(brackersOutDir+"/"+filename+".txt");
			OutFile out2=new OutFile(rawOutDir+"/"+filename+".txt");
			for(int i=0;i<sentences.size();i++){
				out1.println(mucAnnotationToBrackets(sentences.elementAt(i)));
				out2.println(mucAnnotationToPlainText(sentences.elementAt(i)));
			}
			out1.close();
			out2.close();
			count++;
			sentences=readNextDoc(in);
		}
		in.close();
	}
	
	public static String mucAnnotationToBrackets(String text) throws Exception{
		String original=text;
		//finding and replacing all the OPEN-BRACKETS for  locations, org and people
		text=replaceOpenBracketer(text,"<ENAMEX TYPE=\"PERSON"," [PER ");
		text=replaceOpenBracketer(text,"<ENAMEX TYPE=\"ORGANIZATION"," [ORG ");
		text=replaceOpenBracketer(text,"<ENAMEX TYPE=\"LOCATION"," [LOC ");
		text=replaceOpenBracketer(text,"<TIMEX","  ");
		text=replaceOpenBracketer(text,"<NUMEX","  ");

		text=text.replace("</ENAMEX>", " ] ");
		text=text.replace("</TIMEX>", " ");
		text=text.replace("</NUMEX>", " ");
		
		if(text.indexOf("<")>-1||text.indexOf(">")>-1)
			throw new Exception("Some of the markup was not removed!!!\n\t Original Sentence: "+original+"\n\tBrackets sentnece:"+text);
		return text;
	}
	
	public static String mucAnnotationToPlainText(String text) throws Exception{
		String original=text;
		//finding and replacing all the OPEN-BRACKETS for  locations, org and people
		text=replaceOpenBracketer(text,"<ENAMEX TYPE=\"PERSON","  ");
		text=replaceOpenBracketer(text,"<ENAMEX TYPE=\"ORGANIZATION","  ");
		text=replaceOpenBracketer(text,"<ENAMEX TYPE=\"LOCATION","  ");
		text=replaceOpenBracketer(text,"<TIMEX","  ");
		text=replaceOpenBracketer(text,"<NUMEX","  ");

		text=text.replace("</ENAMEX>", "  ");
		text=text.replace("</TIMEX>", " ");
		text=text.replace("</NUMEX>", " ");
		
		if(text.indexOf("<")>-1||text.indexOf(">")>-1)
			throw new Exception("Some of the markup was not removed!!!\n\t Original Sentence: "+original+"\n\tRaw sentnece:"+text);
		return text;
	}
	
	private static Vector<String> readNextDoc(InFile in) throws Exception{
		System.out.println("Reading MUC7 document...");
		Vector<String> res=new Vector<String>();
		String line=in.readLine();
		while(line!=null&&!line.equals("<TEXT>"))
			line=in.readLine();
		if(line==null)
			return null;
		line=in.readLine();
		while(!line.equalsIgnoreCase("</TEXT>")){
			if(!line.equals("<p>"))
				throw new Exception("Start of sentence expected. Instead read: "+line);
			line=in.readLine();
			StringBuffer sentence=new StringBuffer(2000);
			while(!line.equals("<p>")&&!line.equals("</TEXT>"))
			{
				sentence.append(cleanXmlShit(line)+" ");
				line=in.readLine();
			}
			System.out.println("Sentence:\n\t"+sentence.toString());
			res.addElement(sentence.toString());
		}
		return res;
	}
	
	private static String replaceOpenBracketer(String text,String MUC_EXPRESSION, String bracketExpression){
		StringBuffer temp=new StringBuffer(text.length());
		int i=0;
		while(text.indexOf(MUC_EXPRESSION,i)>-1){
			int pos=text.indexOf(MUC_EXPRESSION,i);
			temp.append(text.substring(i,pos)+" "+bracketExpression+" ");
			i=pos+1;
			i=text.indexOf('>',i)+1;
		}
		if(i<text.length())
		temp.append(text.substring(i, text.length()));
		return temp.toString();		
	}
	
	private static String cleanXmlShit(String text){
		  text=text.replace("`","'");
		  text=text.replace("''","\"");
		  text=text.replace("``","\"");
		  text=text.replace("&quot;","\"");
		  text=text.replace("&QUOT;","\"");
		  text=text.replace("&amp;","&");
		  text=text.replace("&AMP;","&");
		  text=text.replace("&HT;"," ");
		  text=text.replace("&ht;"," ");
		  text=text.replace("&MD;","--");
		  text=text.replace("&md;","--");
		  text=text.replace("&UR;","  ");
		  text=text.replace("&ur;","  ");
		  text=text.replace("&QC;","  ");
		  text=text.replace("&qc;","  ");
		  text=text.replace("&LR;","  ");
		  text=text.replace("&lr;","  ");
		  text=text.replace("-LRB-","(");
		  text=text.replace("-RRB-",")");
		  text=text.replace("-LCB-","{");
		  text=text.replace("-RCB-","}");
		  return text;
	}
}
