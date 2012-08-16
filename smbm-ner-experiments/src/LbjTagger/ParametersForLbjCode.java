package LbjTagger;

import java.util.*;


import IO.OutFile;

public class ParametersForLbjCode {
	public static enum  TokenizationScheme {LbjTokenizationScheme,DualTokenizationScheme};
	public static enum InferenceMethods {GREEDY,BEAMSEARCH,VITERBI};

	public static String configFilename=null;//this name must be unique foe each config file, and it will be appended to the model file names, so that we can automatically save the models at the right place
	public static int trainingIteration=0;

	public static String[] labelTypes= {"PER","ORG","LOC","MISC"};//will be initialized to something like {"PER","ORG","LOC","MISC"}; This is necessary for brackets file reader
	
	public static InferenceMethods inferenceMethod=InferenceMethods.GREEDY;
	public static int beamSize=5;
	public static boolean thresholdPrediction=false;
	public static double predictionConfidenceThreshold=-1;
	public static boolean logging = true;
	public static OutFile loggingFile=null;
	public static String  debuggingLogPath=null;
	
	public static boolean sortLexicallyFilesInFolders=true;
	public static boolean treatAllFilesInFolderAsOneBigDocument=false;
	public static boolean forceNewSentenceOnLineBreaks=false;
	public static boolean keepOriginalFileTokenizationAndSentenceSplitting=false;// this will not normalize the text in any way

	public static String pathToModelFile=null;
	public static TokenizationScheme tokenizationScheme=null;// should be either LbjTokenizationScheme or DualTokenizationScheme
	public static TextChunkRepresentationManager.EncodingScheme taggingEncodingScheme=null;// should be  BIO / BILOU/ IOB1/ IOE1/ IOE2
	
    public static RandomLabelGenerator patternLabelRandomGenerator = null;
    public static RandomLabelGenerator level1AggregationRandomGenerator = null;
	public static RandomLabelGenerator prevPredictionsLevel1RandomGenerator = null;
	public static RandomLabelGenerator prevPredictionsLevel2RandomGenerator = null;
	
	public static String pathToTrainDevTest = null; //Path to a folder that contains a 'Train', 'Dev' and 'Test' folder. Will evaluate Wordreps on that data split
		
	public static Hashtable<String,Boolean> featuresToUse=null;
	
}
