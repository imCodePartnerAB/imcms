<%@ page import="java.net.URLEncoder,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.document.DocumentDomainObject,
                 imcode.util.Html,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Utility"%>
<%
    DocumentDomainObject document = (DocumentDomainObject)request.getAttribute( "document" ) ;
    Integer textIndex = (Integer)request.getAttribute( "textIndex" ) ;
    String label = (String)request.getAttribute( "label" ) ;
    String content = (String)request.getAttribute( "content" ) ;
    String[] formats = (String[])request.getAttribute( "formats" ) ;
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;

    String url = request.getContextPath() +"/servlet/ChangeText?meta_id="+document.getId() +"&txt="+textIndex ;
    if (null != label) {
        url += "&label="+URLEncoder.encode( Html.removeTags(label) ) ;
    }
    if (null != formats) {
        for ( int i = 0; i < formats.length; i++ ) {
            String format = formats[i];
            url += "&format="+URLEncoder.encode( format ) ;
        }
    }
%><a href="<%= StringEscapeUtils.escapeHtml( url ) %>" class="imcms_label"><%= label %><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/red.gif" border="0"></a>
<%= content %>
<a href="<%= StringEscapeUtils.escapeHtml( url ) %>"><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif" border="0"></a>
