package com.imcode.imcms.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.MoreObjects;
import com.imcode.imcms.domain.dto.UserDTO;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Document version.
 */
public final class DocumentVersion implements Serializable {

    public static final int WORKING_VERSION_NO = 0;
    private final int no;

    private final UserDTO createdBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final Date createdDt;

    private final UserDTO modifiedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final Date modifiedDt;

    public DocumentVersion(int no, UserDTO createdBy, Date createdDt, UserDTO modifiedBy, Date modifiedDt) {
        this.no = no;
        this.createdBy = createdBy;
        this.createdDt = new Date(createdDt.getTime());
        this.modifiedBy = modifiedBy;
        this.modifiedDt = new Date(modifiedDt.getTime());
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        return o == this || (o instanceof DocumentVersion && equal((DocumentVersion) o));
    }

    protected boolean equal(DocumentVersion that) {
        return Objects.equals(this.no, that.no)
                && Objects.equals(this.createdBy, that.createdBy)
                && Objects.equals(this.createdDt, that.createdDt)
                && Objects.equals(this.modifiedBy, that.modifiedBy)
                && Objects.equals(this.modifiedDt, that.modifiedDt);

    }

    @Override
    public int hashCode() {
        return Objects.hash(no, createdBy, createdDt, modifiedBy, modifiedDt);

    }

    @Override
    public String toString() {
	    return MoreObjects.toStringHelper(this)
			    .add("no", no)
			    .add("createdBy", createdBy)
			    .add("createdDt", createdDt)
			    .add("modifiedBy", modifiedBy)
			    .add("modifiedDt", modifiedDt)
			    .toString();
    }

    public int getNo() {
        return no;
    }

    public UserDTO getCreatedBy() {
        return createdBy;
    }

    public Date getCreatedDt() {
        return createdDt;
    }

    public UserDTO getModifiedBy() {
        return modifiedBy;
    }

    public Date getModifiedDt() {
        return modifiedDt;
    }

    public boolean isWorking() {
        return no == WORKING_VERSION_NO;
    }

    public static class Builder {
        private int no;
        private UserDTO createdBy;
        private Date createdDt;
        private UserDTO modifiedBy;
        private Date modifiedDt;

        public Builder no(int no) {
            this.no = no;
            return this;
        }

        public Builder createdBy(UserDTO createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdDt(Date createdDt) {
            this.createdDt = createdDt;
            return this;
        }

        public Builder modifiedBy(UserDTO modifiedBy) {
            this.modifiedBy = modifiedBy;
            return this;
        }

        public Builder modifiedDt(Date modifiedDt) {
            this.modifiedDt = modifiedDt;
            return this;
        }

        public DocumentVersion build() {
            return new DocumentVersion(no, createdBy, createdDt, modifiedBy, modifiedDt);
        }
    }
}
