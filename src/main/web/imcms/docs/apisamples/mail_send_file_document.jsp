<%@ page import="com.imcode.imcms.api.*,
java.util.*,
                 org.apache.commons.lang.StringUtils" errorPage="error.jsp" %>
<%
ContentManagementSystem imcmsSystem = ContentManagementSystem.fromRequest( request );
User currentLoggedinUser = imcmsSystem.getCurrentUser();
DocumentService documentService = imcmsSystem.getDocumentService() ;
%>
<html>
    <head>
        <title>imCMS Mail</title>
    </head>
    <body>
        <%
            String emailAddress = currentLoggedinUser.getEmailAddress();
            if ( StringUtils.isBlank( emailAddress ) ) {
                %>You don't have an e-mail-address set.<%
            } else {
                FileDocument fileDocument = null ;
                String fileDocumentIdStr = request.getParameter( "id");
                try {
                    fileDocument = (FileDocument)documentService.getDocument( Integer.parseInt(fileDocumentIdStr)) ;
                } catch (Exception ignored) {
                }
                if (null == fileDocument) {
                    if (null != fileDocumentIdStr) {
                        %>Not a filedocument-id: <%= fileDocumentIdStr %><br><%
                    }
                    %><form>Filedocument-id: <input type="text" name="id"><input type="submit" value="Send filedocument to <%= emailAddress %>."></form><%
                } else {
                    MailService mailService = imcmsSystem.getMailService() ;
                    Mail mail = new Mail(emailAddress, new String[] { emailAddress }, "imCMS filedocument "+fileDocument.getId(), "Please see attachments." );
                    mail.setAttachments( fileDocument.getFiles() );
                    mailService.sendMail( mail );
                    %>Filedocument <%= fileDocument.getId() %> sent to <%= emailAddress %>.<%
                }
            }
        %>
    </body>
</html>