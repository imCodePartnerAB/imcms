package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.RestrictedPermissionDTO;
import com.imcode.imcms.model.RestrictedPermission;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Deprecated
public class TextDocumentViewing {

    private final TextDocument textDocument;
    private final RestrictedPermission restrictedPermission;

    private TextDocumentViewing(TextDocument textDocument, RestrictedPermission restrictedPermission) {
        this.textDocument = textDocument;
        this.restrictedPermission = restrictedPermission;
    }

    @Deprecated
    public static TextDocumentViewing fromRequest(HttpServletRequest request) {
        final TextDocumentDomainObject currentDocument =
                (TextDocumentDomainObject) request.getAttribute("currentDocument");

        final ContentManagementSystem contentManagementSystem = new ContentManagementSystem(
                Imcms.getServices(), Imcms.getUser()
        );

        final TextDocument textDocument = new TextDocument(currentDocument, contentManagementSystem);

        final RestrictedPermission restrictedPermission = Optional
                .ofNullable((RestrictedPermission) request.getAttribute("editOptions"))
                .orElse(new RestrictedPermissionDTO());

        return new TextDocumentViewing(textDocument, restrictedPermission);
    }

    @Deprecated
    public TextDocument getTextDocument() {
        return this.textDocument;
    }

    @Deprecated
    public boolean isEditingTexts() {
        return restrictedPermission.isEditText();
    }

    @Deprecated
    public boolean isEditing() {
        return restrictedPermission.isEditText() || restrictedPermission.isEditMenu()
                || restrictedPermission.isEditLoop() || restrictedPermission.isEditImage()
                || restrictedPermission.isEditDocInfo();
    }

    @Deprecated
    public boolean isEditingImages() {
        return restrictedPermission.isEditImage();
    }

    @Deprecated
    public boolean isEditingMenus() {
        return restrictedPermission.isEditMenu();
    }
}