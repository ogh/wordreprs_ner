package ParsingProcessingData;

import IO.*;

import java.util.*;

import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.ParametersForLbjCode;

class BracketFileReader{
	public static Vector<LinkedVector> read(String fileName) throws Exception{
		System.out.println("Reading the file: "+fileName);
		String annotatedText=PlainTextReader.normalizeText(getFileText(fileName));
		return parseTextWithBrackets(annotatedText);
	}	
	public static Vector<LinkedVector> parseTextWithBrackets(String annotatedText) throws Exception{
		if(annotatedText.replace(" ", "").replace("\n", "").replace("\t", "").length()==0)
			return new Vector<LinkedVector>();
		Vector<String> bracketTokens=new Vector<String>();//can include newlines!!!!
		Vector<String> bracketTokensTags=new Vector<String>();
		parseBracketsAnnotatedText(annotatedText, bracketTokensTags, bracketTokens);
		StringBuffer buff=new StringBuffer(bracketTokens.size()*20);
		for(int i=0;i<bracketTokens.size();i++)
			buff.append(bracketTokens.elementAt(i)+" ");
		//the tokens below will have no newline characters.
		//System.out.println("Raw text: "+buff);
		Vector<Vector<String>> parsedTokens=PlainTextReader.sentenceSplitAndTokenizeText(buff.toString());
		//now we need to align the bracket tokens to the sentence split and tokenized tokens.
		//there are two issues to be careful with - 
		// 1) The bracket tokens may have newline characters as individual tokens, the others will not
		// 2) The tokenized/sentence split tokens may be bracket tokens broken into separate tokens.
		Vector<String> parsedTokensFlat=new Vector<String>();
		for(int i=0;i<parsedTokens.size();i++)
			for(int j=0;j<parsedTokens.elementAt(i).size();j++)
				parsedTokensFlat.addElement(parsedTokens.elementAt(i).elementAt(j));
		//System.out.println("----"+parsedTokensFlat.size());
		Vector<String> parsedTokensTagsFlat=new Vector<String>();//to be filled later
		StringBuffer bracketTokensText=new StringBuffer(bracketTokens.size()*20);
		StringBuffer parsedTokensText=new StringBuffer(parsedTokensFlat.size()*20);
		int bracketsTokensPos=0;
		int parsedTokensPos=0;
		while(bracketsTokensPos<bracketTokens.size()){
			while(bracketsTokensPos<bracketTokens.size()&&bracketTokens.elementAt(bracketsTokensPos).equals("\n"))
				bracketsTokensPos++;
			if(bracketsTokensPos<bracketTokens.size()){
				bracketTokensText.append(" "+bracketTokens.elementAt(bracketsTokensPos));
				String currentLabel=bracketTokensTags.elementAt(bracketsTokensPos);
				parsedTokensTagsFlat.addElement(currentLabel);
				parsedTokensText.append(" "+parsedTokensFlat.elementAt(parsedTokensPos));
				parsedTokensPos++;
				while((!bracketTokensText.toString().equals(parsedTokensText.toString()))&&parsedTokensPos<parsedTokensFlat.size()){
					if(currentLabel.startsWith("B-"))
						parsedTokensTagsFlat.addElement("I-"+currentLabel.substring(2));
					else
						parsedTokensTagsFlat.addElement(currentLabel);
					parsedTokensText.append(parsedTokensFlat.elementAt(parsedTokensPos));
					parsedTokensPos++;
				}
				if(!bracketTokensText.toString().equals(parsedTokensText.toString()))
					throw new Exception("Error alligning raw brackets tokens to token/sentence split tokens\nBrackets token text till now:\n"+bracketTokensText+"\nTokenized text till now:\n"+parsedTokensText);
				bracketsTokensPos++;				
			}
		}
		//ok, we're done, just building the output sentences
		Vector<LinkedVector> res=new Vector<LinkedVector>();
		parsedTokensPos=0;
		for(int i=0;i<parsedTokens.size();i++){
			LinkedVector sentence=new LinkedVector();
			for(int j=0;j<parsedTokens.elementAt(i).size();j++){
				NEWord.addTokenToSentence(sentence, parsedTokensFlat.elementAt(parsedTokensPos), parsedTokensTagsFlat.elementAt(parsedTokensPos));
				parsedTokensPos++;
			}
			res.addElement(sentence);
		}
		return res;
	}
	
	/*
	 * note that this one will do very little normalization/tokenization and token splitting.
	 * these fancy stuff is done after we get the brackets files tokens and tags.
	 * it is important however to keep the newline token to know where to split the sentences
	 * if we trust newlines as new sentence starts. 
	 */
	public static void parseBracketsAnnotatedText(String text,Vector<String> tags,Vector<String> words){
		text=text.replace("]", " ] ");
		for(int i=0;i<ParametersForLbjCode.labelTypes.length;i++)
			text=text.replace("["+ParametersForLbjCode.labelTypes[i], " ["+ParametersForLbjCode.labelTypes[i]+" ");
		

		Vector<String> tokens=new Vector<String>();
		text=PlainTextReader.normalizeText(text);
		StringTokenizer stLines=new StringTokenizer(text,"\n");
		while(stLines.hasMoreTokens())
		{
			String line=stLines.nextToken();
			StringTokenizer st=new StringTokenizer(line," \t");
			while(st.hasMoreTokens())
				tokens.addElement(st.nextToken());
			if(ParametersForLbjCode.forceNewSentenceOnLineBreaks||ParametersForLbjCode.keepOriginalFileTokenizationAndSentenceSplitting)
				tokens.addElement("\n");
		}
		for(int i=0;i<tokens.size();i++){
			boolean added=false;
			for(int labelType=0;labelType<ParametersForLbjCode.labelTypes.length;labelType++){
				if(tokens.elementAt(i).equals("["+ParametersForLbjCode.labelTypes[labelType])){
					i++;
					boolean first=true;
					while(!tokens.elementAt(i).equals("]")){
						words.addElement(tokens.elementAt(i));
						if(first){
							tags.addElement("B-"+ParametersForLbjCode.labelTypes[labelType]);
							first=false;
						}
						else{
							tags.addElement("I-"+ParametersForLbjCode.labelTypes[labelType]);						
						}
						i++;
					}
					added=true;
				}
			}
			if(!added){
				words.addElement(tokens.elementAt(i));
				tags.addElement("O");
			}				
		}//loopong on the tokens
	}//func -parseBracketsAnnotatedText



	
	private static String getFileText(String file){
		StringBuffer res=new StringBuffer(200000);
		InFile in=new InFile(file);
		String line=in.readLine();
		while(line!=null){
			res.append(line+"\n");
			line=in.readLine();
		}
		in.close();
		return res.toString();
	}
}
