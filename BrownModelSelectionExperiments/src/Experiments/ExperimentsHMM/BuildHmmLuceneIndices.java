package Experiments.ExperimentsHMM;
import java.util.StringTokenizer;
import java.util.Vector;


import IO.InFile;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;
import LuceneInterface.TermLookupIndex;
import ParsingProcessingData.TaggedDataReader;

public class BuildHmmLuceneIndices {
	public static void main(String[] args) throws Exception{
	}	

	public static void buildReutersHmmIndexFromCwEmbeddings() throws Exception{
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=true;
		ParametersForLbjCode.tokenizationScheme=ParametersForLbjCode.TokenizationScheme.DualTokenizationScheme;
		Vector<String> hmmEmbeddingFiles=new Vector<String>();
		for(int i=0;i<=215;i++){
			String fid=String.valueOf(i);
			while(fid.length()<4)
				fid="0"+fid;
			hmmEmbeddingFiles.addElement("../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/Dev/"+
						fid+".txt.hmm-integers-rcv1.clean.tokenized-CoNLL03.minfreq=2.loglikelihoodCutoff=0.001.xml.txt");
		}
		Vector<LinkedVector> data=TaggedDataReader.readData("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev", "-c");
		indexData(data,hmmEmbeddingFiles,"../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/DevIndexed");

	
		hmmEmbeddingFiles=new Vector<String>();
		for(int i=0;i<=235;i++){
			String fid=String.valueOf(i);
			while(fid.length()<4)
				fid="0"+fid;
			hmmEmbeddingFiles.addElement("../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/Test/"+
						fid+".txt.hmm-integers-rcv1.clean.tokenized-CoNLL03.minfreq=2.loglikelihoodCutoff=0.001.xml.txt");
		}
		data=TaggedDataReader.readData("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test", "-c");
		indexData(data,hmmEmbeddingFiles,"../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/TestIndexed");

		hmmEmbeddingFiles=new Vector<String>();
		for(int i=0;i<=945;i++){
			String fid=String.valueOf(i);
			while(fid.length()<4)
				fid="0"+fid;
			hmmEmbeddingFiles.addElement("../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/Train/"+
						fid+".txt.hmm-integers-rcv1.clean.tokenized-CoNLL03.minfreq=2.loglikelihoodCutoff=0.001.xml.txt");
		}
		data=TaggedDataReader.readData("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train", "-c");
		indexData(data,hmmEmbeddingFiles,"../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/TrainIndexed");	
	}
	
	public static void buildReutersHmmIndexOriginalFiles() throws Exception{
		//TermLookupIndex index=new TermLookupIndex("../Data/DummyLuceneIndex");
		//index.openIndexWriting();
		//index.addToken("lala", "lalal", "tata");
		//index.finalizeIndexWriting();
		//System.exit(0);
		
		ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting=true;
		ParametersForLbjCode.tokenizationScheme=ParametersForLbjCode.TokenizationScheme.DualTokenizationScheme;
		Vector<String> hmmEmbeddingFiles=new Vector<String>();
		for(int i=0;i<=215;i++){
			String fid=String.valueOf(i);
			while(fid.length()<4)
				fid="0"+fid;
			hmmEmbeddingFiles.addElement("../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/Dev/"+
						fid+".txt.hmm-integers-rcv1.clean.tokenized-CoNLL03.minfreq=2.loglikelihoodCutoff=0.001.xml.txt");
		}
		Vector<LinkedVector> data=TaggedDataReader.readData("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Dev", "-c");
		indexData(data,hmmEmbeddingFiles,"../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/DevIndexed");

	
		hmmEmbeddingFiles=new Vector<String>();
		for(int i=0;i<=235;i++){
			String fid=String.valueOf(i);
			while(fid.length()<4)
				fid="0"+fid;
			hmmEmbeddingFiles.addElement("../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/Test/"+
						fid+".txt.hmm-integers-rcv1.clean.tokenized-CoNLL03.minfreq=2.loglikelihoodCutoff=0.001.xml.txt");
		}
		data=TaggedDataReader.readData("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Test", "-c");
		indexData(data,hmmEmbeddingFiles,"../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/TestIndexed");

		hmmEmbeddingFiles=new Vector<String>();
		for(int i=0;i<=945;i++){
			String fid=String.valueOf(i);
			while(fid.length()<4)
				fid="0"+fid;
			hmmEmbeddingFiles.addElement("../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/Train/"+
						fid+".txt.hmm-integers-rcv1.clean.tokenized-CoNLL03.minfreq=2.loglikelihoodCutoff=0.001.xml.txt");
		}
		data=TaggedDataReader.readData("../Data/GoldData/Reuters/ColumnFormatDocumentsSplit/Train", "-c");
		indexData(data,hmmEmbeddingFiles,"../Data/HmmEmbedding/NER-GoldData/Reuters/ColumnFormatDocumentsSplit/TrainIndexed");	
	}
	
	public static void indexData(Vector<LinkedVector> data,Vector<String> hmmEmbeddingFiles,String outIndexFile) throws Exception{
		TermLookupIndex index=new TermLookupIndex(outIndexFile);
		index.openIndexWriting();
		int hmmFileId=0;
		InFile in=new InFile(hmmEmbeddingFiles.elementAt(hmmFileId));
		for(int i=0;i<data.size();i++){
			for(int j=0;j<data.elementAt(i).size();j++){
				String line=in.readLine();
				while(line!=null&&(line.replace(" ", "").length()==0||line.startsWith("-DOCSTART-")))
					line=in.readLine();
				if(line==null){
					hmmFileId++;
					in.close();
					in=new InFile(hmmEmbeddingFiles.elementAt(hmmFileId));
					line=in.readLine();
					while(line!=null&&(line.replace(" ", "").length()==0||line.startsWith("-DOCSTART-")))
						line=in.readLine();
				}
				StringTokenizer st=new StringTokenizer(line,"\n\t ");
				String token=st.nextToken();
				String embedding="";
				while(st.hasMoreTokens())
					embedding+=" "+st.nextToken();
				NEWord w=(NEWord)data.elementAt(i).get(j);
				if(!token.equals(w.form))
					throw new Exception("Misalligned tokens: the data token is: "+w.form+" ; the hmm encoding is for the token: "+token);
				index.addToken(i+"_"+j, token, embedding);
			}
		}	
		in.close();
		index.finalizeIndexWriting();
	}
}
