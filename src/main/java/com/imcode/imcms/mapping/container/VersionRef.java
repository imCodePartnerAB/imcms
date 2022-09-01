package com.imcode.imcms.mapping.container;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public final class VersionRef {

    private final int docId;
    private final int no;
    private final int cachedHashCode;

    public VersionRef(int docId, int versionNo) {
        this.docId = docId;
        this.no = versionNo;
        this.cachedHashCode = Objects.hash(docId, versionNo);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(VersionRef versionRef) {
        return new Builder(versionRef);
    }

    public static VersionRef of(int docId, int no) {
        return new VersionRef(docId, no);
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof VersionRef && equals((VersionRef) o));
    }

    private boolean equals(VersionRef that) {
        return docId == that.docId && no == that.no;
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public String toString() {
	    return MoreObjects.toStringHelper(this)
			    .add("id", docId)
			    .add("no", no)
			    .toString();
    }

    public int getDocId() {
        return docId;
    }

    public int getNo() {
        return no;
    }

    public static class Builder {
        private int docId;
        private int no;

        public Builder() {
        }

        public Builder(VersionRef versionRef) {
            this.docId = versionRef.docId;
            this.no = versionRef.no;
        }

        public Builder docId(int docId) {
            this.docId = docId;
            return this;
        }

        public Builder no(int no) {
            this.no = no;
            return this;
        }

        public VersionRef build() {
            return VersionRef.of(docId, no);
        }
    }
}

