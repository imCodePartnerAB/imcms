package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Data
@Entity
@Table(name = "imcms_text_doc_content_loops")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoopJPA extends VersionedContent {

    @Min(1)
    @NotNull
    @Column(name = "`index`", updatable = false)
    private Integer index;

    @ElementCollection
    @CollectionTable(
            name = "imcms_text_doc_contents",
            joinColumns = @JoinColumn(name = "loop_id")
    )
    @OrderColumn(name = "order_index")
    private List<LoopEntryJPA> entries = new LinkedList<>();

    public LoopJPA(Integer id, Version version, Integer index, List<LoopEntryJPA> entries) {
        setId(id);
        setVersion(version);
        this.index = index;
        this.entries = new LinkedList<>(entries);
    }

    public LoopJPA(Version version, Integer index, List<LoopEntryJPA> entries) {
        this(null, version, index, entries);
    }

    public static LoopJPA emptyLoop(Version version, Integer index) {
        return new LoopJPA(version, index, Collections.emptyList());
    }

    public boolean containsEntry(int entryIndex) {
        return entries.stream().anyMatch(entry -> entry.getIndex() == entryIndex);
    }

}
