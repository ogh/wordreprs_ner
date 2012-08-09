package ParsingProcessingData;
import java.io.File;
import java.util.Vector;

import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;

public class TaggedDataReader {

	/*
	 * The path can be either to a folder or a document, we check and act appropriately
	 */
	public static Vector<LinkedVector> readData(String path,String format)throws Exception{
		File f=new File(path);
		if(f.isDirectory())
			return readFolder(path, format);
		return readFile(path, format);
	}
	
	
	public static Vector<LinkedVector> parseTextAnnotatedWithBrackets(String annotatedText) throws Exception{
		return BracketFileReader.parseTextWithBrackets(annotatedText);
	}
		
	public static Vector<LinkedVector> readFolder(String path,String format)throws Exception{
		Vector<LinkedVector> res=new Vector<LinkedVector>();
		String[] files=(new File(path)).list();
		if(ParametersForLbjCode.sortLexicallyFilesInFolders)
			sortFilesLexicographically(files);
		for(int i=0;i<files.length;i++){
			String file=path+"/"+files[i];
			if((new File(file)).isFile()&&(!files[i].equals(".DS_Store"))){
				Vector<LinkedVector> temp=readFile(file, format);
				for(int j=0;j<temp.size();j++)
					res.addElement(temp.elementAt(j));
			}
		}
		if(ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument){
		    //connecting sentence boundaries
		    for(int i=0;i<res.size();i++)
		    {
		    	for(int j=0;j<res.elementAt(i).size();j++){
		    		NEWord w=(NEWord)res.elementAt(i).get(j);
		    		w.previousIgnoreSentenceBoundary=(NEWord)w.previous;
		    		w.nextIgnoreSentenceBoundary=(NEWord)w.next;
		    	}
		    	if(i>0&&res.elementAt(i).size()>0)
		    	{
		    		NEWord w=(NEWord)res.elementAt(i).get(0);
		    		w.previousIgnoreSentenceBoundary=(NEWord)res.elementAt(i-1).get(res.elementAt(i-1).size()-1);
		    	}
		    	if(i<res.size()-1&&res.elementAt(i).size()>0)
		    	{
		    		NEWord w=(NEWord)res.elementAt(i).get(res.elementAt(i).size()-1);
		    		w.nextIgnoreSentenceBoundary=(NEWord)res.elementAt(i+1).get(0);
		    	}
		    }
		}
		return res;
	}

	/*
	 * 
	 */
	public static Vector<LinkedVector> readFile(String path,String format) throws Exception{
		Vector<LinkedVector> res=null;
		if(format.equals("-c")){
			ColumnFileReader parser = new ColumnFileReader(path);
			res=parser.read();
		}
		else{
			if(format.equals("-r")){
				res=BracketFileReader.read(path);
			}
			else{
				System.out.println("Fatal error: unrecognized file format: "+format);
				System.exit(0);
			}
		}
	    //connecting sentence boundaries
	    for(int i=0;i<res.size();i++)
	    {
	    	for(int j=0;j<res.elementAt(i).size();j++){
	    		NEWord w=(NEWord)res.elementAt(i).get(j);
	    		w.previousIgnoreSentenceBoundary=(NEWord)w.previous;
	    		w.nextIgnoreSentenceBoundary=(NEWord)w.next;
	    	}
	    	if(i>0&&res.elementAt(i).size()>0)
	    	{
	    		NEWord w=(NEWord)res.elementAt(i).get(0);
	    		w.previousIgnoreSentenceBoundary=(NEWord)res.elementAt(i-1).get(res.elementAt(i-1).size()-1);
	    	}
	    	if(i<res.size()-1&&res.elementAt(i).size()>0)
	    	{
	    		NEWord w=(NEWord)res.elementAt(i).get(res.elementAt(i).size()-1);
	    		w.nextIgnoreSentenceBoundary=(NEWord)res.elementAt(i+1).get(0);
	    	}
	    }
		return res;
	}
	
	public static void sortFilesLexicographically(String[] files){
		for(int i=0;i<files.length;i++)
			for(int j=i+1;j<files.length;j++){
				if(files[i].compareTo(files[j])>0){
					String s=files[i];
					files[i]=files[j];
					files[j]=s;
				}
			}
	}
}
