package Experiments.PrepareData;

import java.io.File;
import java.util.Vector;

import LbjTagger.ParametersForLbjCode;
import ParsingProcessingData.*;

import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;

public class SaveGoldReutersinBracketsFormat {
	public static void main(String[] args) throws Exception{
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=true;
		ParametersForLbjCode.tokenizationScheme=ParametersForLbjCode.TokenizationScheme.DualTokenizationScheme;
		String[] fileSets={"Dev","Test","Train"};
		for(int fileSet=0;fileSet<fileSets.length;fileSet++){
			//write the Reuters column files into brackets files		
			String folder="Data/GoldData/Reuters/ColumnFormatDocumentsSplit/"+fileSets[fileSet];
			String[] files=(new File(folder)).list();
			for(int i=0;i<files.length;i++)
				if(!files[i].startsWith(".")&&((new File(folder+"/"+files[i])).isFile()))
				{
					Vector<LinkedVector> data=TaggedDataReader.readFile(folder+"/"+files[i],"-c");
					TaggedDataWriter.writeToFile("Data/GoldData/Reuters/BracketsFormatDocumentsSplitOriginalTokenization/"+fileSets[fileSet]+"/"+files[i], data, "-r", NEWord.LabelToLookAt.GoldLabel);
				}
		}
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=false;
		ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
		for(int fileSet=0;fileSet<fileSets.length;fileSet++){
			String folder="Data/GoldData/Reuters/BracketsFormatDocumentsSplitOriginalTokenization/"+fileSets[fileSet];
			String[] files=(new File(folder)).list();
			for(int i=0;i<files.length;i++)
				if(!files[i].startsWith(".")&&((new File(folder+"/"+files[i])).isFile()))
				{
					Vector<LinkedVector> data=TaggedDataReader.readFile(folder+"/"+files[i],"-r");
					TaggedDataWriter.writeToFile("Data/GoldData/Reuters/BracketsFormatDocumentsSplitMyTokenization/"+fileSets[fileSet]+"/"+files[i], data, "-r", NEWord.LabelToLookAt.GoldLabel);
				}
		}	
	}
}
