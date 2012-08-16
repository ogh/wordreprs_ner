package DebuggingAndTesting;

import java.util.*;


import ExpressiveFeatures.ExpressiveFeaturesAnnotator;
import ExpressiveFeatures.GlobalFeatures;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;
import LbjTagger.Parameters;
import ParsingProcessingData.TaggedDataReader;

public class ContextFeaturesDebugger {
    public static void main(String[] args) throws Exception{
	Parameters.readConfigAndLoadExternalData("Config/contextAggregation.config");

	Vector<LinkedVector> data=TaggedDataReader.readData("Data/GoldData/Reuters/BIO.testa", "-c");
	ExpressiveFeaturesAnnotator.annotate(data);
	for(int i=0;i<data.size();i++)
	    for(int j=0;j<data.elementAt(i).size();j++){
		NEWord w=(NEWord)data.elementAt(i).get(j);
		GlobalFeatures.annotate(w);
		System.out.println(w.form);

		for(Iterator<String> iter=w.nonLocalFeatures.keySet().iterator();iter.hasNext();)
		{
		    String s=iter.next();
		    Integer val=w.nonLocalFeatures.get(s);
		    System.out.println("\t"+s+"\t"+val);
		}
	    }
	
    }
}
