<%@ page
	
	import="java.util.List,
	        java.util.Map,
	        com.imcode.imcms.mapping.jpa.doc.Language,
	        java.util.Collection,
	        com.imcode.imcms.flow.Page,
	        com.imcode.imcms.servlet.admin.ImageEditPage,
	        com.imcode.util.ImageSize,
	        imcode.server.document.FileDocumentDomainObject,
	        imcode.server.document.textdocument.FileDocumentImageSource,
	        imcode.server.document.textdocument.ImageDomainObject,
	        imcode.server.document.textdocument.ImageSource,
	        imcode.server.user.UserDomainObject,
	        imcode.util.Html,
	        imcode.util.ImcmsImageUtils,
	        imcode.util.Utility,
	        org.apache.commons.lang3.StringEscapeUtils,
	       org.apache.commons.lang3.StringUtils,
	        java.util.Properties"
	
	contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"
	
%><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="vel" uri="imcmsvelocity"
%><%

ImageEditPage imageEditPage = ImageEditPage.getFromRequest(request);
assert null != imageEditPage;

ImageDomainObject image = imageEditPage.getImage();
assert null != image;
UserDomainObject user = Utility.getLoggedOnUser(request);

pageContext.setAttribute("imageEditPage", imageEditPage);
pageContext.setAttribute("imagesCount", imageEditPage.getImagesContainer().getImages().size());

%><!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<vel:velocity>
	<html>
	<head>
	<title><? templates/sv/change_img.html/1 ?></title>

	<link rel="stylesheet" type="text/css"
		href="<%=request.getContextPath()%>/imcms/css/imcms_admin.css.jsp">
	<script
		src="<%=request.getContextPath()%>/js/imcms/imcms_admin.js.jsp"
		type="text/javascript">
	</script>

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
    </style>

	<script type="text/javascript">
		<!--
		function setDef() {
			var f   = document.forms[0] ;
			if (!hasDocumentLayers && f.imageref && f.imageref.value == "") f.image_align.selectedIndex = 0;
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
				url.value = "GetDoc?meta_id=" + val ;
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
		
		function setI18nCodeParameterValue(value) {
		    document.mainForm.<%=ImageEditPage.REQUEST_PARAMETER__I18N_CODE%>.value = value;
		}
		
		<%--
		function toggleOptional(checkbox) {
		    var dispalyStype = checkbox.checked ? "none" : "block";
		    var body = document.getElementsByTagName('body')[0];
		    var spans = body.getElementsByTagName('span');
		    var span;
		    for (i = 0; i < spans.length; i++){
		        span = spans[i];
		        
		        if (span.className == 'optional') {
		            span.style.display = dispalyStype;
		        }
		    }
		}
		--%>
		function hideImage(prefix) {
		  getById('ImageUrl' + prefix).value = "";
		  getById('previewDiv' + prefix).style.display = "none";
		}
		
		function getById(id) {
			if (document.getElementById)
    			var returnVar = document.getElementById(id);
			else if (document.all)
	   		    var returnVar = document.all[id];
			else if (document.layers)
			    var returnVar = document.layers[id];			
			
			return returnVar;
		}
        
        function getLangCodes() {
            var langCodes = document.getElementById("lang_codes").value;
            
            return langCodes.split(",");
        }

        function resetCrop(langCode) {
            if (<%= imageEditPage.isShareImages() %>) {
                var langCodes = getLangCodes();

                for (var i = 0, len = langCodes.length; i < len; ++i) {
                    resetCropLang(langCodes[i]);
                }
            } else {
                resetCropLang(langCode);
            }
        }

        function resetCropLang(langCode) {
            var cell = document.getElementById("crop_cell_" + langCode);
            var cropButton = document.getElementById("crop_btn_" + langCode);

            cell.removeChild(cell.getElementsByTagName("table")[0]);

            var croppedSizeRow = document.getElementById("cropped_size_row_" + langCode);
            croppedSizeRow.parentNode.removeChild(croppedSizeRow);

            document.getElementById("h_crop_x1_" + langCode).value = "-1";
            document.getElementById("h_crop_y1_" + langCode).value = "-1";
            document.getElementById("h_crop_x2_" + langCode).value = "-1";
            document.getElementById("h_crop_y2_" + langCode).value = "-1";
            document.getElementById("h_rotate_angle_" + langCode).value = "-1";

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
	<body id="body" bgcolor="#FFFFFF" 
		onload="setDef(); document.forms[0].imageref.focus();">

	<div id="outer_container">
	<div id="inner_container">
	#gui_outer_start() 
	#gui_head( "<? global/imcms_administration ?>" )
	<form method="POST"
		action="<%= request.getContextPath() %>/servlet/PageDispatcher"
		onsubmit="checkLinkType();" name="mainForm"><%=Page.htmlHidden(request)%>
        <input type="hidden" id="forced_width" value="${imageEditPage.forcedWidth}"/>
        <input type="hidden" id="forced_height" value="${imageEditPage.forcedHeight}"/>
        <input type="hidden" id="lang_codes" value="${imageEditPage.langCodes}"/>

	  <%-- 
      Hidden language code parameter.
      
      This parameter value is set by JavaScript function 
      when users clicks 'Chose image' button.
      --%> 
      <input type="hidden" name="<%=ImageEditPage.REQUEST_PARAMETER__I18N_CODE%>" />

	<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="SUBMIT" class="imcmsFormBtn"
						name="<%= ImageEditPage.REQUEST_PARAMETER__CANCEL_BUTTON %>"
						value="<? global/back ?>"></td>
					<td>&nbsp;</td>
					<td><input type="button"
						value="<? templates/sv/change_img.html/2002 ?>"
						title="<? templates/sv/change_img.html/2003 ?>"
						class="imcmsFormBtn" onClick="openHelpW('ImageAdmin')"></td>
				</tr>
			</table>
			</td>
			<td>&nbsp;</td>
		</tr>
	</table>
	#gui_mid()

	<table border="0" cellspacing="0" cellpadding="2" width="660" align="center">		
        <tr>
           <td colspan="2">
            <%
            if (null != imageEditPage.getHeading()) {
                %> 
                #gui_heading( "<%=imageEditPage.getHeading().toLocalizedString(request)%>" )
               <%
            }
            %>            
            </td>
        </tr>
        		
		<c:forEach items="${imageEditPage.imagesContainer}" var="image" varStatus="status">
				<tr>
					<td colspan="2" style="padding-bottom:3px;">
					<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><img src="$contextPath/imcms/images/icons/flags_iso_639_1/${image.language.code}.gif" alt="" style="border:0;" /></td>
						<td class="imcmsAdmText" style="padding-left:10px; font-weight:bold;">
						${image.language.name}${image.language eq defaultLanguage ? " <span title=\"Default\">(d)</span>" : ""}${image.language eq currentLanguage ? " <span title=\"Current/Active\">(c)</span>" : ""}</td>
					</tr>
					</table></td></tr>
				<tr>
		    
			<c:set var="suffix" value="_${image.language.code}" />

			<% 
			    ImageDomainObject i18nImage = (ImageDomainObject) pageContext.getAttribute("image");
			
				if (!i18nImage.isEmpty()) {
					%>
					<tr>
					  <td style="padding-bottom:10px;"><%="<div id=\"theLabel\" class=\"imcmsAdmText\"><i>" + StringEscapeUtils.escapeHtml4(imageEditPage.getLabel()) + "</i></div>"%></td>
						<td style="padding-bottom:10px;" align="center">						
						<div id="previewDiv${suffix}"><%
							String imageTag = (!i18nImage.isEmpty()) ? ImcmsImageUtils.getImagePreviewHtmlTag(i18nImage, request, new Properties()) : "" ;
							if (!"".equals(imageTag)) {
								boolean isScaled = false ;
								try {
									int imgWidth = i18nImage.getWidth() ;
									if (imgWidth > 600) {
										isScaled = true ;
										imageTag = imageTag
													.replaceAll("\\s+width=(\\\")[^\\\"]+\\\"", " width=$1600$1")
													.replaceAll("\\s+width:\\s*[\\d]+px;", " width:600px;")
													.replaceAll("\\s*height=(\\\")[^\\\"]+\\\"", "")
													.replaceAll("\\s*height:\\s*[\\d]+px;", "") ;
									}
								} catch(Exception e) {} %>
								<%= imageTag %><%
								if (isScaled) { %>
								<div style="padding: 5px 0 15px 0; text-align:center; font-style:italic;">
									<? templates/sv/change_img.html/scaledDown ?>
								</div><%
								}
							} %></div></td>
					</tr>
			    <%
				}
			%>

            <%-- 
			<tr>
				<td colspan="2">#gui_hr( "blue" )</td>
			</tr>
            --%>
 
			<%-- Image URL: may be hidden --%>

			<tr>
				<td nowrap>
                    <label for="ImageUrl${suffix}"><? templates/sv/change_img.html/12 ?></label>
                </td>
				<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr><%
						String path = i18nImage.getUrlPathRelativeToContextPath(); %>
						<td colspan="2"><input type="text" id="ImageUrl${suffix}"
							name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_URL %>${suffix}"
							size="50" maxlength="255" style="width:260px;"
							value="<%= StringUtils.isBlank(path) ? "" : StringEscapeUtils.escapeHtml4(request.getContextPath()+path) %>"></td>

                        <%-- Browse image button --%>
						<td style="padding-left:10px;"><input type="submit"
							name="<%= ImageEditPage.REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON %>"
							class="imcmsFormBtnSmall" style="width:80px"
							value="<? templates/sv/change_img.html/2004 ?>"
							onClick="setI18nCodeParameterValue('${image.language.code}')"/></td>

						<%-- Choose from image archive button --%>
						<td style="padding-left:10px;"><input type="submit"
							name="<%= ImageEditPage.REQUEST_PARAMETER__GO_TO_IMAGE_ARCHIVE_BUTTON %>"
							class="imcmsFormBtnSmall" style="width:180px"
							value="<? templates/sv/change_img.html/2009 ?>"
							onClick="setI18nCodeParameterValue('${image.language.code}')"/></td>
						
						<td style="padding-left:10px;"><input type="button" class="imcmsFormBtnSmall"
						           name="<%= ImageEditPage.REQUEST_PARAMETER__DELETE_BUTTON %>"
						           value="  <? templates/sv/change_img.html/clearBtn ?>  "
						           onClick="hideImage('${suffix}')" /></td>
					</tr>
				</table></td>
			</tr>
		

			<%-- Image alt text --%>
			<tr>
				<td nowrap>
                    <label for="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>${suffix}"><? templates/sv/change_img.html/41 ?></label>
                </td>
				<td><input type="text" <% %>
					name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>${suffix}"
					<% %>
					id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>${suffix}"
					<% %> size="92" maxlength="255" style="width: 100%"
					value="<%= StringEscapeUtils.escapeHtml4(StringUtils.defaultString(i18nImage.getAlternateText())) %>"></td>
			</tr>

            <c:set var="cropRegion" value="${image.cropRegion}"/>
            <tr height="38">
                <td nowrap>
                    <input type="hidden" id="h_crop_x1${suffix}" name="<%= ImageEditPage.REQUEST_PARAMETER__CROP_X1 %>${suffix}" value="${cropRegion.cropX1}"/>
                    <input type="hidden" id="h_crop_y1${suffix}" name="<%= ImageEditPage.REQUEST_PARAMETER__CROP_Y1 %>${suffix}" value="${cropRegion.cropY1}"/>
                    <input type="hidden" id="h_crop_x2${suffix}" name="<%= ImageEditPage.REQUEST_PARAMETER__CROP_X2 %>${suffix}" value="${cropRegion.cropX2}"/>
                    <input type="hidden" id="h_crop_y2${suffix}" name="<%= ImageEditPage.REQUEST_PARAMETER__CROP_Y2 %>${suffix}" value="${cropRegion.cropY2}"/>
                    <input type="hidden" id="h_rotate_angle${suffix}" name="<%= ImageEditPage.REQUEST_PARAMETER__ROTATE_ANGLE %>${suffix}" value="${image.rotateDirection.angle}"/>

                    <? templates/sv/change_img.html/4003 ?>
                </td>
                <td id="crop_cell${suffix}">
                    <c:if test="${cropRegion.valid}">
                        <table cellspacing="0" cellpadding="0" border="0">
                            <tr>
                                <td>
                                    <label for="crop_x1${suffix}"><b><? templates/sv/change_img.html/4004 ?></b></label>
                                </td>
                                <td>
                                    <label for="crop_y1${suffix}"><b><? templates/sv/change_img.html/4005 ?></b></label>
                                </td>
                                <td>&nbsp;&nbsp;</td>
                                <td>
                                    <label for="crop_x2${suffix}"><b><? templates/sv/change_img.html/4006 ?></b></label>
                                </td>
                                <td>
                                    <label for="crop_y2${suffix}"><b><? templates/sv/change_img.html/4007 ?></b></label>
                                </td>
                                <td>&nbsp;&nbsp;</td>
                                <td></td>
                                <td>&nbsp;</td>
                                <td></td>
                            </tr>
                            <tr>
                                <td><input id="crop_x1${suffix}" type="text" value="${cropRegion.cropX1}" readonly="readonly" class="imcmsDisabled" size="4" maxlength="4"/></td>
                                <td><input id="crop_y1${suffix}" type="text" value="${cropRegion.cropY1}" readonly="readonly" class="imcmsDisabled" size="4" maxlength="4"/></td>
                                <td>&nbsp;&nbsp;</td>
                                <td><input id="crop_x2${suffix}" type="text" value="${cropRegion.cropX2}" readonly="readonly" class="imcmsDisabled" size="4" maxlength="4"/></td>
                                <td><input id="crop_y2${suffix}" type="text" value="${cropRegion.cropY2}" readonly="readonly" class="imcmsDisabled" size="4" maxlength="4"/></td>
                                <td>&nbsp;&nbsp;</td>
                                <td>
                                    <input type="submit" name="<%= ImageEditPage.REQUEST_PARAMETER__GO_TO_CROP_IMAGE %>"
                                           class="imcmsFormBtnSmall" value="<? templates/sv/change_img.html/4002 ?>"
                                           onclick="setI18nCodeParameterValue('${image.language.code}')"/>
                                </td>
                                <td>&nbsp;</td>
                                <td>
                                    <input type="button" class="imcmsFormBtnSmall" onclick="resetCrop('${image.language.code}');" value="<? templates/sv/change_img.html/4008 ?>"/>
                                </td>
                            </tr>
                        </table>
                    </c:if>
                    <input id="crop_btn${suffix}" type="submit" name="<%= ImageEditPage.REQUEST_PARAMETER__GO_TO_CROP_IMAGE %>"
                           style="${cropRegion.valid ? 'display:none;' : ''}}" onclick="setI18nCodeParameterValue('${image.language.code}')"
                           class="imcmsFormBtnSmall" value="<? templates/sv/change_img.html/4002 ?>"/>
                </td>
            </tr>
			<%
				if (!i18nImage.isEmpty()) {
							ImageSize realImageSize = i18nImage.getRealImageSize();
							ImageSize displayImageSize = i18nImage
									.getDisplayImageSize();

							assert null != realImageSize;
			%>
			<%-- Actual size lable --%>
			<tr>
				<td><? templates/sv/change_img.html/originalSize ?> (px)</td>
				<td height="20"><%= realImageSize.getWidth() %> &nbsp;X&nbsp; <%= realImageSize.getHeight() %> &nbsp;</td>
			</tr>

            <%-- Cropped size label --%>
            <c:if test="${cropRegion.valid}">
                <tr id="cropped_size_row${suffix}">
                    <td><? templates/sv/change_img.html/4010 ?> (px)</td>
                    <td height="20">${cropRegion.width} &nbsp;X&nbsp; ${cropRegion.height} &nbsp;</td>
                </tr>
            </c:if>

			<%-- Display size lable --%>
			<tr>
				<td><? templates/sv/change_img.html/displaySize ?> (px)</td>
				<td height="20"><%= displayImageSize.getWidth() %> &nbsp;X&nbsp; <%= displayImageSize.getHeight() %><%=
				image.getBorder() > 0 ? " (+ border " + image.getBorder() + " px =&gt; " + (displayImageSize.getWidth() + (2 * image.getBorder())) + " &nbsp;X&nbsp; " + (displayImageSize.getHeight() + (2 * image.getBorder())) + " px)" : "" %></td>
			</tr>
			<%
				}
			%>

			<%-- 
              Indicates this images should be shared among all languages. 
              onClick="toggleOptional(this)"
            --%>
            <%--
            if (!i18nImage.isEmpty()) {
            --%>	            
			<c:if test="${status.first && imagesCount > 1}">
				<tr>
					<td colspan="2" style="padding-top:5px;">
					<table border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td><input type="checkbox" name="<%= ImageEditPage.REQUEST_PARAMETER__SHARE_IMAGE %>" id="<%= ImageEditPage.REQUEST_PARAMETER__SHARE_IMAGE %>"<%= imageEditPage.isShareImages() ? " checked=\"checked\"" : "" %> /></td>
						<td class="imcmsAdmText" style="padding-left:5px;"><label for="<%= ImageEditPage.REQUEST_PARAMETER__SHARE_IMAGE %>"><? templates/sv/change_img.html/allShareTheSame ?></label></td>
					</tr>
					</table></td>
				</tr>
			</c:if>
	        <%--
            }
	        --%> 
            

			<tr>
				<td colspan="2">#gui_hr( "blue" )</td>
			</tr>

		</c:forEach>
		<%-- End of Language Loop --%>

		<tr>
			<td nowrap>
                <label for="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_NAME %>"><? templates/sv/change_img.html/14 ?></label>
            </td>
			<td><input type="text"<%
				%> name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_NAME %>"<%
				%> id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_NAME %>"<%
				%> size="50" maxlength="<%= ImageDomainObject.IMAGE_NAME_LENGTH %>" style="width:350px;" value="<%= StringEscapeUtils.escapeHtml4(StringUtils.defaultString(image.getName())) %>"></td>
		</tr>
		<tr>
			<td nowrap><? templates/sv/change_img.html/16 ?></td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td style="padding-bottom:3px;">
                        <label for="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>"><? templates/sv/change_img.html/17 ?></label>
                    </td>
					<td>&nbsp;</td>
					<td style="padding-bottom:3px;">
                        <label for="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>"><? templates/sv/change_img.html/18 ?></label>
                    </td>
					<td colspan="2" style="padding-bottom:3px; padding-left:10px;">
                        <label for="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_BORDER %>"><? templates/sv/change_img.html/19 ?></label>
                    </td>
				</tr>
				<tr>
					<td><input type="text" name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>" id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>"
					           size="4" maxlength="4" value="<%= (image.getWidth() > 0) ? image.getWidth() + "" : "" %>"
                               ${imageEditPage.forcedWidth gt 0 ? 'readonly="readonly" class="imcmsDisabled"' : ''}></td>
					<td style="padding: 0 5px;">X</td>
					<td><input type="text" name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>" id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>"
					           size="4" maxlength="4" value="<%= (image.getHeight() > 0) ? image.getHeight() + "" : "" %>"
                               ${imageEditPage.forcedHeight gt 0 ? 'readonly="readonly" class="imcmsDisabled"' : ''}></td>
					<td style="padding-left:10px;"><input type="text" name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_BORDER %>" id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_BORDER %>"
					           size="4" maxlength="4" value="<%= image.getBorder() %>"></td>
					<td style="padding-left:10px;"><? templates/sv/change_img.html/size_explanation ?></td>
				</tr>
			</table>
			</td>
		</tr>
        <tr>
            <td nowrap><label for="format"><? templates/sv/change_img.html/4009 ?></label></td>
            <td>
                <c:set var="image" value="${imageEditPage.image}"/>
                <select id="format" name="<%= ImageEditPage.REQUEST_PARAMETER__FORMAT %>">
                    <c:forEach var="format" items="${imageEditPage.allowedFormats}">
                        <option value="${format.ordinal}" ${format eq image.format ? 'selected="selected"' : ''}>
                            ${format.format}
                        </option>
                    </c:forEach>
                </select>
            </td>
        </tr>
		<tr>
			<td nowrap><? templates/sv/change_img.html/25 ?></td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="text" name="<%= ImageEditPage.REQUEST_PARAMETER__VERTICAL_SPACE %>" id="<%= ImageEditPage.REQUEST_PARAMETER__VERTICAL_SPACE %>"
					           size="4" maxlength="4" value="<%= image.getVerticalSpace() %>"></td>
					<td>&nbsp;</td>
					<td><? templates/sv/change_img.html/27 ?></td>
					<td>&nbsp; &nbsp;</td>
					<td><input type="text" name="<%= ImageEditPage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>" id="<%= ImageEditPage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>"
					           size="4" maxlength="4" value="<%= image.getHorizontalSpace() %>"></td>
					<td>&nbsp;</td>
					<td><? templates/sv/change_img.html/29 ?></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td nowrap>
                <label for="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALIGN %>"><? templates/sv/change_img.html/30 ?></label>
            </td>
			<td><select name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALIGN %>" id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALIGN %>" size="1"><%
					String align = image.getAlign();
				%>
				<option value=""<%= (StringUtils.isBlank(align)) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/31 ?></option>
				<option value="top"<%= ("top".equalsIgnoreCase(align)) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/33 ?></option>
				<option value="middle"<%= ("middle".equalsIgnoreCase(align)) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/34 ?></option>
				<option value="bottom"<%= ("bottom".equalsIgnoreCase(align)) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/35 ?></option>
				<option value="left"<%= ("left".equalsIgnoreCase(align)) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/39 ?></option>
				<option value="right"<%= ("right".equalsIgnoreCase(align)) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/40 ?></option>
			</select></td>
		</tr><%
	      
			if (imageEditPage.isLinkable()) {
			
		%>
		<tr>
			<td colspan="2">&nbsp;<br>
			#gui_heading( "<? templates/sv/change_img.html/43/1 ?>" )</td>
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
			</table>
			</td>
			<td><input type="text" name="<%= ImageEditPage.REQUEST_PARAMETER__LINK_URL %>"
			           size="92" maxlength="255" style="width:100%;"
			           value="<%= StringEscapeUtils.escapeHtml4(StringUtils.defaultString(image.getLinkUrl())) %>"
			           onfocus="checkLinkOnFocus()" onblur="checkLinkOnBlur()"></td>
		</tr>
		<tr>
			<td nowrap><? templates/sv/change_img.html/46 ?></td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><select name="<%= ImageEditPage.REQUEST_PARAMETER__LINK_TARGET %>" size="1"><%
						String target = StringUtils.defaultString(image.getTarget());
						boolean targetTop = "_top".equalsIgnoreCase(target);
						boolean targetBlank = "_blank".equalsIgnoreCase(target);
						boolean targetParent = "_parent".equalsIgnoreCase(target);
						boolean targetSelf = "_self".equalsIgnoreCase(target) || StringUtils.isWhitespace(target);
						boolean targetOther = !(targetTop || targetBlank || targetParent || targetSelf);
						%>
						<option value="_top"<%= (targetTop) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/47 ?></option>
						<option value="_blank"<%= (targetBlank) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/48 ?></option>
						<option value="_parent"<%= (targetParent) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/49 ?></option>
						<option value="_self"<%= (targetSelf) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/50 ?></option>
						<option<%= (targetOther) ? " selected=\"selected\"" : "" %>><? templates/sv/change_img.html/51 ?></option>
					</select></td>
					<td>&nbsp;&nbsp;</td>
					<td><input type="text" name="<%= ImageEditPage.REQUEST_PARAMETER__LINK_TARGET %>"size="10" maxlength="20"
					           value="<%= StringEscapeUtils.escapeHtml4(targetOther ? target : "") %>"></td>
				</tr>
			</table>
			</td>
		</tr>
		<%
			}
		%>
		<tr>
			<td colspan="2">#gui_hr( "blue" )</td>
		</tr>
		<tr>
			<td colspan="2" align="right">
			<input type="SUBMIT" class="imcmsFormBtn" name="<%= ImageEditPage.REQUEST_PARAMETER__PREVIEW_BUTTON %>" value="<? templates/sv/change_img.html/2006 ?>">
			<input type="SUBMIT" class="imcmsFormBtn" name="<%= ImageEditPage.REQUEST_PARAMETER__OK_BUTTON %>" value="  <? templates/sv/change_img.html/2007 ?>  ">
			<input type="SUBMIT" class="imcmsFormBtn" name="<%= ImageEditPage.REQUEST_PARAMETER__DELETE_BUTTON %>" value="<? templates/sv/change_img.html/clearAllBtn ?>">
			<input type="SUBMIT" class="imcmsFormBtn" name="<%= ImageEditPage.REQUEST_PARAMETER__CANCEL_BUTTON %>" value=" <? global/cancel ?> "></td>
		</tr>
		<tr>
			<td><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1.gif" width="156" height="1" alt=""></td>
			<td><img src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1.gif" width="1" height="1" alt=""></td>
		</tr>
	</table>
	<input type="hidden" name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_LOWSRC %>" value="<%= StringEscapeUtils.escapeHtml4(StringUtils.defaultString(image.getLowResolutionUrl())) %>">
	</form>
	#gui_bottom() 
	#gui_outer_end()
	</div>
	</div>
	</body>
	</html>
</vel:velocity>