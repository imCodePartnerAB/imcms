<%@ page
	
	contentType="text/html; charset=UTF-8"
	
	import="imcode.server.document.DocumentDomainObject,
	        imcode.server.document.DocumentPermissionSetDomainObject,
	        imcode.server.document.TextDocumentPermissionSetDomainObject,
	        imcode.server.document.textdocument.TextDocumentDomainObject,
	        imcode.server.user.UserDomainObject,
	        imcode.util.Html,
	        imcode.util.Utility,
	        imcode.server.Imcms,
	        org.apache.oro.text.perl.Perl5Util,
	        org.apache.commons.lang.StringUtils,
	        java.util.List,
	        com.imcode.imcms.api.*"
	
%><%@ taglib uri="imcmsvelocity" prefix="vel"
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%

UserDomainObject user = (UserDomainObject)request.getAttribute("user") ;
DocumentDomainObject document = (DocumentDomainObject)request.getAttribute("document") ;
DocumentPermissionSetDomainObject documentPermissionSet = user.getPermissionSetFor( document ) ;

String queryString = request.getQueryString();
StringBuffer baseURL = request.getRequestURL();

//TODO: experemental, refactor
if (queryString == null) {
	baseURL.append("?" + "lang=");
} else {
    queryString = queryString.replaceAll("&version=\\w*", "");	
	queryString = queryString.replaceFirst("&?lang=..", "");
	baseURL.append("?" + queryString + "&lang=");
}
	

pageContext.setAttribute("baseURL", baseURL);

/* *******************************************************************************************
 *         Get languages                                                                     *
 ******************************************************************************************* */
List<I18nLanguage> languages = I18nSupport.getLanguages();
I18nLanguage defaultLanguage = I18nSupport.getDefaultLanguage() ;
I18nLanguage currentLanguage = (null != session.getAttribute("lang")) ? (I18nLanguage)session.getAttribute("lang") : defaultLanguage ;

Meta meta = document.getMeta() ;
List<I18nMeta> i18nMetas = meta.getI18nMetas() ;

/* *******************************************************************************************
 *         BROWSER SNIFFER                                                                   *
 ******************************************************************************************* */

Perl5Util re  = new Perl5Util() ;
String uAgent = StringUtils.defaultString(request.getHeader("USER-AGENT")) ;

boolean isIE    = re.match("/(MSIE 4|MSIE 5|MSIE 5\\.5|MSIE 6)/i", uAgent) ;
boolean isIE7   = re.match("/(MSIE 7)/i", uAgent) ;
boolean isGecko = re.match("/Gecko/i", uAgent) ;

%>
<vel:velocity>
<style type="text/css">
/*<![CDATA[*/
.imcms_label,
.imcms_label:link,
.imcms_label:visited {
	display: inline !important;
	margin: 0 !important;
	padding: 0 !important;
	font: 10px/1em Verdana !important;
	color: #c00000 !important;
	text-decoration: none !important;
	background-color: #ffffcc !important;
}
.imcms_label:active,
.imcms_label:hover {
	display: inline !important;
	margin: 0 !important;
	padding: 0 !important;
	font: 10px/1em Verdana !important;
	color: #000099 !important;
	text-decoration: underline !important;
	background-color: #ffffcc !important;
}

/* adminMode */

#adminPanelDiv    { padding: 15px 0 10px 0; }
.adminPanelTable  { border-right: 1px solid #000000; border-bottom: 1px solid #000000; background-color: #f5f5f7; }
.adminPanelTd1    { padding: 2px; background-color: #20568D; }
#adminPanelTd1_1  { }
.adminPanelLogo   { font: bold 11px Verdana,Geneva,sans-serif; color: #ddddff; letter-spacing: -1px; }
#adminPanelTd1_2  {  }
.adminPanelText,
.adminPanelText SPAN { font: 11px Verdana,Geneva,sans-serif; color: #ffffff; }
#adminPanelTd1_3  {  }
.adminPanelTd2    { padding: 3px; height: 32px; vertical-align: top; }
<%
if (isIE || isIE7 || isGecko) { %>
.adminPanelTd2 A:hover IMG {<%
	if (isIE || isIE7) { %>
	<%= "filter: progid:DXImageTransform.Microsoft.BasicImage(grayscale=0, xray=0, mirror=0, invert=0, opacity=0.5, rotation=0);" %><%
	} else { %>
	<%= "-moz-opacity: 0.5;" %><%
	} %>
}<%
} %>
#adminPanelDiv B { font-weight: bold; }
/*]]>*/
</style>
<div id="adminPanelDiv">
<table border="0" cellspacing="0" cellpadding="2" class="adminPanelTable">
<tr>
	<td class="adminPanelTd1">
	<table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
	<tr>
		<td id="adminPanelTd1_1" width="25%" nowrap>
		<span style="font: 12px Verdana, Arial, Helvetica, sans-serif;" class="adminPanelLogo" ondblclick="window.open('http://www.imcms.net/')"><? templates/sv/adminbuttons/adminbuttons.html/1 ?> &nbsp;</span></td>
		<td id="adminPanelTd1_2" width="50%" align="center" nowrap>
		<span style="font: 12px Verdana, Arial, Helvetica, sans-serif;" class="adminPanelText">
		<span title="<? web/imcms/lang/jsp/admin/adminbuttons.jsp/title_id ?>"><b>Id:</b> <%= document.getId() %></span> &nbsp; <span title="<? web/imcms/lang/jsp/admin/adminbuttons.jsp/title_type ?>"><b><? templates/sv/adminbuttons/adminbuttons.html/1001 ?>:</b> <%= document.getDocumentTypeName().toLocalizedString( request ) %></span> &nbsp;</span></td>
		<td id="adminPanelTd1_3" width="25%" align="right"><%= Html.getLinkedStatusIconTemplate( document, user, request ) %></td>
	</tr>
	</table></td>
</tr>
<%
if (null != languages) { %>
<tr>
	<td style="padding: 3px 5px;">
	<table border="0" cellspacing="0" cellpadding="0">
	<tr><%
	int iCount = 0 ;
	int languagesPerRow = 7 ;
	for ( I18nMeta i18nMeta : i18nMetas ) {
		I18nLanguage lang     = i18nMeta.getLanguage() ;
		String langCode       = lang.getCode() ;
		String langName       = lang.getName() ;
		String langNameNative = lang.getNativeName() ;
		boolean isEnabled = (i18nMeta.getEnabled()) ;
		boolean isDefault = (null != defaultLanguage && defaultLanguage.equals(lang)) ;
		boolean isCurrent = (null != currentLanguage && currentLanguage.equals(lang)) ;
		String href_0     = "<a href=\"" + baseURL + langCode + "\" title=\"" + langName + "/" + langNameNative + "#DATA#\" style=\"#STYLE#\">" ;
		String href_1     = "</a>" ;
		String sData = "" ;
		if (isDefault)  sData += "default " ;
		if (isCurrent)  sData += "current " ;
		if (!isEnabled) sData += "disabled " ;
		if (!"".equals(sData)) {
			sData = " (" + sData.trim() + ")" ;
		}
		href_0 = href_0.replace("#DATA#", sData) ;
		String sStyle = "text-decoration:none; " ;
		if (isEnabled) {
			sStyle += "color:#000; " ;
		} else {
			sStyle += "color:#999; " ;
		}
		if (isCurrent)  sStyle += "font-weight:bold; " ;
		href_0 = href_0.replace("#STYLE#", sStyle.trim()) ;
		if (iCount > 0 && iCount % languagesPerRow == 0) { %>
	</tr>
	<tr><%
		} %>
		<td><%= href_0 %><img src="$contextPath/imcms/$language/images/admin/flags_iso_639_1/<%= langCode %>.gif" alt="" style="border:0;" /><%= href_1 %></td>
		<td style="width:<%= isDefault ? 4.6 : 2.2 %>em; padding-left:5px; font: 13px Verdana, Arial, sans-serif;"><%
		%><%= href_0 %><%= langCode %><%= isDefault ? "&nbsp;(d)" : "" %><%= href_1 %></td><%
		iCount++ ;
	}
	while(iCount % languagesPerRow != 0) { %>
		<td colspan="2">&nbsp;</td><%
		iCount++ ;
	} %>
	</tr>
	</table></td>
</tr><%
} %>

<%-- 
Check if published version exists
// replace symbolic version (working|published) with number 
--%>

<tr>
  <td>
    <table bolder="1">    
      <tr>
        <td>
          <%
          if (user.getDocumentShowSettings().getDocumentVersionTag() == DocumentVersionTag.PUBLISHED) {
          %>
              <b>#This is PUBLISHED version#<b>
          <% 
          } else { 
          %>
              <a href="${baseURL}<%=currentLanguage.getCode()%>&version=PUBLISHED">
                [-Show PUBLISHED version-]
             </a>
          <% 
          } 
          %>       
        </td>
      
        <td>
          <%
          if (user.getDocumentShowSettings().getDocumentVersionTag() == DocumentVersionTag.WORKING) {
          %>
              <b>#This is WORKING version#<b>
          <% 
          } else { 
          %>
              <a href="${baseURL}<%=currentLanguage.getCode()%>&version=WORKING">
                 [-Show WORKING version-]
             </a>
          <% 
          } 
          %>        
        </td>
      
	    <% 
	    if (user.canEdit(document) && user.getDocumentShowSettings().getDocumentVersionTag() == DocumentVersionTag.WORKING) {
	    %>
         <td>
           <a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId()%>&flags=4194304">
             <b><font color="red">[-PUBLISH-]</font></b>
           </a>
         </td>
	    <%	        	  
	    }
	    %>
	  </tr>      
    </table>  
  </td>
</tr>

<tr>
	<td class="adminPanelTd2" align="center" nowrap>
        <a href="$contextPath/servlet/BackDoc" id="admHrefBackdoc"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/foregaende.gif"<%
				%> alt="<? templates/sv/adminbuttons/adminbuttons.html/2001 ?>"<%
				%> title="<? templates/sv/adminbuttons/adminbuttons.html/2001 ?>" id="admBtnBackdoc" border="0" /></a><%
        if (user.canEdit( document )) {
            %><a href="<%= Utility.getAbsolutePathToDocument( request, document ) %>" id="admHrefNormal"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/normal.gif"<%
				  %> alt="<? templates/sv/adminbuttons/adminbutton2_0.html/2001 ?>"<%
				  %> title="<? templates/sv/adminbuttons/adminbutton2_0.html/2001 ?>" id="admBtnNormal" border="0" /></a><%
        }
        if( document instanceof TextDocumentDomainObject) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet ;
            if( textDocumentPermissionSet.getEditTexts() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=65536" id="admHrefText"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/btn_text.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_65536.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_65536.html/2001 ?>" id="admBtnText" border="0" /></a><%
              
              // Group editing is under same permission
              %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=2097152" id="admHrefText"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/btn_contentloop.gif"<%
              %> alt="ContentLoop"<%
              %> title="ContentLoop" id="admBtnGroup" border="0" /></a><%                            
            }
            if( textDocumentPermissionSet.getEditImages() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=131072" id="admHrefBild"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/btn_image.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_131072.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_131072.html/2001 ?>" id="admBtnBild" border="0" /></a><%
            }
            if( textDocumentPermissionSet.getEditMenus() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=262144" id="admHrefLank"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/meny.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_262144.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_262144.html/2001 ?>" id="admBtnLank" border="0" /></a><%
            }
            if( textDocumentPermissionSet.getEditTemplates() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=524288" id="admHrefUtseende"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/utseende.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_524288.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_524288.html/2001 ?>" id="admBtnUtseende" border="0" /></a><%
            }
            if( textDocumentPermissionSet.getEditIncludes() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=1048576" id="admHrefInclude"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/include.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_1048576.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_1048576.html/2001 ?>" id="admBtnInclude" border="0" /></a><%
            }
        } else {
            if( documentPermissionSet.getEdit() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=65536" id="admHrefRedigera"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/redigera.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton7_65536.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton7_65536.html/2001 ?>" id="admBtnRedigera" border="0" /></a><%
            }
        }
        if( documentPermissionSet.getEditDocumentInformation() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=1" id="admHrefDokinfo"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/dokinfo.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbutton_1.html/2001 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbutton_1.html/2001 ?>" id="admBtnDokinfo" border="0" /></a><%
        }
        if( documentPermissionSet.getEditPermissions() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=4" id="admHrefRattigheter"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/rattigheter.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbutton_4.html/2001 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbutton_4.html/2001 ?>" id="admBtnRattigheter" border="0" /></a><%
        }
        if ( !user.isDefaultUser() ) {
            %><a href="$contextPath/servlet/LogOut" id="admHrefLoggaut"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/loggaut.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbuttons.html/2002 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbuttons.html/2002 ?>" id="admBtnLoggaut" border="0" /></a><%
        }
        if ( user.canAccessAdminPages() ) {
            %><a href="$contextPath/servlet/AdminManager" target="_blank" id="admHrefAdmin"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/admin.gif"<%
            %> alt="<? templates/sv/adminbuttons/superadminbutton.html/2001 ?>"<%
            %> title="<? templates/sv/adminbuttons/superadminbutton.html/2001 ?>" id="admBtnAdmin" border="0" /></a><%
        }
        %><a href="javascript:void(0)" onClick="openHelpW('HelpStart');return(false)" target="_blank"  id="admHrefHelp"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/help.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbuttons.html/2003 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbuttons.html/2003 ?>" id="admBtnHelp" border="0" /></a></td>
</tr>
</table></div>
<script type="text/javascript">
//<![CDATA[
function openHelpW(helpDocName){
	window.open("@documentationurl@/Help?name=" + helpDocName + "&lang=$language","help");
}
//]]>
</script>
</vel:velocity>