<%@ page import="com.imcode.imcms.servlet.superadmin.ListDocuments,
                 org.apache.commons.lang.math.IntRange,
                 imcode.server.document.DocumentDomainObject,
                 com.imcode.imcms.servlet.superadmin.DocumentReferences,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.ApplicationServer,
                 imcode.server.document.DocumentMapper,
                 imcode.util.Utility,
                 imcode.server.user.UserDomainObject,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 imcode.server.document.textdocument.MenuDomainObject,
                 java.util.*,
                 java.net.URLEncoder,
                 org.apache.commons.lang.ObjectUtils"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<% ListDocuments.FormData formData = (ListDocuments.FormData)request.getAttribute( ListDocuments.REQUEST_ATTRIBUTE__FORM_DATA ) ;%>
<vel:velocity>

    #gui_start_of_page( "<? imcms/lang/jsp/document_list.jsp/title ?>" "AdminManager" "" "37" "" )

    <form method="GET" action="ListDocuments">
        <? imcms/lang/jsp/document_list.jsp/1003 ?>
        <input type="text" name="<%= ListDocuments.PARAMETER__LIST_START %>" value="<%= formData.selectedRange.getMinimumInteger() %>" size="6">
        <? imcms/lang/jsp/document_list.jsp/1004 ?>
        <input type="text" name="<%= ListDocuments.PARAMETER__LIST_END %>" value="<%= formData.selectedRange.getMaximumInteger() %>" size="6">
        <input type="submit" class="imcmsFormBtn" name="<%= ListDocuments.PARAMETER_BUTTON__LIST %>" value="<? imcms/lang/jsp/document_list.jsp/2002 ?>">
    </form>
    <% if (null != formData.documentsIterator) { %>
        <table width="600">
            <tr>
                <th align="left"><? web/imcms/lang/jsp/heading_type ?></th>
                <th align="left"><? web/imcms/lang/jsp/heading_adminlink ?></th>
                <th align="left"><? web/imcms/lang/jsp/heading_references ?></th>
                <th align="left"><? web/imcms/lang/jsp/heading_status ?></th>
                <th align="left"><? imcms/lang/jsp/document_list.jsp/heading_child_documents ?></th>
            </tr>
            <% DocumentMapper documentMapper = ApplicationServer.getIMCServiceInterface().getDocumentMapper();
            UserDomainObject user = Utility.getLoggedOnUser(request);
            Map documentTypes = documentMapper.getAllDocumentTypeIdsAndNamesInUsersLanguage(user) ;

            while ( formData.documentsIterator.hasNext() ) {
                DocumentDomainObject document = (DocumentDomainObject)formData.documentsIterator.next();
                DocumentMapper.TextDocumentMenuIndexPair[] documentMenuPairsContainingDocument = documentMapper.getDocumentMenuPairsContainingDocument( document );
            %>
                <tr>
                    <td><%= StringEscapeUtils.escapeHtml((String)documentTypes.get(new Integer( document.getDocumentTypeId() )))%></td>
                    <td>
                        <a name="<%= document.getId() %>" href="AdminDoc?meta_id=<%= document.getId() %>">
                            <%= document.getId() %> - <%= StringEscapeUtils.escapeHtml( document.getHeadline() ) %>
                        </a>
                    </td>
                    <td>
                        <% if (documentMenuPairsContainingDocument.length > 0 ) {
                            String backUrl = "ListDocuments?" + ObjectUtils.defaultIfNull(request.getQueryString(),"") ;
                            String escapedBackUrl = URLEncoder.encode(backUrl);
                        %>
                            <a href="<%= request.getContextPath() %>/servlet/DocumentReferences?<%= DocumentReferences.REQUEST_PARAMETER__RETURNURL %>=<%= escapedBackUrl %>&<%= DocumentReferences.REQUEST_PARAMETER__REFERENCED_DOCUMENT_ID %>=<%= document.getId() %>">
                        <% } %>
                            <%= documentMenuPairsContainingDocument.length %> <? web/imcms/lang/jsp/parent_count_unit ?>
                        <% if (documentMenuPairsContainingDocument.length > 0 ) { %></a><% } %>
                    </td>
                    <td><%= Utility.getLinkedStatusIconTemplate( document, user ) %></td>
                    <td>
                    <% if (document instanceof TextDocumentDomainObject) {
                        TextDocumentDomainObject textDocument = (TextDocumentDomainObject)document ;
                        List childDocuments = new ArrayList(textDocument.getChildDocuments());
                        if (!childDocuments.isEmpty()) { %>
                                <ul style="padding: 0.5ex;">
                                <%
                                    Collections.sort(childDocuments, DocumentDomainObject.DocumentComparator.ID) ;
                                    for ( Iterator iterator = childDocuments.iterator(); iterator.hasNext(); ) {
                                        DocumentDomainObject childDocument = (DocumentDomainObject)iterator.next();
                                        %><li>
                                            <a href="#<%= childDocument.getId() %>">
                                                <%= childDocument.getId() %> - <%= StringEscapeUtils.escapeHtml(childDocument.getHeadline()) %>
                                            </a>
                                        </li><%
                                    }
                                %>
                                </ul>
                        <% } %>
                    <% } %>
                    </td>
                </tr>
            <% } %>
        </table>
        <% } %>
    #gui_end_of_page()
</vel:velocity>
