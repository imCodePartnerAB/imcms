package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class MenuItem {

    @Column(name = "manual_sort_order")
    private Integer sortKey;

    @Column(name = "tree_sort_index")
    private String treeSortIndex;

    public Integer getSortKey() {
        return sortKey;
    }

    public void setSortKey(Integer sortKey) {
        this.sortKey = sortKey;
    }

    public String getTreeSortIndex() {
        return treeSortIndex;
    }

    public void setTreeSortIndex(String treeSortIndex) {
        this.treeSortIndex = treeSortIndex;
    }
}