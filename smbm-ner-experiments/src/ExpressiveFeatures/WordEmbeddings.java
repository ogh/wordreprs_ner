package ExpressiveFeatures;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;


import IO.InFile;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;

/*
 * This class is a legacy class, I keep it to make the current code compatible with the models I've trained in the past
 */
public class WordEmbeddings {
	
	public  enum NormalizationMethod {INDEPENDENT,OVERALL};
	
	public static Vector<Boolean> isLowecasedEmbeddingByResource=null;
	public static Vector<Boolean> isUppercasedEmbeddingByResource=null;
	public static Vector<String> resources=null;
	public static Vector<Integer> dimensionalities=null;
	public static int dimensionalitiesSum=0;
	public static Vector<HashMap<String,double[]>> embeddingByResource=null;

	/*
	 * For now, the parameter minWordAppearanceThres is not used, but I'm planning to use it
	 * like I was using the word appearance thresholds on Brown Clusters
	 */
	public static void init(Vector<String> filenames,Vector<Integer> embeddingDimensionality,Vector<Integer> minWordAppearanceThres,Vector<Boolean> isLowecasedEmbedding,Vector<Boolean> isUppercasedEmbedding,Vector<Double> normalizationConstant,Vector<NormalizationMethod> methods) throws Exception{
		dimensionalitiesSum=0;
		dimensionalities=new Vector<Integer>();
		resources=new Vector<String>();
		embeddingByResource=new Vector<HashMap<String,double[]>>();
		isLowecasedEmbeddingByResource=new Vector<Boolean>();
		isUppercasedEmbeddingByResource=new Vector<Boolean>();
		for(int resourceId=0;resourceId<filenames.size();resourceId++){
			HashMap<String, double[]> embedding = new HashMap<String,double[]>();
			InFile in=new InFile(filenames.elementAt(resourceId));
			String line=in.readLine();
			double maxAbsValueInAnyDimension=0;
			while(line!=null){
				StringTokenizer st=new StringTokenizer(line," ");
				String token=st.nextToken();
				Vector<String> v=new Vector<String>();
				while(st.hasMoreTokens())
					v.addElement(st.nextToken());
				if(v.size()!=embeddingDimensionality.elementAt(resourceId))
					throw new Exception("Warning: unexpected dimensionality of "+v.size()+" for token "+token);
				double[] arr=new double[v.size()];
				double maxInThisDimension=0;
				for(int i=0;i<arr.length;i++){
					arr[i]=Double.parseDouble(v.elementAt(i));
					if(maxAbsValueInAnyDimension<Math.abs(arr[i]))
						maxAbsValueInAnyDimension=Math.abs(arr[i]);
					if(maxInThisDimension<Math.abs(arr[i]))
						maxInThisDimension=Math.abs(arr[i]);
				}
				if(maxInThisDimension>0&&methods.elementAt(resourceId).equals(NormalizationMethod.INDEPENDENT))
					for(int i=0;i<arr.length;i++)
						arr[i]=arr[i]/(normalizationConstant.elementAt(resourceId)*maxInThisDimension);			
				embedding.put(token,arr);
				line=in.readLine();
			}
			in.close();
			if(maxAbsValueInAnyDimension>0&&methods.elementAt(resourceId).equals(NormalizationMethod.OVERALL))
				for(Iterator<String> i=embedding.keySet().iterator();i.hasNext();){
					double[] arr=embedding.get(i.next());
					for(int j=0;j<arr.length;j++)
						arr[j]=arr[j]/(normalizationConstant.elementAt(resourceId)*maxAbsValueInAnyDimension);			
			}
			embeddingByResource.addElement(embedding);
			dimensionalitiesSum+=embeddingDimensionality.elementAt(resourceId);
			dimensionalities.addElement(embeddingDimensionality.elementAt(resourceId));
			resources.addElement(filenames.elementAt(resourceId));
			isLowecasedEmbeddingByResource.addElement(isLowecasedEmbedding.elementAt(resourceId));
			isUppercasedEmbeddingByResource.addElement(isUppercasedEmbedding.elementAt(resourceId));
		}
	}
	
	public static double[] getEmbedding(NEWord w){
		double[] res=new double[dimensionalitiesSum];
		int pos=0;
		for(int resourceId=0;resourceId<embeddingByResource.size();resourceId++){
			String word=w.form;
			if(isLowecasedEmbeddingByResource.elementAt(resourceId))
				word=word.toLowerCase();
            if(isUppercasedEmbeddingByResource.elementAt(resourceId))
                word=word.toUpperCase();
			double[] v=new double[dimensionalities.elementAt(resourceId)];
			for(int i=0;i<v.length;i++)
				v[i]=0;
			HashMap<String, double[]> embedding = embeddingByResource.elementAt(resourceId);
			if(embedding.containsKey(word))
				v=embedding.get(word);
			else{
				if(embedding.containsKey("*UNKNOWN*"))
					v=embedding.get("*UNKNOWN*");
				else
					if(embedding.containsKey("*unknown*"))
						v=embedding.get("*unknown*");
			}
			for(int i=0;i<v.length;i++){
				res[pos]=v[i];
				pos++;
			}
		}
		return res;
	}
		
	public static void printOovData(Vector<LinkedVector> data){
		int totalTokens=0;
		HashMap<String,Boolean> tokensHash=new HashMap<String,Boolean>();
		HashMap<String,Boolean> tokensHashIC=new HashMap<String,Boolean>();
		for(int i=0;i<data.size();i++)
			for(int j=0;j<data.elementAt(i).size();j++)
			{
				String form=((NEWord)data.elementAt(i).get(j)).form;
				totalTokens++;
				tokensHash.put(form,true);
				tokensHashIC.put(form.toLowerCase(),true);
			}
		System.out.println("Data statistics:");
		System.out.println("\t\t- Total tokens with repetitions ="+ totalTokens);
		System.out.println("\t\t- Total unique tokens  ="+ tokensHash.size());
		System.out.println("\t\t- Total unique tokens ignore case ="+ tokensHashIC.size());
		for(int resourceId=0;resourceId<resources.size();resourceId++){
			HashMap<String, double[]> embedding = embeddingByResource.elementAt(resourceId);
			System.out.println("\t* OOV statistics for the resource: "+resources.elementAt(resourceId)+"(covers "+embedding.size()+" unique tokens)");
			int oovCaseSensitive=0;
			int oovAfterLowercasing=0;
			HashMap<String,Boolean> oovCaseSensitiveHash=new HashMap<String, Boolean>();
			HashMap<String,Boolean> oovAfterLowercasingHash=new HashMap<String, Boolean>();
			for(int i=0;i<data.size();i++)
				for(int j=0;j<data.elementAt(i).size();j++)
				{
					String form=((NEWord)data.elementAt(i).get(j)).form;
					if(!embedding.containsKey(form)){
						oovCaseSensitive++;
						oovCaseSensitiveHash.put(form, true);
					}
					if((!embedding.containsKey(form))&&(!embedding.containsKey(form.toLowerCase()))){
						oovAfterLowercasing++;
						oovAfterLowercasingHash.put(form.toLowerCase(), true);
					}
				}
			System.out.println("\t\t- Total OOV tokens, Case Sensitive ="+ oovCaseSensitive);
			System.out.println("\t\t- OOV tokens, no repetitions, Case Sensitive ="+ oovCaseSensitiveHash.size());
			System.out.println("\t\t- Total OOV tokens even after lowercasing  ="+ oovAfterLowercasing);
			System.out.println("\t\t- OOV tokens even after lowercasing, no repetition  ="+ oovAfterLowercasingHash.size());
		}	
	}
}
