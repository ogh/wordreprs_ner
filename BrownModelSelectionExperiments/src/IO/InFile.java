package IO;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;


public class InFile {
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
