package com.imcode.imcms.mapping.orm;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Content 'index' corresponds to absolute context position in a loop starting from zero.
 * Content 'no' is a unique content identifier in a loop which is assigned (incremented) automatically
 * when a new content is added into the loop.
 */
@Entity
@Table(name = "imcms_text_doc_content_loops")
public class TextDocLoop extends DocVersionedContent {


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

    @Version
    private int version;

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
                .add("docVersion", getDocVersion())
                .add("no", no)
                .add("version", version)
                .add("entries", entries).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getDocVersion(), no, entries, version);
    }


    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof TextDocLoop && equals((TextDocLoop) o));
    }

    private boolean equals(TextDocLoop that) {
        return Objects.equals(getId(), that.getId())
                && Objects.equals(getDocVersion(), that.getDocVersion())
                && Objects.equals(no, that.no)
                && Objects.equals(version, that.version)
                && Objects.equals(entries, that.entries);
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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