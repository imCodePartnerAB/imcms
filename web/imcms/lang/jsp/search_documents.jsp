<%@ page import="com.imcode.imcms.servlet.SearchDocuments,
                 imcode.server.ApplicationServer,
                 imcode.server.document.SectionDomainObject,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.util.Html,
                 org.apache.commons.collections.Transformer,
                 imcode.server.document.DocumentDomainObject,
                 imcode.util.Utility,
                 imcode.server.user.UserDomainObject,
                 org.apache.commons.lang.StringUtils,
                 java.util.*,
                 org.apache.commons.collections.set.ListOrderedSet,
                 java.net.URLEncoder,
                 imcode.util.HttpSessionUtils,
                 com.imcode.imcms.servlet.DocumentFinder,
                 com.imcode.imcms.servlet.SearchDocumentsPage,
                 imcode.util.Html"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<%
    SearchDocumentsPage searchDocumentsPage = (SearchDocumentsPage)request.getAttribute( SearchDocumentsPage.REQUEST_ATTRIBUTE__PAGE ) ;
    DocumentFinder documentFinder = DocumentFinder.getInstance( request ) ;
    DocumentDomainObject[] documentsFound = searchDocumentsPage.getDocumentsFound() ;
    int firstDocumentIndex = searchDocumentsPage.getFirstDocumentIndex() ;
    int documentsPerPage = searchDocumentsPage.getDocumentsPerPage() ;
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
%>
<vel:velocity>
<html>
<head>
<title><? templates/sv/search/search_documents.html/1 ?></title>

<link rel="stylesheet" href="$contextPath/imcms/css/imcms_admin.css.jsp" type="text/css">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

</head>

<body bgcolor="#FFFFFF" onLoad="document.forms[0].question_field.focus()">
#gui_outer_start()
#gui_head( "<? templates/sv/search/search_documents.html/1 ?>" )

<form method="GET" action="SearchDocuments">

<table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td><input type="button" value="<? global/help ?>" title="<? global/openthehelppage ?>" class="imcmsFormBtn" onClick="openHelpW(101)"></td>
        <% if (documentFinder.isCancelable()) { %>
            <td><input class="imcmsFormBtn" type="submit" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? global/back ?>"></td>
        <% } %>
    </tr>
</table>

#gui_mid()

<% if (null != HttpSessionUtils.getSessionAttributeNameFromRequest(request, DocumentFinder.REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER)) { %>
    <input type="hidden" name="<%= DocumentFinder.REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER %>" value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest(request, DocumentFinder.REQUEST_ATTRIBUTE_OR_PARAMETER__DOCUMENT_FINDER) %>">
<% } %>
    <table width="550" border="0" cellspacing="0">
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td colspan="2" class="imcmsAdmText"><font face="Verdana, Arial, Helvetica, sans-serif"><? templates/sv/search/search_documents.html/2 ?></font>
        </tr>
        <tr>
            <td colspan="2" class="imcmsAdmText"><font face="Verdana, Arial, Helvetica, sans-serif">
                <input type="text" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__QUERY_STRING %>" value="<%= StringEscapeUtils.escapeHtml(StringUtils.defaultString(searchDocumentsPage.getQueryString())) %>" size="65" style="width: 100%"></font></td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;</td>
        </tr>
        <tr>
            <td width="100%">
                <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                        <td class="imcmsAdmText"><font size="2" face="Verdana, Arial, Helvetica, sans-serif"><? templates/sv/search/search_documents.html/4 ?>&nbsp;</font></td>
                        <td class="imcmsAdmText">
                            <select name="<%= SearchDocumentsPage.REQUEST_PARAMETER__DOCUMENTS_PER_PAGE %>" size="1">
                                <%
                                    Integer[] ranges = new Integer[] {
                                        new Integer( 10 ),
                                        new Integer( 100 ),
                                        new Integer( 1000 ),
                                    } ;
                                %>
                                <%=
                                    Html.createOptionList(Arrays.asList(ranges), new Integer( documentsPerPage ), new Transformer() {
                                        public Object transform( Object input ) {
                                            return new String[] {""+input, ""+input} ;
                                        }
                                    })
                                %>
                            </select></td>
                        <td class="imcmsAdmText">
                            <font face="Verdana, Arial, Helvetica, sans-serif">&nbsp;&nbsp;<? templates/sv/search/search_documents.html/5 ?>&nbsp;</font>
                        </td>
                        <td class="imcmsAdmText">
                            <select name="<%= SearchDocumentsPage.REQUEST_PARAMETER__SECTION_ID %>">
                                <option value=""><? templates/sv/search/search_documents.html/3 ?></option>
                                <%
                                    SectionDomainObject[] sections = ApplicationServer.getIMCServiceInterface().getDocumentMapper().getAllSections() ;
                                    Arrays.sort(sections) ;
                                    SectionDomainObject selectedSection = searchDocumentsPage.getSection() ; %>
                                    <%= Html.createOptionList(Arrays.asList(sections), selectedSection, new Transformer() {
                                        public Object transform( Object input ) {
                                            SectionDomainObject section = (SectionDomainObject)input ;
                                            return new String[] { ""+section.getId(), section.getName() } ;
                                        }
                                    } ) %>
                            </select>
                        </td>
                    </tr>
                </table>
            </td>
            <td align="right">
                <input type="submit" name="<%= SearchDocumentsPage.REQUEST_PARAMETER__SEARCH_BUTTON %>" class="imcmsFormBtn" value="<? templates/sv/search/search_documents.html/1 ?>">
            </td>
        </tr>
        <tr>
          <td colspan="2">&nbsp;</td>
        </tr>
    </table>
</form>
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
                        <th width="100%">&nbsp;</th>
                    </tr><%
                    int firstDocumentIndexOnNextPage = ( firstDocumentIndex + documentsPerPage );
                    for ( int i = firstDocumentIndex ; i < documentsFound.length
                                                       && i < firstDocumentIndexOnNextPage; i++ ) {
                        DocumentDomainObject document = documentsFound[i]; %>
                        <tr valign="top" <% if (0 != (i - firstDocumentIndex) % 2) { %> bgcolor="#FFFFFF"<% } %>>
                            <td>
                                    <%
                                        if (ApplicationServer.getIMCServiceInterface().getDocumentMapper().userHasMoreThanReadPermissionOnDocument(user, document)) {
                                            %><%= Html.getLinkedStatusIconTemplate(document, user) %><%
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
#gui_end_of_page()
</vel:velocity>
