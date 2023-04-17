<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
${"<!--"}<%@ page trimDirectiveWhitespaces="true" %>${"-->"}
    <%@ page contentType="text/javascript" pageEncoding="UTF-8" %>

    Imcms = {
        expiredSessionTimeInMillis: ${pageContext.session.maxInactiveInterval * 1000},
        userLanguage: "${userLanguage}",
        contextPath: "${pageContext.request.contextPath}",
        imagesPath: "${imagesPath}",
        version: "${version}",
        isEditMode: ${isEditMode or false},
        isPreviewMode: ${isPreviewMode or false},
        isVersioningAllowed: ${isVersioningAllowed or false},
        isInWasteBasket: ${isInWasteBasket or false},
        isSuperAdmin: ${isSuperAdmin or false},
	    hasFileAdminAccess: ${hasFileAdminAccess or false},
        documentationLink: "${documentationLink}",
        editOptions: {
            isEditDocInfo: ${editOptions.editDocInfo or false},
            isEditContent: ${
                    editOptions.editText or editOptions.editMenu or editOptions.editImage or editOptions.editLoop
            },
            permission: "${editOptions.permission}"
        },
        accessToAdminPages: ${accessToAdminPages or false},
        accessToDocumentEditor: ${accessToDocumentEditor or false},
        accessToPublishCurrentDoc: ${accessToPublishCurrentDoc or false},
        document: {
            id: ${empty currentDocument.id ? targetDocId+'' : currentDocument.id},
            type: ${empty currentDocument.documentTypeId ? 'null' : currentDocument.documentTypeId},
            hasNewerVersion: ${hasNewerVersion or false},
            headline: "${StringEscapeUtils.escapeEcmaScript(currentDocument.headline)}",
            alias: "${currentDocument.alias}"
        },
        language: {
            name: "${currentDocument.language.name}",
            nativeName: "${currentDocument.language.nativeName}",
            code: "${empty currentDocument.language.code ? userLanguage : currentDocument.language.code}"
        }
    };

    <%--@elvariable id="currentDocument" type="imcode.server.document.textdocument.TextDocumentDomainObject"--%>
    <%--@elvariable id="isVersioningAllowed" type="boolean"--%>
    <%--@elvariable id="isInWasteBasket" type="boolean"--%>
    <%--@elvariable id="isEditMode" type="boolean"--%>
    <%--@elvariable id="isPreviewMode" type="boolean"--%>
    <%--@elvariable id="hasNewerVersion" type="boolean"--%>
    <%--@elvariable id="version" type="java.lang.String"--%>
    <%--@elvariable id="imagesPath" type="java.lang.String"--%>
    <%--@elvariable id="userLanguage" type="java.lang.String"--%>
    <%--@elvariable id="documentationLink" type="java.lang.String"--%>
    <%--@elvariable id="isSuperAdmin" type="boolean"--%>
    <%--@elvariable id="hasFileAdminAccess" type="boolean"--%>
    <%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>
    <%--@elvariable id="accessToAdminPages" type="boolean"--%>
    <%--@elvariable id="accessToDocumentEditor" type="boolean"--%>
    <%--@elvariable id="accessToPublishCurrentDoc" type="boolean"--%>
    <%--@elvariable id="targetDocId" type="int"--%>