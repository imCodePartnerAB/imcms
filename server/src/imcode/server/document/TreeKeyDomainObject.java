package imcode.server.document;

import java.util.*;

public class TreeKeyDomainObject {
    String treeSortIndex;
    public TreeKeyDomainObject( String treeSortIndex ) {
        this.treeSortIndex = treeSortIndex;
    }

    public int getTreeLevel() {
        StringTokenizer tokenizer = new StringTokenizer( treeSortIndex, "." );
        return tokenizer.countTokens();
    }

    /**
     *
     * @param level The level in the key that you want the sort number from.
     * If the tree key is 1.3.5 then the number on level 2 is 3.
     * @return the sort nuber for the level requested. -1 if there is no token on that level.
     */
    public int getTreeLevelSortNumber( int level ) {
        StringTokenizer tokenizer = new StringTokenizer( treeSortIndex, "." );
        if( tokenizer.countTokens() < level ) {
            return -1;
        }

        int sortNumber = 0;
        for( int i = 0; i < level && tokenizer.hasMoreTokens() ; i++) {
            sortNumber = Integer.parseInt(tokenizer.nextToken());
        }
        return sortNumber;
    }

    public String getValue() {
        return treeSortIndex;
    }
}
