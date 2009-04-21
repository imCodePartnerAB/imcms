package com.imcode.imcms.addon.imagearchive.dto;

import java.io.Serializable;

import com.imcode.imcms.api.User;

public class LibrariesDto implements Serializable {
    private static final long serialVersionUID = -2489487888347287580L;
    
    public static final int USER_LIBRARY_ID = -1;
    
    private int id;
    private String libraryNm;
    private String folderNm;
    private String filepath;
    private short libraryType;
    private boolean canUse;
    private boolean canChange;
    
    
    public LibrariesDto() {
    }
    
    public static LibrariesDto userLibrary(User user) {
        LibrariesDto library = new LibrariesDto();
        library.setId(USER_LIBRARY_ID);
        library.setFolderNm(Integer.toString(user.getId()));
        library.setCanChange(true);
        library.setCanUse(true);
        
        return library;
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

    public boolean isCanChange() {
        return canChange;
    }

    public void setCanChange(boolean canChange) {
        this.canChange = canChange;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
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
    
    public boolean isUserLibrary() {
        return id == USER_LIBRARY_ID;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final LibrariesDto other = (LibrariesDto) obj;
        if (this.id != other.id) {
            return false;
        }
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.id;
        
        return hash;
    }
}
