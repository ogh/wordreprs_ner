package Experiments.NaaclExperiments;



import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import java.io.IOException;

import IO.InFile;
import IO.OutFile;
import ParsingProcessingData.BuildEvaluationFiles;

public class EvaluateStanfordNerTagger {

    public static void main(String[] args) throws IOException {


        /*
    	String s="Good afternoon Rajat Raina, how are you today?\n I am German. I go to school at Stanford University, which is located in California. \n I'm California-based, but I play for Real Madrid.";
        System.out.println("\n------------\n"+tagWithStanfordTagger(s));
        tagFile("Data/RawData/MUC7/MUC7.NE.dryrun.eng.clean", "Data/NaaclData/StanfordTagging/MUC7/dryrun.brackets.stanford", true);
        tagFile("Data/RawData/MUC7/MUC7.NE.formalrun.eng.clean", "Data/NaaclData/StanfordTagging/MUC7/formalrun.brackets.stanford", true);
        tagFile("Data/RawData/Reuters/BIO.testa.raw.txt", "Data/NaaclData/StanfordTagging/Reuters/testa.brackets.stanford", false);
        tagFile("Data/RawData/Reuters/BIO.testb.raw.txt", "Data/NaaclData/StanfordTagging/Reuters/testb.brackets.stanford", false);
        for(int i=1;i<=20;i++)
        	tagFile("Data/RawData/Webpages/"+i, "Data/NaaclData/StanfordTagging/Webpages/"+i+".brackets.stanford", false);

        */
    	
    	
       

        //BuildEvaluationFiles.buildEvaluationFile("Data/GoldData/Reuters/BIO.testa.brackets.gold", "Data/NaaclData/StanfordTagging/Reuters/testa.brackets.stanford", "Data/NaaclData/EvalFilesStanford/ReutersTesta", false);
        //BuildEvaluationFiles.buildEvaluationFile("Data/GoldData/Reuters/BIO.testb.brackets.gold", "Data/NaaclData/StanfordTagging/Reuters/testb.brackets.stanford", "Data/NaaclData/EvalFilesStanford/ReutersTestb", false);
        //BuildEvaluationFiles.buildEvaluationFile("Data/GoldData/MUC7/MUC7.NE.dryrun.eng.brackets.gold", "Data/NaaclData/StanfordTagging/MUC7/dryrun.brackets.stanford", "Data/NaaclData/EvalFilesStanford/MUC7dryRun", true);
        //BuildEvaluationFiles.buildEvaluationFile("Data/GoldData/MUC7/MUC7.NE.formalrun.eng.brackets.gold", "Data/NaaclData/StanfordTagging/MUC7/formalrun.brackets.stanford", "Data/NaaclData/EvalFilesStanford/MUC7formalRun", true);

        
        	
        
        String[] webpagesGold=new String[20];
        String[] webpagedStanford=new String[20];
        for(int i=1;i<=20;i++){
        	webpagesGold[i-1]="Data/GoldData/Webpages/"+i+".brackets.gold";
        	webpagedStanford[i-1]="Data/NaaclData/StanfordTagging/Webpages/"+i+".brackets.stanford";
        }
        BuildEvaluationFiles.buildEvaluationFile(webpagesGold,webpagedStanford, "Data/NaaclData/EvalFilesStanford/webpages", true);
        
    }
    
    public static String tagWithStanfordTagger(String s){
        String serializedClassifier = "StanfordNerModels/ner-eng-ie.crf-4-conll-distsim.ser.gz";      
        AbstractSequenceClassifier classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);

         
        String tagged=classifier.testStringInlineXML(s);
        tagged=stanfordXmlTags2Brackets(tagged);
                
        return tagged;	
    }
    
    public static void tagFile(String inFile,String outFile,boolean removeNewlineBreaks){
    	InFile in=new InFile(inFile);
    	StringBuffer buf=new StringBuffer();
    	String line=in.readLine();
    	while(line!=null){
    		buf.append(line);
    		if(!removeNewlineBreaks)
    			buf.append("\n");
    		else
    			buf.append(" ");
    		line=in.readLine();
    	}
    	in.close();
    	OutFile out=new OutFile(outFile);
    	out.println(tagWithStanfordTagger(buf.toString()));
    	out.close();
    }

	private static String stanfordXmlTags2Brackets(String taggedString) {
		String s=taggedString.replace("<LOCATION>", " [LOC ");
		s=s.replace("<ORGANIZATION>", " [ORG ");
		s=s.replace("<PERSON>", " [PER ");
		s=s.replace("<MISC>", " [MISC ");

		s=s.replace("</LOCATION>", " ] ");
		s=s.replace("</ORGANIZATION>", " ] ");
		s=s.replace("</PERSON>", " ] ");
		s=s.replace("</MISC>", " ] ");
		return s;
	}
	

}
