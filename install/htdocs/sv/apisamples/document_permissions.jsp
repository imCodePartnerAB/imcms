<%@ page import="com.imcode.imcms.*, imcode.server.*,java.util.*"%>


<%
    ContentManagementSystem imcmsSystem = (ContentManagementSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentService documentMapper = imcmsSystem.getDocumentMapper();
    int metaId = 1001;
    Document doc = documentMapper.getDocument(metaId);
    Map permissionsMap = doc.getAllRolesMappedToPermissions();
    out.print( permissionsMap );
%>
