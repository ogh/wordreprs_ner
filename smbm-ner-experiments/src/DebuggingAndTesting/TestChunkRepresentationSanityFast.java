package DebuggingAndTesting;

import java.util.Vector;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;
import LbjTagger.TextChunkRepresentationManager;
import ParsingProcessingData.TaggedDataReader;


/*
 * 
 * this will read the data from a file, transform the data through all the possible
 * representations
 * 
 */
public class TestChunkRepresentationSanityFast {
	public static void main(String[]args) throws Exception{
		//Parameters.readConfigAndLoadExternalData("ConfigOld/allFeaturesBigTrainingSet.config");
		ParametersForLbjCode.tokenizationScheme=ParametersForLbjCode.TokenizationScheme.DualTokenizationScheme;
		ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=false;
		ParametersForLbjCode.taggingEncodingScheme=TextChunkRepresentationManager.EncodingScheme.BIO;
		ParametersForLbjCode.sortLexicallyFilesInFolders=true;
		ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=true;
		Vector<LinkedVector> data0 = TaggedDataReader.readFolder("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev","-c");
		for(int i=0;i<data0.size();i++)
			for(int j=0;j<data0.elementAt(i).size();j++){
				NEWord w=(NEWord)data0.elementAt(i).get(j);
				w.neTypeLevel1=w.neLabel;
				w.neTypeLevel2=w.neLabel;
			}
		ParametersForLbjCode.sortLexicallyFilesInFolders=true;
		ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=true;
		Vector<LinkedVector> data1 = TaggedDataReader.readFolder("Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev","-c");
		for(int i=0;i<data1.size();i++)
			for(int j=0;j<data1.elementAt(i).size();j++){
				NEWord w=(NEWord)data1.elementAt(i).get(j);
				w.neTypeLevel1=w.neLabel;
				w.neTypeLevel2=w.neLabel;
			}

		//changing the representation forth and back  BIO<-->IOE1, then see that the labels stay the same!
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOE1, data1, NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOE1, data1, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOE1, data1, NEWord.LabelToLookAt.PredictionLevel2Tagger);
		assertLabelDifference(data0,data1);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOE1, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1, NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOE1, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOE1, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1, NEWord.LabelToLookAt.PredictionLevel2Tagger);
		assertLabelIdentity(data1, data0);

		//changing the representation forth and back  BIO<-->IOE2, then see that the labels stay the same!
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOE2, data1, NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOE2, data1, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOE2, data1,  NEWord.LabelToLookAt.PredictionLevel2Tagger);
		assertLabelDifference(data0,data1);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOE2, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1, NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOE2, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOE2, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1,  NEWord.LabelToLookAt.PredictionLevel2Tagger);
		assertLabelIdentity(data1, data0);

		//changing the representation forth and back  BIO<-->IOB1, then see that the labels stay the same!
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOB1, data1, NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOB1, data1, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOB1, data1, NEWord.LabelToLookAt.PredictionLevel2Tagger);
		assertLabelDifference(data0,data1);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOB1, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1,NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOB1, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1,NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOB1, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1,  NEWord.LabelToLookAt.PredictionLevel2Tagger);
		assertLabelIdentity(data1, data0);

		//changing the representation forth and back  BIO<-->BILOU, then see that the labels stay the same!
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.BILOU, data1, NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.BILOU, data1, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.BILOU, data1,  NEWord.LabelToLookAt.PredictionLevel2Tagger);
		assertLabelDifference(data0,data1);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1,NEWord.LabelToLookAt.GoldLabel);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1, NEWord.LabelToLookAt.PredictionLevel1Tagger);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data1,  NEWord.LabelToLookAt.PredictionLevel2Tagger);
		assertLabelIdentity(data1, data0);

		System.out.println("passed all tests");
		
		visulaizeData();
	}

	
	public static void assertLabelDifference(Vector<LinkedVector> data, Vector<LinkedVector> dataOriginal) throws Exception{
		boolean allSame=true;
		for(int i=0;i<data.size();i++)
			for(int j=0;j<data.elementAt(i).size();j++){
				NEWord w1=(NEWord)dataOriginal.elementAt(i).get(j);
				NEWord w2=(NEWord)data.elementAt(i).get(j);
				if((!w1.neLabel.equals(w2.neLabel))&&(!w1.neTypeLevel1.equals(w2.neTypeLevel1))&&(!w1.neTypeLevel2.equals(w2.neTypeLevel2)))
					allSame=false;
			}
		if(allSame){
			throw new Exception("Error : after transforming the data from BIO to another format, all the labels are still identical to BIO!!!!");
		}		
	}	
	
	public static void assertLabelIdentity(Vector<LinkedVector> data, Vector<LinkedVector> dataOriginal) throws Exception{
		boolean error=false;
		for(int i=0;i<data.size();i++)
			for(int j=0;j<data.elementAt(i).size();j++){
				NEWord w1=(NEWord)dataOriginal.elementAt(i).get(j);
				NEWord w2=(NEWord)data.elementAt(i).get(j);
				if((!w1.neLabel.equals(w2.neLabel))||(!w1.neTypeLevel1.equals(w2.neTypeLevel1))||(!w1.neTypeLevel2.equals(w2.neTypeLevel2)))
					error=true;
			}
		if(error){
			for(int i=0;i<data.size();i++)
				for(int j=0;j<data.elementAt(i).size();j++){
					NEWord w1=(NEWord)dataOriginal.elementAt(i).get(j);
					NEWord w2=(NEWord)data.elementAt(i).get(j);
					System.out.println(w1.form+" "+w1.neLabel+"/"+w2.neLabel+" "+w1.neTypeLevel1+"/"+w2.neTypeLevel1+" "+w1.neTypeLevel2+"/"+w2.neTypeLevel2);
					if((!w1.neLabel.equals(w2.neLabel))||(!w1.neTypeLevel1.equals(w2.neTypeLevel1))||(!w1.neTypeLevel2.equals(w2.neTypeLevel2)))
						throw new Exception("Error: mismatch in tags!!!");
				}			
		}
	}
	
	public static void visulaizeData() throws Exception{
		ParametersForLbjCode.tokenizationScheme=ParametersForLbjCode.TokenizationScheme.DualTokenizationScheme;
		ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=false;
		ParametersForLbjCode.taggingEncodingScheme=TextChunkRepresentationManager.EncodingScheme.BIO;
		ParametersForLbjCode.sortLexicallyFilesInFolders=true;
		ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=true;
		String text="[PER Lev] [PER Zola] [PER Snob ] [LOC Isreal] [LOC Turkey] [ORG Subway ]loves the [LOC United Stated], but hates the [MISC Americans]";
		Vector<LinkedVector> data = TaggedDataReader.parseTextAnnotatedWithBrackets(text);
		//changing the representation forth and back  BIO<-->IOE1, then see that the labels stay the same!
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOE1, data, NEWord.LabelToLookAt.GoldLabel);
		System.out.println("IOE1");
		displayData(data);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOE1, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data, NEWord.LabelToLookAt.GoldLabel);
		//changing the representation forth and back  BIO<-->IOE2, then see that the labels stay the same!
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOE2, data, NEWord.LabelToLookAt.GoldLabel);
		System.out.println("IOE2");
		displayData(data);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOE2, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data, NEWord.LabelToLookAt.GoldLabel);
		//changing the representation forth and back  BIO<-->IOB1, then see that the labels stay the same!
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.IOB1, data, NEWord.LabelToLookAt.GoldLabel);
		System.out.println("IOB1");
		displayData(data);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.IOB1, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data,NEWord.LabelToLookAt.GoldLabel);
		//changing the representation forth and back  BIO<-->BILOU, then see that the labels stay the same!
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BIO, 
				TextChunkRepresentationManager.EncodingScheme.BILOU, data, NEWord.LabelToLookAt.GoldLabel);
		System.out.println("BILOU");
		displayData(data);
		TextChunkRepresentationManager.changeChunkRepresentation(
				TextChunkRepresentationManager.EncodingScheme.BILOU, 
				TextChunkRepresentationManager.EncodingScheme.BIO, data,NEWord.LabelToLookAt.GoldLabel);
		System.out.println("BIO");
		displayData(data);
	}
	
	public static void displayData(Vector<LinkedVector> data){
		for(int i=0;i<data.size();i++)
			for(int j=0;j<data.elementAt(i).size();j++){
				NEWord w=(NEWord)data.elementAt(i).get(j);
				System.out.println(w.form+"\t"+w.neLabel);
			}
	}
}
