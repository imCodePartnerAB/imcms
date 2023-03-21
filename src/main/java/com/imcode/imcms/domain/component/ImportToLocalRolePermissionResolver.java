package com.imcode.imcms.domain.component;

import com.imcode.imcms.domain.dto.ImportRoleDTO;
import com.imcode.imcms.domain.factory.RoleImportMapper;
import com.imcode.imcms.model.Role;
import com.imcode.imcms.persistence.entity.Meta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ImportToLocalRolePermissionResolver {
	private final RoleImportMapper roleImportMapper;

	public Map<Integer, Meta.Permission> resolve(List<ImportRoleDTO> importRoles) {
		final HashMap<Integer, Meta.Permission> roleIdToPermission = new HashMap<>();
		for (ImportRoleDTO importRole : importRoles) {
			final Role role = roleImportMapper.mapAndSave(importRole);

			if (role != null) {
				String permission = importRole.getPermission();
				if (permission.equals("FULL")) {
					permission = "EDIT";
				}
				if (permission.equals("READ")) {
					permission = "VIEW";
				}
				roleIdToPermission.put(role.getId(), Meta.Permission.valueOf(permission));
			}
		}

		return roleIdToPermission;
	}
}
