package com.imcode.imcms.api;

import com.imcode.imcms.domain.dto.RestrictedPermissionDTO;
import com.imcode.imcms.model.RestrictedPermission;
import imcode.server.Imcms;
import imcode.server.document.textdocument.TextDocumentDomainObject;
import org.apache.commons.lang3.BooleanUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

// TODO: 02.09.19 check, how to use it class and methods, because it all will be deprecated !!!
public class TextDocumentViewing {

    private final TextDocument textDocument;
    private final RestrictedPermission restrictedPermission;
    private final boolean isEditMode;

    private TextDocumentViewing(TextDocument textDocument, RestrictedPermission restrictedPermission, boolean isEditMode) {
        this.textDocument = textDocument;
        this.restrictedPermission = restrictedPermission;
        this.isEditMode = isEditMode;
    }

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

        final boolean isEditMode = BooleanUtils.toBoolean((Boolean) request.getAttribute("isEditMode"));

        return new TextDocumentViewing(textDocument, restrictedPermission, isEditMode);
    }

    @Deprecated
    public TextDocument getTextDocument() {
        return this.textDocument;
    }

    @Deprecated
    public boolean isEditingTexts() {
        return isEditMode && restrictedPermission.isEditText();
    }

    @Deprecated
    public boolean isEditing() {
        return isEditMode && (restrictedPermission.isEditText()
                || restrictedPermission.isEditMenu()
                || restrictedPermission.isEditLoop()
                || restrictedPermission.isEditImage()
                || restrictedPermission.isEditDocInfo()
        );
    }

    @Deprecated
    public boolean isEditingImages() {
        return isEditMode && restrictedPermission.isEditImage();
    }

    @Deprecated
    public boolean isEditingMenus() {
        return isEditMode && restrictedPermission.isEditMenu();
    }
}