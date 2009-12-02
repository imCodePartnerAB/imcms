<%@ page contentType="text/html; charset=UTF-8"
    import="com.imcode.imcms.api.Document,
            com.imcode.imcms.flow.CreateDocumentPageFlow,
            com.imcode.imcms.flow.DocumentPageFlow,
            com.imcode.imcms.flow.EditDocumentInformationPageFlow,
            com.imcode.imcms.flow.PageFlow,
            com.imcode.imcms.mapping.CategoryMapper,
            com.imcode.imcms.mapping.DocumentMapper,
            com.imcode.util.KeywordsParser,
            imcode.server.Imcms,
            imcode.server.ImcmsServices,
            imcode.server.document.CategoryDomainObject,
            imcode.server.document.CategoryTypeDomainObject,
            imcode.server.document.DocumentDomainObject,
            imcode.server.document.textdocument.TextDocumentDomainObject,
            imcode.server.user.ImcmsAuthenticatorAndUserAndRoleMapper,
            imcode.server.user.UserDomainObject,
            imcode.util.*,
            imcode.util.jscalendar.JSCalendar,
            org.apache.commons.lang.ObjectUtils,
            org.apache.commons.lang.StringEscapeUtils,
            org.apache.commons.lang.StringUtils,
            javax.servlet.http.Cookie,
            java.net.URLEncoder,
            java.text.Collator,
            java.text.DateFormat,
            java.text.SimpleDateFormat,
            com.imcode.imcms.api.Meta"
	
%>
<%@ page import="com.imcode.imcms.api.I18nLanguage" %>
<%@ page import="java.util.*" %>
<%@ page import="com.imcode.imcms.api.DocumentLabels" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"
%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"
%><%@	taglib prefix="vel" uri="imcmsvelocity"
%><%

	response.setContentType( "text/html; charset=" + Imcms.DEFAULT_ENCODING );

    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
		boolean isSwe = user.getLanguageIso639_2().equalsIgnoreCase("swe") ;
    final ImcmsServices service = Imcms.getServices();
    ImcmsAuthenticatorAndUserAndRoleMapper userMapper = service.getImcmsAuthenticatorAndUserAndRoleMapper();
    final DocumentMapper documentMapper = service.getDocumentMapper();
    final CategoryMapper categoryMapper = service.getCategoryMapper();

    DocumentPageFlow httpFlow = DocumentPageFlow.fromRequest(request) ;
    boolean creatingNewDocument = httpFlow instanceof CreateDocumentPageFlow ;
    boolean editingExistingDocument = !creatingNewDocument ;

    EditDocumentInformationPageFlow.DocumentInformationPage documentInformationPage = EditDocumentInformationPageFlow.DocumentInformationPage.fromRequest(request) ;
    DocumentDomainObject document = documentInformationPage.getDocument() ;
    boolean adminButtonsHidden = creatingNewDocument || documentInformationPage.isAdminButtonsHidden() ;

    JSCalendar jsCalendar = new JSCalendar( Utility.getLoggedOnUser(request).getLanguageIso639_2(), request ) ;
    String calendarButtonTitle = "<? web/imcms/lang/jscalendar/show_calendar_button ?>";

    pageContext.setAttribute("document", document);

    // ----
    Map<I18nLanguage, DocumentLabels> labelsMap = (Map<I18nLanguage, DocumentLabels>)request.getAttribute("labelsMap");

    if (labelsMap == null) {
        labelsMap = new HashMap<I18nLanguage, DocumentLabels>();
        for (I18nLanguage language: Imcms.getI18nSupport().getLanguages()) {
            DocumentLabels labels = new DocumentLabels();
            labels.setLanguage(language);
            labels.setMenuImageURL("");
            labels.setHeadline("");
            labels.setMenuText("");

            labelsMap.put(language, labels);
        }
    }

    // ---
    Set<I18nLanguage> enabledLanguages = new HashSet<I18nLanguage>();
    
    for (I18nLanguage language: Imcms.getI18nSupport().getLanguages()) {
        enabledLanguages.add(language);
    }

    pageContext.setAttribute("labelsMap", labelsMap.values());
%><%!

String formatDate(Date date) {
    if (null == date) {
        return "" ;
    }
    DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING ) ;
    return dateFormat.format(date) ;
}

String formatTime(Date time) {
    if (null == time) {
        return "" ;
    }
    DateFormat dateFormat = new SimpleDateFormat( DateConstants.TIME_NO_SECONDS_FORMAT_STRING ) ;
    return dateFormat.format(time) ;
}



%><vel:velocity><html>
<head>
<title><? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_information_title ?></title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">
<meta http-equiv="Cache-Control" content="must-revalidate">
<meta http-equiv="Cache-Control" content="no-cache">

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js.jsp" type="text/javascript"></script>
<%= jsCalendar.getHeadTagScripts() %>
<script type="text/javascript">
<!--
var selFocused = false ;

function setNow(oDate,oTime) {
	var elDate = eval("document.forms[0]." + oDate) ;
	var elTime = eval("document.forms[0]." + oTime) ;
	elDate.value = "<%= formatDate(new Date()) %>" ;
	elTime.value = "<%= formatTime(new Date()) %>" ;
}

function checkFocus() {
	if (selFocused) {
		document.body.focus();
		selFocused = false ;
	}
}

function setI18nCodeParameterValue(value) {
    document.mainForm.<%=EditDocumentInformationPageFlow.REQUEST_PARAMETER__I18N_CODE%>.value = value;
}
//-->
</script>

</head>
<body id="theBody" bgcolor="#FFFFFF" onLoad="focusField(0,'<%= StringEscapeUtils.escapeJavaScript( EditDocumentInformationPageFlow.REQUEST_PARAMETER__HEADLINE ) %>');">


#gui_outer_start()
#gui_head( '<? global/imcms_administration ?>' )<%

/* *******************************************************************************************
 *         HEAD                                                                              *
 ******************************************************************************************* */

%>
<form name="mainForm" method="POST" action="<%= request.getContextPath() %>/servlet/DocumentPageFlowDispatcher">

<%-- 
  This request parameter is altered using JavaScript in case user chooses
  image URL for particular language.
--%>
<input type="hidden" name="<%=EditDocumentInformationPageFlow.REQUEST_PARAMETER__I18N_CODE%>"/> 

<table border="0" cellspacing="0" cellpadding="0">
<tr>
	<td><input type="submit" name="<%= PageFlow.REQUEST_PARAMETER__CANCEL_BUTTON %>" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2002 ?>"
	title="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW('PageInfoChange')"></td>
</tr>
</table>
#gui_mid()
<%

/* *******************************************************************************************
 *         META-INFO                                                                         *
 ******************************************************************************************* */

%>
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center" onMouseOver="checkFocus();">
<input type="hidden" name="<%= PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW %>"
    value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest(request,PageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW) %>">
<input type="hidden" name="<%= PageFlow.REQUEST_PARAMETER__PAGE %>"
    value="<%= EditDocumentInformationPageFlow.PAGE__DOCUMENT_INFORMATION %>">
<% if (creatingNewDocument) { %>
<tr>
	<td>#gui_heading( '<? install/htdocs/sv/jsp/docadmin/document_information.jsp/create_document_heading ?>' )</td>
</tr><%
} else { %>
<tr>
	<td>#gui_heading( '<? install/htdocs/sv/jsp/docadmin/document_information.jsp/edit_document_heading ?> <%= document.getId() %>' )</td>
</tr><%
} %>
<tr>
    
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="656">
	<%-- TODO: Escape XML: $Headline$ --%>
	<c:forEach items="${labelsMap}" var="i18nPart">
	
	<c:set var="prefix" value="_${i18nPart.language.code}"/>
	
	<tr>
		<td colspan="2" style="padding-bottom:3px;">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="$contextPath/imcms/$language/images/admin/flags_iso_639_1/${i18nPart.language.code}.gif" alt="" style="border:0;" /></td>
			<td class="imcmsAdmText" style="padding-left:10px; font-weight:bold;">${i18nPart.language.name}</td>
		</tr>
		</table></td></tr>
	<tr>
		<td class="imcmsAdmText" nowrap>
		<? install/htdocs/sv/jsp/docadmin/document_information.jsp/6 ?><sup class="imNote">1</sup></td>
		<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__HEADLINE + pageContext.getAttribute("prefix")%>" size="48" maxlength="255" style="width: 100%"
		value="${i18nPart.headline}"></td>
	</tr>	
	<tr>
		<td></td>
		<td class="imNoteComment"><sup class="imNote">1</sup>
		<? install/htdocs/sv/jsp/docadmin/document_information.jsp/46 ?></td>
	</tr>
	<tr>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="96" height="2" alt=""></td>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="556" height="2" alt=""></td>
	</tr>
	
      <%-- TODO: Escape XML: $MenuText$ --%>
	  <tr>	  
		<td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/docadmin/document_information.jsp/1002 ?>&nbsp;</td>
		<td class="imcmsAdmForm">
		<textarea name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MENUTEXT + pageContext.getAttribute("prefix") %>" class="imcmsAdmForm" cols="47" rows="3" wrap="virtual" style="width:100%; overflow:auto;"><c:out value="${i18nPart.menuText}"/></textarea><%

		if (creatingNewDocument && document instanceof TextDocumentDomainObject) { %>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="CHECKBOX" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS + pageContext.getAttribute("prefix") %>" value="1" checked></td>
			<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/copy_headline_and_text_to_textfields ?></td>
		</tr>
		</table><%
		} %></td>
	  </tr>
	  
	  <%-- 
	  <%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( document.getMenuImage(), "" )) %>
	  --%>
	
	<tr>
		<td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/docadmin/document_information.jsp/10 ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td width="85%">
			  <input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__IMAGE + pageContext.getAttribute("prefix") %>" size="40" maxlength="255" style="width: 100%"
			    value="<c:out value="${i18nPart.menuImageURL}" default=""/>"
			  />
			</td>
			<td align="right"><input type="submit" class="imcmsFormBtnSmall" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER%>"
			value=" <? install/htdocs/global/pageinfo/browse ?> " onClick="setI18nCodeParameterValue('${i18nPart.language.code}')"></td>
		</tr>
        </table></td>
	</tr>
    <tr>
	    <td colspan="2">#gui_hr( 'cccccc' )</td>
	</tr>
	
    </c:forEach>
    
    <%-- ERROR MESSAGE--%>
	<% if( !documentInformationPage.getErrors().isEmpty() &&
            documentInformationPage.getErrors().contains(EditDocumentInformationPageFlow.ALIAS_ERROR__ALREADY_EXIST) ) {%>
    <tr>
        <td colspan="2" class="error"><span style='color:red'><%= EditDocumentInformationPageFlow.ALIAS_ERROR__ALREADY_EXIST.toLocalizedString(user) %></span></td>
    </tr><%} else if( !documentInformationPage.getErrors().isEmpty() &&
            documentInformationPage.getErrors().contains(EditDocumentInformationPageFlow.ALIAS_ERROR__USED_BY_SYSTEM) ) {%>
    <tr>
        <td colspan="2" class="error"><span style='color:red'><%= EditDocumentInformationPageFlow.ALIAS_ERROR__USED_BY_SYSTEM.toLocalizedString(user) %></span></td>
    </tr><%}%>    
    
    <tr>
        <td class="imcmsAdmText" nowrap><? global/Page_alias ?></td>
        <td>
        <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td  align="right" width="85%"><%= "http://" + request.getServerName() + request.getContextPath() + "/" %>&nbsp;<input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__DOCUMENT_ALIAS %>" size="40" maxlength="255"
            value="<%= ObjectUtils.defaultIfNull( StringEscapeUtils.escapeHtml( document.getAlias() ), "" ) %>"></td>
            <td align="right"><input type="submit" class="imcmsFormBtnSmall" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__GO_TO_ALIAS_LIST %>"
            value=" <? global/view ?> "></td>
        </tr>
        </table></td>
    </tr>

    <tr>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="96" height="1" alt=""></td>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="556" height="1" alt=""></td>
	</tr>
	
		
	</table>
	<%

/* *******************************************************************************************
 *         STATUS                                                                            *
 ******************************************************************************************* */

%>
	<table border="0" cellspacing="0" cellpadding="0" width="656">
	<tr>
		<td colspan="2">#gui_hr( 'cccccc' )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText" valign="top">
		<? install/htdocs/sv/jsp/docadmin/document_information.jsp/status ?></td>
		<td>
		<select name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__STATUS %>" onFocus="selFocused = true;">
			<option value="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__STATUS__NEW %>"<%
				if (Document.PublicationStatus.NEW.equals(document.getPublicationStatus())) {
					%> selected<%
				} %>><? install/htdocs/sv/jsp/docadmin/document_information.jsp/status_new ?></option>
			<option value="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__STATUS__APPROVED %>"<%
				if (Document.PublicationStatus.APPROVED.equals(document.getPublicationStatus())) {
					%> selected<%
				} %>><? install/htdocs/sv/jsp/docadmin/document_information.jsp/status_publication_approved ?></option>
			<option value="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__STATUS__DISAPPROVED %>"<%
				if (Document.PublicationStatus.DISAPPROVED.equals(document.getPublicationStatus())) {
					%> selected<%
				} %>><? install/htdocs/sv/jsp/docadmin/document_information.jsp/status_publication_disapproved ?></option>
		</select>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="imcmsAdmText"><%
			Date now = new Date();
			Date publicationStartDatetime = document.getPublicationStartDatetime();
			if (!Document.PublicationStatus.APPROVED.equals(document.getPublicationStatus()) || null == publicationStartDatetime || publicationStartDatetime.after(now)) { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_will_be_published_at ?><%
			} else { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_was_published_at ?><%
			} %></td>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_DATE %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_DATE %>" size="11" maxlength="10" style="width: 7em;"
			value="<%= StringEscapeUtils.escapeHtml( formatDate(publicationStartDatetime) ) %>"></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1007 ?></td>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_TIME %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_TIME %>" size="5" maxlength="5" style="width: 4em;"
			value="<%= StringEscapeUtils.escapeHtml( formatTime(document.getPublicationStartDatetime()) ) %>"></td>
			<td><%= jsCalendar.getInstance(EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_DATE,
			    EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_TIME).getButton(calendarButtonTitle) %>&nbsp;(<%= Utility.formatHtmlDatetime( publicationStartDatetime ) %>)&nbsp;&nbsp;</td>
			<td><input type="button" id="nowBtn0" value="&laquo;&nbsp;<? global/Now ?>" class="imcmsFormBtnSmall" style="width:40px; visibility:hidden;" onClick="setNow('<%=
			EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_DATE %>','<%=
			EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_TIME %>');"></td>
		</tr>
		<tr>
			<td class="imcmsAdmText"><%
			Date archivedDatetime = document.getArchivedDatetime();
			if (null == archivedDatetime || archivedDatetime.after(now)) { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_will_be_archived_at ?><%
			} else { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_was_archived_at ?><%
			} %></td>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_DATE %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_DATE %>" size="11" maxlength="10" style="width: 7em;"
			value="<%= StringEscapeUtils.escapeHtml( formatDate(archivedDatetime) ) %>"></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1009 ?></td>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_TIME %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_TIME %>" size="5" maxlength="5" style="width: 4em;"
			value="<%= StringEscapeUtils.escapeHtml( formatTime(archivedDatetime) ) %>"></td>
			<td><%= jsCalendar.getInstance(EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_DATE,
			    EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_TIME).getButton(calendarButtonTitle) %>&nbsp;(<%= Utility.formatHtmlDatetime( archivedDatetime ) %>)&nbsp;&nbsp;</td>
			<td><input type="button" id="nowBtn1" value="&laquo;&nbsp;<? global/Now ?>" class="imcmsFormBtnSmall" style="width:40px; visibility:hidden;" onClick="setNow('<%=
			EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_DATE %>','<%=
			EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_TIME %>');"></td>
		</tr>
		<tr>
			<td class="imcmsAdmText" nowrap><%
			Date publicationEndDatetime = document.getPublicationEndDatetime();
			if (null == publicationEndDatetime || publicationEndDatetime.after(now)) { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_will_cease_to_be_published_at ?><%
			} else { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_ceased_to_be_published_at ?><%
			} %>&nbsp;</td>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_DATE %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_DATE %>" size="11" maxlength="10" style="width: 7em;"
			value="<%= StringEscapeUtils.escapeHtml( formatDate(publicationEndDatetime) ) %>"></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1009 ?></td>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_TIME %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_TIME %>" size="5" maxlength="5" style="width: 4em;"
			value="<%= StringEscapeUtils.escapeHtml( formatTime(publicationEndDatetime) ) %>"></td>
			<td><%= jsCalendar.getInstance(EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_DATE,
                    EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_TIME).getButton(calendarButtonTitle) %>&nbsp;(<%= Utility.formatHtmlDatetime( publicationEndDatetime ) %>)&nbsp;&nbsp;</td>
			<td><input type="button" id="nowBtn2" value="&laquo;&nbsp;<? global/Now ?>" class="imcmsFormBtnSmall" style="width:40px; visibility:hidden;" onClick="setNow('<%=
			EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_DATE %>','<%=
			EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_TIME %>');"></td>
		</tr>
		
		<%-- Enabled/Dsabled (Active in spec) languages --%>
	    <tr>
		  <td colspan="6">#gui_hr( 'cccccc' )</td>
	    </tr>		
		<tr>
		  <td class="imcmsAdmText" colspan="6" style="font-weight:bold; padding-bottom:3px;">
		    <%= isSwe ? "Aktiva språk" : "Active languages" %>
		  </td>
		</tr>  
		<tr>
		  <td colspan="6">
			<table border="0" cellspacing="0" cellpadding="2">
			<c:forEach items="${labelsMap}" var="i18nPart">
			<c:set var="prefix" value="_${i18nPart.language.code}"/>
			<tr>	  
				<td><input type="checkbox"
				 name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__ENABLED_I18N + pageContext.getAttribute("prefix")%>"
				 id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__ENABLED_I18N + pageContext.getAttribute("prefix")%>"<c:if test="${true}"> checked="checked"</c:if>/></td>
				<td><label for="<%=
				EditDocumentInformationPageFlow.REQUEST_PARAMETER__ENABLED_I18N + pageContext.getAttribute("prefix")%>"><img
				  src="$contextPath/imcms/$language/images/admin/flags_iso_639_1/${i18nPart.language.code}.gif" alt="" style="border:0;" /></label></td>
				<td class="imcmsAdmText" style="padding-left:10px;"><label for="<%=
				EditDocumentInformationPageFlow.REQUEST_PARAMETER__ENABLED_I18N + pageContext.getAttribute("prefix")%>">${i18nPart.language.name}</label></td>
			</tr>
			</c:forEach>	           			 
			</table>		
		  </td>
		</tr>
		<%-- End og Enabled/Dsabled languages --%>
		
	    <%-- Missing i18n show rule --%>
		<tr>
			<td colspan="6">#gui_hr( 'cccccc' )</td>
		</tr>	
		<tr>
			<td colspan="6" style="font-weight:bold; padding-bottom:3px;"><%= isSwe ? "Om efterfrågad sida saknas" : "If requested language is missing" %></td>
		</tr>  
		<tr>
			<td colspan="6">
			<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="radio" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MISSING_I18N_SHOW_RULE%>"
				id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MISSING_I18N_SHOW_RULE%>0"
				value="<%=Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE%>"<%=
				document.getMeta().getDisabledLanguageShowSetting() == Meta.DisabledLanguageShowSetting.SHOW_IN_DEFAULT_LANGUAGE ? " checked=\"checked\"" : "" %> /></td>
				<td style="padding-left:5px;"><label for="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MISSING_I18N_SHOW_RULE%>0"><%=
				isSwe ? "Visa på standardspråket" : "Show in default language" %></label></td>
			</tr>
			</table></td>
		</tr>
		<tr>
			<td colspan="6">
			<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="radio" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MISSING_I18N_SHOW_RULE%>"
				id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MISSING_I18N_SHOW_RULE%>1"
				value="<%=Meta.DisabledLanguageShowSetting.DO_NOT_SHOW%>"<%=
				document.getMeta().getDisabledLanguageShowSetting() == Meta.DisabledLanguageShowSetting.DO_NOT_SHOW ? " checked=\"checked\"" : "" %> /></td>
				<td style="padding-left:5px;"><label for="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MISSING_I18N_SHOW_RULE%>1"><%=
				isSwe ? "Visa inte alls" : "Don't show at all" %></label></td>
			</tr>
			</table></td>
		</tr>	    	    
	    	
		<%-- End of Missing i18n show rule --%>
		
		</table>
		</td>
	</tr>			
	
	<tr>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="96" height="1" alt=""></td>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="556" height="1" alt=""></td>
	</tr>
	</table></td>
</tr><%

/* *******************************************************************************************
 *         ADVANCED                                                                          *
 ******************************************************************************************* */

%>
<tbody id="advanced">
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<tr>
		<td><span class="imcmsAdmHeading"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/21/1 ?></span></td>
		<script type="text/javascript">
		if (hasGetElementById) {
			document.writeln('<td align="right"><input type="button" id="advanced_button1" class="imcmsFormBtnSmall" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/advanced_button_hide ?>" onClick="toggleAdvanced()"></td>') ;
		}
		</script>
	</tr>
	</table>
	#gui_hr( "blue" )</td>
</tr>
<tr>
	<td>
	<table border="0" cellspacing="0" cellpadding="0"><%

	/* *******************************************************************************************
	 *         CATEGORIES                                                                        *
	 ******************************************************************************************* */


		CategoryTypeDomainObject[] categoryTypes = categoryMapper.getAllCategoryTypes() ;
		Arrays.sort(categoryTypes) ;


	if (categoryTypes != null && categoryTypes.length > 0) { %>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/29 ?></td>
		<td class="imcmsAdmText"><%
		for ( int i = 0; i < categoryTypes.length; i++ ) {
			CategoryTypeDomainObject categoryType = categoryTypes[i] ;
			if ( !categoryType.hasImages() ) {
				if (1 != categoryType.getMaxChoices()) { %>
		<div style="float: left; margin: auto 1em 1ex auto; border: 1px solid #ccc;text-align:center;">
		<a href="$contextPath/imcms/$language/jsp/category_descriptions.jsp?category_type_name=<%=
			    URLEncoder.encode(StringEscapeUtils.escapeHtml( categoryType.getName() ),"UTF-8") %>" target="_blank"><%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %></a><br>
			<img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="3" alt=""><br>
		<table>
			<tr>
				<td valign="top"><select name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CATEGORY_IDS_TO_ADD %>" size="4" multiple>
					<%= Html.createOptionListOfCategoriesOfTypeNotSelectedForDocument( documentMapper, categoryType, document) %>
				</select></td>
				<td valign="middle" align="center">
					<input type="submit" name="<%=EditDocumentInformationPageFlow.REQUEST_PARAMETER__ADD_CATEGORY%>" class="imcmsFormBtnSmall" value="<? global/addToRight ?>" style="width:11ex; margin-bottom:3px;"><br>
					<input type="submit" name="<%=EditDocumentInformationPageFlow.REQUEST_PARAMETER__REMOVE_CATEGORY%>" class="imcmsFormBtnSmall" value="<? global/removeToLeft ?>" style="width:11ex; margin-top:3px;">
				</td>
				<td valign="top"><select name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CATEGORY_IDS_TO_REMOVE %>" size="4" multiple>
					<% Set documentSelectedCategories = new TreeSet(categoryMapper.getCategoriesOfType( categoryType, document.getCategoryIds() ));%>
					<%= Html.createOptionListOfCategories(documentSelectedCategories, categoryType) %>
				</select></td>
			</tr>
		</table>
		</div><%
				} else {%>
		<div style="float: left; margin: auto 1em 1ex auto;">
		<a href="$contextPath/imcms/$language/jsp/category_descriptions.jsp?category_type_name=<%=
			    URLEncoder.encode(StringEscapeUtils.escapeHtml( categoryType.getName() ),"UTF-8") %>" target="_blank"><%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %></a><br>
			<img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="3" alt=""><br>
		<select name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CATEGORIES %>" onFocus="selFocused = true;">
			<%= Html.createOptionListOfCategoriesOfTypeForDocument( categoryMapper, categoryType, document, request) %>
		</select></div><%
				}
			} else { %>
		<div style="float: left; margin: auto 1em 1ex auto; border: 1px solid #ccc;">
			<a href="$contextPath/imcms/$language/jsp/category_descriptions.jsp?category_type_name=<%=
			    URLEncoder.encode(StringEscapeUtils.escapeHtml( categoryType.getName() ),"UTF-8") %>"
			target="_blank"><%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %></a><br><%

			boolean radioButton = categoryType.getMaxChoices() == 1;
			String typeStr = radioButton?"radio":"checkbox";
            Set documentCategoryIds = document.getCategoryIds();
			CategoryDomainObject[] categories = categoryMapper.getAllCategoriesOfType(categoryType);
			for (int k = 0; k < categories.length; k++) {
				CategoryDomainObject category = categories[k];
				boolean checked = documentCategoryIds.contains(new Integer(category.getId()));
				String checkedStr = checked?"checked":"";
				boolean hasImage = !category.getImageUrl().equals("");
				String imageStr = hasImage ? "<img style=\"max-width: 10em; max-height: 10em;\" src=\"" + category.getImageUrl() + "\"/>" : ""; %>
		<table border="0" cellspacing="2" cellpadding="0" style="float:left;">
		<tr>
			<td bgcolor="#000000">
			<table border="0" cellspacing="1" cellpadding="2">
			<tr>
				<td bgcolor="#ffffff"><%=imageStr%></td>
				<td bgcolor="#ffffff"><input id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CATEGORIES + "" + category.getId() %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CATEGORIES %>" type="<%=typeStr%>" value="<%=category.getId()%>"<%=checkedStr%>></td>
				<td bgcolor="#ffffff"><label for="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CATEGORIES + "" + category.getId() %>"><%=category.getName()%></label></td>
			</tr>
			</table></td>
		</tr>
		</table><%
				} %></div><%
			}
		}
		%></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr><%
	}

	/* *******************************************************************************************
	 *         SHARE                                                                             *
	 ******************************************************************************************* */


	%>
	<tr>
		<td class="imcmsAdmText" valign="top" style="padding-top:5px;"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/32 ?></td>
		<td class="imcmsAdmText">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="checkbox"
				name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS %>"
				id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS %>"
				value="1"<%
			if (document.isLinkedForUnauthorizedUsers()) {
				%> checked="checked"<%
			} %>></td>
			<td class="imcmsAdmText"><label for="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS %>">&nbsp;<? install/htdocs/global/pageinfo/show_link_to_unauthorized_user ?></label></td>
		</tr>
		<tr>
			<td><input type="checkbox"
				name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS %>"
				id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS %>"
				value="1"<%
			if (document.isLinkableByOtherUsers()) {
				%> checked="checked"<%
			} %>></td>
			<td class="imcmsAdmText"><label for="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS %>">&nbsp;<? install/htdocs/global/pageinfo/share ?></label></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText" valign="top" style="padding-top:5px;"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/35 ?></td>
		<td class="imcmsAdmText">


		<table border="0" cellspacing="0" cellpadding="2" style="width:98%;">
		<tr>
			<td class="imcmsAdmText" style="padding-left:10px; padding-right:25px;">${i18nPart.language.name}</td>
			<td><%
			Set documentKeywords = document.getKeywords();
			String[] keywords = (String[])documentKeywords.toArray(new String[documentKeywords.size()]);
			Collator collator = service.getDefaultLanguageCollator() ;
			Arrays.sort(keywords,collator) ;
			KeywordsParser keywordsParser = new KeywordsParser();
			%><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__KEYWORDS %>"
			      size="48" maxlength="200" style="width:100%;"
			      value="<%=StringEscapeUtils.escapeHtml( keywordsParser.formatKeywords(keywords) )%>" /></td>
		</tr>

		  <%-- Keywords old code:
		  
		  		
		Set documentKeywords = document.getKeywords();
        String[] keywords = (String[])documentKeywords.toArray(new String[documentKeywords.size()]);
		Collator collator = service.getDefaultLanguageCollator() ;
		Arrays.sort(keywords,collator) ;
        KeywordsParser keywordsParser = new KeywordsParser();
		%>
		<input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__KEYWORDS %>" size="48" maxlength="200" style="width: 100%"
		value="<%= StringEscapeUtils.escapeHtml( keywordsParser.formatKeywords(keywords) )%>"><br>
		<span class="imcmsAdmDim"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/keywords_explanation ?></span><br>
		<input type="CHECKBOX" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__SEARCH_DISABLED %>" value="1" <%
		  
		  --%>

            <%--
		<table border="0" cellspacing="0" cellpadding="2" style="width:98%;">

		<c:forEach items="${labelsMap}" var="i18nPart">
		<c:set var="prefix" value="_${i18nPart.language.code}"/>
		<c:set var="keywordsValues" value="${i18nPart.keywords}"/>
		<tr>
			<td><img src="$contextPath/imcms/$language/images/admin/flags_iso_639_1/${i18nPart.language.code}.gif" alt="" style="border:0;" /></td>
			<td class="imcmsAdmText" style="padding-left:10px; padding-right:25px;">${i18nPart.language.name}</td>
			<td><%					  
			Set documentKeywords = (Set) pageContext.getAttribute("keywordsValues");
			String[] keywords = (String[])documentKeywords.toArray(new String[documentKeywords.size()]);
			Collator collator = service.getDefaultLanguageCollator() ; // ???
			Arrays.sort(keywords,collator) ;
			KeywordsParser keywordsParser = new KeywordsParser();							  
			%><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__KEYWORDS + pageContext.getAttribute("prefix")%>"
			      size="48" maxlength="200" style="width:100%;"
			      value="<%=StringEscapeUtils.escapeHtml( keywordsParser.formatKeywords(keywords) )%>" /></td>		
		</tr>
		</c:forEach>
        --%>    

		<tr>
			<td colspan="3" class="imcmsAdmDim" style="padding-top:10px; padding-bottom:10px;"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/keywords_explanation ?></td>
		</tr>
		<tr>
			<td colspan="3">
			<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="checkbox"
				           name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__SEARCH_DISABLED %>"
				           id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__SEARCH_DISABLED %>"
				           value="1"<%= document.isSearchDisabled() ? " checked=\"checked\"" : "" %> /></td>
				<td class="imcmsAdmText"><label for="<%=
				EditDocumentInformationPageFlow.REQUEST_PARAMETER__SEARCH_DISABLED %>">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/37 ?></label></td>
			</tr>
			</table></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/39 ?></td>
		<td class="imcmsAdmText" nowrap>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr><%
			String target = document.getTarget() ; %>
			<td><input type="radio" id="target0" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET %>" value="_self"<%
			if ("_self".equalsIgnoreCase( target ) || "".equals( target )) {
				%> checked<%
				target = null;
			} %>></td>
			<td class="imcmsAdmText">&nbsp;<label for="target0"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/1015 ?></label> &nbsp;</td>
			<td><input type="radio" id="target1" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET %>" value="_blank"<%
			if ("_blank".equalsIgnoreCase( target ) ) {
				%> checked<%
				target = null;
			} %>></td>
			<td class="imcmsAdmText">&nbsp;<label for="target1"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/1016 ?></label> &nbsp;</td>
			<td><input type="radio" id="target2" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET %>" value="_top"<%
			if ("_top".equalsIgnoreCase( target ) ) {
				%> checked<%
				target = null;
			} %>></td>
			<td class="imcmsAdmText">&nbsp;<label for="target2"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/1017 ?></label> &nbsp;</td>
			<td><input type="radio" id="target3" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET %>" <% if (null != target) { %> checked<% } %>></td>
			<td class="imcmsAdmText">&nbsp;<label for="target3"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/1018 ?></label>&nbsp;</td>
			<td>
			<input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET %>" size="9" maxlength="20" style="width:120"
			value="<%
			if (null != target) {
				%><%= StringEscapeUtils.escapeHtml( target ) %><%
			} %>"></td>
		</tr>
		</table></td>
	</tr><%
	if ( editingExistingDocument ) { %>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/created ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CREATED_DATE %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CREATED_DATE %>" size="11" maxlength="10" style="width: 7em;"
			value="<%= formatDate( document.getCreatedDatetime() ) %>"></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/time ?></td>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CREATED_TIME %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CREATED_TIME %>" size="5" maxlength="5" style="width: 4em;"
			value="<%= formatTime( document.getCreatedDatetime() ) %>"></td>
			<td class="imcmsAdmText"><%= jsCalendar.getInstance(EditDocumentInformationPageFlow.REQUEST_PARAMETER__CREATED_DATE,
			                             EditDocumentInformationPageFlow.REQUEST_PARAMETER__CREATED_TIME).getButton(calendarButtonTitle) %>&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/created_by ?>
			<%= Utility.formatUser(userMapper.getUser(document.getCreatorId())) %> &nbsp; <input type="submit" class="imcmsFormBtnSmall" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__GO_TO_CREATOR_BROWSER %>" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/select_creator_button ?>"></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? global/modified ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MODIFIED_DATE %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MODIFIED_DATE %>" size="11" maxlength="10" style="width: 7em;"
			value="<%= formatDate( document.getModifiedDatetime() ) %>"></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/time ?></td>
			<td><input type="text" id="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MODIFIED_TIME %>" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MODIFIED_TIME %>" size="5" maxlength="5" style="width: 4em;"
			value="<%= formatTime( document.getModifiedDatetime() ) %>"></td>
            <td><%= jsCalendar.getInstance(EditDocumentInformationPageFlow.REQUEST_PARAMETER__MODIFIED_DATE,
			                               EditDocumentInformationPageFlow.REQUEST_PARAMETER__MODIFIED_TIME).getButton(calendarButtonTitle) %></td>
		</tr>
		</table></td>
	</tr><%
	} %>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? global/Publisher ?></td>
		<td class="imcmsAdmText">
		<% Integer publisherId = document.getPublisherId();
            UserDomainObject publisher = null == publisherId ? null : userMapper.getUser(publisherId.intValue()) ; %>
		<%=
		null == publisher
		? "<? install/htdocs/sv/jsp/docadmin/document_information.jsp/no_publisher ?>"
		: Utility.formatUser(publisher)
		%> &nbsp; <input type="submit" class="imcmsFormBtnSmall" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__GO_TO_PUBLISHER_BROWSER %>" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/select_publisher_button ?>"></td>
	</tr>
	<tr>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="96" height="1" alt=""></td>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="556" height="1" alt=""></td>
	</tr>
	</table></td>
</tr>
</tbody><%
/* *******************************************************************************************
 *         END ADVANCED                                                                      *
 ******************************************************************************************* */
%>
<tr>
	<td>#gui_hr( "blue" )</td>
</tr>
<tr>
	<td align="right">
	<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<script type="text/javascript">
		if (hasGetElementById) {
			document.writeln('<td><input type="button" id="advanced_button2" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/advanced_button ?>" onClick="toggleAdvanced()"></td>') ;
			document.writeln('<td>&nbsp;&nbsp;</td>') ;
		}
		</script>
		<td><input type="SUBMIT" class="imcmsFormBtn" name="<%= PageFlow.REQUEST_PARAMETER__OK_BUTTON %>" onClick="return singleclicked();"
		value=" <? install/htdocs/sv/jsp/docadmin/document_information.jsp/2004 ?> "></td>
		<td>&nbsp;</td>
		<td><input type="SUBMIT" class="imcmsFormBtn" name="<%= PageFlow.REQUEST_PARAMETER__CANCEL_BUTTON %>"
		value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2006 ?>"></td>
	</tr>
	</table></td>
</tr>
</table>
</form>
#gui_bottom()
#gui_outer_end()
</vel:velocity>
<%
if (!adminButtonsHidden) {
	String adminButtons = Html.getAdminButtons( user, document, request, response ) ;
	if (!"".equals( adminButtons )) {
		%><center><%= adminButtons %></center><%
	}
}
%>
<script type="text/javascript">
<!--<%
String viewCookieName = "document_information_view" ;

Cookie[] all_cookies = request.getCookies() ;
String actCookieValue = "simple" ;
if (all_cookies.length > 0) {
	for (int i = 0; i < all_cookies.length; i++) {
		Cookie actCookie = all_cookies[i] ;
		String actCookieName = actCookie.getName() ;
		if (actCookieName.equals(viewCookieName)) {
			actCookieValue = actCookie.getValue() ;
		}
	}
} %>
var viewCookieName = "<%= viewCookieName %>" ;
var BTN_SHOW_VALUE = "<? install/htdocs/sv/jsp/docadmin/document_information.jsp/advanced_button ?>" ;
var BTN_HIDE_VALUE = "<? install/htdocs/sv/jsp/docadmin/document_information.jsp/advanced_button_hide ?>" ;<%

if (actCookieValue.equals("advanced")) { %>
document.getElementById("advanced").style.display = (isGecko) ? "table-row-group" : "block" ;
document.getElementById("advanced_button1").value = BTN_HIDE_VALUE ;
document.getElementById("advanced_button2").value = BTN_HIDE_VALUE ;<%
} else { %>
document.getElementById("advanced").style.display = "none" ;
document.getElementById("advanced_button1").value = BTN_SHOW_VALUE ;
document.getElementById("advanced_button2").value = BTN_SHOW_VALUE ;<%
} %>

function toggleAdvanced(show) {
	if (!hasGetElementById) {
		return ;
	}
	var advancedDisplay = document.getElementById("advanced").style.display ;
	var advancedButtonValue = BTN_SHOW_VALUE ;
	var viewCookieValue = "simple" ;
	if (advancedDisplay == "none") {
		advancedDisplay = (isGecko) ? "table-row-group" : "block" ;
		advancedButtonValue = BTN_HIDE_VALUE ;
		viewCookieValue = "advanced" ;
	} else {
		advancedDisplay = "none" ;
	}
	document.getElementById("advanced").style.display = advancedDisplay ;
	document.getElementById("advanced_button1").value = advancedButtonValue ;
	document.getElementById("advanced_button2").value = advancedButtonValue ;
	setCookie(viewCookieName, viewCookieValue) ;
}

function setCookie(sName, sValue) {
	var sPath = '/';
	var today = new Date();
	var expire = new Date();
	expire.setTime(today.getTime() + 1000*60*60*24*2); // 2 days
	var sCookieCont = sName + "=" + escape(sValue);
	sCookieCont += (expire == null) ? "" : "\; expires=" + expire.toGMTString();
	sCookieCont += "; path=" + sPath;
	document.cookie = sCookieCont;
}

if (document.getElementById) {
	for (var i = 0; i <= 2; i++) {
		if (document.getElementById("nowBtn" + i)) document.getElementById("nowBtn" + i).style.visibility = "visible" ;
	}
}
// -->
</script>

</body>
</html>

