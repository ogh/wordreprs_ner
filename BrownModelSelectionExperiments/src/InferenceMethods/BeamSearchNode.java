package InferenceMethods;

import LbjTagger.NEWord;

class BeamSearchNode {
	int idx=0;//the index of the element in the beam (much like an index in a list), starts with 0
	NEWord w=null;//the word this beam node corresponds to.
	String label=null;//the label assigned to the current node
	double logProbCurrentPrediction=0;//the log probability for predicting the label
	double logProbSequencePrediction=0;//the log probability of the entire beam up till now, including now
	BeamSearchNode prev=null;
}
