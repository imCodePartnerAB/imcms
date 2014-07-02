<%@ page import="imcode.server.document.DocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Html,
                 imcode.util.Utility,
                 org.apache.commons.lang3.StringEscapeUtils,
                 java.net.URLEncoder" %>
<%

    DocumentDomainObject document = (DocumentDomainObject) request.getAttribute("document");
    Integer no = (Integer) request.getAttribute("loopNo");
    String label = (String) request.getAttribute("label");
    String content = (String) request.getAttribute("content");
    UserDomainObject user = Utility.getLoggedOnUser(request);

    String url = request.getContextPath() + "/imcms/docadmin/loop?meta_id=" + document.getId() + "&no=" + no;
    if (null != label) {
        url += "&label=" + URLEncoder.encode(Html.removeTags(label));
    }

%><a href="<%= StringEscapeUtils.escapeHtml4(url) %>" class="imcms_label"><%= label %><img
        src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/red.gif"
        border="0"></a>
<%= content %>
<a href="<%= StringEscapeUtils.escapeHtml4(url) %>"><img
        src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif"
        border="0"></a>
