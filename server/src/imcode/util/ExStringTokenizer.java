package imcode.util ;

import java.util.*;

class ExStringTokenizer implements Enumeration {

    private LinkedList list;
	ExStringTokenizer (String str, String delim) {
		list = new LinkedList();
		int i;
		while((i = str.indexOf(delim))!=-1) {
			if (i > 0) {
				list.add(str.substring(0,i)) ;
			}
			str = str.substring(i+delim.length()) ;
		}
		if ( str.length() > 0 ) {
			list.add(str);
		}
	}
	
	public boolean hasMoreElements () {
		return (list.size() > 0);
	}

	public boolean hasMoreTokens () {
		return (list.size() > 0);
	}
	
	public String nextToken () {
		return (String) list.removeFirst() ;
	}
	
	public Object nextElement () {
		return list.removeFirst() ;
	}
	
	public int countTokens () {
		return list.size() ;
	}	
}