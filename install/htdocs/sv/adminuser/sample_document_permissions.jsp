<%@ page import="com.imcode.imcms.*, imcode.server.*,java.util.*"%>


<%
    ImcmsSystem imcmsSystem = (ImcmsSystem)request.getAttribute( RequestConstants.SYSTEM );
    DocumentMapperBean documentMapper = imcmsSystem.getDocumentMapper();
    int metaId = 1001;
    DocumentBean doc = documentMapper.getDocument(metaId);
    Map permissionsMap = doc.getAllRolesMappedToPermissions();
    out.print( permissionsMap );
%>
