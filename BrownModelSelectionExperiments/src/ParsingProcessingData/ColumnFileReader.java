package ParsingProcessingData;


import java.util.Vector;

import LBJ2.nlp.*;
import LBJ2.parse.LinkedVector;
import LbjTagger.NEWord;


class ColumnFileReader extends ColumnFormat
{
	String filename=null;
	public ColumnFileReader(String file) { super(file);filename=file; }

	public Object next()
	{	 
		// System.out.println("next");
		String[] line = (String[]) super.next();
		while (line != null && (line.length == 0 || line[4].equals("-X-")))
			line = (String[]) super.next();
		if (line == null) return null;

		LinkedVector res=new LinkedVector(); 
		NEWord w=new NEWord(new Word(line[5], line[4]), null, line[0]);
		NEWord.addTokenToSentence(res, w.form, w.neLabel);

		for (line = (String[]) super.next(); line != null && line.length > 0;
		line = (String[]) super.next())
		{
			w=new NEWord(new Word(line[5], line[4]), null, line[0]);
			NEWord.addTokenToSentence(res, w.form, w.neLabel);
		}
		if(res.size()==0)
			return null;

		return res;
	}

	public Vector<LinkedVector> read(){
		System.out.println("Reading  the file: "+fileName);
		Vector<LinkedVector> res=new Vector<LinkedVector>();
		for (LinkedVector vector = (LinkedVector) this.next(); vector != null; vector = (LinkedVector) this.next())
			res.addElement(vector);
		return res;
	}
}

