package Experiments.NaaclExperiments;

import LbjTagger.NETagPlain;
import LbjTagger.Parameters;
import LbjTagger.ParametersForLbjCode;

public class AnnotateWebpagesWithLbjNerTagger {
	public static void main(String[] args) throws Exception {
		ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
		String[] configFiles={"Config/baselineFeatures.config",
				"Config/allLayer1.config",
				"Config/level1AndPatterns.config",
				"Config/allFeatures.config"};
		String[] suffixes={".brackets.lbjNer.baseline",
				".brackets.lbjNer.allLevel1",
				".brackets.lbjNer.level1AndPatterns",
				".brackets.lbjNer.allFeatures"};
		for(int confId=0;confId<configFiles.length;confId++){
			Parameters.readConfigAndLoadExternalData(configFiles[confId]);
			for(int i=1;i<=20;i++)
				NETagPlain.tagData("Data/RawData/Webpages/"+i, "Data/TaggedData/Webpages/"+i+suffixes[confId],false);
		}
	}

}
