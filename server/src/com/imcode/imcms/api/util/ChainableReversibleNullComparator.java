/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-18
 * Time: 13:30:03
 */
package com.imcode.imcms.api.util;

import org.apache.commons.collections.ComparatorUtils;

import java.util.Comparator;

public abstract class ChainableReversibleNullComparator implements Comparator {

    public ChainableReversibleNullComparator chain( Comparator comparator ) {
        return new ComparatorWrapper( ComparatorUtils.chainedComparator( this, comparator ) );
    }

    public ChainableReversibleNullComparator reversed() {
        return new ComparatorWrapper( ComparatorUtils.reversedComparator( this ) );
    }

    public ChainableReversibleNullComparator nullsFirst() {
        return new ComparatorWrapper( this ) {
            public int compare( Object o1, Object o2 ) {
                try {
                    return super.compare( o1, o2 );
                } catch (NullPointerException npe) {
                    return compareNulls( wrappedComparator, o1, o2 );
                }
            }
        };
    }

    public ChainableReversibleNullComparator nullsLast() {
        return new ComparatorWrapper( this ) {
            public int compare( Object o1, Object o2 ) {
                try {
                    return super.compare( o1, o2 );
                } catch (NullPointerException npe) {
                    return -compareNulls( wrappedComparator, o1, o2 );
                }
            }
        };
    }

    private int compareNulls( Comparator comparator, Object o1, Object o2 ) {
        boolean value1IsNull = false;
        boolean value2IsNull = false;
        try {
            comparator.compare( o1, o1 );
        } catch ( NullPointerException npe ) {
            value1IsNull = true;
        }
        try {
            comparator.compare( o2, o2 );
        } catch ( NullPointerException npe ) {
            value2IsNull = true;
        }
        if ( value1IsNull && value2IsNull ) {
            return 0;
        } else if ( value1IsNull ) {
            return -1;
        } else if ( value2IsNull ) {
            return +1;
        }
        return 0 ;
    }

}