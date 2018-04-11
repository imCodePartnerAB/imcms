package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.LoopEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Table;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "imcms_text_doc_contents")
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LoopEntryJPA extends LoopEntry {

    @Column(name = "`index`")
    private int index;

    private boolean enabled;

    LoopEntryJPA(LoopEntry from) {
        super(from);
    }

}
