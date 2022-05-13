package com.imcode.imcms.mapping.container;

import com.google.common.base.MoreObjects;

import java.util.Objects;

public final class DocRef implements LanguageContainer {

    private final int id;
    private final int versionNo;
    private final String languageCode;
    private final int cachedHashCode;
    private final String cachedToString;

    public DocRef(int id, int versionNo, String languageCode) {
	    this.id = id;
	    this.versionNo = versionNo;
	    this.languageCode = Objects.requireNonNull(languageCode);
	    this.cachedHashCode = Objects.hash(id, versionNo);
	    this.cachedToString = MoreObjects.toStringHelper(this)
			    .add("id", id)
			    .add("versionNo", versionNo)
			    .add("languageCode", languageCode)
			    .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DocRef docRef) {
        return new Builder(docRef);
    }

    public static DocRef of(VersionRef versionRef, String languageCode) {
        Objects.requireNonNull(versionRef);

        return new DocRef(versionRef.getDocId(), versionRef.getNo(), languageCode);
    }

    public static DocRef of(int id, int versionNo, String languageCode) {
        return new DocRef(id, versionNo, languageCode);
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof DocRef && equals((DocRef) o));
    }

    private boolean equals(DocRef that) {
        return id == that.id
                && versionNo == that.versionNo
                && Objects.equals(languageCode, that.languageCode);
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return cachedToString;
    }

    public int getId() {
        return id;
    }

    public int getVersionNo() {
        return versionNo;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public VersionRef getVersionRef() {
        return VersionRef.of(id, versionNo);
    }

    public static class Builder {
        private int id;
        private int versionNo;
        private String languageCode;

        public Builder() {
        }

        public Builder(DocRef docRef) {
            this.id = docRef.id;
            this.versionNo = docRef.versionNo;
            this.languageCode = docRef.languageCode;
        }

        public Builder id(int docId) {
            this.id = docId;
            return this;
        }

        public Builder versionNo(int versionNo) {
            this.versionNo = versionNo;
            return this;
        }

        public Builder languageCode(String languageCode) {
            this.languageCode = languageCode;
            return this;
        }

        public Builder versionRef(VersionRef versionRef) {
            Objects.requireNonNull(versionRef);

            this.id = versionRef.getDocId();
            this.versionNo = versionRef.getNo();

            return this;
        }

        public DocRef build() {
            return DocRef.of(id, versionNo, languageCode);
        }

    }
}

