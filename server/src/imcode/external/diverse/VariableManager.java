package imcode.external.diverse ;

import java.util.* ;

public class VariableManager {

    private List tags = new ArrayList();
    private List data = new ArrayList();

    public void addProperty( Object property, Object value){
        tags.add(property) ;
        data.add(value) ;
    }

    public String getProperty(String aProp) {
        String tmp = "" ;
        for(int i = 0; i < tags.size(); i++) {
            tmp = tags.get(i).toString() ;
            if(tmp.equals(aProp))
                return data.get(i).toString() ;
        }
        return "" ;
    }
    
    public List getAllProps(){
        return tags ;
    }
    
    public List getAllValues(){
        return data ;
    }

    public List getTagsAndData() {
        List tagsAndData = new ArrayList(tags.size()*2) ;
        for ( Iterator tagsIterator = tags.iterator(), dataIterator = data.iterator(); tagsIterator.hasNext(); ) {
            tagsAndData.add("#"+tagsIterator.next()+"#") ;
            tagsAndData.add(dataIterator.next()) ;
        }
        return tagsAndData ;
    }

    public String toString() {
        
        String tmp = "" ;
        String aProp = "" ;
        String aVal = "" ;
        for(int i = 0; i < tags.size(); i++) {
            aProp = tags.get(i).toString() ;
            aVal = data.get(i).toString() ;
            tmp = tmp + aProp + "=" + aVal + '\n';
        }
        return tmp ;
    }

} // end of class