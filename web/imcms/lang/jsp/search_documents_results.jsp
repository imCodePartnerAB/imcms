<%@ page import="com.imcode.imcms.servlet.DocumentFinder,
                 imcode.server.document.DocumentDomainObject,
                 com.imcode.imcms.servlet.SearchDocumentsPage,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                 imcode.server.document.DocumentMapper,
                 imcode.server.Imcms,
                 imcode.util.Html,
                 com.imcode.imcms.flow.Page"%>
<%
    SearchDocumentsPage searchDocumentsPage = (SearchDocumentsPage)Page.fromRequest(request);
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
    DocumentDomainObject[] documentsFound = searchDocumentsPage.getDocumentsFound() ;
    DocumentFinder documentFinder = searchDocumentsPage.getDocumentFinder() ;
    int firstDocumentIndex = searchDocumentsPage.getFirstDocumentIndex() ;
    int documentsPerPage = searchDocumentsPage.getDocumentsPerPage() ;
%>
<% if (null != documentsFound) { %>
    <table border="0" cellpadding="0" cellspacing="0" width="550">
        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="4" class="imcmsAdmText"><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b><? templates/sv/search/search_result.html/7 ?></b></font></td>
        </tr>
        <tr>
            <td colspan="4">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                    <td class="imcmsAdmText"><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><b><? templates/sv/search/search_result.html/1003 ?></b>&nbsp;&nbsp;</font></td>
                    <td class="imcmsAdmText"><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><%= documentsFound.length %></font></td>
                </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td colspan="2"><hr></td>
        </tr>
        <tr>
            <td colspan="2">
                <table border="0" cellpadding="4" cellspacing="0" width="100%">
                <%
                    if (0 == documentsFound.length) { %>
                    <tr>
                        <td colspan="3"><font size="1" face="Verdana, Arial, Helvetica, sans-serif" color="#cc0000"><? templates/sv/search/search_result_no_hit.html/1 ?></font></td>
                    </tr>
                <% } else { %>
                    <tr>
                        <th>&nbsp;</th>
                        <th align="left"><? imcms/lang/jsp/search_documents.jsp/document_id ?></th>
                        <th align="left"><? imcms/lang/jsp/search_documents.jsp/document_headline ?></th>
                        <%
                                DocumentFinder.SearchResultColumn[] searchResultColumns = documentFinder.getExtraSearchResultColumns() ;
                                for ( int i = 0; i < searchResultColumns.length; i++ ) {
                                    DocumentFinder.SearchResultColumn searchResultColumn = searchResultColumns[i];
                                    %><th align="left"><%= searchResultColumn.getName().toLocalizedString(request) %></th><%
                                }
                        %>
                        <th width="100%">&nbsp;</th>
                    </tr><%
                    int firstDocumentIndexOnNextPage = ( firstDocumentIndex + documentsPerPage );
                    for ( int i = firstDocumentIndex ; i < documentsFound.length
                                                       && i < firstDocumentIndexOnNextPage; i++ ) {
                        DocumentDomainObject document = documentsFound[i]; %>
                        <tr valign="top" <% if (0 != (i - firstDocumentIndex) % 2) { %> bgcolor="#FFFFFF"<% } %>>
                            <td>
                                    <%
                                        if (user.canEdit(document)) {
                                            %><a href="SearchDocuments?<%= searchDocumentsPage.getParameterStringWithParameter(request, SearchDocumentsPage.REQUEST_PARAMETER__TO_EDIT_DOCUMENT_ID, ""+document.getId()) %>"><%= Html.getLinkedStatusIconTemplate(document, user, request ) %></a><%
                                        } else {
                                            %>&nbsp;<%
                                        }
                                    %>
                            </td>
                            <td>
                                <font size="1" face="Verdana, Arial, Helvetica, sans-serif"><%= document.getId() %></font>
                            </td>
                            <td nowrap>
                                <font size="1" face="Verdana, Arial, Helvetica, sans-serif">
                                    <a href="GetDoc?meta_id=<%= document.getId() %>"><b><%= document.getHeadline() %></b></a>
                                </font>
                            </td>
                            <%
                                for ( int j = 0; j < searchResultColumns.length; j++ ) {
                                    DocumentFinder.SearchResultColumn searchResultColumn = searchResultColumns[j];
                                    %><td><%= searchResultColumn.render(document, request ) %></td><%
                                }
                            %>
                            <td align="right">&nbsp;
                                <% if (documentFinder.isDocumentsSelectable()) { %>
                                    <a href="SearchDocuments?<%= SearchDocumentsPage.REQUEST_PARAMETER__SELECTED_DOCUMENT_ID+"="+document.getId()+"&"+searchDocumentsPage.getParameterString(request)%>"><? imcms/lang/jsp/search_documents.jsp/select_document ?></a>
                                <% } %>
                            </td>
                        </tr>
                    <% }
                } %>
                </table>
            </td>
        </tr>
        <% if (documentsFound.length > documentsPerPage) { %>
        <tr>
            <td colspan="2"><hr></td>
        </tr>
        <tr>
            <td colspan="2">
                <%
                    if (firstDocumentIndex > 0) {
                        int firstDocumentIndexOnPreviousPage = Math.max(0, firstDocumentIndex - documentsPerPage) ; %>
                        <a href="SearchDocuments?<%= searchDocumentsPage.getParameterStringWithParameter(request, SearchDocumentsPage.REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX, ""+firstDocumentIndexOnPreviousPage) %>">
                            <? templates/sv/search/search_nav_prev.html/1001 ?></a>&nbsp;<%
                    }
                    for (int i = 0; (i * documentsPerPage) < documentsFound.length && documentsFound.length > documentsPerPage; i++) { %>
                        <a href="SearchDocuments?<%= searchDocumentsPage.getParameterStringWithParameter(request, SearchDocumentsPage.REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX, ""+(i*documentsPerPage)) %>"><%= i + 1 %></a>
                        &nbsp;<%
                    }
                    int firstDocumentIndexOnNextPage = ( firstDocumentIndex + documentsPerPage );
                    if (documentsFound.length > firstDocumentIndexOnNextPage) { %>
                        <a href="SearchDocuments?<%= searchDocumentsPage.getParameterStringWithParameter(request, SearchDocumentsPage.REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX, ""+firstDocumentIndexOnNextPage) %>">
                            <? templates/sv/search/search_nav_next.html/1001 ?>
                        </a><%
                    } %>
            </td>
        </tr>
        <% } %>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
    </table>
<% } %>