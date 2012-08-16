package Experiments.PrepareData;

import java.util.Vector;

import IO.InFile;
import IO.OutFile;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;
import LbjTagger.TextChunkRepresentationManager;
import ParsingProcessingData.PlainTextWriter;
import ParsingProcessingData.TaggedDataReader;
import ParsingProcessingData.TaggedDataWriter;

public class StripTagsAndToBracketsFormatMucAndWebpages {
	public static void main(String[] args) throws Exception{
		ParametersForLbjCode.tokenizationScheme=ParametersForLbjCode.TokenizationScheme.DualTokenizationScheme;
		ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=false;
		ParametersForLbjCode.taggingEncodingScheme=TextChunkRepresentationManager.EncodingScheme.BIO;
		ParametersForLbjCode.sortLexicallyFilesInFolders=true;
		ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=false;
		
		/*
		for(int i=1;i<=20;i++){
			ParametersForLbjCode.forceNewSentenceOnLineBreaks=false;
			ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=true;
			Vector<LinkedVector> data=TaggedDataReader.readData("Data/GoldData/WebpagesBrackets/"+i+".brackets.gold","-r");
			TaggedDataWriter.writeToFile("Data/GoldData/WebpagesColumns/"+i+".columns.gold", data, "-c", NEWord.LabelToLookAt.GoldLabel);
			PlainTextWriter.write(data, "Data/TagsStrippedData/Webpages/"+i+".txt");
		}

		ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=false;
		String fileText=InFile.readFileText("Data/GoldData/MUC7Brackets/MUC7.NE.dryrun.eng.brackets.gold");
		fileText=fileText.replace("\n\n", "******newline******");
		fileText=fileText.replace('\n', ' ');
		fileText=fileText.replace("******newline******","\n");
		OutFile outFile=new OutFile("Data/GoldData/MUC7Brackets/MUC7.NE.dryrun.sentences.brackets.gold");
		outFile.println(fileText);
		outFile.close();
		Vector<LinkedVector> data=TaggedDataReader.readData("Data/GoldData/MUC7Brackets/MUC7.NE.dryrun.sentences.brackets.gold","-r");
		TaggedDataWriter.writeToFile("Data/GoldData/MUC7Columns/MUC7.NE.dryrun.sentences.columns.gold", data, "-c", NEWord.LabelToLookAt.GoldLabel);
		PlainTextWriter.write(data, "Data/TagsStrippedData/MUC7/MUC7.NE.dryrun.sentences.txt");

		fileText=InFile.readFileText("Data/GoldData/MUC7Brackets/MUC7.NE.formalrun.eng.brackets.gold");
		fileText=fileText.replace("\n\n", "******newline******");
		fileText=fileText.replace('\n', ' ');
		fileText=fileText.replace("******newline******","\n");
		outFile=new OutFile("Data/GoldData/MUC7Brackets/MUC7.NE.formalrun.sentences.brackets.gold");
		outFile.println(fileText);
		outFile.close();
		data=TaggedDataReader.readData("Data/GoldData/MUC7Brackets/MUC7.NE.formalrun.sentences.brackets.gold","-r");
		TaggedDataWriter.writeToFile("Data/GoldData/MUC7Columns/MUC7.NE.formalrun.sentences.columns.gold", data, "-c", NEWord.LabelToLookAt.GoldLabel);
		PlainTextWriter.write(data, "Data/TagsStrippedData/MUC7/MUC7.NE.formalrun.sentences.txt");

		fileText=InFile.readFileText("Data/GoldData/MUC7Brackets/MUC7.NE.training.eng.brackets.gold");
		fileText=fileText.replace("\n\n", "******newline******");
		fileText=fileText.replace('\n', ' ');
		fileText=fileText.replace("******newline******","\n");
		outFile=new OutFile("Data/GoldData/MUC7Brackets/MUC7.NE.training.sentences.brackets.gold");
		outFile.println(fileText);
		outFile.close();
		data=TaggedDataReader.readData("Data/GoldData/MUC7Brackets/MUC7.NE.training.sentences.brackets.gold","-r");
		TaggedDataWriter.writeToFile("Data/GoldData/MUC7Columns/MUC7.NE.training.sentences.columns.gold", data, "-c", NEWord.LabelToLookAt.GoldLabel);
		PlainTextWriter.write(data, "Data/TagsStrippedData/MUC7/MUC7.NE.training.sentences.txt");
		*/
		ParametersForLbjCode.forceNewSentenceOnLineBreaks=false;
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=true;
		Vector<LinkedVector> data = TaggedDataReader.readData("Data/GoldData/Arts/godby.brackets.gold","-r");
		TaggedDataWriter.writeToFile("Data/GoldData/Arts/godby.columns.gold", data, "-c", NEWord.LabelToLookAt.GoldLabel);
		PlainTextWriter.write(data, "Data/TagsStrippedData/Arts/godby.txt");
	}
}
