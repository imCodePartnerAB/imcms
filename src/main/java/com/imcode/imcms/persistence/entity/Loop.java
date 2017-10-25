package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.mapping.jpa.doc.content.VersionedContent;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "imcms_text_doc_content_loops")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = true)
public class Loop extends VersionedContent {

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
    private List<LoopEntry> entries = new LinkedList<>();

    public Loop(Integer id, Version version, Integer index, List<LoopEntry> entries) {
        setId(id);
        setVersion(version);
        this.index = index;
        this.entries = new LinkedList<>(entries);
    }

    public Loop(Version version, Integer index, List<LoopEntry> entries) {
        this(null, version, index, entries);
    }

    public static Loop emptyLoop(Version version, Integer index) {
        return new Loop(version, index, Collections.emptyList());
    }

    public boolean containsEntry(int entryIndex) {
        return entries.stream().anyMatch(entry -> entry.getIndex() == entryIndex);
    }

}
