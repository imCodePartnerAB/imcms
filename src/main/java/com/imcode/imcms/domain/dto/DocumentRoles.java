package com.imcode.imcms.domain.dto;


import com.imcode.imcms.persistence.entity.DocumentRole;
import com.imcode.imcms.persistence.entity.Meta;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DocumentRoles {
    private final List<DocumentRole> documentRoles;
    private final Meta document;

    public DocumentRoles(List<DocumentRole> documentRoles, Meta document) {
        this.documentRoles = documentRoles;
        this.document = document;
    }

    public boolean hasNoRoles() {
        return documentRoles.isEmpty();
    }

    public Meta.Permission getMostPermission() {
        return documentRoles.stream()
                .map(DocumentRole::getPermission)
                .min(Comparator.naturalOrder())
                .orElse(Meta.Permission.NONE);
    }

    public Meta getDocument() {
        return document;
    }

    public List<DocumentRole> getDocumentRoles() {
        return documentRoles;
    }

    public Set<Meta.Permission> getPermissions() {
        return documentRoles.stream()
                .map(DocumentRole::getPermission)
                .collect(Collectors.toSet());
    }
}
