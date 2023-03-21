package com.imcode.imcms.domain.factory;

import com.imcode.imcms.domain.dto.ImportEntityReferenceDTO;
import com.imcode.imcms.domain.dto.RoleDTO;
import com.imcode.imcms.domain.dto.ImportRoleDTO;
import com.imcode.imcms.domain.service.ImportEntityReferenceManagerService;
import com.imcode.imcms.domain.service.RoleService;
import com.imcode.imcms.model.ImportEntityReferenceType;
import com.imcode.imcms.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleImportMapper {
	private final RoleService roleService;
	private final ImportEntityReferenceManagerService importEntityReferenceManagerService;

	public Role mapAndSave(ImportRoleDTO importRole) {
		final ImportEntityReferenceDTO roleReference = importEntityReferenceManagerService.getReference(importRole.getName(), ImportEntityReferenceType.ROLE);
		final Integer linkedEntityId = roleReference.getLinkedEntityId();

		Role role;
		final List<String> allRoleNames = roleService.getAll().stream().map(Role::getName).collect(Collectors.toList());
		if (linkedEntityId != null) {
			role = roleService.getById(linkedEntityId);
		} else if (!allRoleNames.contains(importRole.getName())) {
			role = roleService.saveNewRole(new RoleDTO(importRole.getName()));
		} else {
			role = roleService.getByName(roleReference.getName());
		}

		return role;
	}
}
