package InferenceMethods;

import LBJ2.classify.*;
import LBJ2.learn.*;
import StringStatisticsUtils.CharacteristicWords;

class PredictionsToProbabilities
{
	public static CharacteristicWords getPredictionLogConfidences(SparseNetworkLearner c,Object o){
		Score[] scores= c.scores(o).toArray();
		//System.out.println(c.scores(o));
		double[] correctedScores=new double[scores.length];
		double min=scores[0].score;
		for(int i=0;i<scores.length;i++)
			if(min>scores[i].score)
				min=scores[i].score;
		for(int i=0;i<scores.length;i++)
			correctedScores[i]=scores[i].score-min;
		double sum=0;
		for(int i=0;i<correctedScores.length;i++)
		{			  
			correctedScores[i]=Math.exp(correctedScores[i]);
			sum+=correctedScores[i];
		}
		for(int i=0;i<correctedScores.length;i++)
			correctedScores[i]/=sum;
		for(int i=0;i<correctedScores.length;i++)
			correctedScores[i]=Math.log(correctedScores[i]);
		CharacteristicWords res=new CharacteristicWords(scores.length);
		for(int i=0;i<scores.length;i++)
			res.addElement(scores[i].value, correctedScores[i]);
		return res;
	}
	public static CharacteristicWords getPredictionConfidences(SparseNetworkLearner c,Object o){
		Score[] scores= c.scores(o).toArray();
		//System.out.println(c.scores(o));
		double[] correctedScores=new double[scores.length];
		double min=scores[0].score;
		for(int i=0;i<scores.length;i++)
			if(min>scores[i].score)
				min=scores[i].score;
		for(int i=0;i<scores.length;i++)
			correctedScores[i]=scores[i].score-min;
		double sum=0;
		for(int i=0;i<correctedScores.length;i++)
		{			  
			correctedScores[i]=Math.exp(correctedScores[i]);
			sum+=correctedScores[i];
		}
		for(int i=0;i<correctedScores.length;i++)
			correctedScores[i]/=sum;
		for(int i=0;i<correctedScores.length;i++)
			correctedScores[i]=correctedScores[i];
		CharacteristicWords res=new CharacteristicWords(scores.length);
		for(int i=0;i<scores.length;i++)
			res.addElement(scores[i].value, correctedScores[i]);
		return res;
	}
}

