package com.imcode.util;

import java.util.Comparator;
import java.io.Serializable;

public class ComparatorWrapper extends ChainableReversibleNullComparator implements Serializable {

    Comparator wrappedComparator;

    public ComparatorWrapper(Comparator comparator) {
        this.wrappedComparator = comparator;
    }

    public int compare( Object o1, Object o2 ) {
        return wrappedComparator.compare( o1,o2 ) ;
    }
}