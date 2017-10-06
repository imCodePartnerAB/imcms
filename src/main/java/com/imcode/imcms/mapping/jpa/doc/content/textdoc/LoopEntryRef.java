package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LoopEntryRef implements Serializable, Cloneable {

    @Column(name = "loop_index")
    private int loopIndex;

    @Column(name = "loop_entry_index")
    private int loopEntryIndex;
}
