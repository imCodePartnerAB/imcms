<%@ page import="com.imcode.imcms.*, imcode.server.*,java.util.*"%>


<%

DocumentMapperBean documentMapper = (DocumentMapperBean)request.getAttribute( WebAppConstants.DOCUMENT_MAPPER_ATTRIBUTE_NAME );
int metaId = 1001;
DocumentBean doc = documentMapper.getDocument(metaId);
Map permissionsMap = doc.getAllRolesMappedToPermissions();
out.print( permissionsMap );

%>
