<%@ page import="imcode.server.document.textdocument.ImageDomainObject,
                 com.imcode.imcms.servlet.admin.ChangeImage,
                 org.apache.commons.lang.StringUtils,
                 imcode.server.ApplicationServer,
                 org.apache.commons.lang.StringEscapeUtils,
                 imcode.server.document.textdocument.TextDocumentDomainObject,
                 imcode.util.ImageData,
                 org.apache.commons.lang.ObjectUtils,
                 imcode.util.Utility,
                 imcode.util.Html,
                 org.apache.commons.collections.Transformer"%>
<%@page contentType="text/html"%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%>
<%
    ChangeImage.ImageEditPage imageEditPage = (ChangeImage.ImageEditPage)request.getAttribute( ChangeImage.ImageEditPage.REQUEST_ATTRIBUTE__PAGE ) ;
    TextDocumentDomainObject document = imageEditPage.getDocument() ;
    ImageDomainObject image = imageEditPage.getImage() ;
    int imageIndex = imageEditPage.getImageIndex() ;
    ImageData imageFileData = imageEditPage.getImageFileData() ;
%>
<vel:velocity>
<html>
<head>
<title><? templates/sv/change_img.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

<script language="JavaScript">
<!--
function setDef() {
	var f   = document.forms[0] ;
	if (!hasDocumentLayers && f.imageref.value == "") f.image_align.selectedIndex = 0;
	changeLinkType(1) ;
}

/* *******************************************************************************************
 *         Image-Link functions                                                              *
 ******************************************************************************************* */

var defValues = new Array("meta_id","http://") ;

function changeLinkType(idx) {
	var f   = document.forms[0] ;
	var rad = f.linkType ;
	var url = f.imageref_link ;
	var val = url.value ;
	var re  = /^GetDoc\?meta_id=(\d+)$/ ;
	if (val == "" || val == defValues[0] || val == defValues[1]) {
		url.value = defValues[idx] ;
		rad[idx].checked = 1 ;
	} else if (re.test(val)) {
		url.value = val.replace(re, "$1") ;
		rad[0].checked = 1 ;
	}
}

function checkLinkType() {
	var f   = document.forms[0] ;
	var url = f.imageref_link ;
	var val = url.value ;
	if (val == defValues[0] || val == defValues[1]) {
		url.value = "" ;
	} else if (/^\d+$/.test(val)) {
		url.value = "GetDoc?meta_id=" + val ;
	}
	return true ;
}

function checkLinkOnFocus() {
	var f   = document.forms[0] ;
	var url = f.imageref_link ;
	var val = url.value ;
	if (val == defValues[0]) {
		url.value = "" ;
	}
}

function checkLinkOnBlur() {
	var f   = document.forms[0] ;
	var rad = f.linkType ;
	var url = f.imageref_link ;
	var val = url.value ;
	if (val == "") {
		url.value = defValues[0] ;
		rad[0].checked = 1 ;
	}
}
//-->
</script>

</head>
<body bgcolor="#FFFFFF" onLoad="setDef();">


#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )
<form method="POST" action="ChangeImage">
    <input type="HIDDEN" name="<%= ChangeImage.REQUEST_PARAMETER__DOCUMENT_ID %>" value="<%= document.getId() %>">
    <input type="HIDDEN" name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_INDEX %>" value="<%= imageIndex %>">
    <input type="hidden" name="<%= ChangeImage.REQUEST_PARAMETER__LABEL %>" value="<%= StringEscapeUtils.escapeHtml(imageEditPage.getLabel()) %>">

    <table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? global/back ?>"></td>
            <td>&nbsp;</td>
            <td><input type="button" value="<? templates/sv/change_img.html/2002 ?>" title="<? templates/sv/change_img.html/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(72)"></td>
        </tr>
        </table></td>
        <td>&nbsp;</td>
    </tr>
    </table>
    #gui_mid()

    <table border="0" cellspacing="0" cellpadding="2" width="660" align="center">
        <tr>
            <td colspan="2">
                &nbsp;<br>
                #gui_heading( "<? templates/sv/change_img.html/9/1 ?> <%= imageIndex %> <? templates/sv/change_img.html/9/2 ?> <%= document.getId() %>" )
                <div id="theLabel" class="imcmsAdmText"><i><%= StringEscapeUtils.escapeHtml(imageEditPage.getLabel()) %></i></div>
            </td>
        </tr>
        <% if (StringUtils.isNotBlank(image.getUrl())) { %>
        <tr>
            <td colspan="2" align="center">
                <%= Html.getImageTag( image ) %>
            </td>
        </tr>
        <% } %>
        <tr>
            <td>&nbsp;</td>
            <td>
                <table>
                    <tr>
                        <td>
                                <input type="submit" name="<%= ChangeImage.REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER %>" class="imcmsFormBtnSmall" value="<? templates/sv/change_img.html/2004 ?>">
                        </td>
                        <td>
                                <input type="submit" name="<%= ChangeImage.REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH %>" class="imcmsFormBtnSmall" value="Hämta från det nya bildarkivet" >
                        </td>
                    </tr>
                </table>
             </td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/12 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr>
                <td colspan="2"><input type="text" name="imageref" size="50" maxlength="255" style="width: 350" value="<%= StringEscapeUtils.escapeHtml(image.getUrl()) %>"></td>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/14 ?></td>
            <td><input type="text" name="image_name" size="50" maxlength="255" style="width: 350" value="<%= StringEscapeUtils.escapeHtml(image.getName()) %>"></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/16 ?></td>
            <td>
                <table border="0" cellspacing="0" cellpadding="0">
                    <tr>
                        <td><? templates/sv/change_img.html/17 ?></td>
                        <td>&nbsp;</td>
                        <td><? templates/sv/change_img.html/18 ?></td>
                        <td>&nbsp;</td>
                        <td><? templates/sv/change_img.html/19 ?></td>
                    </tr>
                    <tr>
                        <td><input type="text" name="image_width" size="4" maxlength="4" value="<% if (image.getWidth() > 0) { %><%= image.getWidth() %><% } %>"></td>
                        <td>&nbsp;X&nbsp;</td>
                        <td><input type="text" name="image_height" size="4" maxlength="4" value="<% if (image.getHeight() > 0) { %><%= image.getHeight() %><% } %>"></td>
                        <td>&nbsp;</td>
                        <td><input type="text" name="image_border" size="4" maxlength="4" value="<%= image.getBorder() %>"></td>
                    </tr>
                    <tr>
                        <td height="20">&nbsp;<%= imageFileData.getWidth() %></td>
                        <td>&nbsp;X&nbsp;</td>
                        <td>&nbsp;<%= imageFileData.getHeight() %></td>
                        <td>&nbsp;</td>
                        <td><? templates/sv/change_img.html/originalSize ?></td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/25 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><input type="text" name="v_space" size="4" maxlength="4" value="<%= image.getVerticalSpace() %>"></td>
                <td>&nbsp;</td>
                <td><? templates/sv/change_img.html/27 ?></td>
                <td>&nbsp; &nbsp;</td>
                <td><input type="text" name="h_space" size="4" maxlength="4" value="<%= image.getHorizontalSpace() %>"></td>
                <td>&nbsp;</td>
                <td><? templates/sv/change_img.html/29 ?></td>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/30 ?></td>
            <td>
            <select name="image_align" size="1">
                <% String align = image.getAlign() ; %>
                <option value="none" <% if (StringUtils.isBlank(align)) { %> selected <% } %>><? templates/sv/change_img.html/31 ?></option>
                <option value="baseline" <% if ("baseline".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/32 ?></option>
                <option value="top" <% if ("top".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/33 ?></option>
                <option value="middle" <% if ("middle".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/34 ?></option>
                <option value="bottom" <% if ("bottom".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/35 ?></option>
                <option value="texttop" <% if ("texttop".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/36 ?></option>
                <option value="absmiddle" <% if ("absmiddle".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/37 ?></option>
                <option value="absbottom" <% if ("absbottom".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/38 ?></option>
                <option value="left" <% if ("left".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/39 ?></option>
                <option value="right" <% if ("right".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/40 ?></option>
            </select></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/41 ?></td>
            <td><input type="text" name="alt_text" size="92" maxlength="255" style="width: 100%" value="<%= image.getAlternateText() %>"></td>
        </tr>
        <tr>
            <td colspan="2">&nbsp;<br>#gui_heading( "<? templates/sv/change_img.html/43/1 ?>" )</td>
        </tr>
        <tr>
            <td nowrap>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr>
                <td rowspan="2" nowrap><? templates/sv/change_img.html/44 ?></td>
                <td><input type="radio" name="linkType" id="linkType0" value="0" onClick="changeLinkType(0);"></td>
                <td><label for="linkType0"><? templates/sv/change_img.html/4000 ?></label></td>
            </tr>
            <tr>
                <td><input type="radio" name="linkType" id="linkType1" value="1" onClick="changeLinkType(1);"></td>
                <td><label for="linkType1"><? templates/sv/change_img.html/4001 ?></label></td>
            </tr>
            </table></td>
            <td><input type="text" name="imageref_link" size="92" maxlength="255" style="width: 100%" value="<%= image.getLinkUrl() %>" onFocus="checkLinkOnFocus()" onBlur="checkLinkOnBlur()"></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/46 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
                <select name="target" size="1">
                    <% String target = image.getTarget() ;
                       boolean targetTop = "_top".equalsIgnoreCase(target);
                       boolean targetBlank = "_blank".equalsIgnoreCase(target);
                       boolean targetParent = "_parent".equalsIgnoreCase(target);
                       boolean targetSelf = "_self".equalsIgnoreCase(target) || StringUtils.isWhitespace(target);
                       boolean targetOther = !(targetTop || targetBlank || targetParent || targetSelf) ;
                    %>
                    <option value="_top" <% if (targetTop) { %> selected<% } %>><? templates/sv/change_img.html/47 ?></option>
                    <option value="_blank" <% if (targetBlank) { %> selected<% } %>><? templates/sv/change_img.html/48 ?></option>
                    <option value="_parent" <% if (targetParent) { %> selected<% } %>><? templates/sv/change_img.html/49 ?></option>
                    <option value="_self" <% if (targetSelf) { %> selected<% } %>><? templates/sv/change_img.html/50 ?></option>
                    <option <% if (targetOther) { %> selected<% } %>><? templates/sv/change_img.html/51 ?></option>
                </select></td>
                <td>&nbsp;&nbsp;</td>
                <td><input type="text" name="target" size="10" maxlength="20" value="<%= StringEscapeUtils.escapeHtml(targetOther ? target : "") %>"></td>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td colspan="2">#gui_hr( "blue" )</td>
        </tr>
        <tr>
            <td colspan="2" align="right">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__PREVIEW_BUTTON %>" value="  <? templates/sv/change_img.html/2006 ?>  ">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__OK_BUTTON %>" value="  <? templates/sv/change_img.html/2007 ?>  ">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__DELETE_BUTTON %>" value="  <? templates/sv/change_img.html/2009 ?>  ">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value=" <? templates/sv/change_img.html/2008 ?> "></td>
        </tr>
        <tr>
            <td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="156" height="1"></td>
            <td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="1"></td>
        </tr>
        <input type="hidden" name="low_scr" value="<%= image.getLowResolutionUrl() %>">
    </table>
</form>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>