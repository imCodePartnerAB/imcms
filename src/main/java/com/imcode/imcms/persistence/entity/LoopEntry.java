package com.imcode.imcms.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class LoopEntry {

    @Column(name = "`index`")
    private int index;

    private boolean enabled;

    public LoopEntry(int index) {
        this(index, true);
    }
}
