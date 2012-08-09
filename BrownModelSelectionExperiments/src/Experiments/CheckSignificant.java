package Experiments;

import java.util.Vector;

import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;
import ParsingProcessingData.TaggedDataReader;


public class CheckSignificant {
	
	public static void main(String[] args) throws Exception{
		System.out.println("Usage <classifier1TaggedDataPath> <classifier2TaggedDataPath> <goldDataPath> <dataFormat> <isFolder>");
		System.out.println("The first three parameters can be either files or folders");
		Vector<LinkedVector> classifier1TaggedData=null;
		Vector<LinkedVector> classifier2TaggedData=null;
		Vector<LinkedVector> goldData=null;
		boolean isFolder=Boolean.parseBoolean(args[4]);
		if(isFolder){
			ParametersForLbjCode.sortLexicallyFilesInFolders=true;
			ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=false;
			classifier1TaggedData=TaggedDataReader.readFolder(args[0], args[3]);
			classifier2TaggedData=TaggedDataReader.readFolder(args[1], args[3]);
			goldData=TaggedDataReader.readFolder(args[2], args[3]);
		}
		System.out.println("Are the two classifiers statistically significant? The answer is: "+ isStatisticallySignificantMultipleRuns(classifier1TaggedData, classifier2TaggedData, goldData));
	}
	
		//returns whether the changes between classifier 1 and classifier 2 are statistically significant
	public static boolean isStatisticallySignificantMultipleRuns(Vector<LinkedVector> classifier1TaggedData,
			Vector<LinkedVector> classifier2TaggedData,Vector<LinkedVector> goldData) {
		double A_Wrong_B_Right = 0;
		double B_Wrong_A_Right = 0;
		
		for(int i=0;i<goldData.size();i++)
			for(int j=0;j<goldData.elementAt(i).size();j++)
			{
				String p1=((NEWord)classifier1TaggedData.elementAt(i).get(j)).neLabel;
				String p2=((NEWord)classifier2TaggedData.elementAt(i).get(j)).neLabel;
				String gold=((NEWord)goldData.elementAt(i).get(j)).neLabel;
				if (p1.equals(gold)&&(!p2.equals(gold)))
					A_Wrong_B_Right++;
				if (p2.equals(gold)&&(!p1.equals(gold)))
					B_Wrong_A_Right++;
			}
				
		double statistic = 0;
		if((A_Wrong_B_Right + B_Wrong_A_Right)>0)
			statistic=((Math.abs(A_Wrong_B_Right - B_Wrong_A_Right) - 1) * 
					(Math.abs(A_Wrong_B_Right - B_Wrong_A_Right) - 1))
				/ (A_Wrong_B_Right + B_Wrong_A_Right);
		System.out.println("McNemar Test Statistic: 	" + statistic);
		return statistic > 3.841	;
	}
}
