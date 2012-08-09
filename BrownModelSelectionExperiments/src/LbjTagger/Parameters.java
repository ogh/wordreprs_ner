package LbjTagger;

import java.util.*;


import ExpressiveFeatures.BrownClusters;
import ExpressiveFeatures.Gazzetteers;
import ExpressiveFeatures.WordEmbeddings;
import ExpressiveFeatures.WordEmbeddings.NormalizationMethod;
import IO.InFile;
import IO.OutFile;

public class Parameters extends ParametersForLbjCode{
	
	public static void readConfigAndLoadExternalData(String configFile) throws Exception{
		InFile in=new InFile(configFile);

		//reading the next parameter...
		String line=in.readLine();
		StringTokenizer st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("configFilename"))
			throw new Exception("Error- expecting the 'configFilename' parameter, read:"+line);
		configFilename=st.nextToken();

		
		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("sortLexicallyFilesInFolders"))
			throw new Exception("Error- expecting the 'sortLexicallyFilesInFolders' parameter, read:"+line);
		sortLexicallyFilesInFolders=Boolean.parseBoolean(st.nextToken());

		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("treatAllFilesInFolderAsOneBigDocument"))
			throw new Exception("Error- expecting the 'treatAllFilesInFolderAsOneBigDocument' parameter, read:"+line);
		treatAllFilesInFolderAsOneBigDocument=Boolean.parseBoolean(st.nextToken());
		
		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("inferenceMethod"))
			throw new Exception("Error- expecting the 'inferenceMethod' parameter, read:"+line);
		inferenceMethod=ParametersForLbjCode.InferenceMethods.valueOf(st.nextToken());

		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("beamSize"))
			throw new Exception("Error- expecting the 'beamSize' parameter, read:"+line);
		beamSize=Integer.parseInt(st.nextToken());		
		
		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("thresholdPrediction"))
			throw new Exception("Error- expecting the 'thresholdPrediction' parameter, read:"+line);
		thresholdPrediction=Boolean.parseBoolean(st.nextToken());		
		
		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("predictionConfidenceThreshold"))
			throw new Exception("Error- expecting the 'predictionConfidenceThreshold' parameter, read:"+line);
		predictionConfidenceThreshold=Double.parseDouble(st.nextToken());		

		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("labelTypes"))
			throw new Exception("Error- expecting the 'labelTypes' parameter, read:"+line);
		Vector<String> types=new Vector<String>();
		while(st.hasMoreTokens())
			types.addElement(st.nextToken());
		labelTypes=new String[types.size()];
		for(int i=0;i<labelTypes.length;i++)
			labelTypes[i]=types.elementAt(i);

		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("logging"))
			throw new Exception("Error- expecting the 'logging' parameter, read:"+line);
		logging=Boolean.parseBoolean(st.nextToken());

		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("debuggingLogPath"))
			throw new Exception("Error- expecting the 'debuggingLogPath' parameter, read:"+line);
		debuggingLogPath=st.nextToken()+"/"+configFilename+"debugLog.txt";
		if(logging)
			loggingFile=new OutFile(debuggingLogPath);
		
		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("taggingEncodingScheme"))
			throw new Exception("Error- expecting the 'taggingEncodingScheme' parameter, read:"+line);
		String taggingEncodingSchemeString=st.nextToken();
		taggingEncodingScheme=TextChunkRepresentationManager.EncodingScheme.valueOf(taggingEncodingSchemeString);
		

		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("pathToGazetteers"))
			throw new Exception("Error- expecting the 'pathToGazetteers' parameter, read:"+line);
		String pathToGazetteers=st.nextToken();

		//reading the next parameter...
		Vector<String> pathsToBrownClusters=new Vector<String>();
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("pathsToBrownClusters"))
			throw new Exception("Error- expecting the 'pathsToBrownClusters' parameter, read:"+line);
		while(st.hasMoreTokens())
			pathsToBrownClusters.addElement(st.nextToken());

		//reading the next parameter...
		Vector<Integer> minWordAppThresholdsForBrownClusters=new Vector<Integer>();
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("minWordAppThresholdsForBrownClusters"))
			throw new Exception("Error- expecting the 'minWordAppThresholdsForBrownClusters' parameter, read:"+line);
		while(st.hasMoreTokens())
			minWordAppThresholdsForBrownClusters.addElement(Integer.parseInt(st.nextToken()));

		//reading the next parameter...
		Vector<Boolean> lowercaseBrown=new Vector<Boolean>();
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("isLowercaseBrownClusters"))
			throw new Exception("Error- expecting the 'isLowercaseBrownClusters' parameter, read:"+line);
		while(st.hasMoreTokens())
			lowercaseBrown.addElement(Boolean.parseBoolean(st.nextToken()));

		//reading the next parameter...
		Vector<String> pathsToWordEmbeddings=new Vector<String>();
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("pathsToWordEmbeddings"))
			throw new Exception("Error- expecting the 'pathsToWordEmbeddings' parameter, read:"+line);
		while(st.hasMoreTokens())
			pathsToWordEmbeddings.addElement(st.nextToken());

		//reading the next parameter...
		Vector<Integer> dimensionality=new Vector<Integer>();
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("embeddingDimensionalities"))
			throw new Exception("Error- expecting the 'embeddingDimensionalities' parameter, read:"+line);
		while(st.hasMoreTokens())
			dimensionality.addElement(Integer.parseInt(st.nextToken()));

		//reading the next parameter...
		Vector<Integer> wordAppThresEmbeddings=new Vector<Integer>();
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("minWordAppThresholdsForEmbeddings"))
			throw new Exception("Error- expecting the 'minWordAppThresholdsForEmbeddings' parameter, read:"+line);
		while(st.hasMoreTokens())
			wordAppThresEmbeddings.addElement(Integer.parseInt(st.nextToken()));

		//reading the next parameter...
		Vector<Double> normalizationConstantsForEmbeddings=new Vector<Double>();
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("normalizationConstantsForEmbeddings"))
			throw new Exception("Error- expecting the 'normalizationConstantsForEmbeddings' parameter, read:"+line);
		while(st.hasMoreTokens())
			normalizationConstantsForEmbeddings.addElement(Double.parseDouble(st.nextToken()));
		
		//reading the next parameter...
		Vector<NormalizationMethod> normalizationMethodsForEmbeddings=new Vector<NormalizationMethod>();
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("normalizationMethodsForEmbeddings"))
			throw new Exception("Error- expecting the 'normalizationMethodsForEmbeddings' parameter, read:"+line);
		while(st.hasMoreTokens())
			normalizationMethodsForEmbeddings.addElement(NormalizationMethod.valueOf(st.nextToken()));


		//reading the next parameter...
		Vector<Boolean> isLowercaseWordEmbeddings=new Vector<Boolean>();
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("isLowercaseWordEmbeddings"))
			throw new Exception("Error- expecting the 'isLowercaseWordEmbeddings' parameter, read:"+line);
		while(st.hasMoreTokens())
			isLowercaseWordEmbeddings.addElement(Boolean.parseBoolean(st.nextToken()));

		
		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("pathToModelFile"))
			throw new Exception("Error- expecting the 'pathToModelFile' parameter, read:"+line);
		pathToModelFile=st.nextToken()+"/"+configFilename+".model";

		//reading the next parameter...
		line=in.readLine();
		st=new StringTokenizer(line,"\t ");
		if(!st.nextToken().equals("tokenizationScheme"))
			throw new Exception("Error- expecting the 'tokenizationScheme' parameter, read:"+line);
		tokenizationScheme=TokenizationScheme.valueOf(st.nextToken());

		featuresToUse=new Hashtable<String,Boolean>();
		line=in.readLine();
		while(line!=null){
			st=new StringTokenizer(line,"\t ");
			String feature=st.nextToken();
			if(st.nextToken().equals("1")){
				System.out.println("Adding feature: "+feature);
				featuresToUse.put(feature,true);
			}
			line=in.readLine();
		}
		in.close();
		in.close();

		System.out.println("Working parameters are:");
		System.out.println("inferenceMethod="+ParametersForLbjCode.inferenceMethod.toString());
		System.out.println("beamSize="+ParametersForLbjCode.beamSize);
		System.out.println("thresholdPrediction="+ParametersForLbjCode.thresholdPrediction);
		System.out.println("predictionConfidenceThreshold="+ParametersForLbjCode.predictionConfidenceThreshold);		
		System.out.println("labelTypes");
		for(int i=0;i<labelTypes.length;i++)
			System.out.print("\t"+labelTypes[i]);
		System.out.println("\nlogging="+logging);		
		System.out.println("debuggingLogPath="+debuggingLogPath);
		System.out.println("forceNewSentenceOnLineBreaks="+forceNewSentenceOnLineBreaks);
		System.out.println("keepOriginalFileTokenizationAndSentenceSplitting="+keepOriginalFileTokenizationAndSentenceSplitting);
		System.out.println("taggingScheme="+ParametersForLbjCode.taggingEncodingScheme.toString());
		System.out.println("tokenizationScheme="+ParametersForLbjCode.tokenizationScheme.toString());
		System.out.println("pathToModelFile="+ParametersForLbjCode.pathToModelFile);
		if(Parameters.featuresToUse.containsKey("BrownClusterPaths")){
			for(int i=0;i<pathsToBrownClusters.size();i++){
				System.out.println("Brown clusters resource: ");
				System.out.println("\t-Path: "+pathsToBrownClusters.elementAt(i));
				System.out.println("\t-WordThres="+minWordAppThresholdsForBrownClusters.elementAt(i));
				System.out.println("\t-IsLowercased="+lowercaseBrown.elementAt(i));
			}
		}
		if(Parameters.featuresToUse.containsKey("WordEmbeddings")){
			for(int i=0;i<pathsToWordEmbeddings.size();i++){
				System.out.println("Words Embeddings resource: ");
				System.out.println("\t-Path: "+pathsToWordEmbeddings.elementAt(i));
				System.out.println("\t-Dimensionality="+dimensionality.elementAt(i));
				System.out.println("\t-WordThres="+wordAppThresEmbeddings.elementAt(i));
				System.out.println("\t-IsLowercased="+isLowercaseWordEmbeddings.elementAt(i));
			}			
		}
		
		
		if(Parameters.featuresToUse.containsKey("BrownClusterPaths"))
			BrownClusters.init(pathsToBrownClusters,minWordAppThresholdsForBrownClusters,lowercaseBrown);
		if(Parameters.featuresToUse.containsKey("WordEmbeddings"))
			WordEmbeddings.init(pathsToWordEmbeddings,dimensionality,wordAppThresEmbeddings,isLowercaseWordEmbeddings,normalizationConstantsForEmbeddings,normalizationMethodsForEmbeddings);
		if(Parameters.featuresToUse.containsKey("GazetteersFeatures"))
			Gazzetteers.init(pathToGazetteers);
		
		
		//don't forget that these should be initialized only after we know the target labels and the encoding scheme
		patternLabelRandomGenerator=new RandomLabelGenerator();
		level1AggregationRandomGenerator=new RandomLabelGenerator();
		prevPredictionsLevel1RandomGenerator=new RandomLabelGenerator();
		prevPredictionsLevel2RandomGenerator=new RandomLabelGenerator();
	}	
}
