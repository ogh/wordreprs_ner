package StringStatisticsUtils;
import java.util.Date;

public class MyString {
	public static String cleanPunctuation(String s) {
		StringBuffer res=new StringBuffer(s.length());
		for(int i=0;i<s.length();i++)
		{
			char c=s.charAt(i);
			if(Character.isLetter(c)||Character.isDigit(c))
				res.append(c);
		}
		return res.toString();
	}

    public static String normalizeDigitsForFeatureExtraction(String s){
	String form=s;
	if(MyString.isDate(form))
	    form="*DATE*";
	if(MyString.hasDigits(form))
	    form= MyString.normalizeDigits(form);
        return form;
    }
    
    public static boolean isDate(String s){
	try{
	    Date.parse(s.replace("-", "/").replace(".","/"));
	    return true;
	}catch (Exception e) {
	    return false;
	}
    }
    
    public static String collapseDigits(String s){
	StringBuffer res=new StringBuffer(s.length()*2);
	for(int i=0;i<s.length();i++){
	    if(Character.isDigit(s.charAt(i))){
		while(i<s.length()&&Character.isDigit(s.charAt(i)))
		    i++;
		res.append("*D*");
		if(i<s.length())
		    res.append(s.charAt(i));
	    }
	    else{
		res.append(s.charAt(i));
	    }
	}
	return res.toString();
    }
    
    public static String normalizeDigits(String s){
	StringBuffer res=new StringBuffer(s.length()*2);
	for(int i=0;i<s.length();i++){
	    if(Character.isDigit(s.charAt(i))){
		res.append("*D*");
	    }
	    else{
		res.append(s.charAt(i));
	    }
	}
	return res.toString();
    }

    public static boolean hasDigits(String s){
	for(int i=0;i<s.length();i++)
	    if(Character.isDigit(s.charAt(i)))
		return true;
	return false;
    }
  
}
