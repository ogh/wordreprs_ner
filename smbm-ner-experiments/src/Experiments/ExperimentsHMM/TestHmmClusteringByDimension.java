package Experiments.ExperimentsHMM;

import java.util.StringTokenizer;

import IO.InFile;
import StringStatisticsUtils.CharacteristicWords;
import StringStatisticsUtils.OccurrenceCounter;

public class TestHmmClusteringByDimension {

	public static void main(String[] args){
		String[] devFiles=new String[216];
		for(int i=0;i<=215;i++){
			String fid=String.valueOf(i);
			while(fid.length()<4)
				fid="0"+fid;
			devFiles[i]="../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/Dev/"+
						fid+".txt.hmm-integers-rcv1.clean.tokenized-CoNLL03.minfreq=2.loglikelihoodCutoff=0.001.xml.txt";
		}
		System.out.println("*************\t\t\t DEV SET\t\t\t***************");
		printSummary(devFiles,80);
	/*
		String[] testFiles=new String[236];
		for(int i=0;i<=235;i++){
			String fid=String.valueOf(i);
			while(fid.length()<4)
				fid="0"+fid;
			testFiles[i]="../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/Test/"+
						fid+".txt.hmm-integers-rcv1.clean.tokenized-CoNLL03.minfreq=2.loglikelihoodCutoff=0.001.xml.txt";
		}
		System.out.println("*************\t\t\t TEST SET\t\t\t***************");
		printSummary(testFiles,80);
		
		
		String[] trainFiles=new String[946];
		for(int i=0;i<=945;i++){
			String fid=String.valueOf(i);
			while(fid.length()<4)
				fid="0"+fid;
			trainFiles[i]="../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/Train/"+
						fid+".txt.hmm-integers-rcv1.clean.tokenized-CoNLL03.minfreq=2.loglikelihoodCutoff=0.001.xml.txt";
		}
		System.out.println("*************\t\t\t HMM EMCODING : TRAIN SET\t\t\t***************");
		printSummary(trainFiles,80);
		*/
		String[] hlblFile=new String[]{"../Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.case-intact.txt"};
		System.out.println("*************\t\t\t HLBL ENCODING SUMMARY:\t\t\t***************");
		printSummary(hlblFile,100);
		

	}
	
	public static void printSummary(String[] inputFiles,int dimension){
		DimensionSummary[] dimensions=new DimensionSummary[dimension];
		for(int i=0;i<dimensions.length;i++)
			dimensions[i]=new DimensionSummary();
		for(int i=0;i<inputFiles.length;i++){
			InFile in=new InFile(inputFiles[i]);
			String s=in.readLine();
			while(s!=null){
				if(s.replace(" ", "").replace("\t", "").length()>0){
					StringTokenizer st=new StringTokenizer(s," \t \n\r");
					String t=st.nextToken();
					for(int j=0;j<dimension;j++)
						dimensions[j].addToken(t, Double.parseDouble(st.nextToken()));				
				}
				s=in.readLine();
			}
		}
		for(int i=0;i<dimensions.length;i++){
			System.out.println(">>>>>>>>>\t\t Dimension "+i+" TOP tokens:");
			for(int j=0;j<dimensions[i].topWords.topWords.size();j++){
				String t=dimensions[i].topWords.topWords.elementAt(j);
				System.out.println(t+"; val:"+dimensions[i].topWords.topScores.elementAt(j));
			}
		}
	}
	
	public static class DimensionSummary{
		public static final int maxInstances=10;//the max number of same tokens per dimension
		CharacteristicWords topWords=new CharacteristicWords(100);
				
		public void addToken(String token,double val){
			double count=0;
			for(int i=0;i<topWords.topWords.size();i++)
				if(topWords.topWords.elementAt(i).equals(token))
					count++;
			if(count<maxInstances){
				topWords.addElement(token, val);
			}
		}		
	}
}
