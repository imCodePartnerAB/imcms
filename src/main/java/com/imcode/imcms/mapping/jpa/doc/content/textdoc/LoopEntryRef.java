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

    @Column(name = "content_loop_no")
    private int loopNo;

    @Column(name = "content_no")
    private int entryNo;
}
