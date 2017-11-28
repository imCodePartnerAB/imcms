package com.imcode.imcms.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@Table(name = "imcms_text_doc_content_loops")
public class LoopJPA extends Loop<LoopEntryJPA> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "doc_id", referencedColumnName = "doc_id"),
            @JoinColumn(name = "doc_version_no", referencedColumnName = "no")
    })
    private Version version;

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
    private List<LoopEntryJPA> entries;

    private LoopJPA(Integer id, Version version, Integer index, List<LoopEntryJPA> entries) {
        this.id = id;
        this.version = version;
        this.index = index;
        this.entries = entries;
    }

    public <LE2 extends LoopEntry, L extends Loop<LE2>> LoopJPA(L from, Version version) {
        super(from, LoopEntryJPA::new);
        this.version = version;
    }

    public static LoopJPA emptyLoop(Version version, Integer index) {
        return new LoopJPA(null, version, index, Collections.emptyList());
    }

    public boolean containsEntry(int entryIndex) {
        return entries.stream().anyMatch(entry -> entry.getIndex() == entryIndex);
    }

}
