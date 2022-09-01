package com.imcode.imcms.persistence.entity;

import com.imcode.imcms.model.Loop;
import com.imcode.imcms.model.LoopEntry;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@Table(name = "imcms_text_doc_content_loops")
@EqualsAndHashCode(callSuper=false)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class LoopJPA extends Loop {

    private static final long serialVersionUID = 5387027625204254001L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "doc_id", referencedColumnName = "doc_id"),
            @JoinColumn(name = "doc_version_no", referencedColumnName = "no")
    })
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    public LoopJPA(Loop from, Version version) {
        super(from);
        this.version = version;
    }

    public static LoopJPA emptyLoop(Version version, Integer index) {
        return new LoopJPA(null, version, index, new ArrayList<>());
    }

    public boolean containsEntry(int entryIndex) {
        return entries.stream().anyMatch(entry -> entry.getIndex() == entryIndex);
    }

    @Override
    public List<LoopEntry> getEntries() {
        return (entries == null) ? null : new ArrayList<>(entries);
    }

    @Override
    public void setEntries(List<LoopEntry> entries) {
        this.entries = (entries == null) ? null
                : entries.stream().map(LoopEntryJPA::new).collect(Collectors.toList());
    }

    @Override
    public Integer getDocId() {
        return (version == null) ? null : version.getDocId();
    }
}
