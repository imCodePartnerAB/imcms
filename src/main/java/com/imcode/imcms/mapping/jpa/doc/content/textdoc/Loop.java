package com.imcode.imcms.mapping.jpa.doc.content.textdoc;

import com.imcode.imcms.mapping.jpa.doc.Version;
import com.imcode.imcms.mapping.jpa.doc.content.VersionedContent;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
public class Loop extends VersionedContent {

    @Embeddable
    public static class Entry {

        private int no;

        private boolean enabled;

        public Entry() {
        }

        public Entry(int no) {
            this(no, true);
        }

        public Entry(int no, boolean enabled) {
            this.no = no;
            this.enabled = enabled;
        }

        @Override
        public String toString() {
            return com.google.common.base.Objects.toStringHelper(this)
                    .add("no", no)
                    .add("enabled", enabled).toString();
        }

        @Override
        public int hashCode() {
            return Objects.hash(no, enabled);
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof Entry && equals((Entry) o));
        }

        private boolean equals(Entry that) {
            return this.enabled == that.enabled && this.no == that.no;
        }

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    @Min(1)
    @NotNull
    @Column(updatable = false)
    private Integer no;

    @Min(1)
    @NotNull
    @Column(name = "next_content_no")
    private Integer nextEntryNo;

    public Loop() {
    }

    public Loop(Integer id, Version version, Integer no, Integer nextEntryNo, List<Entry> entries) {
        setId(id);
        setVersion(version);
        this.no = no;
        this.nextEntryNo = nextEntryNo;
        this.entries = new LinkedList<>(entries);
    }

    public Loop(Version version, Integer no, Integer nextEntryNo, List<Entry> entries) {
        this(null, version, no, nextEntryNo, entries);
    }


    @ElementCollection
    @CollectionTable(
            name = "imcms_text_doc_contents",
            joinColumns = @JoinColumn(name = "content_id")
    )
    @OrderColumn(name = "ix")
    private List<Entry> entries = new LinkedList<>();

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("id", getId())
                .add("docVersion", getVersion())
                .add("no", no)
                .add("entries", entries).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVersion(), no, entries);
    }


    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof Loop && equals((Loop) o));
    }

    private boolean equals(Loop that) {
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getVersion(), that.getVersion())
                && Objects.equals(no, that.no)
                && Objects.equals(entries, that.entries);
    }

    public boolean containsEntry(int entryNo) {
        return entries.stream().anyMatch(entry -> entry.no == entryNo);
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> items) {
        this.entries = items;
    }

    public Integer getNextEntryNo() {
        return nextEntryNo;
    }

    public void setNextEntryNo(Integer nextEntryNo) {
        this.nextEntryNo = nextEntryNo;
    }
}