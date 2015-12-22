package com.imcode.imcms.imagearchive.command;

import com.imcode.imcms.imagearchive.dto.LibraryRolesDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
                        // roleId, canUse
                        String[] catRightsPart = part.split(",");
                        if (catRightsPart.length == 2) {
                            LibraryRolesDto dto = new LibraryRolesDto();
                            dto.setRoleId(Integer.parseInt(catRightsPart[0]));
                            dto.setCanUse("1".equals(catRightsPart[1]));

                            if (dto.isCanUse()) {
                                libraryRoles.add(dto);
                            }

                            /* always to false, not used anymore */
                            dto.setCanChange(false);
                        }
                    } catch (NumberFormatException ex) {
                    }
                }
            }

            this.libraryRolesStr = libraryRolesStr;
        }
    }
}
