package imcode.server ;

import java.util.* ;

public class Table implements java.io.Serializable {
	Hashtable table = new Hashtable(10,0.5f) ;
	Vector fieldData = new Vector() ;

	public Table() {
	}

	public Table(List fieldData) {
		for(int i = 0 ; i < fieldData.size() ; i++) {
			this.fieldData.addElement(fieldData.get(i)) ;
		}
	}		

	public Table(String[] fieldData) {
		for(int i = 0 ; i < fieldData.length ; i++) {
			this.fieldData.addElement(fieldData[i]) ;
		}
	}		


	public Table(Vector fieldNames,Vector fieldData) {
		for(int i = 0 ; i < fieldData.size() ; i++) {
			table.put(fieldNames.elementAt(i).toString(),fieldData.elementAt(i)) ;
		}
	}	

	public void addFieldData(Vector fieldData) {
		for(int i = 0 ; i < fieldData.size() ; i++) {
			this.fieldData.addElement(fieldData.elementAt(i)) ;
		}
	}


	public void addFieldNames(Vector fieldNames) {		
		for(int i = 0 ; i < fieldData.size() ; i++) {
			table.put(fieldNames.elementAt(i).toString(),fieldData.elementAt(i)) ;
		}
	}

	public void addFieldNames(String[] fieldNames) {		
		for(int i = 0 ; i < fieldData.size() ; i++) {
			table.put(fieldNames[i],fieldData.elementAt(i)) ;
		}
	}

	// add fields
	public void addFields(Vector fieldNames,Vector fieldData) {
		for(int i = 0 ; i < fieldData.size() ; i++) {
			table.put(fieldNames.elementAt(i).toString(),fieldData.elementAt(i)) ;
		}	
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

	// add fieldString
	public void addFieldString(String fieldName,String fieldData,String defaultData) {

		if ( fieldData.toString().length() > 0) {
			table.put(fieldName,fieldData) ;
		} else {
			table.put(fieldName,defaultData) ;	
		}
	}

	public void clear() {
		table.clear() ;
		fieldData.clear() ;	
	}

	// add table
	public void add(Table table) {
		Object obj ;
		for(Enumeration e  = table.keys() ; e.hasMoreElements();) {
			obj = e.nextElement() ;
			this.table.put(obj.toString(),table.getObject(obj.toString())) ;	
		}	
	}

	// get table size
	public int getSize() {
		return table.size() ;
	}

	// get keys
	public synchronized Enumeration keys() {
		return table.keys() ;
	}


	// get table
	public String getTable() {
		return table.toString() ;
	}

	// get object
	public Object getObject(String fieldName) {
		return table.get(fieldName) ;
	}

	// get String
	public String getString(String fieldName) {
		if (table.get(fieldName) != null)
			return table.get(fieldName).toString() ;
		else
			return null ;
	}

	// get int
	public int getInt(String fieldName) {
		return Integer.parseInt(table.get(fieldName).toString()) ;
	}


	// get boolean
	public boolean getBoolean(String fieldName) {
		return (Integer.parseInt(table.get(fieldName).toString()) !=0) ;
	}

	// toString()
	public String toString() {
		return table.toString() ;
	}


}
