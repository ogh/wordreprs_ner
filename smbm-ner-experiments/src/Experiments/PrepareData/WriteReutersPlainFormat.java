package Experiments.PrepareData;

import java.io.File;
import java.util.Vector;

import LBJ2.parse.LinkedVector;
import LbjTagger.Parameters;
import ParsingProcessingData.*;

public class WriteReutersPlainFormat {
	public static void main(String[] args) throws Exception {
		Parameters.readConfigAndLoadExternalData("Config/FeaturesUtility/baselineBilou.config");
		String[] fileSets={"Dev","Test","Train"};
		for(int fileSet=0;fileSet<fileSets.length;fileSet++){
			//write the Reuters column files into brackets files		
			String folder="Data/GoldData/Reuters/ColumnFormat/"+fileSets[fileSet];
			String[] files=(new File(folder)).list();
			for(int i=0;i<files.length;i++)
				if(!files[i].startsWith(".")&&((new File(folder+"/"+files[i])).isFile()))
				{
					Vector<LinkedVector> data=TaggedDataReader.readFile(folder+"/"+files[i],"-c");
					PlainTextWriter.write(data,"Data/RawData/Reuters/"+fileSets[fileSet]+"/"+files[i]);
				}
		}		
	}
}
