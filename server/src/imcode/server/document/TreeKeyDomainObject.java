package imcode.server.document;

import java.util.*;

public class TreeKeyDomainObject {
    String treeSortIndex;
    public TreeKeyDomainObject( String treeSortIndex ) {
        this.treeSortIndex = treeSortIndex;
    }

    public int getLevelCount() {
        StringTokenizer tokenizer = new StringTokenizer( treeSortIndex, "." );
        int depth = tokenizer.countTokens();
        return depth;
    }

    /**
     *
     * @param level The level in this three key that you want the sort number from.
     * If the tree key is 1.3.5 then the level key on level 2 is 3.
     * @return the key on the level requested. Throws a NoSuchElementException() if there is none.
     *
     */
    public int getLevelKey( int level ) {
        int myMaxLevel = getLevelCount();
        int sortNumber = -1;
        if( myMaxLevel >= level ) {
            StringTokenizer tokenizer = new StringTokenizer( treeSortIndex, "." );
            for( int i = 0; i < level && tokenizer.hasMoreTokens(); i++ ){
                sortNumber = Integer.parseInt(tokenizer.nextToken());
            }
        } else {
            throw new NoSuchElementException();
        }
        return sortNumber;
    }

    public String toString() {
        return treeSortIndex;
    }
}
