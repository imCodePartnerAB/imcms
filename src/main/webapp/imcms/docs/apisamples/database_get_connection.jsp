<%@ page import="com.imcode.imcms.api.*, java.sql.Connection,
                 java.sql.DatabaseMetaData" errorPage="error.jsp" %>

<%
    ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
    Connection connection = imcmsSystem.getDatabaseService().getConnection() ;
    try {
        DatabaseMetaData metaData = connection.getMetaData() ;
%>
Connected to database <%= connection.getCatalog() %> on <%= metaData.getDatabaseProductName() %>
using driver <%= metaData.getDriverName() %> version <%= metaData.getDriverVersion() %>.
<%
    } finally {
        /* IMPORTANT: Do not forget to close the connection!
           If you do, it won't be returned to the connectionpool. */
        connection.close() ;
    }

%>