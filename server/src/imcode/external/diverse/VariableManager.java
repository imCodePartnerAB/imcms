package imcode.external.diverse ;
import java.util.* ;

public class VariableManager {
	Vector tags ;
	Vector data ;

	public VariableManager() {
		tags = new Vector() ;
		data = new Vector() ;
	}

	public void addProperty( Object property, Object value){
		tags.addElement(property) ;
		data.addElement(value) ;
	}

	public Vector getHtmlProperties(){
		String tmp = "" ;
		Vector aVector = (Vector) tags.clone();
		for(int i = 0 ; i < aVector.size() ; i++){
				tmp = aVector.elementAt(i).toString() ;
				tmp = "#" + tmp +"#" ;
				aVector.set(i, tmp) ;
		}
	return aVector ;
	}


	public String getProperty(String aProp) {
		String tmp = "" ;
		for(int i = 0; i < tags.size(); i++) {
			tmp = tags.elementAt(i).toString() ;
			if(tmp.equals(aProp))
			  return data.elementAt(i).toString() ;
		}
		return "" ;
	}

	public Vector getAllProps(){
		return tags ;
	}

	public Vector getAllValues(){
		return data ;
	}

	public boolean merge(VariableManager man) {

		this.merge(man.getAllProps(), man.getAllValues()) ;
		return true ;
	}


	public boolean merge(Vector tagVect, Vector datVect ) {

		// Lets assure that the vectors has the same size
		if(tagVect.size() != datVect.size()) {
			log("merge, inVectors differs in size!") ;
			return false ;
		}

		Object tagObj ;
		Object datObj ;

		for(int i = 0; i < tagVect.size(); i++) {
			tagObj = tagVect.elementAt(i) ;
			datObj = datVect.elementAt(i) ;
			this.addProperty(tagObj, datObj) ;
		}
		return true ;
	}

	public boolean merge(Properties props) {

		Enumeration enumValues = props.elements() ;
		Enumeration enumKeys = props.keys() ;

		while((enumValues.hasMoreElements() && enumKeys.hasMoreElements())) {
		 Object tagObj = (enumKeys.nextElement()) ;
				 Object datObj = (enumValues.nextElement()) ;
				 if(datObj == null)
						datObj = " " ;
				 this.addProperty(tagObj, datObj) ;

		}
		return true ;
	}

public String toString() {

		String tmp = "" ;
		String aProp = "" ;
		String aVal = "" ;
		for(int i = 0; i < tags.size(); i++) {
			aProp = tags.elementAt(i).toString() ;
			aVal = data.elementAt(i).toString() ;
			tmp = tmp + aProp + "=" + aVal + '\n';
		}
		return tmp ;
}


	protected void log(Object msg) {

		System.out.println("VariableManager: " + msg);
	}

} // end of class