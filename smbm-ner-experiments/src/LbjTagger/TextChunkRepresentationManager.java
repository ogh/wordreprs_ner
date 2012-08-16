package LbjTagger;

import java.util.Vector;

import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord.LabelToLookAt;

public class TextChunkRepresentationManager {
	
	public enum EncodingScheme {BIO,IOB1,IOE1,IOE2,BILOU};

	public static void changeChunkRepresentation(EncodingScheme fromRep,EncodingScheme toRep,Vector<LinkedVector> data,LabelToLookAt labelType) {
		//Translate to BIO
		if(!fromRep.equals(EncodingScheme.BIO))
		{
			if(fromRep.equals(EncodingScheme.BILOU))
				Bilou2Bio(data, labelType);
			if(fromRep.equals(EncodingScheme.IOB1))
				IOB2Bio(data, labelType);
			if(fromRep.equals(EncodingScheme.IOE1))
				IOEa2Bio(data, labelType);
			if(fromRep.equals(EncodingScheme.IOE2))
				IOEb2Bio(data, labelType);
		}
		//Now translate from BIO to whatever format we want
		if(!toRep.equals(EncodingScheme.BIO))
		{
			if(toRep.equals(EncodingScheme.BILOU))
				Bio2Bilou(data, labelType);
			if(toRep.equals(EncodingScheme.IOB1))
				Bio2IOB(data, labelType);
			if(toRep.equals(EncodingScheme.IOE1))
				Bio2IOEa(data, labelType);
			if(toRep.equals(EncodingScheme.IOE2))
				Bio2IOEb(data, labelType);
		}
	}
	
	//-------------  BILOU -----------------
	
	private static void Bio2Bilou(Vector<LinkedVector> data,LabelToLookAt labelType) {
		for(int i=0;i<data.size();i++){
			LinkedVector v=data.elementAt(i);
			for(int j=0;j<v.size();j++){
				NEWord w=(NEWord)v.get(j);
				String label=w.getPrediction(labelType);
				if(label!=null&&!label.equalsIgnoreCase("O")){
					label=label.substring(2);
					NEWord prev=(NEWord)w.previous;
					NEWord next=(NEWord)w.next;
					String nextType="O";
					String nextLabel="O";
					String prevType="O";
					if(next!=null){
						nextType=next.getPrediction(labelType);
						nextLabel=next.getPrediction(labelType);
					}
					if(nextType.indexOf("-")>-1)
						nextType=nextType.substring(2);
					if(prev!=null)
						prevType=prev.getPrediction(labelType);
					if(prevType.indexOf("-")>-1)
						prevType=prevType.substring(2);
					if((!nextType.equalsIgnoreCase(label))&&(!prevType.equalsIgnoreCase(label)))
						w.setPrediction("U-"+label, labelType);
					else
						if(((!nextType.equalsIgnoreCase(label))||nextLabel.startsWith("B-"))&&(w.getPrediction(labelType).startsWith("B-")))
							w.setPrediction("U-"+label, labelType);
						else
							if((!nextType.equalsIgnoreCase(label))&&(prevType.equalsIgnoreCase(label)))
								w.setPrediction("L-"+label, labelType);
				}
			}
		}
	}
	private static void Bilou2Bio(Vector<LinkedVector> data,LabelToLookAt labelType) {
		for(int i=0;i<data.size();i++){
			LinkedVector v=data.elementAt(i);
			for(int j=0;j<v.size();j++){
				NEWord w=(NEWord)v.get(j);
				String label=w.getPrediction(labelType);
				if(!label.equalsIgnoreCase("O")){
					if(w.getPrediction(labelType).startsWith("U-"))
						w.setPrediction("B-"+label.substring(2), labelType);
					if(w.getPrediction(labelType).startsWith("L-"))
						w.setPrediction("I-"+label.substring(2), labelType);
				}
			}
		}	
	}
	
	
	//------------------- IOB1 ---------------------------
	private static void Bio2IOB(Vector<LinkedVector> data,LabelToLookAt labelType){
		for(int i=0;i<data.size();i++){
			LinkedVector v=data.elementAt(i);
			for(int j=0;j<v.size();j++){
				NEWord w=(NEWord)v.get(j);
				String label=w.getPrediction(labelType);
				if(label!=null&&!label.equalsIgnoreCase("O")){
					label=label.substring(2);
					NEWord prev=(NEWord)w.previous;
					String prevLabel="O";
					if(prev!=null)
						prevLabel=prev.getPrediction(labelType);
					if(prevLabel.indexOf("-")>-1)
						prevLabel=prevLabel.substring(2);
					if((!prevLabel.equals(label))&&w.getPrediction(labelType).startsWith("B-"))
						w.setPrediction("I-"+label, labelType);
				}
			}
		}
	}

	private static void IOB2Bio(Vector<LinkedVector> data,LabelToLookAt labelType) {
		for(int i=0;i<data.size();i++){
			LinkedVector v=data.elementAt(i);
			for(int j=0;j<v.size();j++){
				NEWord w=(NEWord)v.get(j);
				String label=w.getPrediction(labelType);
				if(label!=null&&!label.equalsIgnoreCase("O")){
					label=label.substring(2);
					NEWord prev=(NEWord)w.previous;
					String prevLabel="O";
					if(prev!=null)
						prevLabel=prev.getPrediction(labelType);
					if(prevLabel.indexOf("-")>-1)
						prevLabel=prevLabel.substring(2);
					if(!prevLabel.equalsIgnoreCase(label))
						w.setPrediction("B-"+label, labelType);
				}
			}
		}	
	}

	
	
	//----------------------- IOE1 -------------------
	
	private static void Bio2IOEa(Vector<LinkedVector> data,LabelToLookAt labelType){
		for(int i=0;i<data.size();i++){
			LinkedVector v=data.elementAt(i);
			for(int j=0;j<v.size();j++){
				NEWord w=(NEWord)v.get(j);
				String label=w.getPrediction(labelType);
				if(label!=null&&!label.equalsIgnoreCase("O")){
					String labelSuffix=label.substring(2);
					NEWord next=(NEWord)w.next;
					String nextLabel="O";
					String nextLabelSuffix="O";
					if(next!=null){
						nextLabel=next.getPrediction(labelType);
						nextLabelSuffix=nextLabel;
					}
					if(nextLabel.indexOf("-")>-1)
						nextLabelSuffix=nextLabel.substring(2);
					if((nextLabelSuffix.equals(labelSuffix))&&nextLabel.startsWith("B-"))
						w.setPrediction("E-"+labelSuffix, labelType);
					else
						if(label.startsWith("B-"))
							w.setPrediction("I-"+labelSuffix, labelType);

				}
			}
		}
	}

	private static void IOEa2Bio(Vector<LinkedVector> data,LabelToLookAt labelType) {
		for(int i=data.size()-1;i>=0;i--){
			LinkedVector v=data.elementAt(i);
			for(int j=v.size()-1;j>=0;j--){
				NEWord w=(NEWord)v.get(j);
				String label=w.getPrediction(labelType);
				if(label!=null&&!label.equalsIgnoreCase("O")){
					String labelSuffix=label.substring(2);
					NEWord prev=(NEWord)w.previous;
					String prevLabel="O";
					String prevLabelSuffix="O";
					if(prev!=null)
						prevLabel=prev.getPrediction(labelType);
					if(prevLabel.indexOf("-")>-1)
						prevLabelSuffix=prevLabel.substring(2);

					if((!prevLabelSuffix.equalsIgnoreCase(labelSuffix))||(prevLabel.startsWith("E-")))
						w.setPrediction("B-"+labelSuffix, labelType);
					if(w.getPrediction(labelType).startsWith("E-"))
						w.setPrediction("I-"+labelSuffix, labelType);
				}
			}
		}	
	}

	//----------------------- IOE2 -------------------
	
	private static void Bio2IOEb(Vector<LinkedVector> data,LabelToLookAt labelType) {
		for(int i=0;i<data.size();i++){
			LinkedVector v=data.elementAt(i);
			for(int j=0;j<v.size();j++){
				NEWord w=(NEWord)v.get(j);
				String label=w.getPrediction(labelType);
				if(label!=null&&!label.equalsIgnoreCase("O")){
					String labelSuffix=label.substring(2);
					NEWord next=(NEWord)w.next;
					String nextLabel="O";
					String nextLabelSuffix="O";
					if(next!=null){
						nextLabel=next.getPrediction(labelType);
						nextLabelSuffix=nextLabel;
					}
					if(nextLabel.indexOf("-")>-1)
						nextLabelSuffix=nextLabel.substring(2);

					if((!nextLabelSuffix.equals(labelSuffix))||nextLabel.startsWith("B-"))
						w.setPrediction("E-"+labelSuffix, labelType);
					else
						if(w.getPrediction(labelType).startsWith("B-"))
							w.setPrediction("I-"+labelSuffix, labelType);

				}
			}
		}
	}

	private static void IOEb2Bio(Vector<LinkedVector> data,LabelToLookAt labelType) {
		for(int i=data.size()-1;i>=0;i--){
			LinkedVector v=data.elementAt(i);
			for(int j=v.size()-1;j>=0;j--){
				NEWord w=(NEWord)v.get(j);
				String label=w.getPrediction(labelType);
				if(label!=null&&!label.equalsIgnoreCase("O")){
					String labelSuffix=label.substring(2);
					NEWord prev=(NEWord)w.previous;
					String prevLabel="O";
					String prevLabelSuffix="O";
					if(prev!=null){
						prevLabel=prev.getPrediction(labelType);
						prevLabelSuffix=prevLabel;
					}
					if(prevLabel.indexOf("-")>-1)
						prevLabelSuffix=prevLabel.substring(2);

					if((!prevLabelSuffix.equalsIgnoreCase(labelSuffix))||prevLabel.startsWith("E-"))
						w.setPrediction("B-"+labelSuffix, labelType);
					if(w.getPrediction(labelType).startsWith("E-"))
						w.setPrediction("I-"+labelSuffix, labelType);
				}
			}
		}	
	}
}
