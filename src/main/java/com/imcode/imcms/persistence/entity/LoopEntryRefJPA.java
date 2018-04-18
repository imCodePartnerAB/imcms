package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.LoopEntryRef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LoopEntryRefJPA extends LoopEntryRef {

    private static final long serialVersionUID = 8008376855086586220L;

    @Column(name = "loop_index")
    private int loopIndex;

    @Column(name = "loop_entry_index")
    private int loopEntryIndex;

    public LoopEntryRefJPA(LoopEntryRef from) {
        super(from);
    }
}
