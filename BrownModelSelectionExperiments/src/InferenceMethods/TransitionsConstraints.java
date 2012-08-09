package InferenceMethods;

import LbjTagger.ParametersForLbjCode;
import LbjTagger.TextChunkRepresentationManager;

class TransitionsConstraints {
	
	public static boolean isLegalTransition(int prevStateId,int currentStateId,String[] possibleLabelNames)throws Exception{
		String prev=possibleLabelNames[prevStateId];
		String now=possibleLabelNames[currentStateId];
		return isLegalTransition(prev, now);
	}	
	
	public static boolean isLegalTransition(String prev,String now)throws Exception{
		if(ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.BIO)
			return isLegalTransitionBio(prev, now);
		if(ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.BILOU)
			return isLegalTransitionBilou(prev, now);
		if(ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.IOB1)
			return isLegalTransitionIob1(prev, now);
		if(ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.IOE1)
			return isLegalTransitionIoe1(prev, now);
		if(ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.IOE2)
			return isLegalTransitionIoe2(prev, now);
		System.out.println("Fatal error - unknown text chunks representation scheme");
		Exception e=new Exception("Fatal error - unknown text chunks representation scheme");
		throw e;
	}	
	
	private static boolean isLegalTransitionBio(String prevPrediction,String currentPrediction){
		if(prevPrediction==null||currentPrediction==null)
			return true;
		if(currentPrediction.startsWith("I-")){
			//Inside can follow only begin or inside of the same type
			if((!prevPrediction.startsWith("B-"))&&(!prevPrediction.startsWith("I-")))
				return false;
			if(!prevPrediction.substring(2).equals(currentPrediction.substring(2)))
				return false;			
		}
		return true;
	}

	private static boolean isLegalTransitionIob1(String prevPrediction,String currentPrediction){
		if(prevPrediction==null||currentPrediction==null)
			return true;
		if(currentPrediction.startsWith("B-")){
			//Begin can follow only begin or inside of the same type
			if((!prevPrediction.startsWith("B-"))&&(!prevPrediction.startsWith("I-")))
				return false;
			if(!prevPrediction.substring(2).equals(currentPrediction.substring(2)))
				return false;			
		}
		return true;
	}
	
	private static boolean isLegalTransitionIoe1(String prevPrediction,String currentPrediction){
		if(prevPrediction==null||currentPrediction==null)
			return true;
		if(prevPrediction.startsWith("E-")){
			//End can only precede end or inside of the same type
			if((!currentPrediction.startsWith("E-"))&&(!currentPrediction.startsWith("I-")))
				return false;
			if(!prevPrediction.substring(2).equals(currentPrediction.substring(2)))
				return false;			
		}
		return true;
	}
	
	private static boolean isLegalTransitionIoe2(String prevPrediction,String currentPrediction){
		if(prevPrediction==null||currentPrediction==null)
			return true;
		if(currentPrediction.startsWith("E-")){
			//End can only follow outside, end or inside of the same type
			if(prevPrediction.equals("O"))
				return true;	
			if(prevPrediction.startsWith("I-")&&
					prevPrediction.substring(2).equals(currentPrediction.substring(2)))
				return true;
			if(prevPrediction.startsWith("E-")&&
					prevPrediction.substring(2).equals(currentPrediction.substring(2)))
				return true;
			return false;
		}
		return true;
	}
	
	private static boolean isLegalTransitionBilou(String prevPrediction,String currentPrediction){
		if(prevPrediction==null||currentPrediction==null)
			return true;
		if(currentPrediction.startsWith("I-")){
			//Inside can follow only begin or inside of the same type
			if((!prevPrediction.startsWith("B-"))&&(!prevPrediction.startsWith("I-")))
				return false;
			if(!prevPrediction.substring(2).equals(currentPrediction.substring(2)))
				return false;			
		}
		if(currentPrediction.startsWith("L-")){
			//last can follow only Begin or inside of the same type
			if((!prevPrediction.startsWith("B-"))&&(!prevPrediction.startsWith("I-")))
				return false;
			if(!prevPrediction.substring(2).equals(currentPrediction.substring(2)))
				return false;			
		}
		if(currentPrediction.startsWith("U-")){
			if(prevPrediction.startsWith("B-"))
				return false;//the "B-" should be "U-" 
			if(prevPrediction.startsWith("I-"))
				return false;//the "I-" should be "L-" 
		}
		if(currentPrediction.startsWith("B-"))
		{
			if(prevPrediction.startsWith("B-"))
				return false;// the previous B- should be "U-"
			if(prevPrediction.startsWith("I-"))
				return false;// the previous I- should be "L-"
		}
		return true;
	}
}
