/*
 * Created by IntelliJ IDEA.
 * User: kreiger
 * Date: 2004-feb-18
 * Time: 13:17:01
 */
package com.imcode.imcms.api.util;

import java.util.Comparator;

public class ComparatorWrapper extends ChainableReversibleNullComparator {

    protected Comparator wrappedComparator;

    public ComparatorWrapper(Comparator comparator) {
        this.wrappedComparator = comparator;
    }

    public int compare( Object o1, Object o2 ) {
        return wrappedComparator.compare( o1,o2 ) ;
    }
}