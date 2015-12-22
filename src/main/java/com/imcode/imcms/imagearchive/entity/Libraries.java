package com.imcode.imcms.imagearchive.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(
        name = "archive_libraries",
        uniqueConstraints = @UniqueConstraint(columnNames = {"folder_nm", "filepath"})
)
public class Libraries implements Serializable {
    public static final short TYPE_STANDARD = 0;
    public static final short TYPE_OLD_LIBRARY = 1;
    private static final long serialVersionUID = 8469633941559619115L;
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private int id;

    @Column(name = "library_nm", length = 120, nullable = false)
    private String libraryNm;

    @Column(name = "folder_nm", length = 255, nullable = false)
    private String folderNm;

    @Column(name = "filepath", length = 255)
    private String filepath;

    @Column(name = "library_type", nullable = false)
    private short libraryType = TYPE_STANDARD;

    @Column(name = "created_dt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDt = new Date();

    @Column(name = "updated_dt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDt = new Date();


    public Libraries() {
    }


    public Date getCreatedDt() {
        return createdDt;
    }

    public void setCreatedDt(Date createdDt) {
        this.createdDt = createdDt;
    }

    public String getFolderNm() {
        return folderNm;
    }

    public void setFolderNm(String folderNm) {
        this.folderNm = folderNm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLibraryNm() {
        return libraryNm;
    }

    public void setLibraryNm(String libraryNm) {
        this.libraryNm = libraryNm;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public short getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(short libraryType) {
        this.libraryType = libraryType;
    }

    public Date getUpdatedDt() {
        return updatedDt;
    }

    public void setUpdatedDt(Date updatedDt) {
        this.updatedDt = updatedDt;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Libraries other = (Libraries) obj;
        if (this.id != other.id) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.id;

        return hash;
    }

    @Override
    public String toString() {
        return String.format("Libraries[id: %d, folderNm: %s, filepath: %s, libraryNm: %s]",
                id, folderNm, filepath, libraryNm);
    }
}
