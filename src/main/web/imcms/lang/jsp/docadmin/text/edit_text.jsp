<%@ page import="imcode.server.document.DocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Html,
                 imcode.util.Utility,
                 org.apache.commons.lang.StringEscapeUtils,
                 org.apache.commons.lang.math.NumberUtils,
                 java.net.URLEncoder, org.apache.commons.lang.StringUtils"%>
<%@ page import="com.imcode.imcms.api.ContentLoop" %>
<%@ page import="com.imcode.imcms.api.Content" %><%

    DocumentDomainObject document = (DocumentDomainObject)request.getAttribute( "document" ) ;
    Integer textIndex = (Integer)request.getAttribute( "textIndex" ) ;
    String label = (String)request.getAttribute( "label" ) ;
    String content = (String)request.getAttribute( "content" ) ;
    String rows = (String) request.getAttribute("rows") ;
    String[] formats = (String[])request.getAttribute( "formats" ) ;
    UserDomainObject user = Utility.getLoggedOnUser( request ) ;

    String url = request.getContextPath() +"/servlet/ChangeText?meta_id="+document.getId() +"&txt="+textIndex ;
    if (null != label) {
        url += "&label="+URLEncoder.encode( Html.removeTags(label) ) ;
    }
    if (null != formats) {
        for ( String format : formats ) {
            if ( StringUtils.isNotBlank(format) ) {
                url += "&format=" + URLEncoder.encode(format);
            }
        }
    }
    if (null != rows && NumberUtils.isDigits( rows )) {
        url += "&rows="+rows ;
    }

    Content loopContent = (Content)request.getAttribute("tag.text.loop.content");

    if (loopContent != null) {
            ContentLoop loop = (ContentLoop)request.getAttribute("tag.text.loop");

        url += "&loop_no="+loop.getNo()+"&content_index="+loopContent.getNo();
    }


%><a href="<%= StringEscapeUtils.escapeHtml( url ) %>" class="imcms_label"><%= label %><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/red.gif" border="0"></a>
<%= content %>
<a href="<%= StringEscapeUtils.escapeHtml( url ) %>"><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif" border="0"></a>
