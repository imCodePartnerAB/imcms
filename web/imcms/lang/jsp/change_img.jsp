<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="com.imcode.imcms.api.I18nLanguage"%>
<%@page import="java.util.Collection"%>

<%@ page
	import="com.imcode.imcms.flow.Page,
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
	org.apache.commons.lang.StringEscapeUtils,
	org.apache.commons.lang.StringUtils,
	java.util.Properties"

	contentType="text/html; charset=UTF-8"
%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="vel" uri="imcmsvelocity"%>
<%
	ImageEditPage imageEditPage = ImageEditPage.getFromRequest(request);
	assert null != imageEditPage;

	ImageDomainObject image = imageEditPage.getImage();
	assert null != image;
	UserDomainObject user = Utility.getLoggedOnUser(request);

	pageContext.setAttribute("imageEditPage", imageEditPage);
	pageContext.setAttribute("imagesCount", imageEditPage.getImages().size());
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<vel:velocity>
	<html>
	<head>
	<title><? templates/sv/change_img.html/1 ?></title>

	<link rel="stylesheet" type="text/css"
		href="<%=request.getContextPath()%>/imcms/css/imcms_admin.css.jsp">
	<script
		src="<%=request.getContextPath()%>/imcms/$language/scripts/imcms_admin.js.jsp"
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
		function addScrolling() {
			if (window.opener) {
				var obj = document.getElementById("outer_container") ;
				obj.style.height = "100%" ;
				obj.style.overflow = "scroll" ;
				window.resizeTo(800,760) ;
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
		//-->
		</script>
	</head>
	<body id="body" bgcolor="#FFFFFF"
		onload="setDef(); addScrolling(); document.forms[0].imageref.focus();">

	<div id="outer_container">
	<div id="inner_container">
	#gui_outer_start() 
	#gui_head( "<? global/imcms_administration ?>" )
	<form method="POST"
		action="<%= request.getContextPath() %>/servlet/PageDispatcher"
		onsubmit="checkLinkType();" name="mainForm"><%=Page.htmlHidden(request)%>

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
        		
		<c:forEach items="${imageEditPage.images}" var="image" varStatus="status">
		    <tr>
		      <td>
		        ${image.language.name} 
		        ${image.language eq currentLanguage ? "(current)" : ""} 
		        ${image.language eq defaultLanguage ? "(default)" : ""}
		      </td>	      
		    </tr>
		    
			<c:set var="suffix" value="_${image.language.code}" />

			<% 
			    ImageDomainObject i18nImage = (ImageDomainObject) pageContext.getAttribute("image");
			
				if (!i18nImage.isEmpty()) {
					%>
					<tr>
					    <td>
  					      <%="<div id=\"theLabel\" class=\"imcmsAdmText\"><i>" + StringEscapeUtils.escapeHtml(imageEditPage.getLabel()) + "</i></div>"%>
					    </td>
						<td align="center">						
						<div id="previewDiv">
						  <%=!i18nImage.isEmpty() ? ImcmsImageUtils.getImageHtmlTag(i18nImage, request, new Properties()) : ""%></div>
						</td>
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
				<td nowrap><? templates/sv/change_img.html/12 ?></td>
				<td>
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td colspan="2"><input type="text" <% %>
							name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_URL %>${suffix}"
							<% %>
							id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_URL %>${suffix}"
							<%
							 String path = i18nImage.getUrlPathRelativeToContextPath();
                            %>
							size="50" maxlength="255" style="width: 350"
							value="<%= StringUtils.isBlank(path) ? "" : StringEscapeUtils.escapeHtml(request.getContextPath()+path) %>">
						</td>

						<%-- Browse Image button --%>
						<td><input type="submit" <% %>
							name="<%= ImageEditPage.REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER_BUTTON %>"
							class="imcmsFormBtnSmall" style="width: 200px"
							value="<? templates/sv/change_img.html/2004 ?>"
							onClick="setI18nCodeParameterValue('${image.language.code}')"/>
						</td>
						<td>
                            <input
                                type="button" 
                                class="imcmsFormBtnSmall"
                                name="<%= ImageEditPage.REQUEST_PARAMETER__DELETE_BUTTON %>"
                                value="  <? templates/sv/change_img.html/2009 ?>  "/>
						</td>
					</tr>
				</table>
				</td>
			</tr>
		

			<%-- Image alt text --%>
			<tr>
				<td nowrap><? templates/sv/change_img.html/41 ?></td>
				<td><input type="text" <% %>
					name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>${suffix}"
					<% %>
					id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALT %>${suffix}"
					<% %> size="92" maxlength="255" style="width: 100%"
					value="<%= StringEscapeUtils.escapeHtml(StringUtils.defaultString(i18nImage.getAlternateText())) %>"></td>
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
				<td><? templates/sv/change_img.html/originalSize ?></td>
				<td height="20">&nbsp;<%=realImageSize.getWidth()%>
				&nbsp;X&nbsp; &nbsp;<%=realImageSize.getHeight()%> &nbsp;</td>
			</tr>

			<%-- Display size lable --%>
			<tr>
				<td>#DISPLAY SIZE#</td>
				<td height="20">&nbsp;<%=displayImageSize.getWidth()%>
				&nbsp;X&nbsp; &nbsp;<%=displayImageSize.getHeight()%> &nbsp;</td>
			</tr>
			<%
				}
			%>

			<%-- 
              Indicates this images should be shared among all languages. 
            --%>
            <%
            if (!i18nImage.isEmpty()) {
            %>	            
			<c:if test="${status.first && imagesCount > 1}">
				<tr>
					<td colspan="2"><input type="checkbox" name=""
						onClick="toggleOptional(this)"
						<%--=imageEditPage.getImagesSharesSameSource() ? " checked=\"true\" " : ""--%>
                    />
					#All languages share same image. NB! This will overwrite current settings.#</td>
				</tr>
			</c:if>
	        <%
            }
	        %> 
            

			<tr>
				<td colspan="2">#gui_hr( "blue" )</td>
			</tr>

		</c:forEach>
		<%-- End of Language Loop --%>

		<tr>
			<td nowrap><? templates/sv/change_img.html/14 ?></td>
			<td><input type="text" <% %>
				name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_NAME %>"
				<% %>
				id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_NAME %>"
				<% %> size="50" maxlength="255" style="width: 350"
				value="<%= StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getName())) %>"></td>
		</tr>
		<tr>
			<td nowrap><? templates/sv/change_img.html/16 ?></td>
			<td>
			<table border="3" cellspacing="0" cellpadding="0">
				<tr>
					<td><? templates/sv/change_img.html/17 ?></td>
					<td>&nbsp;</td>
					<td><? templates/sv/change_img.html/18 ?></td>
					<td>&nbsp;</td>
					<td><? templates/sv/change_img.html/19 ?></td>
					<td>&nbsp;</td>
					<td>&nbsp;</td>
				</tr>
				<tr>
					<td><input type="text" <% %>
						name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>"
						<% %>
						id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_WIDTH %>"
						<% %> size="4" maxlength="4"
						value="<%
						if (image.getWidth() > 0) {
							%><%= image.getWidth() %>
							<%
						} %>"></td>
					<td>&nbsp;X&nbsp;</td>
					<td><input type="text" <%
						%>
						name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>"
						<%
						%>
						id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_HEIGHT %>"
						<% %> size="4" maxlength="4"
						value="<%
						if (image.getHeight() > 0) {
							%><%= image.getHeight() %><%
						} %>"></td>
					<td>&nbsp;</td>
					<td><input type="text" <%
						%>
						name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_BORDER %>"
						<% %>
						id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_BORDER %>"
						<% %> size="4" maxlength="4"
						value="<%= image.getBorder() %>"></td>
					<td>&nbsp;</td>
					<td><? templates/sv/change_img.html/size_explanation ?></td>
				</tr>
				<!-- break -->
				<tr>
					<td colspan="7">&nbsp;</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td nowrap><? templates/sv/change_img.html/25 ?></td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><input type="text" <%
								%>
						name="<%= ImageEditPage.REQUEST_PARAMETER__VERTICAL_SPACE %>"
						<% %>
						id="<%= ImageEditPage.REQUEST_PARAMETER__VERTICAL_SPACE %>"
						<% %> size="4" maxlength="4"
						value="<%= image.getVerticalSpace() %>"></td>
					<td>&nbsp;</td>
					<td><? templates/sv/change_img.html/27 ?></td>
					<td>&nbsp; &nbsp;</td>
					<td><input type="text" <% %>
						name="<%= ImageEditPage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>"
						<% %>
						id="<%= ImageEditPage.REQUEST_PARAMETER__HORIZONTAL_SPACE %>"
						<% %> size="4" maxlength="4"
						value="<%= image.getHorizontalSpace() %>"></td>
					<td>&nbsp;</td>
					<td><? templates/sv/change_img.html/29 ?></td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td nowrap><? templates/sv/change_img.html/30 ?></td>
			<td><select
				name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALIGN %>"
				id="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_ALIGN %>" size="1">
				<%
					String align = image.getAlign();
				%>
				<option value="" <%      if (StringUtils.isBlank(align)) { %>
					selected <% } %>><? templates/sv/change_img.html/31 ?></option>
				<option value="top" <%       if ("top".equalsIgnoreCase(align)) { %>
					selected <% } %>><? templates/sv/change_img.html/33 ?></option>
				<option value="middle"
					<%    if ("middle".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/34 ?></option>
				<option value="bottom"
					<%    if ("bottom".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/35 ?></option>
				<option value="left"
					<%      if ("left".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/39 ?></option>
				<option value="right"
					<%     if ("right".equalsIgnoreCase(align)) { %> selected <% } %>><? templates/sv/change_img.html/40 ?></option>
			</select></td>
		</tr>

		<%
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
					<td><input type="radio" name="linkType" id="linkType0"
						value="0" onClick="changeLinkType(0);"></td>
					<td><label for="linkType0"><? templates/sv/change_img.html/4000 ?></label></td>
				</tr>
				<tr>
					<td><input type="radio" name="linkType" id="linkType1"
						value="1" onClick="changeLinkType(1);"></td>
					<td><label for="linkType1"><? templates/sv/change_img.html/4001 ?></label></td>
				</tr>
			</table>
			</td>
			<td><input type="text"
				name="<%= ImageEditPage.REQUEST_PARAMETER__LINK_URL %>" size="92"
				maxlength="255" style="width: 100%"
				value="<%= StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getLinkUrl())) %>"
				onFocus="checkLinkOnFocus()" onBlur="checkLinkOnBlur()"></td>
		</tr>
		<tr>
			<td nowrap><? templates/sv/change_img.html/46 ?></td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td><select
						name="<%= ImageEditPage.REQUEST_PARAMETER__LINK_TARGET %>"
						size="1">
						<%
							String target = StringUtils
											.defaultString(image.getTarget());
									boolean targetTop = "_top".equalsIgnoreCase(target);
									boolean targetBlank = "_blank".equalsIgnoreCase(target);
									boolean targetParent = "_parent".equalsIgnoreCase(target);
									boolean targetSelf = "_self".equalsIgnoreCase(target)
											|| StringUtils.isWhitespace(target);
									boolean targetOther = !(targetTop || targetBlank
											|| targetParent || targetSelf);
						%>
						<option value="_top" <% if (targetTop) { %> selected <% } %>><? templates/sv/change_img.html/47 ?></option>
						<option value="_blank" <% if (targetBlank) { %> selected <% } %>><? templates/sv/change_img.html/48 ?></option>
						<option value="_parent" <% if (targetParent) { %> selected <% } %>><? templates/sv/change_img.html/49 ?></option>
						<option value="_self" <% if (targetSelf) { %> selected <% } %>><? templates/sv/change_img.html/50 ?></option>
						<option <% if (targetOther) { %> selected <% } %>><? templates/sv/change_img.html/51 ?></option>
					</select></td>
					<td>&nbsp;&nbsp;</td>
					<td><input type="text"
						name="<%= ImageEditPage.REQUEST_PARAMETER__LINK_TARGET %>"
						size="10" maxlength="20"
						value="<%= StringEscapeUtils.escapeHtml(targetOther ? target : "") %>"></td>
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
			<td colspan="2" align="right"><input type="SUBMIT"
				class="imcmsFormBtn"
				name="<%= ImageEditPage.REQUEST_PARAMETER__PREVIEW_BUTTON %>"
				value="  <? templates/sv/change_img.html/2006 ?>  "> <input
				type="SUBMIT" class="imcmsFormBtn"
				name="<%= ImageEditPage.REQUEST_PARAMETER__OK_BUTTON %>"
				value="  <? templates/sv/change_img.html/2007 ?>  "> <input
				type="SUBMIT" class="imcmsFormBtn"
				name="<%= ImageEditPage.REQUEST_PARAMETER__DELETE_BUTTON %>"
				value="  <? templates/sv/change_img.html/2009 ?>  "> <input
				type="SUBMIT" class="imcmsFormBtn"
				name="<%= ImageEditPage.REQUEST_PARAMETER__CANCEL_BUTTON %>"
				value=" #Clear all# "></td>
		</tr>
		<tr>
			<td><img
				src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1.gif"
				width="156" height="1" alt=""></td>
			<td><img
				src="<%= request.getContextPath() %>/imcms/<%= user.getLanguageIso639_2() %>/images/admin/1x1.gif"
				width="1" height="1" alt=""></td>
		</tr>
		<input type="hidden"
			name="<%= ImageEditPage.REQUEST_PARAMETER__IMAGE_LOWSRC %>"
			value="<%= StringEscapeUtils.escapeHtml(StringUtils.defaultString(image.getLowResolutionUrl())) %>">
	</table>
	</form>
	#gui_bottom() 
	#gui_outer_end()
	</div>
	</div>
	</body>
	</html>
</vel:velocity>