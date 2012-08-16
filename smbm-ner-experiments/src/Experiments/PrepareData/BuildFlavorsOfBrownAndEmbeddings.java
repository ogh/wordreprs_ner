package Experiments.PrepareData;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import IO.InFile;
import IO.OutFile;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;
import LbjTagger.TextChunkRepresentationManager;
import ParsingProcessingData.TaggedDataReader;

public class BuildFlavorsOfBrownAndEmbeddings {
		public static HashMap<String,Boolean> getRareWordsRcv(int maxAppCount){
			HashMap<String,Boolean> res=new HashMap<String,Boolean>();
			InFile in=new InFile("/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.txt");
			String line=in.readLine();
			while(line!=null){
				StringTokenizer st=new StringTokenizer(line);
				st.nextToken();
				String token=st.nextToken();
				int app=Integer.parseInt(st.nextToken());
				if(app<=maxAppCount)
					res.put(token, true);
				line=in.readLine();
			}
			return res;
		}
		
		
		public static HashMap<String,Integer> getCoNLLTokenCounts() throws Exception{
			ParametersForLbjCode.tokenizationScheme=ParametersForLbjCode.TokenizationScheme.DualTokenizationScheme;
			ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
			ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=false;
			ParametersForLbjCode.taggingEncodingScheme=TextChunkRepresentationManager.EncodingScheme.BIO;
			ParametersForLbjCode.sortLexicallyFilesInFolders=true;
			ParametersForLbjCode.treatAllFilesInFolderAsOneBigDocument=true;
			Vector<Vector<LinkedVector>> data=new Vector<Vector<LinkedVector>>();
			data.addElement(TaggedDataReader.readFolder("/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev","-c"));
			data.addElement(TaggedDataReader.readFolder("/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test","-c"));
			data.addElement(TaggedDataReader.readFolder("/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train","-c"));
			HashMap<String,Integer> counts=new HashMap<String, Integer>();
			for(int i=0;i<data.size();i++)
				for(int j=0;j<data.elementAt(i).size();j++)
					for(int k=0;k<data.elementAt(i).elementAt(j).size();k++){
						String w=((NEWord)data.elementAt(i).elementAt(j).get(k)).form;
						if(counts.containsKey(w))
							counts.put(w, counts.get(w)+1);
						else
							counts.put(w, 1);
					}
			return counts;
		}
		
		public static HashMap<String,Boolean> getCommonWordsRcv(int minAppCount){
			HashMap<String,Boolean> res=new HashMap<String,Boolean>();
			InFile in=new InFile("/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.txt");
			String line=in.readLine();
			while(line!=null){
				StringTokenizer st=new StringTokenizer(line);
				st.nextToken();
				String token=st.nextToken();
				int app=Integer.parseInt(st.nextToken());
				if(app>=minAppCount)
					res.put(token, true);
				line=in.readLine();
			}
			return res;
		}
		
		public static void printLowerCaseOnlyUpperCaseOnly(String resourceName,String resourceLowerCaseOut,
				String resourceUpperCaseOut,HashMap<String,Integer> coNllTokens,
				boolean isBrown, boolean isNN){
			InFile in=new InFile(resourceName);
			OutFile out1=new OutFile(resourceLowerCaseOut);
			OutFile out2=new OutFile(resourceUpperCaseOut);
			
			String line=in.readLine();
			while(line!=null){
				StringTokenizer st=new StringTokenizer(line);
				String token=st.nextToken();
				if(isBrown)
					token=st.nextToken();
				if(coNllTokens.containsKey(token)&&(!token.equals("*UNKNOWN*"))){
					if(Character.isUpperCase(token.charAt(0)))
						out2.println(line);
					if(Character.isLowerCase(token.charAt(0)))
						out1.println(line);
				}
				line=in.readLine();
			}			
			in.close();
			out1.close();
			out2.close();
		}
		

		public static void printRareAndCommonInRCV(String resourceName,String resourceRareOut,
				String resourceCommonOut,HashMap<String,Integer> coNllTokens,
				boolean isBrown, boolean isNN){
			HashMap<String,Boolean> rare=getRareWordsRcv(5);
			HashMap<String,Boolean> common=getCommonWordsRcv(100);
			System.out.println("The number of rare tokens in RCV: "+rare.size());
			System.out.println("The number of common tokens in RCV: "+common.size());
			InFile in=new InFile(resourceName);
			OutFile out1=new OutFile(resourceRareOut);
			OutFile out2=new OutFile(resourceCommonOut);
			
			String line=in.readLine();
			while(line!=null){
				StringTokenizer st=new StringTokenizer(line);
				String token=st.nextToken();
				if(isBrown)
					token=st.nextToken();
				if(coNllTokens.containsKey(token)&&(!token.equals("*UNKNOWN*"))){
					if(common.containsKey(token))
						out2.println(line);
					if(rare.containsKey(token))
						out1.println(line);
				}
				line=in.readLine();
			}			
			in.close();
			out1.close();
			out2.close();
		}
		

		public static void printRareAndCommonInCoNLL(String resourceName,String resourceRareOut,
				String resourceCommonOut,HashMap<String,Integer> coNllTokens,
				boolean isBrown, boolean isNN){
			HashMap<String,Boolean> rare=new HashMap<String, Boolean>();
			HashMap<String,Boolean> common=new HashMap<String, Boolean>();
			for(Iterator<String> i=coNllTokens.keySet().iterator();i.hasNext();){
				String s=i.next();
				if(coNllTokens.get(s)<=2)
					rare.put(s,true);
				if(coNllTokens.get(s)>10)
					common.put(s,true);
			}
			System.out.println("The number of rare tokens in CoNLL: "+rare.size());
			System.out.println("The number of common tokens in CoNLL: "+common.size());
			InFile in=new InFile(resourceName);
			OutFile out1=new OutFile(resourceRareOut);
			OutFile out2=new OutFile(resourceCommonOut);
			
			String line=in.readLine();
			while(line!=null){
				StringTokenizer st=new StringTokenizer(line);
				String token=st.nextToken();
				if(isBrown)
					token=st.nextToken();
				if(coNllTokens.containsKey(token)&&(!token.equals("*UNKNOWN*"))){
					if(common.containsKey(token))
						out2.println(line);
					if(rare.containsKey(token))
						out1.println(line);
				}
				line=in.readLine();
			}			
			in.close();
			out1.close();
			out2.close();
		}
		
		
		public static void main(String[] args) throws Exception{
			HashMap<String,Integer> coNllTokens=getCoNLLTokenCounts();
			printLowerCaseOnlyUpperCaseOnly(
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.case-intact.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.lowercaseOnly.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.upperCaseOnly.txt",
					coNllTokens,false,true);
			printLowerCaseOnlyUpperCaseOnly(
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/cw_rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/cw_rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.lowercaseOnly.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/cw_rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.upperCaseOnly.txt",
					coNllTokens,false,true);
			printLowerCaseOnlyUpperCaseOnly(
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.lowercaseOnly.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.upperCaseOnly.txt",
					coNllTokens,true,false);
			printRareAndCommonInRCV(
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.case-intact.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.rare.rcv.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.common.rcv.txt",
					coNllTokens,false,true);
			printRareAndCommonInRCV(
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/cw_rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/cw_rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.rare.rcv.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/cw_rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.common.rcv.txt",
					coNllTokens,false,true);
			printRareAndCommonInRCV(
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.rare.rcv.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.common.rcv.txt",
					coNllTokens,true,false);
			printRareAndCommonInCoNLL(
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.case-intact.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.rare.conll.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/hlbl_reps_clean_1.rcv1.clean.tokenized-CoNLL03.common.conll.txt",
					coNllTokens,false,true);
			printRareAndCommonInCoNLL(
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/cw_rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/cw_rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.rare.conll.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/WordEmbedding/cw_rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.common.conll.txt",
					coNllTokens,false,true);
			printRareAndCommonInCoNLL(
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.rare.conll.txt",
					"/shared/grandma/ratinov2/TurianExperimentsNaacl/Data/BrownHierarchicalWordClusters/brown-rcv1.clean.tokenized-CoNLL03.txt-c1000-freq1-v3.common.conll.txt",
					coNllTokens,true,false);
		}
}
