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
    String IMG_PATH  = request.getContextPath()+"/imcms/"+Utility.getLoggedOnUser( request ).getLanguageIso639_2()+"/images/admin/" ;
%>
<% if (null != documentsFound) { %>
    <table border="0" cellspacing="0" cellpadding="2" width="656">
        <tr>
            <td><span class="imcmsAdmHeading"><? templates/sv/search/search_result.html/7 ?></span></td>
        </tr>
        <tr>
            <td><img src="<%= IMG_PATH %>/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
        </tr>
        <tr>
            <td class="imcmsAdmText"><b><? templates/sv/search/search_result.html/1003 ?></b>&nbsp;&nbsp;
            <%= documentsFound.length %></td>
        </tr>
        <tr>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="15"></td>
        </tr>
        <tr>
            <td>
						<table border="0" cellspacing="0" cellpadding="2" width="100%"><%
					if (0 == documentsFound.length) { %>
						<tr>
							<td colspan="3"><span class="imcmsAdmText" style="color=:#cc0000"><? templates/sv/search/search_result_no_hit.html/1 ?></span></td>
						</tr><%
					} else { %>
						<tr>
							<td width="40">&nbsp;</td>
							<td width="50" class="imcmsAdmText"><b><? imcms/lang/jsp/search_documents.jsp/document_id ?></b></td>
							<td class="imcmsAdmText"><b><? imcms/lang/jsp/search_documents.jsp/document_headline ?></b></td><%
						DocumentFinder.SearchResultColumn[] searchResultColumns = documentFinder.getExtraSearchResultColumns() ;
						for ( int i = 0; i < searchResultColumns.length; i++ ) {
							DocumentFinder.SearchResultColumn searchResultColumn = searchResultColumns[i]; %>
							<td class="imcmsAdmText">&nbsp;<b><%= searchResultColumn.getName().toLocalizedString(request) %></b></td><%
						} %>
							<td>&nbsp;</td>
						</tr>
						<tr>
							<td colspan="<%= 4 + searchResultColumns.length %>"><img src="<%= IMG_PATH %>/1x1_cccccc.gif" width="100%" height="1"></td>
						</tr><%
						int firstDocumentIndexOnNextPage = ( firstDocumentIndex + documentsPerPage );
						for ( int i = firstDocumentIndex ; i < documentsFound.length && i < firstDocumentIndexOnNextPage; i++ ) {
							DocumentDomainObject document = documentsFound[i]; %>
						<tr valign="top"<%= ((i - firstDocumentIndex) % 2 == 0) ? " bgcolor=\"#FFFFFF\"" : "" %>>
							<td align="center"><%
							if (user.canEditDocumentInformationFor(document)) {
								%><a href="SearchDocuments?<%= searchDocumentsPage.getParameterStringWithParameter(request, SearchDocumentsPage.REQUEST_PARAMETER__TO_EDIT_DOCUMENT_ID, ""+document.getId()) %>"><%
							}
							%><%= Html.getStatusIconTemplate(document, user) %><%
							if (user.canEditDocumentInformationFor(document)) {
								%></a><%
							} %></td>
							<td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3"><br><%
							if (user.canEditDocumentInformationFor(document)) {
								%><a href="AdminDoc?meta_id=<%= document.getId() %>" title="AdminDoc?meta_id=<%= document.getId() %>"><%
							}
							%><%= document.getId() %><%
							if (user.canEditDocumentInformationFor(document)) {
								%></a><%
							} %></td>
							<td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3"><br><%
							%><a href="GetDoc?meta_id=<%= document.getId() %>"<%
								%><%= (user.canEditDocumentInformationFor(document)) ? " title=\"GetDoc?meta_id=" + document.getId() + "\"" : "" %>><%
								%><%= document.getHeadline() %><%
							%></a></td><%
							for ( int j = 0; j < searchResultColumns.length; j++ ) {
								DocumentFinder.SearchResultColumn searchResultColumn = searchResultColumns[j]; %>
							<td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3"><br><%
								%><%= searchResultColumn.render(document, request, response ) %></td><%
							} %>
							<td align="right"><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="3"><br><%
							if (documentFinder.isDocumentsSelectable()) {
								%><a href="SearchDocuments?<%=
								searchDocumentsPage.getParameterStringWithParameter(request, SearchDocumentsPage.REQUEST_PARAMETER__SELECTED_DOCUMENT_ID, ""+document.getId())
								%>"><? imcms/lang/jsp/search_documents.jsp/select_document ?></a><%
							} %></td>
						</tr><%
						}
					} %>
						</table></td>
        </tr>
        <% if (documentsFound.length > documentsPerPage) { %>
        <tr>
            <td><img src="<%= IMG_PATH %>/1x1_20568d.gif" width="100%" height="1" vspace="8"></td>
        </tr>
        <tr>
            <td align="center">
                <%
                    if (firstDocumentIndex > 0) {
                        int firstDocumentIndexOnPreviousPage = Math.max(0, firstDocumentIndex - documentsPerPage) ; %>
                        <a href="SearchDocuments?<%= searchDocumentsPage.getParameterStringWithParameter(request, SearchDocumentsPage.REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX, ""+firstDocumentIndexOnPreviousPage) %>">
                            <? templates/sv/search/search_nav_prev.html/1001 ?></a>&nbsp;<%
                    } else { %>
                        <span style="color:#999999"><? templates/sv/search/search_nav_prev.html/1001 ?></span>&nbsp;<%
                    }
                    for (int i = 0; (i * documentsPerPage) < documentsFound.length && documentsFound.length > documentsPerPage; i++) {
                      int iActivePageIndex = (firstDocumentIndex / documentsPerPage) ;
                      if (i == iActivePageIndex) { %>
                        <%= i + 1 %>
                        &nbsp;<%
                      } else { %>
                        <a href="SearchDocuments?<%= searchDocumentsPage.getParameterStringWithParameter(request, SearchDocumentsPage.REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX, ""+(i*documentsPerPage)) %>"><%= i + 1 %></a>
                        &nbsp;<%
                      }
                    }
                    int firstDocumentIndexOnNextPage = ( firstDocumentIndex + documentsPerPage );
                    if (documentsFound.length > firstDocumentIndexOnNextPage) { %>
                        <a href="SearchDocuments?<%= searchDocumentsPage.getParameterStringWithParameter(request, SearchDocumentsPage.REQUEST_PARAMETER__FIRST_DOCUMENT_INDEX, ""+firstDocumentIndexOnNextPage) %>">
                            <? templates/sv/search/search_nav_next.html/1001 ?>
                        </a><%
                    } else { %>
                        <span style="color:#999999"><? templates/sv/search/search_nav_next.html/1001 ?></span><%
                    } %>
            </td>
        </tr>
        <% } %>
        <tr>
            <td><img src="<%= IMG_PATH %>/1x1.gif" width="1" height="15"></td>
        </tr>
    </table>
<% } %>