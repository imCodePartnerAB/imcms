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
            
            libraryRoles = new ArrayList<LibraryRolesDto>();
            if (!StringUtils.isEmpty(libraryRolesStr)) {
                String[] parts = libraryRolesStr.split("/");
                
                if (parts.length != 0 && (parts.length % 3) == 0) {
                    for (int i = 0, len = parts.length; i < len; i += 3) {
                        try {
                            String roleName = Utils.decodeUrl(parts[i]);
                            int roleId = Integer.parseInt(parts[i + 1], 10);
                            int choice = Integer.parseInt(parts[i + 2], 10);
                            
                            LibraryRolesDto dto = new LibraryRolesDto();
                            dto.setRoleName(roleName);
                            dto.setRoleId(roleId);
                            
                            if (choice == 1) {
                                dto.setPermissions(LibraryRoles.PERMISSION_CHANGE);
                            } else {
                                dto.setPermissions(LibraryRoles.PERMISSION_USE);
                            }
                            
                            libraryRoles.add(dto);
                        } catch (Exception ex) {
                        }
                    }
                }
            }
        }
        
        this.libraryRolesStr = libraryRolesStr;
    }
}
