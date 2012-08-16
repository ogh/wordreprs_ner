package ExpressiveFeatures;

import java.util.Vector;

import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.Parameters;

public class ExpressiveFeaturesAnnotator {

	/*
	 * Do not worry about the brown clusters and word embeddings, 
	 * this stuff is added on the fly in the .lbj feature generators... 
	 */
	public static void annotate(Vector<LinkedVector> res){
		System.out.println("Annotating the data with expressive features...");
		
		if(Parameters.featuresToUse.containsKey("BrownClusterPaths")){
			System.out.println("Brown clusters OOV statistics:");
			BrownClusters.printOovData(res);
		}		
		if(Parameters.featuresToUse.containsKey("WordEmbeddings")){
			System.out.println("Word Embeddings OOV statistics:");
			WordEmbeddings.printOovData(res);
		}
	    //annotating with Gazzetteers;
	    for(int i=0;i<res.size();i++){
	    	LinkedVector vector=res.elementAt(i);
			if(Parameters.featuresToUse!=null){
	  			if(Parameters.featuresToUse.containsKey("GazetteersFeatures")){
	  				for(int j=0;j<vector.size();j++)
	  					Gazzetteers.annotate((NEWord)vector.get(j));
	  			}
			}
  		}
	    //annotating the nonlocal features;
	    for(int i=0;i<res.size();i++)
	    	for(int j=0;j<res.elementAt(i).size();j++)
	    		GlobalFeatures.annotate((NEWord)res.elementAt(i).get(j));
	    

		System.out.println("Done Annotating the data with expressive features...");
   }
}
