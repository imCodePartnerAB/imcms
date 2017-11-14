package com.imcode.imcms.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "imcms_text_doc_contents")
public class LoopEntry {

    @Column(name = "`index`")
    private int index;

    private boolean enabled;

    public LoopEntry(int index) {
        this(index, true);
    }
}
