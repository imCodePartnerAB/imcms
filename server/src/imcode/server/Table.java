package imcode.server ;

import java.util.* ;

public class Table implements java.io.Serializable {

    private Hashtable table = new Hashtable(10,0.5f) ;

    public Table() {
	}

    // add field
	public void addField(String fieldName,Object fieldData) {
		try {
			table.put(fieldName,fieldData) ;
		} catch(NullPointerException ex) {
			System.out.println("Put: " + fieldName) ;
			throw new NullPointerException ("Put: "+fieldName) ;
		}
	}

    // get String
	public String getString(String fieldName) {
		if (table.get(fieldName) != null)
			return table.get(fieldName).toString() ;
		else
			return null ;
	}

    // toString()
	public String toString() {
		return table.toString() ;
	}


}
