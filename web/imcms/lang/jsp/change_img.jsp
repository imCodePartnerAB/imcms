<%@ page

  import="com.imcode.imcms.flow.Page,
          com.imcode.imcms.servlet.admin.ImageEditPage,
          com.imcode.util.ImageSize,
          imcode.server.document.FileDocumentDomainObject,
          imcode.server.document.textdocument.FileDocumentImageSource,
          imcode.server.document.textdocument.ImageDomainObject,
          imcode.server.document.textdocument.ImageDomainObject.CropRegion,
          imcode.server.document.textdocument.ImageSource,
          imcode.server.document.textdocument.TextDocumentDomainObject,
          imcode.server.user.UserDomainObject,
          imcode.util.Html,
          imcode.util.ImcmsImageUtils,
          imcode.util.Utility,
          imcode.util.image.Format,
          org.apache.commons.lang.StringEscapeUtils,
          org.apache.commons.lang.StringUtils, java.util.Properties, imcode.server.ImcmsConstants"

	contentType="text/html; charset=UTF-8"

%><%@taglib prefix="vel" uri="imcmsvelocity"%><%!

final static int GUI_INNER_WIDTH = 900 ;

%><%

    ImageEditPage imageEditPage = ImageEditPage.getFromRequest(request);
    assert null != imageEditPage;
    TextDocumentDomainObject document = imageEditPage.getDocument();
    ImageDomainObject image = imageEditPage.getImage();
    assert null != image;
    ImageSize realImageSize = image.getRealImageSize();
    assert null != realImageSize;
    UserDomainObject user = Utility.getLoggedOnUser( request );
    CropRegion cropRegion = image.getCropRegion();

%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<vel:velocity>
<html>
<head>
<title><? templates/sv/change_img.html/1 ?></title>

<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/imcms/css/imcms_admin.css.jsp">
<script src="<%=request.getContextPath()%>/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>

<style type="text/css">
HTML {
	height: 100%;
}
BODY {
	margin: 0 !important;
	padding: 0 !important;
}
#outer_container {
	margin: 0 !important;
	padding: 0 !important;
}
#inner_container {
	margin: 30px 10px !important;
	padding: 0 !important;
}
.dim {
    width: 60px;
}
</style>

<script type="text/javascript">
<!--
$(function() {
    function limitDimension(dimName) {
        var maxDim = $("#max_" + dimName).val();
        maxDim = parseInt(maxDim, 10);
        
        if (!isFinite(maxDim) || maxDim <= 0) {
            return;
        }
        
        function restrict() {
            var input = $(this), 
                value = input.val();
            
            if (/[^0-9\s]/.test(value)) {
                value = value.replace(/[^0-9]+/, "");
                input.val(value);
            }
            value = parseInt(value, 10);
            
            if (!isFinite(value) || value <= 0) {
                input.val("");
                return;
            }
            
            if (value > maxDim) {
                input.val(maxDim);
            }
        }
        
        var input = $("#image_" + dimName);
        
        input.bind("change keyup", restrict)
        input.bind("cut paste", function(e) {
            var self = this;
            
            setTimeout(function() {
                restrict.call(self);
            }, 1);
        });
    }
    
    limitDimension("width");
    limitDimension("height");
});

function addScrolling() {
	if (window.opener) {
		var obj = document.getElementById("outer_container") ;
		obj.style.height = "100%" ;
		obj.style.overflow = "auto" ;
	}
}

function setDef() {
	var f   = document.forms[0] ;
	if (!hasDocumentLayers && f.imageref.value == "") f.image_align.selectedIndex = 0;
	changeLinkType(1) ;
}
        
var defValues = new Array("meta_id","http://") ;

function changeLinkType(idx) {<%
	if (imageEditPage.isLinkable()) { %>
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
	} else {
        rad[idx].checked = 1 ;
    }<%
	} %>
}

function checkLinkType() {<%
	if (imageEditPage.isLinkable()) { %>
	var f   = document.forms[0] ;
	var url = f.imageref_link ;
	var val = url.value ;
	if (val == defValues[0] || val == defValues[1]) {
		url.value = "" ;
	} else if (/^\d+$/.test(val)) {
		url.value = val ;
	}
	return true ;<%
	} %>
}

function checkLinkOnFocus() {<%
	if (imageEditPage.isLinkable()) { %>
	var f   = document.forms[0] ;
	var url = f.imageref_link ;
	var val = url.value ;
	if (val == defValues[0]) {
		url.value = "" ;
	}<%
	} %>
}

function checkLinkOnBlur() {<%
	if (imageEditPage.isLinkable()) { %>
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

function resetCrop() {
    var cell = document.getElementById("crop_cell");
    var cropButton = document.getElementById("crop_btn");

    cell.removeChild(cell.getElementsByTagName("table")[0]);

    var croppedSizeRow = document.getElementById("croppedSize");
    var croppedSizeLabelRow = document.getElementById("croppedSizeLabel");

    croppedSizeRow.parentNode.removeChild(croppedSizeRow);
    croppedSizeLabelRow.parentNode.removeChild(croppedSizeLabelRow);

    document.getElementById("h_crop_x1").value = "-1";
    document.getElementById("h_crop_y1").value = "-1";
    document.getElementById("h_crop_x2").value = "-1";
    document.getElementById("h_crop_y2").value = "-1";

    var forcedWidth = parseInt(document.getElementById("forced_width").value, 10), 
        forcedHeight = parseInt(document.getElementById("forced_height").value, 10);

    var widthInput = document.getElementById("image_width");
    if (forcedWidth > 0) {
        widthInput.value = forcedWidth;
    } else {
        widthInput.readOnly = false;
    }

    var heightInput = document.getElementById("image_height");
    if (forcedHeight > 0) {
        heightInput.value = forcedHeight;
    } else {
        heightInput.readOnly = false;
    }

    cropButton.style.display = "inline";
}
//-->
</script>

</head>
<body id="body" bgcolor="#FFFFFF" onload="setDef(); addScrolling(); document.forms[0].imageref.focus();">

<div id="outer_container">
	<div id="inner_container">
#gui_outer_start()
#gui_head( "<? global/imcms_administration ?>" )
<form method="POST" action="<%= request.getContextPath() %>/servlet/PageDispatcher" onsubmit="checkLinkType();">
<%= Page.htmlHidden(request) %>
<input type="hidden" name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ARCHIVE_IMAGE_ID %>" value="<%= image.getArchiveImageId() %>"/>
<input type="hidden" id="forced_width" value="<%= imageEditPage.getForcedWidth() %>"/>
<input type="hidden" id="forced_height" value="<%= imageEditPage.getForcedHeight() %>"/>
<input type="hidden" id="max_width" value="<%= imageEditPage.getMaxWidth() %>"/>
<input type="hidden" id="max_height" value="<%= imageEditPage.getMaxHeight() %>"/>
<input type="hidden" id="h_crop_x1" name="<%= ImageEditPage.REQUEST_PARAMETER__CROP_X1 %>" value="<%= cropRegion.getCropX1() %>"/>
<input type="hidden" id="h_crop_y1" name="<%= ImageEditPage.REQUEST_PARAMETER__CROP_Y1 %>" value="<%= cropRegion.getCropY1() %>"/>
<input type="hidden" id="h_crop_x2" name="<%= ImageEditPage.REQUEST_PARAMETER__CROP_X2 %>" value="<%= cropRegion.getCropX2() %>"/>
<input type="hidden" id="h_crop_y2" name="<%= ImageEditPage.REQUEST_PARAMETER__CROP_Y2 %>" value="<%= cropRegion.getCropY2() %>"/>
<input type="hidden" name="<%= ImageEditPage.REQUEST_PARAMETER__ROTATE_ANGLE %>" value="<%= image.getRotateDirection().getAngle() %>"/><%

// Sometimes it jumped wrong if this wasn't here.
if (null != imageEditPage.getReturnUrl() && !"".equals(imageEditPage.getReturnUrl())) { %>
<input type="hidden" name="<%= ImcmsConstants.REQUEST_PARAM__RETURN_URL %>" value="<%= StringEscapeUtils.escapeHtml(imageEditPage.getReturnUrl()) %>" /><%
} %>
    <table border="0" cellspacing="0" cellpadding="0">
    <tr>
        <td>
        <table border="0" cellspacing="0" cellpadding="0">
        <tr>
            <td><input type="SUBMIT" class="imcmsFormBtn" name="<%= ImageEditPage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value="<? global/back ?>"></td>
            <td>&nbsp;</td>
            <td><input type="button" value="<? templates/sv/change_img.html/2002 ?>" title="<? templates/sv/change_img.html/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('ImageAdmin')"></td>
        </tr>
        </table></td>
        <td>&nbsp;</td>
    </tr>
    </table>
    #gui_mid()

    <table border="0" cellspacing="0" cellpadding="2" width="<%= GUI_INNER_WIDTH %>" align="center">
        <tr>
            <td colspan="2">
                &nbsp;<br>
                <% if (null != imageEditPage.getHeading()) { %>
                #gui_heading( "<%= imageEditPage.getHeading().toLocalizedString(request) %>" )
                <% } %>
                <%=
            "<div id=\"theLabel\" class=\"imcmsAdmText\"><i>" + StringEscapeUtils.escapeHtml(imageEditPage.getLabel()) + "</i></div>"  %></td>
        </tr><%
		if (!image.isEmpty()) {
			int previewW = GUI_INNER_WIDTH ;
			int previewH = 0 ;
			String overFlow = " overflow:auto;" ;
			try {
				int width  = realImageSize.getWidth();
				int height = realImageSize.getHeight();
				if (cropRegion.isValid()) {
					width = cropRegion.getWidth() ;
					height = cropRegion.getHeight() ;
				}
				if (height > 400) {
					previewH = 400 ;
				}
			} catch (Exception e) {}
        %>
		<tr>
			<td colspan="2" align="center" style="padding:0;">
			<div id="previewDiv" style="padding:0; text-align:center;<%= previewW > 0 ? " width:" + previewW + "px;" : "" %><%= previewH > 0 ? " height:" + previewH + "px;" : "" %><%= overFlow %>">
				<%= !image.isEmpty() ? ImcmsImageUtils.getImagePreviewHtmlTag(image, request, new Properties()) : "" %>
			</div></td>
		</tr><%
				ImageSource imageSource = image.getSource();
				if ( imageSource instanceof FileDocumentImageSource) { %>
		<tr>
			<td colspan="2" align="center"><%
					FileDocumentImageSource fileDocumentImageSource = (FileDocumentImageSource)imageSource ;
					FileDocumentDomainObject imageFileDocument = fileDocumentImageSource.getFileDocument() ; %>
			 <%= Html.getAdminButtons( Utility.getLoggedOnUser(request), imageFileDocument, request, response ) %></td>
		</tr><%
				} %>
		<tr>
			<td colspan="2">#gui_hr( "blue" )</td>
		</tr><%
		} %>
		<tr>
			<td colspan="2" align="center">
            <% if (ImageEditPage.allowImageArchive(user) || ImageEditPage.allowChooseFile(user)) { %>
			<table>
			<tr>
                <% if (ImageEditPage.allowImageArchive(user)) { %>
                <td><input type="submit" <%
                    %>name="<%= ImageEditPage.REQUEST_PARAMETER__GO_TO_IMAGE_ARCHIVE_BUTTON %>" <%
                    %>class="imcmsFormBtnSmall" style="width:200px" <%
                    %>value="<? templates/sv/change_img.html/2010 ?>"></td>
                <% } %>
                <% if (ImageEditPage.allowChooseFile(user)) { %>
				<td><input type="submit" <%
						%>name="<%= ImageEditPage.REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON %>" <%
						%>class="imcmsFormBtnSmall" style="width:200px" <%
						%>value="<? templates/sv/change_img.html/2004 ?>" ></td>
                <% } %>
			</tr>
			</table>
            <% } %>
            </td>
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
								%>name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_URL %>" <%
								%>id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_URL %>" <%
								String path = image.getUrlPathRelativeToContextPath();
                %>size="50" maxlength="255" style="width:350px;" value="<%= StringUtils.isBlank(path) ? "" : StringEscapeUtils.escapeHtml(request.getContextPath()+path) %>"></td>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/14 ?></td>
            <td><input type="text" <%
								%>name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_NAME %>" <%
								%>id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_NAME %>" <%
								%>size="50" maxlength="255" style="width:350px;" value="<%=
						StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getName())) %>"></td>
        </tr>
				<tr>
					<td nowrap><? templates/sv/change_img.html/16 ?></td>
					<td>
					<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><% 
                        if (imageEditPage.getMaxWidth() > 0) {
                            %><? templates/sv/change_img.html/4012 ?><%
                        } else {
                            %><? templates/sv/change_img.html/17 ?><%
                        }
                        %></td>
						<td>&nbsp;</td>
						<td><%
                        if (imageEditPage.getMaxHeight() > 0) {
                            %><? templates/sv/change_img.html/4013 ?><%
                        } else {
                            %><? templates/sv/change_img.html/18 ?><%
                        }
                        %></td>
						<td>&nbsp;</td>
						<td><? templates/sv/change_img.html/19 ?></td>
						<td>&nbsp;</td>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<td><input class="dim" type="text" <%
						%>name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>" <%
						%>id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>" <%
						%>size="4" maxlength="4" value="<%
						if (image.getWidth() > 0) {
							%><%= image.getWidth() %><%
						} %>" <%= (imageEditPage.getForcedWidth() > 0 ? "readonly='readonly' class='imcmsDisabled'" : "") %> ></td>
						<td>&nbsp;X&nbsp;</td>
						<td><input class="dim" type="text" <%
						%>name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>" <%
						%>id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>" <%
						%>size="4" maxlength="4" value="<%
						if (image.getHeight() > 0) {
							%><%= image.getHeight() %><%
						} %>" <%= (imageEditPage.getForcedHeight() > 0 ? "readonly='readonly' class='imcmsDisabled'" : "") %> ></td>
						<td>&nbsp;</td>
						<td><input class="dim" type="text" <%
						%>name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_BORDER %>" <%
						%>id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_BORDER %>" <%
						%>size="4" maxlength="4" value="<%= image.getBorder() %>"></td>
						<td>&nbsp;</td>
						<td><input type="submit" name="<%= ImageEditPage.REQUEST_PARAMETER__PREVIEW_BUTTON %>" 
                                   class="imcmsFormBtnSmall" value="<? templates/sv/change_img.html/2006 ?>"/></td>
					</tr>
					</table></td>
				</tr>
        <% if (!image.isEmpty()) { %>
            <tr height="38">
                <td nowrap><? templates/sv/change_img.html/4003 ?></td>
                <td id="crop_cell">
                    <% if (cropRegion.isValid()) { %>
                        <table cellspacing="0" cellpadding="0" border="0">
                            <tr>
                                <td>
                                    <label for="crop_x1"><b><? templates/sv/change_img.html/4004 ?></b></label>
                                </td>
                                <td>
                                    <label for="crop_y1"><b><? templates/sv/change_img.html/4005 ?></b></label>
                                </td>
                                <td>&nbsp;&nbsp;</td>
                                <td>
                                    <label for="crop_x2"><b><? templates/sv/change_img.html/4006 ?></b></label>
                                </td>
                                <td>
                                    <label for="crop_y2"><b><? templates/sv/change_img.html/4007 ?></b></label>
                                </td>
                                <td>&nbsp;&nbsp;</td>
                                <td></td>
                                <td>&nbsp;</td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><input id="crop_x1" type="text" value="<%= cropRegion.getCropX1() %>" readonly="readonly" class="imcmsDisabled" size="4" maxlength="4"/></td>
                                <td><input id="crop_y1" type="text" value="<%= cropRegion.getCropY1() %>" readonly="readonly" class="imcmsDisabled" size="4" maxlength="4"/></td>
                                <td>&nbsp;&nbsp;</td>
                                <td><input id="crop_x2" type="text" value="<%= cropRegion.getCropX2() %>" readonly="readonly" class="imcmsDisabled" size="4" maxlength="4"/></td>
                                <td><input id="crop_y2" type="text" value="<%= cropRegion.getCropY2() %>" readonly="readonly" class="imcmsDisabled" size="4" maxlength="4"/></td>
                                <td>&nbsp;&nbsp;</td>
                                <td>
                                    <input type="submit" name="<%= ImageEditPage.REQUEST_PARAMETER__GO_TO_CROP_IMAGE %>" 
                                           class="imcmsFormBtnSmall" value="<? templates/sv/change_img.html/4002 ?>"/>
                                </td>
                                <td>&nbsp;</td>
                                <td>
                                    <input type="button" class="imcmsFormBtnSmall" onclick="resetCrop();" value="<? templates/sv/change_img.html/4008 ?>"/>
                                </td>
                            </tr>
                        </table>
                    <% } %>
                    
                    <input id="crop_btn" type="submit" name="<%= ImageEditPage.REQUEST_PARAMETER__GO_TO_CROP_IMAGE %>"
                           style='<%= (cropRegion.isValid() ? "display:none;" : "") %>' 
                           class="imcmsFormBtnSmall" value="<? templates/sv/change_img.html/4002 ?>"/>
                </td>
            </tr>
        <% } %>
        <tr>
            <td nowrap>
                <table cellspacing="0" cellpadding="0" border="0">
                    <% if (cropRegion.isValid()) { %>
                    <tr id="croppedSizeLabel">
                        <td height="25"><? templates/sv/change_img.html/4010 ?></td>
                    </tr>
                    <% } %>
                    <tr>
                        <td height="25"><? templates/sv/change_img.html/4011 ?></td>
                    </tr>
                </table>
            </td>
            <td>
                <table cellspacing="0" cellpadding="0" border="0">
                    <% if (cropRegion.isValid()) { %>
                    <tr id="croppedSize">
                        <td height="25"><%= cropRegion.getWidth() %></td>
                        <td>&nbsp;&nbsp;X&nbsp;&nbsp;</td>
                        <td><%= cropRegion.getHeight() %></td>
                    </tr>
                    <% } %>
                    <tr>
                        <td height="25"><%= realImageSize.getWidth() %></td>
                        <td>&nbsp;&nbsp;X&nbsp;&nbsp;</td>
                        <td><%= realImageSize.getHeight() %></td>
                    </tr>
                </table>
            </td>
        </tr>
        
        <tr>
            <td nowrap><label for="format"><? templates/sv/change_img.html/4009 ?></label></td>
            <td>
                <select id="format" name="<%= ImageEditPage.REQUEST_PARAMETER__FORMAT %>">
                    <% for (Format format : ImageEditPage.ALLOWED_FORMATS) { %>
                        <option value="<%= format.getOrdinal() %>" <% if (format == image.getFormat()) { %> selected="selected" <% } %>>
                            <%= format.getFormat() %>
                        </option>
                    <% } %>
                </select>
            </td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/25 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><input type="text" <%
								%>name="<%= ImageEditPage.REQUEST_PARAMETER__VERTICAL_SPACE %>" <%
								%>id="<%= ImageEditPage.REQUEST_PARAMETER__VERTICAL_SPACE %>" <%
								%>size="4" maxlength="4" value="<%= image.getVerticalSpace() %>"></td>
                <td>&nbsp;</td>
                <td><? templates/sv/change_img.html/27 ?></td>
                <td>&nbsp; &nbsp;</td>
                <td><input type="text" <%
								%>name="<%= ImageEditPage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>" <%
								%>id="<%= ImageEditPage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>" <%
								%>size="4" maxlength="4" value="<%= image.getHorizontalSpace() %>"></td>
                <td>&nbsp;</td>
                <td><? templates/sv/change_img.html/29 ?></td>
            </tr>
            </table></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/30 ?></td>
            <td>
						<select name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALIGN %>" id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALIGN %>" size="1"><%
							String align = image.getAlign() ; %>
							<option value="" <%      if (StringUtils.isBlank(align)) { %> selected <% } %>><? templates/sv/change_img.html/31 ?></option>
							<option value="top" <%       if ("top".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/33 ?></option>
							<option value="middle" <%    if ("middle".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/34 ?></option>
							<option value="bottom" <%    if ("bottom".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/35 ?></option>
							<option value="left" <%      if ("left".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/39 ?></option>
							<option value="right" <%     if ("right".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/40 ?></option>
						</select></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/41 ?></td>
            <td><input type="text" <%
						%>name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>" <%
						%>id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>" <%
						%>size="92" maxlength="255" style="width: 100%" value="<%=
						StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getAlternateText())) %>"></td>
        </tr>
        <% if (imageEditPage.isLinkable()) { %>
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
            <td><input type="text" name="<%= ImageEditPage.REQUEST_PARAMETER__LINK_URL %>" size="92" maxlength="255" style="width: 100%" value="<%=
						StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getLinkUrl())) %>" onFocus="checkLinkOnFocus()" onBlur="checkLinkOnBlur()"></td>
        </tr>
        <tr>
            <td nowrap><? templates/sv/change_img.html/46 ?></td>
            <td>
            <table border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td>
								<select name="<%= ImageEditPage.REQUEST_PARAMETER__LINK_TARGET %>" size="1"><%
									String target = StringUtils.defaultString( image.getTarget() );
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
                <td><input type="text" name="<%= ImageEditPage.REQUEST_PARAMETER__LINK_TARGET %>" size="10" maxlength="20" value="<%= StringEscapeUtils.escapeHtml(targetOther ? target : "") %>"></td>
            </tr>
            </table></td>
        </tr>
        <% } %>
        <tr>
            <td colspan="2">#gui_hr( "blue" )</td>
        </tr>
        <tr>
            <td colspan="2" align="right">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ImageEditPage.REQUEST_PARAMETER__PREVIEW_BUTTON %>" value="  <? templates/sv/change_img.html/2006 ?>  ">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ImageEditPage.REQUEST_PARAMETER__OK_BUTTON %>" value="  <? templates/sv/change_img.html/2007 ?>  ">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ImageEditPage.REQUEST_PARAMETER__DELETE_BUTTON %>" value="  <? templates/sv/change_img.html/2009 ?>  ">
            <input type="SUBMIT" class="imcmsFormBtn" name="<%= ImageEditPage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value=" <? templates/sv/change_img.html/2008 ?> "></td>
        </tr>
        <tr>
            <td style="padding:0; width:170px;"><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1.gif" width="170" height="1" alt=""></td>
            <td style="padding:0;"><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1.gif" width="1" height="1" alt=""></td>
        </tr>
    </table>
<input type="hidden" name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_LOWSRC %>" value="<%= StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getLowResolutionUrl())) %>">
</form>
#gui_bottom()
#gui_outer_end()
	</div>
</div>
</body>
</html>
</vel:velocity>
