package ParsingProcessingData;

import java.util.Vector;

import IO.OutFile;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;

public class TaggedDataWriter {
	public static void writeToFile(String outputFile,Vector<LinkedVector> data,String fileFormat,NEWord.LabelToLookAt labelType) throws Exception {
		OutFile out=new OutFile(outputFile);
		if(fileFormat.equalsIgnoreCase("-r"))
			out.println(toBracketsFormat(data,labelType));
		else{
			if(fileFormat.equalsIgnoreCase("-c"))
				out.println(toColumnsFormat(data, labelType));
			else{
				throw new Exception("Unknown file format (only options -r and -c are supported): "+fileFormat);
			}
		}
		out.close();
	}

	/*
	 * labelType=NEWord.GoldLabel/NEWord.PredictionLevel2Tagger/NEWord.PredictionLevel1Tagger
	 * 
	 * Note : the only reason this function is public is because we want to be able to use it 
	 * in the demo and insert html tags into the string
	 */
    public static String toBracketsFormat(Vector<LinkedVector> data,NEWord.LabelToLookAt labelType) throws Exception{
    	StringBuffer res=new StringBuffer(data.size()*100);
        for(int i=0;i<data.size();i++){
            LinkedVector vector = data.elementAt(i);
            boolean open=false;
            String[] predictions=new String[vector.size()];
            String[] words=new String[vector.size()];
            for(int j=0;j<vector.size();j++){
            	predictions[j] =  null;
            	if(labelType==NEWord.LabelToLookAt.PredictionLevel2Tagger)
            		predictions[j] = ((NEWord)vector.get(j)).neTypeLevel2; 
            	if(labelType==NEWord.LabelToLookAt.PredictionLevel1Tagger)
            	 	predictions[j] = ((NEWord)vector.get(j)).neTypeLevel1;
            	if(labelType==NEWord.LabelToLookAt.GoldLabel)
            	 	predictions[j] = ((NEWord)vector.get(j)).neLabel;
            	words[j]=((NEWord)vector.get(j)).form;
            }
            for(int j=0;j<vector.size();j++)
            { 
            	if (predictions[j].startsWith("B-")
            			|| 
            			(j>0&&predictions[j].startsWith("I-") && (!predictions[j-1].endsWith(predictions[j].substring(2))))){
            		res.append("[" + predictions[j].substring(2) + " ");
            		open=true;
            	}
            	res.append(words[j]+ " ");
            	if(open){
            		boolean close=false;
            		if(j==vector.size()-1){
            			close=true;
            		}
            		else
            		{
            			if(predictions[j+1].startsWith("B-"))
            				close=true;
            			if(predictions[j+1].equals("O"))
            				close=true;
            			if(predictions[j+1].indexOf('-')>-1&&(!predictions[j].endsWith(predictions[j+1].substring(2))))
            				close=true;
            		}
            		if(close){
            			res.append(" ] ");
            			open=false;
            		}
            	}
            }
            res.append("\n");
        }
        return res.toString();
    }
    
    private static String toColumnsFormat(Vector<LinkedVector> data,NEWord.LabelToLookAt labelType) throws Exception{
    	StringBuffer res=new StringBuffer(data.size()*100);
    	for(int i=0;i<data.size();i++){
    		LinkedVector vector=data.elementAt(i);
    		if(((NEWord)vector.get(0)).previousIgnoreSentenceBoundary==null)
    			res.append("O	0	0	O	-X-	-DOCSTART-	x	x	0\n\n");
    		for(int j=0;j<vector.size();j++)
    		{
    			NEWord w=(NEWord)vector.get(j);
    			res.append(w.getPrediction(labelType)+"\t0\t"+j+"\tO\tO\t"+w.form+"\tx\tx\t0\n");
    		}
    		res.append("\n");
    	}
    	return res.toString();
    }    
}
