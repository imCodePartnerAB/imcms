<%@ page import="com.imcode.imcms.api.*" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentService = imcmsSystem.getDocumentService();
    int docId = 1001;
    TextDocument document = documentService.getTextDocument(docId);
    Template docTemplate = document.getTemplate();
%>

<h3><? sv/apisamples/document_show_templates.jsp/1 ?></h3>
<? sv/apisamples/document_show_templates.jsp/2 ?>

<h4><? sv/apisamples/document_show_templates.jsp/3 ?></h4>
<? sv/apisamples/document_show_templates.jsp/4 ?>
        <ul><%
        Template[] templates = templateService.getTemplates( templateGroup );
        for( int k = 0; k < templates.length; k++ ) {
            Template template = templates[k];%>
            <li><%=template.getName()%></li><%
        }%>
        </ul><%
    }

%>


