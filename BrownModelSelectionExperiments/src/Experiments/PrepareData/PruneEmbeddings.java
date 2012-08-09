package Experiments.PrepareData;

import java.util.StringTokenizer;
import java.util.Vector;

import ExpressiveFeatures.BrownClusters;
import IO.InFile;
import IO.OutFile;
import LBJ2.nlp.Word;
import LbjTagger.NEWord;

public class PruneEmbeddings {
	public static void saveEmbeddingsWithRestrictedVocabulary(
			String embeddingInFile,
			String embeddingOutFile,
			String pathToBrownClustersVocabulary,
			int appThreshold,
			boolean isLowercased
			){
		Vector<String> v1=new Vector<String>();
		v1.addElement(pathToBrownClustersVocabulary);
		Vector<Integer> v2=new Vector<Integer>();
		v2.addElement(appThreshold);
		Vector<Boolean> v3=new Vector<Boolean>();
		v3.addElement(isLowercased);
		BrownClusters.init(v1, v2, v3);
		InFile in=new InFile(embeddingInFile);
		OutFile out=new OutFile(embeddingOutFile);
		String line=in.readLine();
		while(line!=null){
			StringTokenizer st =new StringTokenizer(line,"\t");
			String token=st.nextToken();
			if(BrownClusters.getPrefixes(new NEWord(new Word(token),null,null))!=null&&BrownClusters.getPrefixes(new NEWord(new Word(token),null,null)).length>0){
				System.out.println("Token added");
				out.println(line);
			}
			line=in.readLine();
		}
		in.close();
		out.close();
	}
	
	
	public static void main(String[] args){
		saveEmbeddingsWithRestrictedVocabulary(
				"Data/WordEmbedding/embeddings-20090916-rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.txt",
				"Data/WordEmbedding/embeddings-20090916-rcv1.case-intact.wordApp5.txt",
				"Data/BrownHierarchicalWordClusters/rcv1.clean.tokenized-c1000-p1.paths.txt",
				5,false);
		saveEmbeddingsWithRestrictedVocabulary(
				"Data/WordEmbedding/embeddings-20090916-rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.txt",
				"Data/WordEmbedding/embeddings-20090916-rcv1.case-intact.wordApp10.txt",
				"Data/BrownHierarchicalWordClusters/rcv1.clean.tokenized-c1000-p1.paths.txt",
				10,false);
		saveEmbeddingsWithRestrictedVocabulary(
				"Data/WordEmbedding/embeddings-20090916-rcv1.case-intact.LEARNING_RATE=0_000000001_--EMBEDDING_LEARNING_RATE=0_0000032.model-720000000.txt",
				"Data/WordEmbedding/embeddings-20090916-rcv1.case-intact.wordApp15.txt",
				"Data/BrownHierarchicalWordClusters/rcv1.clean.tokenized-c1000-p1.paths.txt",
				15,false);
		saveEmbeddingsWithRestrictedVocabulary(
				"Data/WordEmbedding/embeddings-wikipedia-20090819-english.lowercase.with-unknown-word.LEARNING_RATE=0_0000000032_EMBEDDING_LEARNING_RATE=0_0000032.model-1080000000.txt",
				"Data/WordEmbedding/embeddings-wikipedia-20090819-english.lowercase.wordApp40.txt",
				"Data/BrownHierarchicalWordClusters/brown-english-wikitext.lowercase.train-c1000-p1.out.minfreq20.txt",
				40,true);
		saveEmbeddingsWithRestrictedVocabulary(
				"Data/WordEmbedding/embeddings-wikipedia-20090819-english.lowercase.with-unknown-word.LEARNING_RATE=0_0000000032_EMBEDDING_LEARNING_RATE=0_0000032.model-1080000000.txt",
				"Data/WordEmbedding/embeddings-wikipedia-20090819-english.lowercase.wordApp60.txt",
				"Data/BrownHierarchicalWordClusters/brown-english-wikitext.lowercase.train-c1000-p1.out.minfreq20.txt",
				60,true);
		saveEmbeddingsWithRestrictedVocabulary(
				"Data/WordEmbedding/embeddings-wikipedia-20090819-english.lowercase.with-unknown-word.LEARNING_RATE=0_0000000032_EMBEDDING_LEARNING_RATE=0_0000032.model-1080000000.txt",
				"Data/WordEmbedding/embeddings-wikipedia-20090819-english.lowercase.wordApp100.txt",
				"Data/BrownHierarchicalWordClusters/brown-english-wikitext.lowercase.train-c1000-p1.out.minfreq20.txt",
				100,true);
	}
}
