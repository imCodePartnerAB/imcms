package imcode.external.diverse ;
import java.util.* ;

/**
 * Constructs a string tokenizer for the specified string.
 * if no delimiter is specified then
 * The tokenizer uses the default delimiter set, which is "\t\n\r\f":
 * the space character, the tab character, the newline character,
 * the carriage-return character, and the form-feed character.
 */

class StringManager {

    private String delim ;
    private String str ;

    /**
 * Returns wanted item from string, returns empty if itemnumber not found
 */
    
    public String getItem(String delim, int itemNbr){
        String retVal = "" ;
        itemNbr = itemNbr - 1 ;
        StringTokenizer st = new StringTokenizer(str, delim) ;
        int counter = 0 ;
        
        while (st.hasMoreTokens()) {
            String tmp = st.nextToken() ;
            if( counter == itemNbr)
                retVal = tmp ;
            counter = counter + 1 ;
        }
        return retVal ;
    }
    
    public String getItem( int itemNbr){
        String retVal = "" ;
        itemNbr = itemNbr - 1 ;
        StringTokenizer st = new StringTokenizer(str, delim) ;
        int counter = 0 ;
        
        while (st.hasMoreTokens()) {
            String tmp = st.nextToken() ;
            if( counter == itemNbr)
                retVal = tmp ;
            counter = counter + 1 ;
        }
        return retVal ;
    }
    
    
    
    public int getTotalItems(String newStr, String delim) {
        StringTokenizer st = new StringTokenizer(newStr, delim) ;
        return st.countTokens() ;
    }
    
    public int getTotalItems() {
        StringTokenizer st = new StringTokenizer(str, delim) ;
        return st.countTokens() ;
    }
    
} // end class