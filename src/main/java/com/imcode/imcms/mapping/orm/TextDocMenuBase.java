package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@MappedSuperclass
public class TextDocMenuBase extends DocVersionedContent {

    @NotNull
    @Column(name = "no")
    private Integer no;

    @Column(name = "sort_order")
    private int sortOrder;

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}