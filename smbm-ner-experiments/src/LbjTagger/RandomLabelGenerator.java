package LbjTagger;

import java.util.Random;

public class RandomLabelGenerator {
	public static  String[] labelTypes=null; //will be initialized to something like: {"O","PER","ORG","LOC","MISC"};
	public static  String[] labelNames=null; //will be initialized to something like: {"O","B-PER","I-PER","B-LOC","I-LOC","B-ORG","I-ORG","B-MISC","I-MISC"};

	public static final double noiseLevel=0.1;//this is the noise that we put into label aggregation feature for previous predictions and for level2; set this value to 0 to eliminate any noise 
	public static final int randomizationSeed=7;
	
	private Random rand=null;
	
	public RandomLabelGenerator(){
		rand=new Random(randomizationSeed);
		labelTypes=new String[ParametersForLbjCode.labelTypes.length+1];
		labelTypes[0]="O";
		for(int i=0;i<ParametersForLbjCode.labelTypes.length;i++)
			labelTypes[i+1]=ParametersForLbjCode.labelTypes[i];
		//now dealing with label names
		if(ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.BIO
				||ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.IOB1){
			labelNames=new String[ParametersForLbjCode.labelTypes.length*2+1];
			labelNames[0]="O";
			for(int i=0;i<ParametersForLbjCode.labelTypes.length;i++){
				labelNames[2*i+1]="B-"+ParametersForLbjCode.labelTypes[i];
				labelNames[2*i+2]="I-"+ParametersForLbjCode.labelTypes[i];
			}
		}
		if(ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.IOE1
				||ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.IOE2){
			labelNames=new String[ParametersForLbjCode.labelTypes.length*2+1];
			labelNames[0]="O";
			for(int i=0;i<ParametersForLbjCode.labelTypes.length;i++){
				labelNames[2*i+1]="E-"+ParametersForLbjCode.labelTypes[i];
				labelNames[2*i+2]="I-"+ParametersForLbjCode.labelTypes[i];
			}
		}
		if(ParametersForLbjCode.taggingEncodingScheme==TextChunkRepresentationManager.EncodingScheme.BILOU){
			labelNames=new String[ParametersForLbjCode.labelTypes.length*4+1];
			labelNames[0]="O";
			for(int i=0;i<ParametersForLbjCode.labelTypes.length;i++){
				labelNames[4*i+1]="B-"+ParametersForLbjCode.labelTypes[i];
				labelNames[4*i+2]="I-"+ParametersForLbjCode.labelTypes[i];
				labelNames[4*i+3]="L-"+ParametersForLbjCode.labelTypes[i];
				labelNames[4*i+4]="U-"+ParametersForLbjCode.labelTypes[i];
			}
		}
	}
	
	public  boolean useNoise(){
		return rand.nextDouble()<noiseLevel;
	}

	public  String randomLabel(){
		int pos=(int)(rand.nextDouble()*labelNames.length);
		if(pos>=labelNames.length)
			pos=labelNames.length-1;
		return labelNames[pos];
	}
	public  String randomType(){
		int pos=(int)(rand.nextDouble()*labelTypes.length);
		if(pos>=labelTypes.length)
			pos=labelTypes.length-1;
		return labelTypes[pos];
	}
}
