package ExpressiveFeatures;

import java.util.HashMap;
import java.util.Vector;

import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LuceneInterface.TermLookupIndex;

public class HmmEmbeddings{
	public static double normalizationConstraint=1.0;
	public static HashMap<String,TermLookupIndex> hmmEmbeddings=new HashMap<String, TermLookupIndex>(10);
	
	public static void addDataset(String pathToHmmEmbeddingIndex,Vector<LinkedVector> data,String dataSetName) throws Exception{
		assignKeysToTokens(data,dataSetName);
		TermLookupIndex lookup=new TermLookupIndex(pathToHmmEmbeddingIndex);
		lookup=new TermLookupIndex(pathToHmmEmbeddingIndex);
		lookup.openIndexForRetrieval();
		hmmEmbeddings.put(dataSetName, lookup);
		verifyDataToLuceneIndexedHmmEmbedding(data);
	}
	private static void assignKeysToTokens(Vector<LinkedVector> data,String dataSetName){
		for(int i=0;i<data.size();i++)
			for(int j=0;j<data.elementAt(i).size();j++){
				NEWord w=(NEWord)data.elementAt(i).get(j);
				w.sentenceId=i;
				w.tokenId=j;
				w.datasetName=dataSetName;
			}
	}
	private static void verifyDataToLuceneIndexedHmmEmbedding(Vector<LinkedVector> data) throws Exception{
		System.out.println("Verifying the hmm features for the datasets");
		for(int i=0;i<data.size();i++)
			for(int j=0;j<data.elementAt(i).size();j++){
				NEWord w=(NEWord)data.elementAt(i).get(j);
				w.sentenceId=i;
				w.tokenId=j;
				String expectedToken=hmmEmbeddings.get(w.datasetName).getToken(i+"_"+j);
				if(!expectedToken.equals(w.form))
					throw new Exception("Fatal error: Mismatch in Hmm embedding. The expected token for sentence "+i+" token "+j+" is: "+expectedToken+", but the token in data is: "+w.form);
			}
	}
	
	public static double[] getEmbedding(NEWord w) {
		if(hmmEmbeddings==null){
			System.out.println("Fatal error- hmm embeddings were not initialized!");
			System.exit(0);
		}
		try{
			double[] res=hmmEmbeddings.get(w.datasetName).getEmbedding(w.sentenceId+"_"+w.tokenId);
			double sum=0;
			for(int i=0;i<res.length;i++)
				sum+=res[i];
			for(int i=0;i<res.length;i++)
				res[i]=(res[i]/sum)*normalizationConstraint;
			return res;
		}catch (Exception e) {
			System.out.println("Failed to retrieve the embedding for the word : sentenceId="+w.sentenceId+ "; tokenId="+w.tokenId);
			e.printStackTrace();
			System.exit(0);
		}
		return null;
	}
	
}
