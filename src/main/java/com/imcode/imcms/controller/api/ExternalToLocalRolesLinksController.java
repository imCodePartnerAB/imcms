package com.imcode.imcms.controller.api;

import com.imcode.imcms.domain.dto.ExternalRole;
import com.imcode.imcms.domain.service.ExternalToLocalRoleLinkService;
import com.imcode.imcms.model.Role;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/external-to-local-roles-links")
class ExternalToLocalRolesLinksController {

    private final ExternalToLocalRoleLinkService externalToLocalRoleLinkService;

    ExternalToLocalRolesLinksController(ExternalToLocalRoleLinkService externalToLocalRoleLinkService) {
        this.externalToLocalRoleLinkService = externalToLocalRoleLinkService;
    }

    @GetMapping
    public Set<Role> getLinkedLocalRoles(ExternalRole externalRole) {
        return externalToLocalRoleLinkService.getLinkedLocalRoles(externalRole);
    }

}
