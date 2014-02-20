package com.imcode.imcms.mapping.orm;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;

public class ImageId implements Serializable {

    @Column(name = "meta_id")
    private int docId;

    private String name = "";

    public ImageId() {
    }

    public ImageId(int docId, String name) {
        setDocId(docId);
        setName(name);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ImageId)) {
            return false;
        }

        ImageId imageId = (ImageId) o;

        return docId == imageId.docId
                && name.equals(imageId.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, docId);
    }

    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
