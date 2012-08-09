package Experiments.ExperimentsHMM;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


public class BuildRvcRepresentationForEM {
	public static final int appearanceThreshold=10;
	
	public static void main(String[] args){
		String infile="/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/UnlabeledData/rcv1.clean.tokenized-CoNLL03.case-intact.txt";
		String outFileVocabulary="/shared/grandma/ratinov2/TurianExperimentsNaacl/HMM_EM_Turian/umdhmm-v1.02/RCV.vocab";
		String outFileData="/shared/grandma/ratinov2/TurianExperimentsNaacl/HMM_EM_Turian/umdhmm-v1.02/RCV.seq";
		String outFileDataTokens="/shared/grandma/ratinov2/TurianExperimentsNaacl/HMM_EM_Turian/umdhmm-v1.02/RCV.unknownWords.txt";
		HashMap<String, Integer> appearanceCounter=new HashMap<String, Integer>();
		System.out.println("\nReading the word appearance counts.\n Lines Read: ");
		InFile in =new InFile(infile);
		String line=in.readLine();
		long tokenCount=0;
		int lines=0;
		while(line!=null){
			if(lines++%1000==0)
				System.out.print(lines+" ");
			StringTokenizer st=new StringTokenizer(line);
			while(st.hasMoreTokens()){
				String t=st.nextToken();
				tokenCount++;
				if(appearanceCounter.containsKey(t))
					appearanceCounter.put(t, appearanceCounter.get(t)+1);
				else
					appearanceCounter.put(t, 1);
			}
			line=in.readLine();
		}
		in.close();
		System.out.println("\n\nDone reading the word appearance counts, "+tokenCount+" tokens read");
		System.out.println("\nConstructing the feature map... ");
		OutFile out=new OutFile(outFileVocabulary);
		HashMap<String, Integer> map=new HashMap<String, Integer>();
		map.put("*UNKNOWN*", 1);
		out.println("*UNKNOWN*\t1");
		for(Iterator<String> i=appearanceCounter.keySet().iterator();i.hasNext();){
			String t=i.next();
			int count=appearanceCounter.get(t);
			if(count<appearanceThreshold){
				map.put(t, 1);
				out.println(t+"\t"+map.get(t));				
			}
			else{
				map.put(t, map.size()+1);
				out.println(t+"\t"+map.get(t));
			}				
		}
		out.close();
		System.out.println("Vocabulary size="+map.size());
		System.out.println("\nThrowing out the token sequence.\n Lines Read: ");
		out=new OutFile(outFileData);
		OutFile out2 = new OutFile(outFileDataTokens);
		out.println("T= "+tokenCount);
		in =new InFile(infile);
		line=in.readLine();
		lines=0;
		while(line!=null){
			if(lines++%1000==0)
				System.out.print(lines+" ");
			StringTokenizer st=new StringTokenizer(line);
			while(st.hasMoreTokens()){
				String t=st.nextToken();
				if(appearanceCounter.get(t)<appearanceThreshold)
					out2.print("*UNKNOWN* ");
				else
					out2.print(t+" ");
				out.print(map.get(t)+" ");
			}
			out.println("");
			out2.println("");
			line=in.readLine();
		}
		in.close();
		out.close();
		out2.close();
		System.out.println("\n\nDone!!!");
	}
	public static  class InFile {
		public static boolean convertToLowerCaseByDefault=false;
		public static boolean normalize=false;
		public static boolean pruneStopSymbols=false;
		public BufferedReader  in = null;
		public static String stopSymbols="@";
		
		public InFile(String filename){
			try{
				in= new BufferedReader(new FileReader(filename));
			}catch(Exception e){
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		public String readLine(){
			try{
				String s=in.readLine();
				if(s==null)
					return null;
				if(convertToLowerCaseByDefault)
					return s.toLowerCase().trim();
				return s;
			}catch(Exception e)
			{
				e.printStackTrace();
				System.exit(0);
			}
			return null;
		}
		
		public Vector<String> readLineTokens(){
			return tokenize(readLine());
		}	
		
		public static Vector<String> tokenize(String s){
			if(s==null)
				return null;
			Vector<String> res=new Vector<String>();
			StringTokenizer st=new StringTokenizer(s," ");
			while(st.hasMoreTokens())
				res.addElement(st.nextToken());
			return res;
		}	
		public void close(){
			try{
				this.in.close();
			}catch(Exception E){}
		}
		

		public static String readFileText(String file) {
			StringBuffer text=new StringBuffer();
			InFile in=new InFile(file);
			String line=in.readLine();
			while(line!=null){
				text.append(line+"\n");
				line=in.readLine();
			}
			in.close();
			return text.toString();
		}
	}

	public static class OutFile {
		public PrintStream	 out = null;
		
		public OutFile(String filename){
			try{
				out= new PrintStream(new FileOutputStream(filename));
			}catch(Exception e){
				e.printStackTrace();
				System.exit(0);
			}
		}
		
		public void println(String s){
			out.println(s);
		}
		public void print(String s){
			out.print(s);
		}
		public void close(){
			out.flush();
			out.close();
		}
	}

}
