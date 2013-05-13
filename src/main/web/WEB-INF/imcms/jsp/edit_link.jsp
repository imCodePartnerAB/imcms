<%@ page
	
	import="com.imcode.imcms.flow.Page,
	        static com.imcode.imcms.servlet.admin.LinkEditPage.Parameter.*,
	        com.imcode.imcms.api.ContentManagementSystem,
	        com.imcode.imcms.flow.OkCancelPage,
	        com.imcode.imcms.servlet.admin.LinkEditPage,
	        org.apache.commons.lang.StringEscapeUtils, com.imcode.imcms.servlet.admin.EditLink, com.imcode.imcms.servlet.AjaxServlet, org.apache.commons.lang.StringUtils"
	
	contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8"
	
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"
%><%@	taglib prefix="vel" uri="imcmsvelocity"
%><%!

boolean DEBUG = false ;

%><%

boolean isSwe = false ;
try {
	isSwe =	ContentManagementSystem.fromRequest(request).getCurrentUser().getLanguage().getIsoCode639_2().equals("swe");
} catch (Exception e) {}

String cp = request.getContextPath() ;

LinkEditPage linkEditPage = (LinkEditPage) Page.fromRequest(request) ;
pageContext.setAttribute("linkEditPage", linkEditPage) ;


EditLink.Link link = linkEditPage.getLink() ;
String href        = link.getHref().replace("%3C?imcms:contextpath?%3E", cp).replace(cp, "").replaceFirst("^\\/", "") ;
String target      = link.getTarget() ;
String title       = link.getTitle() ;
String cssClass    = link.getCssClass() ;
String cssStyle    = link.getCssStyle() ;
String otherParams = link.getOtherParams().trim() ;

%><vel:velocity><html>
<head>
<title><fmt:message key="install/htdocs/sv/jsp/docadmin/document_information.jsp/document_information_title"/></title>

<base target="_self"/>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">
<meta http-equiv="Cache-Control" content="must-revalidate">
<meta http-equiv="Cache-Control" content="no-cache">

<link rel="stylesheet" type="text/css" href="<%= cp %>/imcms/css/imcms_admin.css.jsp" />
<script type="text/javascript" src="<%= cp %>/imcms/$language/scripts/imcms_admin.js.jsp"></script>
<link rel="stylesheet" type="text/css" href="<%= cp %>/imcms/css/imcms4_admin.css.jsp" />
<script type="text/javascript" src="<%= cp %>/imcms/swe/scripts/imcms4_admin_script.js.jsp?loadJq=false"></script>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.5.0/jquery.min.js"></script>
<link rel="stylesheet" type="text/css" media="all" href="<%= cp %>/imcms/scripts/autocomplete/imcms_jquery.autocomplete.css.jsp" />
<script type="text/javascript" src="<%= cp %>/imcms/scripts/autocomplete/imcms_jquery.autocomplete.min.js"></script>
<script type="text/javascript" src="<%= cp %>/imcms/scripts/defaultvalue/imcms_jquery.defaultValue.js"></script>

<style type="text/css">
td.imcmsAdmText {
	padding-right: 10px;
}
.lightgrey {
	color: #999 !important;
}
</style>

</head>
<body id="theBody" bgcolor="#FFFFFF" onLoad="focusField(0,'typeSelect');">


#gui_outer_start()
#gui_head('<fmt:message key="edit/link/dialog/title" />')
<form id="theForm" action="<%= cp %>/servlet/PageDispatcher">
<%= Page.htmlHidden(request) %>
<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td><input type="SUBMIT" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__CANCEL %>" value="<fmt:message key="global/back"/>"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<fmt:message key="global/help"/>" title="<fmt:message key="global/openthehelppage"/>" class="imcmsFormBtn" onClick="openHelpW('EditLink')"></td>
	<td>&nbsp;</td>
	<td><input type="button" id="editLinkAdvancedSettingsBtn" value="<fmt:message key="global/AdvancedSettings"/>" title="<fmt:message key="global/AdvancedSettings"/>" class="imcmsFormBtn"></td>
</tr>
</table>

#gui_mid()

</vel:velocity><%
if (DEBUG) { %>
HREF:	<input type="text" id="<%= HREF.toString() %>" name="<%= HREF.toString() %>" value="<%= StringEscapeUtils.escapeHtml(href) %>" style="width:500px;" /><%
} else { %>
<input type="hidden" id="<%= HREF.toString() %>" name="<%= HREF.toString() %>" value="<%= StringEscapeUtils.escapeHtml(href) %>" /><%
} %>
<table border="0" cellspacing="2" cellpadding="0" style="width:630px;">
<tr>
	<td class="imcmsAdmText"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/7" /></td>
	<td colspan="2">
	<select id="typeSelect" onchange="setValue(jQ, this.selectedIndex);">
		<option value="" selected="selected">-</option>
		<option value="1"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/8" /></option>
		<option value="2"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/9" /></option>
		<option value="3"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/10" /></option>
		<option value="4"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/11" /></option>
		<option value="5"><fmt:message key="install/htdocs/imcms/html/link_editor.jsp/12" /></option>
	</select></td>
</tr>
<tr>
	<td class="imcmsAdmText" style="padding-right:0;">
	<table border="0" cellspacing="0" cellpadding="0" style="width:100%;">
	<tr>
		<td><fmt:message key="edit/link/href" /></td>
		<td align="right" id="pathStart" style="padding-right:3px;">&nbsp;</td>
	</tr>
	</table></td>
	<td id="tdInput"><input type="text" id="<%= HREF.toString() %>dummy" value="<%= StringEscapeUtils.escapeHtml(href) %>"
	           maxlength="300" size="60" style="width:100%;"/></td>
	<td align="right"><input type="submit" class="imcmsFormBtnSmall toolTip" name="SEARCH" id="searchBtn"
	                         value="<fmt:message key="global/Search"/>"
	                         title="<fmt:message key="install/htdocs/imcms/html/link_editor.jsp/search/title"/>" /></td>
</tr>
<tr>
	<td class="imcmsAdmText"><fmt:message key="edit/link/title" /></td>
	<td><input type="text" id="<%= TITLE.toString() %>" name="<%= TITLE.toString() %>" value="<%= StringEscapeUtils.escapeHtml(title) %>"
	           maxlength="300" size="60" style="width:100%;"/></td>
	<td>&nbsp;</td>
</tr><%
if (linkEditPage.isTargetEditable()) { %>
<tr>
	<td class="imcmsAdmText"><fmt:message key="edit/link/target" /></td>
	<td colspan="2">
	<table border="0" cellspacing="0" cellpadding="0" id="<%= TARGET.toString() %>">
	<tr>
	<td><input type="radio" name="<%= TARGET.toString() %>" id="target_self" value="_self"<%
	if ("_self".equalsIgnoreCase( target ) || "".equals( target )) {
		%> checked<%
		target = null;
	} %>></td>
	<td class="imcmsAdmText">&nbsp;<label for="target_self"><fmt:message key="install/htdocs/sv/jsp/docadmin/document_information.jsp/1015"/></label>&nbsp;</td>
	<td><input type="radio" name="<%= TARGET.toString() %>" id="target_blank" value="_blank"<%
	if ("_blank".equalsIgnoreCase( target ) ) {
		%> checked<%
		target = null;
	} %>></td>
	<td class="imcmsAdmText">&nbsp;<label for="target_blank"><fmt:message key="install/htdocs/sv/jsp/docadmin/document_information.jsp/1016"/></label>&nbsp;</td>
	<td><input type="radio" name="<%= TARGET.toString() %>" id="target_top" value="_top"<%
	if ("_top".equalsIgnoreCase( target ) ) {
		%> checked<%
		target = null;
	} %>></td>
	<td class="imcmsAdmText">&nbsp;<label for="target_blank"><fmt:message key="install/htdocs/sv/jsp/docadmin/document_information.jsp/1017"/></label>&nbsp;</td>
	<td><input type="radio" name="<%= TARGET.toString() %>" id="target_other" value="" <% if (null != target) { %> checked<% } %>></td>
	<td class="imcmsAdmText">&nbsp;<label for="target_other"><fmt:message key="install/htdocs/sv/jsp/docadmin/document_information.jsp/1018"/></label>&nbsp;</td>
	<td>
	<input type="text" name="<%= TARGET.toString() %>" size="9" maxlength="20" style="width:120px;" value="<%
	if (null != target) {
		%><%= StringEscapeUtils.escapeHtml( target ) %><%
	} %>"></td>
	</tr>
	</table></td>
</tr><%
} %>
<tr>
	<td style="padding:0;"><img src="<%= cp %>/imcms/swe/images/admin/1x1.gif" width="110" height="1" alt="" /></td>
	<td style="padding:0;"><img src="<%= cp %>/imcms/swe/images/admin/1x1.gif" width="450" height="1" alt="" /></td>
	<td style="padding:0;"><img src="<%= cp %>/imcms/swe/images/admin/1x1.gif" width="70" height="1" alt="" /></td>
</tr>
</table>

<div id="advancedSettingsDiv" style="display:none;">
	<table border="0" cellspacing="2" cellpadding="0" style="width:630px;">
	<tr><vel:velocity>
		<td colspan="3" style="padding-top:10px;">#gui_heading( '<fmt:message key="global/AdvancedSettings"/>' )</td>
	</tr></vel:velocity>
	<tr>
		<td class="imcmsAdmText" nowrap="nowrap"><fmt:message key="edit/link/cssClass" /></td>
		<td><input type="text" id="<%= CLASS.toString() %>" name="<%= CLASS.toString() %>" size="20" maxlength="50" style="width:100%;"
							 value="<%= StringEscapeUtils.escapeHtml(cssClass) %>" /></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="imcmsAdmText" nowrap="nowrap"><fmt:message key="edit/link/cssStyle" /></td>
		<td><input type="text" id="<%= STYLE.toString() %>" name="<%= STYLE.toString() %>" size="20" maxlength="50" style="width:100%;"
							 value="<%= StringEscapeUtils.escapeHtml(cssStyle) %>" /></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td class="imcmsAdmText" nowrap="nowrap"><fmt:message key="edit/link/otherParams" /></td>
		<td><input type="text" id="<%= OTHER.toString() %>" name="<%= OTHER.toString() %>" size="20" maxlength="50" style="width:100%;"
							 value="<%= StringEscapeUtils.escapeHtml(otherParams) %>" /></td>
		<td>&nbsp;</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
		<td colspan="2" class="imcmsAdmDim"><fmt:message key="edit/link/otherParamsExample" /> <%= StringUtils.join(EditLink.OTHER_PARAMETERS, ", ") %></td>
	</tr>
	<tr>
		<td style="padding:0;"><img src="<%= cp %>/imcms/swe/images/admin/1x1.gif" width="110" height="1" alt="" /></td>
		<td style="padding:0;"><img src="<%= cp %>/imcms/swe/images/admin/1x1.gif" width="450" height="1" alt="" /></td>
		<td style="padding:0;"><img src="<%= cp %>/imcms/swe/images/admin/1x1.gif" width="70" height="1" alt="" /></td>
	</tr>
	</table>
</div>

<table border="0" cellspacing="2" cellpadding="0" style="width:630px;">
<tr><vel:velocity>
	<td style="padding-top:10px;">#gui_hr( 'blue' )</td>
</tr></vel:velocity>
<tr>
	<td align="right"><input type="submit" class="imcmsFormBtn" name="<%= OkCancelPage.REQUEST_PARAMETER__OK %>" value="<fmt:message key="global/OK"/>" /></td>
</tr>
</table>
</form>
<vel:velocity>
#gui_bottom()
#gui_outer_end()
</vel:velocity>


<script type="text/javascript">
window.resizeTo(<%= DEBUG ? "1600,900" : "800,600" %>) ;

var defaultValueInitiated = false ;

var defaultValueTextInternalLinks = "<%= StringEscapeUtils.escapeJavaScript(isSwe ?
		"Skriv meta_id, alias eller del av rubrik så kommer förslag" :
		"Write meta_id, alias or a part of the headline for suggestions") %>" ;

var arrValues = [
	defaultValueTextInternalLinks,
	"http://www.domain.com/path/",
	"mailto:whoever@domain.com",
	"ftp://ftp.domain.com/path/",
	"#anchorName"
] ;

var showConfirmOverWrite = true ;

function setValue($, idx) {
	var $objHrefDummy     = $('#<%= HREF.toString() %>dummy') ;
	var $objHrefContainer = $('#tdInput') ;
	if (idx > 0) {
		var confirmed = showConfirmOverWrite ;
		if (showConfirmOverWrite && '' != $objHrefDummy.val()) {
			confirmed = confirm('<%= isSwe ? "Vill du skriva \u00F6ver det gamla l\u00e4nkadress-v\u00e4rdet med ett exempel?" :
			                                 "Do you want to overwrite the old link address value with an example?" %>') ;
		}
		var isDefaultValue = (1 == idx) ;
		if (confirmed) {
			if (isDefaultValue) {
				$objHrefDummy.addClass('defaultValue').val('').attr('title', arrValues[idx-1]) ;
				$objHrefContainer.defaultValue('lightgrey') ;
				defaultValueInitiated = true ;
			} else {
				$objHrefDummy.removeClass('defaultValue').removeClass('lightgrey').removeAttr('title').val(arrValues[idx-1]) ;
				if (defaultValueInitiated) {
					jQ.defaultValueRemove() ;
				}
			}
		}
		var deActivateTarget = (5 == idx) ;
		if (deActivateTarget) {
			$('input[name=<%= TARGET %>]').attr('disabled', 'disabled') ;
			$('label[for^=target_]').attr('style', 'text-decoration:line-through !important;') ;
		} else {
			$('input[name=<%= TARGET %>]').removeAttr('disabled') ;
			$('label[for^=target_]').removeAttr('style') ;
		}
	}
	showConfirmOverWrite = true ;
	checkSearchEnabled($) ;
	stripRealHref($) ;
	copyRealHref($) ;
}

function checkSearchEnabled($) {
	var $objType = $('#typeSelect') ;
	var $objBtn  = $('#searchBtn') ;
	if (1 != $objType.val()) {
		$objBtn.attr('class', 'imcmsFormBtnSmallDisabled').attr('disabled', 'disabled') ;
		disableAutoComplete($) ;
	} else {
		$objBtn.attr('class', 'imcmsFormBtnSmall').removeAttr('disabled') ;
		enableAutoComplete($) ;
	}
}

function setSelectToLinkType($) {
	var $objType = $('#typeSelect') ;
	var $objHref = $('#<%= HREF.toString() %>') ;
	var href = $objHref.val() ;
	if (/^#.*/.test(href)) {
		$objType.val(5) ;
	} else if (/^ftp:.*/.test(href)) {
		$objType.val(4) ;
	} else if (/^mailto:.*/.test(href)) {
		$objType.val(3) ;
	} else if (href.indexOf(":") != -1) {
		$objType.val(2) ;
	} else {
		$objType.val(1) ;
	}
	stripRealHref($) ;
	showConfirmOverWrite = false ;
	$('#typeSelect').trigger('change') ;
}

function stripRealHref($) {
	var $objHrefDummy = $('#<%= HREF.toString() %>dummy') ;
	var $label        = $('#pathStart') ;
	var $objType      = $('#typeSelect') ;
	if (1 == $objType.val()) {
		$label.html('<span class="imcmsAdmDim"><%= cp + "/" %></span>') ;
		var re = new RegExp('<%= cp.replace("\\/", "\\\\/") + "\\/" %>', '') ;
		$objHrefDummy.val($objHrefDummy.val().replace(re, '')) ;
	} else {
		$label.html('&nbsp;') ;
	}
}

function copyRealHref($) {
	var $objHrefDummy = $('#<%= HREF.toString() %>dummy') ;
	var $objHref      = $('#<%= HREF.toString() %>') ;
	var $objType      = $('#typeSelect') ;
	var theValue      = $objHrefDummy.val().replace(defaultValueTextInternalLinks, '') ;
	if (1 == $objType.val()) {
		$objHref.val('<%= cp + "/" %>' + theValue) ;
	} else {
		$objHref.val(theValue) ;
	}
}

var editLinkAdvancedSettingsActive = ('true' == getCookie('editLinkAdvancedSettingsActive')) ;

jQ(document).ready(function($) {
	
	$('#theForm').submit(function() {
		copyRealHref($) ;
	}) ;
	$('#<%= HREF.toString() %>dummy').live('change keyup mouseup', function() {
		copyRealHref($) ;
	}) ;
	
	$('#editLinkAdvancedSettingsBtn').click(function(event) {
		event.preventDefault() ;
		if (editLinkAdvancedSettingsActive) {
			editLinkAdvancedSettingsActive = false ;
			setCookie('editLinkAdvancedSettingsActive', null) ;
		} else {
			editLinkAdvancedSettingsActive = true ;
			setCookie('editLinkAdvancedSettingsActive', 'true') ;
		}
		checkAdvancedDiv($) ;
	}) ;
	checkAdvancedDiv($) ;
	
	setSelectToLinkType($) ;
	copyRealHref($) ;
	
}) ;

function checkAdvancedDiv($) {
	if (editLinkAdvancedSettingsActive) {
		$('#advancedSettingsDiv').slideDown('slow') ;
		$('#editLinkAdvancedSettingsBtn').addClass('imcmsFormBtnActive').removeClass('imcmsFormBtn') ;
	} else {
		$('#advancedSettingsDiv').slideUp('slow') ;
		$('#editLinkAdvancedSettingsBtn').addClass('imcmsFormBtn').removeClass('imcmsFormBtnActive') ;
	}
}


jQuery(document).ready(function($) {
	
	$('div[id^=valueShow]').live('mouseenter mouseleave', function(event) {
		var idNbr  = $(this).attr('id').replace(/[^\d]/g, '') ;
		if ('mouseenter' == event.type) {
			hoverAutoCompleteItem($, idNbr, true) ;
		} else {
			hoverAutoCompleteItem($, idNbr, false) ;
		}
	}) ;
	
	$('#<%= HREF.toString() %>dummy').live(($.browser.opera ? "keypress" : "keydown"), function(event) {
		var KEY = {
			UP: 38,
			DOWN: 40,
			PAGEUP: 33,
			PAGEDOWN: 34
		} ;
		switch(event.keyCode) {
			case KEY.UP:
			case KEY.DOWN:
			case KEY.PAGEUP:
			case KEY.PAGEDOWN:
				checkActiveItemInAutoComplete($) ;
				break;
			default:
				break;
		}
	}) ;
	
}) ;

function hoverAutoCompleteItem($, idNbr, isHover) {
	var oThis  = $('#valueShow' + idNbr) ;
	var defVal = $('#valueDefault' + idNbr).html() ;
	var hovVal = $('#valueHover' + idNbr).html() ;
	if (isHover) {
		oThis.height(oThis.height()) ;
		oThis.html(hovVal) ;
	} else {
		oThis.html(defVal) ;
	}
}

function checkActiveItemInAutoComplete($) {
	$('div.ac_results ul li').each(function() {
		var $oThis = $(this) ;
		var idNbr = $oThis.find('div[id^=valueShow]:first').attr('id').replace(/[^\d]/g, '') ;
		if ($oThis.hasClass('ac_over')) {
			hoverAutoCompleteItem($, idNbr, true) ;
		} else {
			hoverAutoCompleteItem($, idNbr, false) ;
		}
	}) ;
}

var autoCompleteEnabled = false ;

function enableAutoComplete($) {
	if (autoCompleteEnabled) {
		return ;
	}
	autoCompleteEnabled = true ;
	var isStartsWithSearch = false ;
	
	$('#<%= HREF.toString() %>dummy')<%-- http://view.jquery.com/trunk/plugins/autocomplete/demo/ --%>
		.attr('autocomplete', 'off')
		.autocomplete('<%= AjaxServlet.getPath(cp) %>',
		{
			matchSubset  : !isStartsWithSearch,
			highlight    : false,
			selectFirst  : false,
			max          : 20,
			minChars     : 1,
			cacheLength  : 0,
			width        : 520,
			scroll       : true,
			scrollHeight : 400,
			autoFill     : isStartsWithSearch,
			formatItem: function(array, i, max, value, term) {
				try {
					if (isStartsWithSearch) {
						return value ;<%-- ('' == term) ? value : value.replace(new RegExp('^(' + term + ')(.*?)$', 'gi'), '<strong>$1</strong>$2') ;--%>
					} else {
						return value ;<%-- ('' == term) ? value : value.replace(new RegExp('(' + term + ')', 'gi'), '<strong>$1</strong>') ;--%>
					}
				} catch(e) {
					return value ;
				}
			},
			formatResult: function(array) {
				try {
					return array[0].replace(/<!-- (.+) -->.+/gi, '$1') ;
				} catch(e) {
					return array[0] ;
				}
			},
			extraParams : {
				action : 'linkEditAutoCompleteSearch',
				isStartsWithSearch : isStartsWithSearch
			}
		}
	) ;
	
	$('#<%= HREF.toString() %>dummy').result(function (event, data, formatted) {
		if (data) {
			var $objHref = $('#<%= HREF.toString() %>') ;
			$objHref.val('<%= cp + "/" %>' + formatted.replace(/<!-- (.+) -->.+/gi, '$1')) ;
		}
	}) ;
}

function disableAutoComplete($) {
	$('#<%= HREF.toString() %>dummy')
		.attr('autocomplete', 'on')
		.unautocomplete() ;
	autoCompleteEnabled = false ;
}

/* *************************************
 *            Set Cookie               *
 ************************************* */

function setCookie(name, value) {
	var sPath = '/';
	var today = new Date();
	var expire = new Date();
	if (null == value) {
		expire.setTime(today.getTime() + 1000*60*60*24*-1); // yesterday
	} else {
		expire.setTime(today.getTime() + 1000*60*60*24*365); // 365 days
	}
	var sCookieCont = name + "=" + escape(value);
	sCookieCont += (expire == null) ? "" : "\; expires=" + expire.toGMTString();
	sCookieCont += "; path=" + sPath;
	document.cookie = sCookieCont;
}


/* *************************************
 *            Get Cookie               *
 ************************************* */

function getCookie(Name) {
	var search = Name + "=";
	if (document.cookie.length > 0) {
		var offset = document.cookie.indexOf(search);
		if (offset != -1) {
			offset += search.length;
			end = document.cookie.indexOf(";", offset);
			if (end == -1) {
				end = document.cookie.length;
			}
			return unescape(document.cookie.substring(offset, end));
		}
	}
	return '' ;
}
</script>


</body>
</html>