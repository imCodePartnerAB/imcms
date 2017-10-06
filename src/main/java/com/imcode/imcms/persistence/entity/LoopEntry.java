package com.imcode.imcms.persistence.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Setter
@Getter
@ToString
@EqualsAndHashCode
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
