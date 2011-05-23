package com.imcode.imcms.addon.imagearchive.command;

import com.imcode.imcms.addon.imagearchive.dto.LibraryRolesDto;
import com.imcode.imcms.addon.imagearchive.entity.LibraryRoles;
import com.imcode.imcms.addon.imagearchive.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class SaveLibraryRolesCommand implements Serializable {
    private static final long serialVersionUID = 7135495070544485257L;

    private String libraryNm;
    private String libraryRolesStr;
    private List<LibraryRolesDto> libraryRoles;


    public SaveLibraryRolesCommand() {
    }


    public String getLibraryNm() {
        return libraryNm;
    }

    public void setLibraryNm(String libraryNm) {
        this.libraryNm = libraryNm;
    }

    public List<LibraryRolesDto> getLibraryRoles() {
        return libraryRoles;
    }

    public void setLibraryRoles(List<LibraryRolesDto> libraryRoles) {
        this.libraryRoles = libraryRoles;
    }

    public String getLibraryRolesStr() {
        return libraryRolesStr;
    }

    public void setLibraryRolesStr(String libraryRolesStr) {
        if (libraryRolesStr != null) {
            libraryRolesStr = libraryRolesStr.trim();

            if (libraryRolesStr != null) {
                String[] parts = libraryRolesStr.split("-");
                libraryRoles = new ArrayList<LibraryRolesDto>();
                for (String part : parts) {
                    try {
                        // roleId, canUse, canEdit
                        String[] catRightsPart = part.split(",");
                        if (catRightsPart.length == 3) {
                            LibraryRolesDto dto = new LibraryRolesDto();
                            dto.setRoleId(Integer.parseInt(catRightsPart[0]));
                            dto.setCanUse("1".equals(catRightsPart[1]));
                            dto.setCanChange("1".equals(catRightsPart[2]));
                            libraryRoles.add(dto);
                        }
                    } catch (NumberFormatException ex) {
                    }
                }
            }

            this.libraryRolesStr = libraryRolesStr;
        }
    }
}
