<%@ page

  import="imcode.server.document.textdocument.ImageDomainObject,
          com.imcode.imcms.servlet.admin.ChangeImage,
          org.apache.commons.lang.StringUtils,
          imcode.server.Imcms,
          org.apache.commons.lang.StringEscapeUtils,
          imcode.server.document.textdocument.TextDocumentDomainObject,
          org.apache.commons.lang.ObjectUtils,
          imcode.util.Utility,
          imcode.util.Html,
          org.apache.commons.collections.Transformer,
          imcode.server.document.DocumentMapper,
          imcode.server.user.UserDomainObject,
          imcode.server.document.DocumentDomainObject,
          imcode.util.ImcmsImageUtils,
          imcode.server.document.FileDocumentDomainObject,
          com.imcode.util.ImageSize,
          com.imcode.util.ImageSize"

	contentType="text/html"

%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%><%


boolean fromEditor = (request.getParameter("editor_image") != null && request.getParameter("editor_image").equals("true")) ;

String image_url = request.getContextPath() + Imcms.getServices().getConfig().getImageUrl() ;



ChangeImage.ImageEditPage imageEditPage = null ;
TextDocumentDomainObject document       = null ;
ImageDomainObject image                 = null ;
int imageIndex                          = 1 ;
ImageSize realImageSize                 = null ;
UserDomainObject user                   = Utility.getLoggedOnUser( request );


if (fromEditor) {
	imageEditPage = (ChangeImage.ImageEditPage) request.getAttribute( ChangeImage.ImageEditPage.REQUEST_ATTRIBUTE__PAGE ) ;
	document      = imageEditPage.getDocument() ;
	image         = imageEditPage.getImage() ;
	imageIndex    = imageEditPage.getImageIndex() ;
	realImageSize = image.getRealImageSize();
} else {
	imageEditPage = (ChangeImage.ImageEditPage) request.getAttribute( ChangeImage.ImageEditPage.REQUEST_ATTRIBUTE__PAGE ) ;
	document      = imageEditPage.getDocument() ;
	image         = imageEditPage.getImage() ;
	imageIndex    = imageEditPage.getImageIndex() ;
	realImageSize = image.getRealImageSize();
}

%><vel:velocity>
<html>
<head>
<title><? templates/sv/change_img.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script><%

if (fromEditor) { %>

<script type="text/javascript" src="$contextPath/imcms/htmlarea/popups/popup.js"></script>

<script type="text/javascript">
function Init() {
	__dlg_init(null, true);
	window.resizeTo(800,screen.height - 50) ;
	window.moveTo((screen.width/2) - 400, 10) ;
	var param = window.dialogArguments;
	if (param) {
		var re = new RegExp("^<%= image_url %>", "") ;
		var imgSrc = param["imageref"].replace(re, "") ;
		var w = (param["image_width"] >= 0)  ? param["image_width"]  : 0 ;
		var h = (param["image_height"] >= 0) ? param["image_height"] : 0 ;
		var b = (param["image_border"] >= 0) ? param["image_border"] : 0 ;
		document.getElementById("imageref").value     = imgSrc ;
		document.getElementById("image_name").value   = param["image_name"] ;
		document.getElementById("image_width").value  = w ;
		document.getElementById("image_height").value = h ;
		document.getElementById("image_border").value = b ;
		document.getElementById("v_space").value      = (param["v_space"] >= 0)      ? param["v_space"]      : 0 ;
		document.getElementById("h_space").value      = (param["h_space"] >= 0)      ? param["h_space"]      : 0 ;
		document.getElementById("image_align").value  = param["image_align"] ;
		document.getElementById("alt_text").value     = param["alt_text"] ;
		var imgTag = "<img src=\"<%= image_url %>" + imgSrc + "\" width=\"" + w + "\" height=\"" + h + "\" border=\"" + b + "\" align=\"" + param["image_align"] + "\">" ;
		if (document.getElementById("previewDiv").innerHTML == "") {
			document.getElementById("previewDiv").innerHTML = imgTag ;
		}
	}
};

function onOK() {
	try {
		var required = {
			"imageref"     : ["\.(gif|jpe?g?|png)$", "You must select an image!"],
			"image_width"  : ["^\\d+$", "Specify width in pixels!"],
			"image_height" : ["^\\d+$", "Specify height in pixels!"],
			"image_border" : ["^\\d+$", "Specify border in pixels!"],
			"v_space"      : ["^\\d+$", "Specify vertical space in pixels!"],
			"h_space"      : ["^\\d+$", "Specify horizontal space in pixels!"]
		} ;
		for (var i in required) {
			var el = document.getElementById(i) ;
			var re = new RegExp(required[i][0], "gi") ;
			if (!re.test(el.value)) {
				alert(required[i][1]) ;
				el.focus() ;
				return false ;
			}
		}
		// pass data back to the calling window
		var fields = [
			"imageref", "image_name", "image_width", "image_height", "image_border", "v_space", "h_space", "image_align", "alt_text"
		] ;
		var param = new Object() ;
		for (var i in fields) {
			var id = fields[i] ;
			var el = document.getElementById(id) ;
			if (id == "imageref") {
				param[id] = "<%= image_url %>" + el.value ;
			} else {
				param[id] = el.value ;
			}
		}
		__dlg_close(param) ;
		return false ;
	} catch(e) {
		return false ;
	}
};

function onCancel() {
  __dlg_close(null);
  return false;
};
</script><%
} %>

<script language="JavaScript">
<!--<%
if (!fromEditor) { %>
function setDef() {
	var f   = document.forms[0] ;
	if (!hasDocumentLayers && f.imageref.value == "") f.image_align.selectedIndex = 0;
	changeLinkType(1) ;
}<%
} %>

var defValues = new Array("meta_id","http://") ;

function changeLinkType(idx) {<%
	if (!fromEditor) { %>
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
	}<%
	} %>
}

function checkLinkType() {<%
	if (!fromEditor) { %>
	var f   = document.forms[0] ;
	var url = f.imageref_link ;
	var val = url.value ;
	if (val == defValues[0] || val == defValues[1]) {
		url.value = "" ;
	} else if (/^\d+$/.test(val)) {
		url.value = "GetDoc?meta_id=" + val ;
	}
	return true ;<%
	} %>
}

function checkLinkOnFocus() {<%
	if (!fromEditor) { %>
	var f   = document.forms[0] ;
	var url = f.imageref_link
	var val = url.value ;
	if (val == defValues[0]) {
		url.value = "" ;
	}<%
	} %>
}

function checkLinkOnBlur() {<%
	if (!fromEditor) { %>
	var f   = document.forms[0] ;
	var rad = f.linkType ;
	var url = f.imageref_link ;
	var val = url.value ;
	if (val == "") {
		url.value = defValues[0] ;
		rad[0].checked = 1 ;
	}<%
	} %>
}
//-->
</script>

</head>
<body id="body" bgcolor="#FFFFFF" onLoad="<%
	// Don't init when from browser or on preview
	if (fromEditor && request.getParameter("imglist") == null && request.getParameter("imageref") == null) {
		%>Init(); <%
	} else if (!fromEditor) {
		%>setDef(); <%
	}%>document.forms[0].imageref.focus();"<%
	if (fromEditor) {
		%> style="overflow:auto;" scroll="auto"<%
	} %>>


#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )
<form method="POST" action="ChangeImage" onsubmit="checkLinkType();">
<input type="hidden" name="editor_image" value="<%= fromEditor %>">
<input type="HIDDEN" name="<%= ChangeImage.REQUEST_PARAMETER__DOCUMENT_ID %>" value="<%= (document != null) ? document.getId() : 1001 %>">
<input type="HIDDEN" name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_INDEX %>" value="<%= imageIndex %>">
<input type="hidden" name="<%= ChangeImage.REQUEST_PARAMETER__LABEL %>" value="<%= (imageEditPage != null) ? StringEscapeUtils.escapeHtml(imageEditPage.getLabel()) : "" %>">

    <table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? global/back ?>"<%
						if (fromEditor) {
							%> onClick="return onCancel(); return false"<%
						} %>></td>
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
                #gui_heading( "<? templates/sv/change_img.html/9/1 ?><%
			if (!fromEditor) {
				%> <%= imageIndex %> <? templates/sv/change_img.html/9/2 ?> <%= (document != null) ? document.getId() : 0 %><%
			} %>" )<%=
				(imageEditPage != null) ? "<div id=\"theLabel\" class=\"imcmsAdmText\"><i>" + StringEscapeUtils.escapeHtml(imageEditPage.getLabel()) + "</i></div>" : "" %></td>
        </tr><%
		if (fromEditor || (image != null && !image.isEmpty())) { %>
		<tr>
			<td colspan="2" align="center">
			<div id="previewDiv"><%= (image != null && !image.isEmpty()) ? ImcmsImageUtils.getImageHtmlTag( image, request ) : "" %></div></td>
		</tr><%
			if (!fromEditor) {
				ImageDomainObject.ImageSource imageSource = image.getSource();
				if ( imageSource instanceof ImageDomainObject.FileDocumentImageSource) { %>
		<tr>
			<td colspan="2" align="center"><%
					ImageDomainObject.FileDocumentImageSource fileDocumentImageSource = (ImageDomainObject.FileDocumentImageSource)imageSource ;
					FileDocumentDomainObject imageFileDocument = fileDocumentImageSource.getFileDocument() ; %>
			 <%= Html.getAdminButtons( Utility.getLoggedOnUser(request), imageFileDocument, request, response ) %></td>
		</tr><%
				}
			} %>
		<tr>
			<td colspan="2">#gui_hr( "blue" )</td>
		</tr><%
		} %>
		<tr>
			<td colspan="2" align="center">
			<table>
			<tr><%
				if (!fromEditor) {
					if (user.canCreateDocumentOfTypeIdFromParent( DocumentDomainObject.DOCTYPE_FILE.getId(), document )) { %>
				<td><input type="submit" <%
							%>name="<%= ChangeImage.REQUEST_PARAMETER__GO_TO_ADD_RESTRICTED_IMAGE_BUTTON %>" <%
							%>class="imcmsFormBtnSmall" style="width:200px" <%
							%>value="<? web/imcms/lang/jsp/change_img.jsp/add_restricted_image ?>" ></td><%
					} %>
				<td><input type="submit" <%
						%>name="<%= ChangeImage.REQUEST_PARAMETER__GO_TO_IMAGE_SEARCH_BUTTON %>" <%
						%>class="imcmsFormBtnSmall" style="width:200px" <%
						%>value="<? web/imcms/lang/jsp/change_img.jsp/image_search ?>" ></td><%
				} %>
				<td><input type="submit" <%
					%>name="<%= ChangeImage.REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON %>" <%
					%>class="imcmsFormBtnSmall" style="width:200px" <%
					%>value="<? templates/sv/change_img.html/2004 ?>"></td>
			</tr>
			</table></td>
		</tr>
        <tr>
            <td colspan="2">#gui_hr( "blue" )</td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/12 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="0" width="100%">
            <tr>
                <td colspan="2"><input type="text" <%
								%>name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_URL %>" <%
								%>id="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_URL %>" <%
								%>size="50" maxlength="255" style="width: 350" value="<%=
								(image != null) ? StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getSource().toStorageString())) : "" %>"></td>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/14 ?></td>
            <td><input type="text" <%
								%>name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_NAME %>" <%
								%>id="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_NAME %>" <%
								%>size="50" maxlength="255" style="width: 350" value="<%=
						(image != null) ? StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getName())) : "" %>"></td>
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
						<td><? templates/sv/change_img.html/19 ?></td><%
						if (!fromEditor) { %>
						<td>&nbsp;</td>
						<td>&nbsp;</td><%
						} %>
					</tr>
					<tr>
						<td><input type="text" <%
						%>name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_WIDTH %>" <%
						%>id="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_WIDTH %>" <%
						%>size="4" maxlength="4" value="<%
						if (fromEditor && realImageSize != null) {
							%><%= realImageSize.getWidth() %><%
						} else if (image != null && image.getWidth() > 0) {
							%><%= image.getWidth() %><%
						} %>"></td>
						<td>&nbsp;X&nbsp;</td>
						<td><input type="text" <%
						%>name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_HEIGHT %>" <%
						%>id="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_HEIGHT %>" <%
						%>size="4" maxlength="4" value="<%
						if (fromEditor && realImageSize != null) {
							%><%= realImageSize.getHeight() %><%
						} else if (image != null && image.getHeight() > 0) {
							%><%= image.getHeight() %><%
						} %>"></td>
						<td>&nbsp;</td>
						<td><input type="text" <%
						%>name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_BORDER %>" <%
						%>id="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_BORDER %>" <%
						%>size="4" maxlength="4" value="<%= (image != null) ? image.getBorder() : 0 %>"></td><%
						if (!fromEditor) { %>
						<td>&nbsp;</td>
						<td><? templates/sv/change_img.html/size_explanation ?></td><%
						} %>
					</tr><%
					if (!fromEditor) { %>
					<tr>
						<td height="20">&nbsp;<%= (realImageSize != null) ? realImageSize.getWidth() + "" : "" %></td>
						<td>&nbsp;X&nbsp;</td>
						<td>&nbsp;<%= (realImageSize != null) ? realImageSize.getHeight() + "" : "" %></td>
						<td>&nbsp;</td>
						<td colspan="3"><? templates/sv/change_img.html/originalSize ?></td>
					</tr><%
					} %>
					</table></td>
				</tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/25 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><input type="text" <%
								%>name="<%= ChangeImage.REQUEST_PARAMETER__VERTICAL_SPACE %>" <%
								%>id="<%= ChangeImage.REQUEST_PARAMETER__VERTICAL_SPACE %>" <%
								%>size="4" maxlength="4" value="<%= (image != null) ? image.getVerticalSpace() : 0 %>"></td>
                <td>&nbsp;</td>
                <td><? templates/sv/change_img.html/27 ?></td>
                <td>&nbsp; &nbsp;</td>
                <td><input type="text" <%
								%>name="<%= ChangeImage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>" <%
								%>id="<%= ChangeImage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>" <%
								%>size="4" maxlength="4" value="<%= (image != null) ? image.getHorizontalSpace() : 0 %>"></td>
                <td>&nbsp;</td>
                <td><? templates/sv/change_img.html/29 ?></td>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/30 ?></td>
            <td>
						<select name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_ALIGN %>" id="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_ALIGN %>" size="1"><%
							String align = (image != null) ? image.getAlign() : "" ; %>
							<option value="<%= fromEditor ? "" : "none" %>" <%      if (StringUtils.isBlank(align)) { %> selected <% } %>><? templates/sv/change_img.html/31 ?></option>
							<option value="baseline" <%  if ("baseline".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/32 ?></option>
							<option value="top" <%       if ("top".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/33 ?></option>
							<option value="middle" <%    if ("middle".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/34 ?></option>
							<option value="bottom" <%    if ("bottom".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/35 ?></option>
							<option value="texttop" <%   if ("texttop".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/36 ?></option>
							<option value="absmiddle" <% if ("absmiddle".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/37 ?></option>
							<option value="absbottom" <% if ("absbottom".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/38 ?></option>
							<option value="left" <%      if ("left".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/39 ?></option>
							<option value="right" <%     if ("right".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/40 ?></option>
						</select></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/41 ?></td>
            <td><input type="text" <%
						%>name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_ALT %>" <%
						%>id="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_ALT %>" <%
						%>size="92" maxlength="255" style="width: 100%" value="<%=
						(image != null) ? StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getAlternateText())) : "" %>"></td>
        </tr><%
				if (!fromEditor) { %>
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
            <td><input type="text" name="<%= ChangeImage.REQUEST_PARAMETER__LINK_URL %>" size="92" maxlength="255" style="width: 100%" value="<%=
						(image != null) ? StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getLinkUrl())) : "" %>" onFocus="checkLinkOnFocus()" onBlur="checkLinkOnBlur()"></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/46 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
								<select name="<%= ChangeImage.REQUEST_PARAMETER__LINK_TARGET %>" size="1"><%
									String target = (image != null) ? StringUtils.defaultString( image.getTarget() ) : "" ;
									boolean targetTop    = "_top".equalsIgnoreCase(target);
									boolean targetBlank  = "_blank".equalsIgnoreCase(target);
									boolean targetParent = "_parent".equalsIgnoreCase(target);
									boolean targetSelf   = "_self".equalsIgnoreCase(target) || StringUtils.isWhitespace(target);
									boolean targetOther  = !(targetTop || targetBlank || targetParent || targetSelf) ; %>
									<option value="_top" <% if (targetTop) { %> selected<% } %>><? templates/sv/change_img.html/47 ?></option>
									<option value="_blank" <% if (targetBlank) { %> selected<% } %>><? templates/sv/change_img.html/48 ?></option>
									<option value="_parent" <% if (targetParent) { %> selected<% } %>><? templates/sv/change_img.html/49 ?></option>
									<option value="_self" <% if (targetSelf) { %> selected<% } %>><? templates/sv/change_img.html/50 ?></option>
									<option <% if (targetOther) { %> selected<% } %>><? templates/sv/change_img.html/51 ?></option>
								</select></td>
                <td>&nbsp;&nbsp;</td>
                <td><input type="text" name="<%= ChangeImage.REQUEST_PARAMETER__LINK_TARGET %>" size="10" maxlength="20" value="<%= StringEscapeUtils.escapeHtml(targetOther ? target : "") %>"></td>
            </tr>
            </table></td>
        </tr><%
				} else { %>
				<input type="hidden" name="<%= ChangeImage.REQUEST_PARAMETER__LINK_TARGET %>" value="_self"><%
				} // end !fromEditor %>
        <tr>
            <td colspan="2">#gui_hr( "blue" )</td>
        </tr>
        <tr>
            <td colspan="2" align="right">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__PREVIEW_BUTTON %>" value="  <? templates/sv/change_img.html/2006 ?>  ">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__OK_BUTTON %>" value="  <? templates/sv/change_img.html/2007 ?>  "<%
						if (fromEditor) {
							%> onClick="return onOK(); return false"<%
						} %>><%
						if (!fromEditor) { %>
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__DELETE_BUTTON %>" value="  <? templates/sv/change_img.html/2009 ?>  "><%
						} %>
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ChangeImage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value=" <? templates/sv/change_img.html/2008 ?> "<%
						if (fromEditor) {
							%> onClick="return onCancel(); return false"<%
						} %>></td>
        </tr>
        <tr>
            <td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="156" height="1"></td>
            <td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="1"></td>
        </tr>
        <input type="hidden" name="<%= ChangeImage.REQUEST_PARAMETER__IMAGE_LOWSRC %>" value="<%=
				(image != null) ? StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getLowResolutionUrl())) : "" %>">
    </table>
</form>
#gui_bottom()
#gui_outer_end()
</body>
</html>
</vel:velocity>