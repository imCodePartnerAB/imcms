package com.imcode.imcms.mapping;

import com.imcode.imcms.domain.dto.PermissionDTO;
import imcode.server.document.DocumentPermissionSetDomainObject;
import imcode.server.document.TextDocumentPermissionSetDomainObject;
import junit.framework.TestCase;

public class TestDocumentPermissionSetMapper extends TestCase {

    private TextDocumentPermissionSetDomainObject textDocumentPermissionSet;

    public void setUp() throws Exception {
        super.setUp();
        textDocumentPermissionSet = new TextDocumentPermissionSetDomainObject(PermissionDTO.RESTRICTED_1);
    }

    public void testSetTextDocumentPermissionSetFromBits() throws Exception {
        textDocumentPermissionSet.setEditDocumentInformation(0 != (DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & DocumentPermissionSetDomainObject.EDIT_DOCINFO_PERMISSION_ID));
        textDocumentPermissionSet.setEditPermissions(0 != (DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & DocumentPermissionSetDomainObject.EDIT_PERMISSIONS_PERMISSION_ID));
        textDocumentPermissionSet.setEdit(0 != (DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID));
        textDocumentPermissionSet.setEditTexts(0 != (DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEXTS_PERMISSION_ID));
        textDocumentPermissionSet.setEditLoops(0 != (DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_LOOPS_PERMISSION_ID));
        textDocumentPermissionSet.setEditImages(0 != (DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_IMAGES_PERMISSION_ID));
        textDocumentPermissionSet.setEditMenus(0 != (DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_MENUS_PERMISSION_ID));
        textDocumentPermissionSet.setEditIncludes(0
                != (DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_INCLUDES_PERMISSION_ID));
        textDocumentPermissionSet.setEditTemplates(0
                != (DocumentPermissionSetDomainObject.EDIT_DOCUMENT_PERMISSION_ID & TextDocumentPermissionSetDomainObject.EDIT_TEXT_DOCUMENT_TEMPLATE_PERMISSION_ID));
        assertTrue(textDocumentPermissionSet.getEditTexts());
    }

}