package ParsingProcessingData;

import java.util.StringTokenizer;
import java.util.Vector;

import IO.InFile;
import LBJ2.nlp.Sentence;
import LBJ2.nlp.SentenceSplitter;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;

public class PlainTextReader {	
	public static Vector<LinkedVector> parsePlainTextFile(String file){
		InFile in=new InFile(file);
		String line=in.readLine();
		StringBuffer buf=new StringBuffer(100000);
		while(line!=null){
			buf.append(line+ " \n");
			line=in.readLine();
		}
		buf.append(" ");
		in.close();
		return parseText(normalizeText(buf.toString()));
	}

	public static Vector<LinkedVector> parseText(String text){
		Vector<Vector<String>> processed=sentenceSplitAndTokenizeText(text);
		Vector<LinkedVector> res=new Vector<LinkedVector>(); 
		for(int i=0;i<processed.size();i++){
			LinkedVector sentence=new LinkedVector();
			for(int j=0;j<processed.elementAt(i).size();j++)
				NEWord.addTokenToSentence(sentence, processed.elementAt(i).elementAt(j), "unlabeled");
			res.addElement(sentence);
		}
		return res;
	}


	public static Vector<Vector<String>> sentenceSplitAndTokenizeText(String text){
		text=normalizeText(text);
		Vector<String> sentences1=new Vector<String>();//sentences split by newlines. will keep just one element- the text if no sentence splitting on newlines is used...
		if(ParametersForLbjCode.forceNewSentenceOnLineBreaks||ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting){
			StringTokenizer st=new StringTokenizer(text,"\n");
			while(st.hasMoreTokens())
				sentences1.addElement(st.nextToken());
		}
		else
			sentences1.addElement(text);
		
		Vector<String> sentences2=new Vector<String>();//we add Lbj sentence splitting on top.
		if(!ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting){
			for(int i=0;i<sentences1.size();i++){
				SentenceSplitter parser=new SentenceSplitter(new String[]{sentences1.elementAt(i)});
				Sentence s=(Sentence)parser.next();
				while(s!=null){
					sentences2.addElement(s.text);
					s=(Sentence)parser.next();
				}
			}
		}
		else
			sentences2=sentences1;
		Vector<Vector<String>> res=new Vector<Vector<String>>();
		//tokenizing
		for(int i=0;i<sentences2.size();i++){
			String sentenceText=sentences2.elementAt(i);
			if(sentenceText.length()>0){
				//adding the space before the final period in the sentence, 
				//this is just a formatting issue with LBJ sentence splitter that can happen
				if(sentenceText.charAt(sentenceText.length()-1)=='.'&&
						!ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting)
					sentenceText=sentenceText.substring(0,sentenceText.length()-1)+" . ";
				//now tokenizing for real...
				StringTokenizer st=new StringTokenizer(sentenceText," \n\t");
				Vector<String> sentence=new Vector<String>();
				while(st.hasMoreTokens())
					sentence.addElement(st.nextToken());
				if(sentence.size()>0){
					//fixing a bug in LBJ sentence splitter if needed
					if((!ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting)&&
							sentence.size()==1&&res.size()>0&&
							(sentence.elementAt(0).equals("\"")||  
									sentence.elementAt(0).equals("''")||  
									sentence.elementAt(0).equals("'")))
						res.elementAt(res.size()-1).add(sentence.elementAt(0));
					else
						res.addElement(sentence);				  					
				}
			}				  
		}
		return res;
	}

	public static String normalizeText(String text){
		if(ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting)
			return text;
		StringBuffer buf=new StringBuffer((int)(text.length()*1.2));
		for(int i=0;i<text.length();i++){
			if(text.charAt(i)==',')
			{	
				if(i>0&&i<text.length()-1&&Character.isDigit(text.charAt(i-1))&&Character.isDigit(text.charAt(i+1)))
					buf.append(",");
				else
					buf.append(" , ");
			}
			else{
				buf.append(text.charAt(i));
			}
		}
		text=buf.toString().replace("\""," \" ");
		text=text.replace("`","'");
		text=text.replace("``","\"");
		text=text.replace("&quot;","\"");
		text=text.replace("&QUOT;","\"");
		text=text.replace("&amp;","&");
		text=text.replace("&AMP;","&");
		text=text.replace("&HT;"," ");
		text=text.replace("&ht;"," ");
		text=text.replace("&MD;","--");
		text=text.replace("&md;","--");
		text=text.replace("-LRB-","(");
		text=text.replace("-RRB-",")");
		text=text.replace("-LCB-","{");
		text=text.replace("-RCB-","}");
		text=text.replace("'nt"," 'nt ");
		text=text.replace("'s"," 's ");
		text=text.replace("'d"," 'd ");
		text=text.replace("'m"," 'm ");
		text=text.replace("'ve"," 've ");
		text=text.replace("``"," \" ");
		text=text.replace("''"," \" ");
		text=text.replace(";"," ; ");
		text=text.replace("]"," ] ");		
		//now, I want to replace all '[' by ' [ ', but I have to be careful with chunk markers!
		for(int i=0;i<ParametersForLbjCode.labelTypes.length;i++)
			text=text.replace("["+ParametersForLbjCode.labelTypes[i], "_START_"+ParametersForLbjCode.labelTypes[i]+"_");
		text=text.replace("["," [ ");
		for(int i=0;i<ParametersForLbjCode.labelTypes.length;i++)
			text=text.replace("_START_"+ParametersForLbjCode.labelTypes[i]+"_"," ["+ParametersForLbjCode.labelTypes[i]);
		text=text.replace(")"," ) ");
		text=text.replace("("," ( ");
		text=text.replace("{"," { ");
		text=text.replace("}"," } ");
		text=text.replace("?"," ? ");
		text=text.replace("!"," ! ");
		return text;
	}
}
