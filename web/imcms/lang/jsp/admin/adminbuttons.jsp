<%@ page contentType="text/html" import="imcode.server.user.UserDomainObject,
                 imcode.util.Utility,
                                         imcode.server.document.textdocument.TextDocumentDomainObject,
                                         imcode.util.Html,
                                         imcode.server.document.*"%><%@taglib uri="/WEB-INF/velocitytag.tld" prefix="vel" %>
<%
    UserDomainObject user = (UserDomainObject)request.getAttribute("user") ;
    DocumentDomainObject document = (DocumentDomainObject)request.getAttribute("document") ;
    DocumentPermissionSetDomainObject documentPermissionSet = user.getPermissionSetFor( document ) ;
%><vel:velocity>
<style type="text/css">
<!--
/* IMCMS SPECIFIC */

/* imCMS label. Ändra utseende om så önskas. */

.imcms_label, .imcms_label:link, .imcms_label:visited { font: 10px Verdana; color:#c00000; text-decoration:none; background-color:#ffffcc }
.imcms_label:active, .imcms_label:hover { font: 10px Verdana; color:#000099; text-decoration:underline; background-color:#ffffcc }

/* adminMode */

#adminPanelDiv      { padding: 15px 0px 10px 0px; }
 .adminPanelTable    { border-right: 1px solid #000000; border-bottom: 1px solid #000000; background-color:#f5f5f7; }
  .adminPanelTd1      { padding:2px; background-color:#20568D; }
    #adminPanelTd1_1  { }
      .adminPanelLogo  { font: bold 11px Verdana,Geneva,sans-serif; color:#ddddff; letter-spacing:-1px; }
    #adminPanelTd1_2  {  }
      .adminPanelText, .adminPanelText SPAN { font: 11px Verdana,Geneva,sans-serif; color:#ffffff; }
    #adminPanelTd1_3  {  }
  .adminPanelTd2      { padding:3px; height:32; vertical-align:top; }

  .adminPanelTd2 A IMG { filter: }
  .adminPanelTd2 A:hover IMG {
     filter: progid:DXImageTransform.Microsoft.BasicImage(grayscale=0, xray=0, mirror=0, invert=0, opacity=0.5, rotation=0);
		 -moz-opacity: 0.5;
     /* position:relative; top:+1; left:+1; */
	}
B { font-weight: bold; }
-->
</style>
<div id="adminPanelDiv">
<table border="0" cellspacing="0" cellpadding="2" class="adminPanelTable">
<tr>
	<td class="adminPanelTd1">
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td id="adminPanelTd1_1" width="25%" nowrap>
		<font face="Verdana, Arial, Helvetica, sans-serif" size="2" class="adminPanelLogo" onDblClick="window.open('http://www.imcms.net/')"><? templates/sv/adminbuttons/adminbuttons.html/1 ?> &nbsp;</font></td>
		<td id="adminPanelTd1_2" width="50%" align="center" nowrap>
		<font face="Verdana, Arial, Helvetica, sans-serif" size="2" class="adminPanelText">
		<span title="<? web/imcms/lang/jsp/admin/adminbuttons.jsp/title_id ?>"><b>Id:</b> <%= document.getId() %></span> &nbsp; <span title="<? web/imcms/lang/jsp/admin/adminbuttons.jsp/title_type ?>"><b><? templates/sv/adminbuttons/adminbuttons.html/1001 ?>:</b> <%= document.getDocumentTypeName().toLocalizedString( request ) %></span> &nbsp;</font></td>
		<td id="adminPanelTd1_3" width="25%" align="right"><%= Html.getLinkedStatusIconTemplate( document, user, request ) %></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td class="adminPanelTd2" align="center" nowrap>
        <a href="$contextPath/servlet/BackDoc" id="admHrefBackdoc"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/foregaende.gif"<%
				%> alt="<? templates/sv/adminbuttons/adminbuttons.html/2001 ?>"<%
				%> title="<? templates/sv/adminbuttons/adminbuttons.html/2001 ?>" id="admBtnBackdoc" border="0"></a><%
        if (user.canEdit( document )) {
            %><a href="<%= Utility.getAbsolutePathToDocument( request, document ) %>" id="admHrefNormal"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/normal.gif"<%
				  %> alt="<? templates/sv/adminbuttons/adminbutton2_0.html/2001 ?>"<%
				  %> title="<? templates/sv/adminbuttons/adminbutton2_0.html/2001 ?>" id="admBtnNormal" border="0"></a><%
        }
        if( document instanceof TextDocumentDomainObject) {
            TextDocumentPermissionSetDomainObject textDocumentPermissionSet = (TextDocumentPermissionSetDomainObject)documentPermissionSet ;
            if( textDocumentPermissionSet.getEditTexts() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=65536" id="admHrefText"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/btn_text.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_65536.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_65536.html/2001 ?>" id="admBtnText" border="0"></a><%
            }
            if( textDocumentPermissionSet.getEditImages() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=131072" id="admHrefBild"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/btn_image.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_131072.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_131072.html/2001 ?>" id="admBtnBild" border="0"></a><%
            }
            if( textDocumentPermissionSet.getEditMenus() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=262144" id="admHrefLank"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/meny.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_262144.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_262144.html/2001 ?>" id="admBtnLank" border="0"></a><%
            }
            if( textDocumentPermissionSet.getEditTemplates() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=524288" id="admHrefUtseende"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/utseende.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_524288.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_524288.html/2001 ?>" id="admBtnUtseende" border="0"></a><%
            }
            if( textDocumentPermissionSet.getEditIncludes() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=1048576" id="admHrefInclude"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/include.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton2_1048576.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton2_1048576.html/2001 ?>" id="admBtnInclude" border="0"></a><%
            }
        } else {
            NonTextDocumentPermissionSetDomainObject nonTextDocumentPermissionSet = (NonTextDocumentPermissionSetDomainObject)documentPermissionSet ;
            if( nonTextDocumentPermissionSet.getEdit() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=65536" id="admHrefRedigera"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/redigera.gif"<%
              %> alt="<? templates/sv/adminbuttons/adminbutton7_65536.html/2001 ?>"<%
              %> title="<? templates/sv/adminbuttons/adminbutton7_65536.html/2001 ?>" id="admBtnRedigera" border="0"></a><%
            }
        }
        if( documentPermissionSet.getEditDocumentInformation() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=1" id="admHrefDokinfo"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/dokinfo.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbutton_1.html/2001 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbutton_1.html/2001 ?>" id="admBtnDokinfo" border="0"></a><%
        }
        if( documentPermissionSet.getEditPermissions() ) {
                %><a href="$contextPath/servlet/AdminDoc?meta_id=<%= document.getId() %>&flags=4" id="admHrefRattigheter"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/rattigheter.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbutton_4.html/2001 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbutton_4.html/2001 ?>" id="admBtnRattigheter" border="0"></a><%
        }
        if ( !user.isDefaultUser() ) {
            %><a href="$contextPath/servlet/LogOut" id="admHrefLoggaut"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/loggaut.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbuttons.html/2002 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbuttons.html/2002 ?>" id="admBtnLoggaut" border="0"></a><%
        }
        if ( user.canAccessAdminPages() ) {
            %><a href="$contextPath/servlet/AdminManager" target="_blank" id="admHrefAdmin"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/admin.gif"<%
            %> alt="<? templates/sv/adminbuttons/superadminbutton.html/2001 ?>"<%
            %> title="<? templates/sv/adminbuttons/superadminbutton.html/2001 ?>" id="admBtnAdmin" border="0"></a><%
        }
        %><a href="javascript:void(0)" onClick="openHelpW(1);return(false)" target="_blank"  id="admHrefHelp"><img src="$contextPath/imcms/$language/images/admin/adminbuttons/help.gif"<%
            %> alt="<? templates/sv/adminbuttons/adminbuttons.html/2003 ?>"<%
            %> title="<? templates/sv/adminbuttons/adminbuttons.html/2003 ?>" id="admBtnHelp" border="0"></a></td>
</tr>
</table></div>
<SCRIPT language=JavaScript>
<!--
function openHelpW(id){
	var helpMetaId = parseInt(<? templates/helpDocumentationMetaIdStartIndex ?>) + id;
	window.open("@documentationurl@/GetDoc?meta_id=" + helpMetaId,"help");
}
//-->
</SCRIPT>
</vel:velocity>