<%@ page import="imcode.server.document.DocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Html,
                 imcode.util.Utility,
                 org.apache.commons.lang.math.NumberUtils,
                 java.net.URLEncoder, org.apache.commons.lang.StringUtils"%><%

    DocumentDomainObject document = (DocumentDomainObject)request.getAttribute( "document" ) ;
    Integer textIndex = (Integer)request.getAttribute( "textIndex" ) ;
    String label = (String)request.getAttribute( "label" ) ;
    String content = (String)request.getAttribute( "content" ) ;
    String rows = (String) request.getAttribute("rows") ;
    String[] formats = (String[])request.getAttribute( "formats" ) ;
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;

    String url = request.getContextPath() +"/servlet/ChangeText?meta_id="+document.getId() +"&amp;txt="+textIndex ;
    if (null != label) {
        url += "&amp;label="+URLEncoder.encode( Html.removeTags(label) ) ;
    }
    if (null != formats) {
        for ( String format : formats ) {
            if ( StringUtils.isNotBlank(format) ) {
                url += "&amp;format=" + URLEncoder.encode(format);
            }
        }
    }
    if (null != rows && NumberUtils.isDigits( rows )) {
        url += "&amp;rows="+rows ;
    }
%><a href="<%= url %>" class="imcms_label"><%= label %><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/red.gif" alt="" style="border:0 !important;" /></a>
<%= content %>
<a href="<%= url %>"><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif" alt="" style="border:0 !important;" /></a>
