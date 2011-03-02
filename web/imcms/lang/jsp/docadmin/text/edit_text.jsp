<%@ page
	
	import="imcode.server.document.DocumentDomainObject,
	        imcode.server.user.UserDomainObject,
	        imcode.util.Html,
	        imcode.util.Utility,
	        imcode.server.Imcms,
	        java.net.URLEncoder,
	        org.apache.commons.lang.math.NumberUtils,
	        org.apache.commons.lang.StringUtils,
	        org.apache.commons.lang.RandomStringUtils"
	
  pageEncoding="UTF-8"
  
%><%!

final static boolean INLINE_EDITING_ACTIVE = true ;

%><%

UserDomainObject user         = Utility.getLoggedOnUser( request ) ;
DocumentDomainObject document = (DocumentDomainObject)request.getAttribute( "document" ) ;
Integer textIndex             = (Integer)request.getAttribute( "textIndex" ) ;
String label                  = (String)request.getAttribute( "label" ) ;
String content                = (String)request.getAttribute( "content" ) ;
String rows                   = (String) request.getAttribute("rows") ;
String[] formats              = (String[])request.getAttribute( "formats" ) ;
int metaId                    = document.getId() ;

String cp   = request.getContextPath() ;
String lang = user.getLanguageIso639_2() ;

String url = cp +"/servlet/ChangeText?meta_id=" + metaId + "&amp;txt=" + textIndex ;

String imcmsInlineEditClassName = "imcmsInlineEditEditor" ;
String mode = "html" ;

if (null != label) {
	url += "&amp;label=" + URLEncoder.encode( Html.removeTags(label), Imcms.UTF_8_ENCODING) ;
}
if (null != formats) {
	for ( String format : formats ) {
		if ( StringUtils.isNotBlank(format) ) {
			url += "&amp;format=" + URLEncoder.encode(format, Imcms.UTF_8_ENCODING) ;
			imcmsInlineEditClassName = "imcmsInlineEditTextarea" ;
		}
	}
	if (1 == formats.length && "text".equals(formats[0])) {
		mode = "text" ;
	}
}
if (null != rows && NumberUtils.isDigits( rows )) {
	url += "&amp;rows=" + rows ;
	if ("1".equals(rows)) {
		imcmsInlineEditClassName = "imcmsInlineEditInput" ;
	}
}

// Added random string - If it's more editable instances. Else JS might fail.
String uniqueId = "imcmsInlineTextEdit_" + metaId + "_" + textIndex + "_" + mode + "_" + RandomStringUtils.random(8, true, true) ;

%><div id="<%= uniqueId %>_dummy" style="display:none;"></div><%-- Dummy for width check --%>
<a id="<%= uniqueId %>_link" href="<%= url %>" class="imcms_text_admin imcms_label" rev="<%= uniqueId %>"><%= label %><%
	%><img src="<%= cp %>/imcms/<%= lang %>/images/admin/red.gif" alt="" style="border:0 !important;" /></a><%
if (INLINE_EDITING_ACTIVE) { %>
<span id="<%= uniqueId %>_container" rev="<%= uniqueId %>" class="imcmsInlineEditContainer <%= imcmsInlineEditClassName %>"><%= content %></span><%
} else { %>
<%= content %><%
} %>
<a id="<%= uniqueId %>_link2" href="<%= url %>" class="imcms_text_admin" rev="<%= uniqueId %>"><%
	%><img src="<%= cp %>/imcms/<%= lang %>/images/admin/ico_txt.gif" alt="" style="border:0 !important;" /></a>
