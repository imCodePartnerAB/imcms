package imcode.server.document;

import java.util.*;

public class TreeKeyDomainObject {
    String treeSortIndex;
    public TreeKeyDomainObject( String treeSortIndex ) {
        this.treeSortIndex = treeSortIndex;
    }

    public int getLevel() {
        StringTokenizer tokenizer = new StringTokenizer( treeSortIndex, "." );
        int depth = tokenizer.countTokens();
        return depth;
    }

    /**
     *
     * @param level The depth in the key that you want the sort number from.
     * If the tree key is 1.3.5 then the number on depth 2 is 3.
     * @return the sort nuber for the depth requested. -1 if there is none on that depth.
     */
    public int getSortNumber( int level ) {
        int myMaxLevel = getLevel();
        int sortNumber = -1;
        if( myMaxLevel >= level ) {
            StringTokenizer tokenizer = new StringTokenizer( treeSortIndex, "." );
            for( int i = 0; i < level && tokenizer.hasMoreTokens(); i++ ){
                sortNumber = Integer.parseInt(tokenizer.nextToken());
            }
        }
        return sortNumber;
    }

    /*
    public int getSortNumber( int onDepth ) {
        int sortNumber = -1;
        StringTokenizer tokenizer = new StringTokenizer( treeSortIndex, "." );
        if( tokenizer.countTokens() < onDepth ) {
            sortNumber = -1;
        } else if( 0 == tokenizer.countTokens() ) {
            sortNumber = 1;
        } else {
            for( int i = 0; i < onDepth && tokenizer.hasMoreTokens() ; i++) {
                sortNumber = Integer.parseInt(tokenizer.nextToken());
            }
        }
        return sortNumber;
    }

    */
    public String getValue() {
        return treeSortIndex;
    }

    public String toString() {
        return getValue();
    }
}
