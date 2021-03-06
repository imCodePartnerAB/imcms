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
        isAdmin: ${isAdmin or false},
        editOptions: {
            isEditDocInfo: ${editOptions.editDocInfo or false},
            isEditContent: ${
                    editOptions.editText or editOptions.editMenu or editOptions.editImage or editOptions.editLoop
            }
        },
        document: {
            id: ${empty currentDocument.id ? 'null' : currentDocument.id},
            type: ${empty currentDocument.documentTypeId ? 'null' : currentDocument.documentTypeId},
            hasNewerVersion: ${hasNewerVersion or false},
            headline: "${currentDocument.headline}",
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
    <%--@elvariable id="isEditMode" type="boolean"--%>
    <%--@elvariable id="isPreviewMode" type="boolean"--%>
    <%--@elvariable id="hasNewerVersion" type="boolean"--%>
    <%--@elvariable id="version" type="java.lang.String"--%>
    <%--@elvariable id="imagesPath" type="java.lang.String"--%>
    <%--@elvariable id="userLanguage" type="java.lang.String"--%>
    <%--@elvariable id="isAdmin" type="boolean"--%>
    <%--@elvariable id="editOptions" type="com.imcode.imcms.domain.dto.RestrictedPermissionDTO"--%>