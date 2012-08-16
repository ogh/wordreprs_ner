package StringStatisticsUtils;

import java.util.Vector;


public class MemoryEfficientHashtable<T1,T2> {
	public Vector<T1> keys=new Vector<T1>();
	public Vector<T2> values=new Vector<T2>();
	
	public MemoryEfficientHashtable(int size){
		keys=new Vector<T1>();
		values=new Vector<T2>();
	}
	
	public T2 get(T1 key){
		for(int i=0;i<keys.size();i++)
			if(keys.elementAt(i).equals(key))
				return values.elementAt(i);
		return null;
	}
	
	public boolean containsKey(T1 key){
		for(int i=0;i<keys.size();i++)
			if(keys.elementAt(i).equals(key))
				return true;
		return false;
	}

	public void put(T1 key,T2 value){
		keys.addElement(key);
		values.addElement(value);
	}
	
	public void remove(T1 key){
		int idx=-1;
		for(int i=0;i<keys.size();i++)
			if(keys.elementAt(i).equals(key))
				idx=i;
		keys.removeElementAt(idx);
		values.removeElementAt(idx);
	}	
}
