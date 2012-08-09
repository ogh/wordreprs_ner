package Experiments.PrepareData;

import java.io.File;

import IO.InFile;
import IO.OutFile;

public class BuildMergedAndSubsampledReutersDatasets {
	public static void main(String[] args){
		//merge the training and the testing
    	String infolder="Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev";
    	String outfolder="Data/GoldData/Reuters/ColumnFormatDocumentsSplit/TrainPlusDev";
    	File f=new File(infolder);
    	String[] files=f.list();
    	for(int i=0;i<files.length;i++){
    		if(!files[i].startsWith(".")){
    			//copying the file!!!
    			InFile in=new InFile(infolder+"/"+files[i]);
    			OutFile out=new OutFile(outfolder+"/"+"dev."+files[i]);
    			String line=in.readLine();
    			while(line!=null){
    				out.println(line);
    				line=in.readLine();
    			}
    			in.close();
    			out.close();
    		}
    	}
    	infolder="Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train";
    	f=new File(infolder);
    	files=f.list();
    	for(int i=0;i<files.length;i++){
    		if(!files[i].startsWith(".")){
    			//copying the file!!!
    			InFile in=new InFile(infolder+"/"+files[i]);
    			OutFile out=new OutFile(outfolder+"/"+"train."+files[i]);
    			String line=in.readLine();
    			while(line!=null){
    				out.println(line);
    				line=in.readLine();
    			}
    			in.close();
    			out.close();
    		}
    	}
    	//subsample the data- this will be used for learning the different voting methods
    	infolder="Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train";
    	outfolder="Data/GoldData/Reuters/ColumnFormatDocumentsSplit/TrainSubsampled";

    	f=new File(infolder);
    	files=f.list();
    	for(int i=0;i<files.length;i++){
    		if(!files[i].startsWith(".")&&Math.random()<0.1){
    			//copying the file!!!
    			InFile in=new InFile(infolder+"/"+files[i]);
    			OutFile out=new OutFile(outfolder+"/"+"train."+files[i]);
    			String line=in.readLine();
    			while(line!=null){
    				out.println(line);
    				line=in.readLine();
    			}
    			in.close();
    			out.close();
    		}
    	}
	}
}
