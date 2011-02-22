<%@ page import="imcode.server.document.DocumentDomainObject,
                 imcode.server.user.UserDomainObject,
                 imcode.util.Html,
                 imcode.util.Utility,
                 org.apache.commons.lang.math.NumberUtils,
                 java.net.URLEncoder, org.apache.commons.lang.StringUtils,
                 imcode.server.Imcms"
	
%><%

DocumentDomainObject document = (DocumentDomainObject)request.getAttribute( "document" ) ;
Integer textIndex = (Integer)request.getAttribute( "textIndex" ) ;
String label = (String)request.getAttribute( "label" ) ;
String content = (String)request.getAttribute( "content" ) ;
String rows = (String) request.getAttribute("rows") ;
String[] formats = (String[])request.getAttribute( "formats" ) ;
UserDomainObject user = Utility.getLoggedOnUser( request ) ;
int metaId = document.getId() ;

String url = request.getContextPath() +"/servlet/ChangeText?meta_id=" + metaId + "&amp;txt=" + textIndex ;

if (null != label) {
	url += "&amp;label=" + URLEncoder.encode( Html.removeTags(label), Imcms.UTF_8_ENCODING) ;
}
if (null != formats) {
	for ( String format : formats ) {
		if ( StringUtils.isNotBlank(format) ) {
			url += "&amp;format=" + URLEncoder.encode(format, Imcms.UTF_8_ENCODING);
		}
	}
}
if (null != rows && NumberUtils.isDigits( rows )) {
	url += "&amp;rows=" + rows ;
}

String uniqueId = metaId + "_" + textIndex ; // If it's one editable instance - Else JS might fail.

%><div id="imcms_text_field_dummy_<%= uniqueId %>" style="display:none;"></div><a href="<%= url %>" class="imcms_text_admin imcms_label" rev="<%= uniqueId %>"><%= label %><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/red.gif" alt="" style="border:0 !important;" /></a>
<%= content %>
<a href="<%= url %>" class="imcms_text_admin" rev="<%= uniqueId %>"><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/ico_txt.gif" alt="" style="border:0 !important;" /></a>
