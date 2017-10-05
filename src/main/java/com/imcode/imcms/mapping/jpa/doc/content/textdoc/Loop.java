package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.VersionedContent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Content 'index' corresponds to absolute context position in a loop starting from zero.
 * Content 'no' is a unique content identifier in a loop which is assigned (incremented) automatically
 * when a new content is added into the loop.
 */
@Entity
@Table(name = "imcms_text_doc_content_loops")
@ToString
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
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
    private List<Entry> entries = new LinkedList<>();

    public Loop() {
    }

    public Loop(Integer id, Version version, Integer index, List<Entry> entries) {
        setId(id);
        setVersion(version);
        this.index = index;
        this.entries = new LinkedList<>(entries);
    }

    public Loop(Version version, Integer index, List<Entry> entries) {
        this(null, version, index, entries);
    }

    public static Loop emptyLoop(Version version, Integer index) {
        return new Loop(version, index, Collections.emptyList());
    }

    public boolean containsEntry(int entryIndex) {
        return entries.stream().anyMatch(entry -> entry.index == entryIndex);
    }

    @Embeddable
    public static class Entry {

        @Column(name = "`index`")
        private int index;

        private boolean enabled;

        public Entry() {
        }

        public Entry(int index) {
            this(index, true);
        }

        public Entry(int index, boolean enabled) {
            this.index = index;
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return com.google.common.base.Objects.toStringHelper(this)
                    .add("index", index)
                    .add("enabled", enabled).toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(index, enabled);
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof Entry && equals((Entry) o));
        }

        private boolean equals(Entry that) {
            return this.enabled == that.enabled && this.index == that.index;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}