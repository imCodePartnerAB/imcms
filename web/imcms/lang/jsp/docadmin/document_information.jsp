<%@ page
    contentType="text/html;
    charset=cp1252"
    
    import="imcode.server.ApplicationServer,
            org.apache.commons.lang.StringEscapeUtils,
            java.text.SimpleDateFormat,
            java.text.DateFormat,
            org.apache.commons.lang.StringUtils,
            imcode.util.Html,
            org.apache.commons.collections.iterators.TransformIterator,
            org.apache.commons.collections.Transformer,
            imcode.server.LanguageMapper,
            imcode.server.IMCServiceInterface,
            imcode.server.user.UserDomainObject,
            imcode.server.document.*,
            com.imcode.imcms.servlet.admin.DocumentComposer,
            org.apache.commons.lang.ObjectUtils,
            java.util.regex.Pattern,
            org.apache.oro.text.perl.Perl5Util,
            java.text.Collator,
            java.util.*,
            com.imcode.imcms.servlet.admin.UserBrowser,
            com.imcode.imcms.servlet.admin.UserFinder,
            imcode.util.*,
            imcode.server.document.textdocument.TextDocumentDomainObject,
            com.imcode.imcms.flow.EditDocumentInformationPageFlow,
            com.imcode.imcms.flow.CreateDocumentPageFlow,
            com.imcode.imcms.flow.DocumentPageFlow,
            com.imcode.imcms.flow.HttpPageFlow"

%><%@taglib prefix="vel" uri="/WEB-INF/velocitytag.tld"%><%

    UserDomainObject user = Utility.getLoggedOnUser( request ) ;
    final IMCServiceInterface service = ApplicationServer.getIMCServiceInterface();
    final DocumentMapper documentMapper = service.getDocumentMapper();

    DocumentPageFlow httpFlow = DocumentPageFlow.fromRequest(request) ;
    boolean creatingNewDocument = httpFlow instanceof CreateDocumentPageFlow ;
    boolean editingExistingDocument = !creatingNewDocument ;

    EditDocumentInformationPageFlow.DocumentInformationPage documentInformationPage = EditDocumentInformationPageFlow.DocumentInformationPage.fromRequest(request) ;
    DocumentDomainObject document = documentInformationPage.getDocument() ;
    boolean adminButtonsHidden = creatingNewDocument || documentInformationPage.isAdminButtonsHidden() ;

%><%!

String formatDatetimeWithParentheses(Date datetime) {
    if (null == datetime) {
        return "" ;
    }
    DateFormat dateFormat = new SimpleDateFormat( DateConstants.DATE_FORMAT_STRING+"'&nbsp;'"+DateConstants.TIME_NO_SECONDS_FORMAT_STRING ) ;
    return "("+dateFormat.format(datetime)+")" ;
}

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

String formatUser(UserDomainObject user) {
    return StringEscapeUtils.escapeHtml( user.getLastName()+", "+user.getFirstName()+" ("+user.getLoginName()+")" );
}
%><vel:velocity><html>
<head>
<title><? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_information_title ?></title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="Expires" content="-1">
<meta http-equiv="Cache-Control" content="must-revalidate"> 
<meta http-equiv="Cache-Control" content="no-cache">

<link rel="stylesheet" type="text/css" href="$contextPath/imcms/css/imcms_admin.css.jsp">
<script src="$contextPath/imcms/$language/scripts/imcms_admin.js" type="text/javascript"></script>

<script language="JavaScript">
<!--
var selFocused = false ;

function checkFocus() {
	if (selFocused) {
		document.body.focus();
		selFocused = false ;
	}
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
<table border="0" cellspacing="0" cellpadding="0">
<form name="mainForm" method="POST" action="<%= request.getContextPath() %>/servlet/DocumentComposer">
<tr>
	<td><input type="submit" name="<%= HttpPageFlow.REQUEST_PARAMETER__CANCEL_BUTTON %>" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2001 ?>"></td>
	<td>&nbsp;</td>
	<td><input type="button" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2002 ?>"
	title="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2003 ?>" class="imcmsFormBtn" onClick="openHelpW(77)"></td>
</tr>
</table>
#gui_mid()
<%

/* *******************************************************************************************
 *         META-INFO                                                                         *
 ******************************************************************************************* */

%>
<table border="0" cellspacing="0" cellpadding="2" width="660" align="center" onMouseOver="checkFocus();">
<input type="hidden" name="<%= HttpPageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW %>"
    value="<%= HttpSessionUtils.getSessionAttributeNameFromRequest(request,HttpPageFlow.REQUEST_ATTRIBUTE_OR_PARAMETER__FLOW) %>">
<input type="hidden" name="<%= HttpPageFlow.REQUEST_PARAMETER__PAGE %>"
    value="<%= EditDocumentInformationPageFlow.PAGE__DOCUMENT_INFORMATION %>">
<% if (creatingNewDocument) { %>
<tr>
	<td class="imcmsAdmText">
	<? install/htdocs/sv/jsp/docadmin/document_information.jsp/new_document_procedure_description ?> &nbsp;</td>
</tr>
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
	<tr>
		<td class="imcmsAdmText" nowrap>
		<? install/htdocs/sv/jsp/docadmin/document_information.jsp/6 ?><sup class="imNote">1</sup></td>
		<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__HEADLINE %>" size="48" maxlength="255" style="width: 100%"
		value="<%= StringEscapeUtils.escapeHtml(document.getHeadline()) %>"></td>
	</tr>
    <tr>
        <td></td>
        <td class="imNoteComment"><sup class="imNote">1</sup>
        <? install/htdocs/sv/jsp/docadmin/document_information.jsp/46 ?></td>
    </tr>
	<tr>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="96" height="2"></td>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="556" height="2"></td>
	</tr>
	<tr>
		<td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/docadmin/document_information.jsp/1002 ?>&nbsp;</td>
		<td class="imcmsAdmForm">
		<textarea name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MENUTEXT %>" class="imcmsAdmForm" cols="47" rows="3" wrap="virtual" style="width:100%; overflow:auto;">
<%= StringEscapeUtils.escapeHtml(document.getMenuText()) %></textarea><%
		
		if (creatingNewDocument && document instanceof TextDocumentDomainObject) { %>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="CHECKBOX" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__COPY_HEADLINE_AND_TEXT_TO_TEXTFIELDS %>" value="1" checked></td>
			<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/copy_headline_and_text_to_textfields ?></td>
		</tr>
		</table><%
		} %></td>
	</tr>
	<tr>
		<td class="imcmsAdmText" nowrap><? install/htdocs/sv/jsp/docadmin/document_information.jsp/10 ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td width="85%"><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__IMAGE %>" size="40" maxlength="255" style="width: 100%"
			value="<%= StringEscapeUtils.escapeHtml( (String)ObjectUtils.defaultIfNull( document.getMenuImage(), "" )) %>"></td>
			<td align="right"><input type="submit" class="imcmsFormBtnSmall" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__GO_TO_IMAGE_BROWSER%>"
			value=" <? install/htdocs/global/pageinfo/browse ?> "></td>
		</tr>
		<input type="hidden" name="<%=
		DocumentComposer.PARAMETER__PREVIOUS_ACTION %>" value="<%=
		request.getAttribute( DocumentComposer.REQUEST_ATTRIBUTE_OR_PARAMETER__ACTION ) %>"/>
		</table></td>
	</tr>
	<tr>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="96" height="1"></td>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="556" height="1"></td>
	</tr>
	</table><%

/* *******************************************************************************************
 *         STATUS                                                                            *
 ******************************************************************************************* */

%>
	<table border="0" cellspacing="0" cellpadding="0" width="656">
	<tr>
		<td colspan="2">#gui_hr( 'cccccc' )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText">
		<? install/htdocs/sv/jsp/docadmin/document_information.jsp/status ?></td>
		<td>
		<select name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__STATUS %>" onFocus="selFocused = true;">
			<option value="<%= DocumentDomainObject.STATUS_NEW %>"<%
				if (DocumentDomainObject.STATUS_NEW == document.getStatus()) {
					%> selected<%
				} %>><? install/htdocs/sv/jsp/docadmin/document_information.jsp/status_new ?></option>
			<option value="<%= DocumentDomainObject.STATUS_PUBLICATION_APPROVED %>"<%
				if (DocumentDomainObject.STATUS_PUBLICATION_APPROVED == document.getStatus()) {
					%> selected<%
				} %>><? install/htdocs/sv/jsp/docadmin/document_information.jsp/status_publication_approved ?></option>
			<option value="<%= DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED %>"<%
				if (DocumentDomainObject.STATUS_PUBLICATION_DISAPPROVED == document.getStatus()) {
					%> selected<%
				} %>><? install/htdocs/sv/jsp/docadmin/document_information.jsp/status_publication_disapproved ?></option>
		</select>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td class="imcmsAdmText"><%
			Date now = new Date();
			Date publicationStartDatetime = document.getPublicationStartDatetime();
			if (document.getStatus() != DocumentDomainObject.STATUS_PUBLICATION_APPROVED || null == publicationStartDatetime || publicationStartDatetime.after(now)) { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_will_be_published_at ?><%
			} else { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_was_published_at ?><%
			} %></td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_DATE %>" size="11" maxlength="10" style="width: 7em;"
				value="<%= StringEscapeUtils.escapeHtml( formatDate(publicationStartDatetime) ) %>"></td>
				<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1007 ?></td>
				<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_START_TIME %>" size="5" maxlength="5" style="width: 4em;"
				value="<%= StringEscapeUtils.escapeHtml( formatTime(document.getPublicationStartDatetime()) ) %>"></td>
				<td>&nbsp;<%= formatDatetimeWithParentheses( publicationStartDatetime ) %></td>
			</tr>
			</table></td>
		</tr>
		<tr>
			<td class="imcmsAdmText"><%
			Date archivedDatetime = document.getArchivedDatetime();
			if (null == archivedDatetime || archivedDatetime.after(now)) { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_will_be_archived_at ?><%
			} else { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_was_archived_at ?><%
			} %></td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_DATE %>" size="11" maxlength="10" style="width: 7em;"
				value="<%= StringEscapeUtils.escapeHtml( formatDate(archivedDatetime) ) %>"></td>
				<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1009 ?></td>
				<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__ARCHIVED_TIME %>" size="5" maxlength="5" style="width: 4em;"
				value="<%= StringEscapeUtils.escapeHtml( formatTime(archivedDatetime) ) %>"></td>
				<td>&nbsp;<%= formatDatetimeWithParentheses( archivedDatetime ) %></td>
			</tr>
			</table></td>
		</tr>
		<tr>
			<td class="imcmsAdmText" nowrap><%
			Date publicationEndDatetime = document.getPublicationEndDatetime();
			if (null == publicationEndDatetime || publicationEndDatetime.after(now)) { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_will_cease_to_be_published_at ?><%
			} else { %>
			<? install/htdocs/sv/jsp/docadmin/document_information.jsp/document_ceased_to_be_published_at ?><%
			} %>&nbsp;</td>
			<td>
			<table border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_DATE %>" size="11" maxlength="10" style="width: 7em;"
				value="<%= StringEscapeUtils.escapeHtml( formatDate(publicationEndDatetime) ) %>"></td>
				<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1009 ?></td>
				<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__PUBLICATION_END_TIME %>" size="5" maxlength="5" style="width: 4em;"
				value="<%= StringEscapeUtils.escapeHtml( formatTime(publicationEndDatetime) ) %>"></td>
				<td>&nbsp;<%= formatDatetimeWithParentheses( publicationEndDatetime ) %></td>
			</tr>
			</table></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="96" height="1"></td>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="556" height="1"></td>
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
		<script language="javascript">
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
	<table border="0" cellspacing="0" cellpadding="0">
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/22 ?></td>
		<td class="imcmsAdmText">
		<select name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__SECTIONS %>" size="5" multiple><%
		SectionDomainObject[] sections = documentMapper.getAllSections() ;
		Arrays.sort(sections) ;
		SectionDomainObject[] documentSections = document.getSections() ;
		Transformer sectionToStrings = new Transformer() {
			public Object transform( Object o ) {
				SectionDomainObject section = (SectionDomainObject) o ;
				return new String[] { ""+section.getId(), section.getName() } ;
			}
		} ; %>
		<%= Html.createOptionList( Arrays.asList( sections ), Arrays.asList(documentSections), sectionToStrings ) %>
		</select>
		&nbsp; <? install/htdocs/sv/jsp/docadmin/document_information.jsp/current_section ?>
		<%=
		0 == documentSections.length
		? "<? install/htdocs/sv/jsp/docadmin/document_information.jsp/no_section ?>"
		: StringUtils.join(documentSections, ", ")
		%></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/26 ?></td>
		<td class="imcmsAdmText">
		<select name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__LANGUAGE %>" size="1" onFocus="selFocused = true;">
			<%= LanguageMapper.getLanguageOptionList( user, document.getLanguageIso639_2() ) %>
		</select>
		&nbsp; <? install/htdocs/sv/jsp/docadmin/document_information.jsp/current_language ?> <%= LanguageMapper.getCurrentLanguageNameInUsersLanguage( user, document.getLanguageIso639_2() )%></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/29 ?></td>
		<td class="imcmsAdmText"><%
		CategoryTypeDomainObject[] categoryTypes = documentMapper.getAllCategoryTypes() ;
		Arrays.sort(categoryTypes) ;
		for ( int i = 0; i < categoryTypes.length; i++ ) {
			CategoryTypeDomainObject categoryType = categoryTypes[i] ;
			if ( !categoryType.hasImages() ) { %>
		<div style="float: left; margin: auto 1em 1ex auto;">
		<a href="$contextPath/imcms/$language/jsp/category_descriptions.jsp?category_type_name=<%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %>" target="_blank"><%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %></a><br><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="1" height="3"><br>
		<select name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CATEGORIES %>"<% if (1 != categoryType.getMaxChoices()) { %> size="4" multiple<% } else { %> onFocus="selFocused = true;"<% } %>>
			<%= Html.createOptionListOfCategoriesOfTypeForDocument( documentMapper, categoryType, document) %>
		</select></div><%
			}
		}
		for ( int i = 0; i < categoryTypes.length; i++ ) {
			CategoryTypeDomainObject categoryType = categoryTypes[i] ;
			if ( categoryType.hasImages() ) { %>
		<div style="float: left; margin: auto 1em 1ex auto;">
			<a href="$contextPath/imcms/$language/jsp/category_descriptions.jsp?category_type_name=<%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %>"
			target="_blank"><%= StringEscapeUtils.escapeHtml( categoryType.getName() ) %></a><br><%
		
			boolean radioButton = categoryType.getMaxChoices() == 1;
			String typeStr = radioButton?"radio":"checkbox";
			CategoryDomainObject[] documentSelectedCategories = document.getCategoriesOfType(categoryType);
			Set selectedValuesSet = new HashSet( Arrays.asList(documentSelectedCategories) );
			CategoryDomainObject[] categories = documentMapper.getAllCategoriesOfType(categoryType);
			for (int k = 0; k < categories.length; k++) {
				CategoryDomainObject category = categories[k];
				boolean checked = selectedValuesSet.contains(category);
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
				}
			} %></div><%
		} %></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/32 ?></td>
		<td class="imcmsAdmText">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="CHECKBOX" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__VISIBLE_IN_MENU_FOR_UNAUTHORIZED_USERS %>" value="1"<%
			if (document.isVisibleInMenusForUnauthorizedUsers()) {
				%> checked<%
			} %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/global/pageinfo/show_link_to_unauthorized_user ?></td>
		</tr>
		<tr>
			<td><input type="CHECKBOX" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__LINKABLE_BY_OTHER_USERS %>" value="1"<%
			if (document.isLinkableByOtherUsers()) {
				%> checked<%
			} %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/global/pageinfo/share ?></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/35 ?></td>
		<td class="imcmsAdmText"><%
		String[] keywords = document.getKeywords();
		Collator collator = service.getDefaultLanguageCollator() ;
		Arrays.sort(keywords,collator) ;
		for ( int i = 0; i < keywords.length; i++ ) {
			if (Pattern.compile("[^\\p{L}\\d]").matcher(keywords[i]).find()) {
				keywords[i] = '"'+keywords[i]+'"' ;
			}
		}
		%>
		<input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__KEYWORDS %>" size="48" maxlength="200" style="width: 100%"
		value="<%= StringEscapeUtils.escapeHtml( StringUtils.join( keywords, ", " ) ) %>"><br>
		<span class="imcmsAdmDim"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/keywords_explanation ?></span><br>
		<input type="CHECKBOX" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__SEARCH_DISABLED %>" value="1" <%
		if (document.isSearchDisabled()) {
			%> checked<%
		} %>> <? install/htdocs/sv/jsp/docadmin/document_information.jsp/37 ?></td>
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
			<td><input type="radio" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET %>" value="_self"<%
			if ("_self".equalsIgnoreCase( target ) || "".equals( target )) {
				%> checked<%
				target = null;
			} %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1015 ?> &nbsp;</td>
			<td><input type="radio" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET %>" value="_blank"<%
			if ("_blank".equalsIgnoreCase( target ) ) {
				%> checked<%
				target = null;
			} %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1016 ?> &nbsp;</td>
			<td><input type="radio" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET %>" value="_top"<%
			if ("_top".equalsIgnoreCase( target ) ) {
				%> checked<%
				target = null;
			} %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1017 ?> &nbsp;</td>
			<td><input type="radio" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__TARGET %>" <% if (null != target) { %> checked<% } %>></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/1018 ?>&nbsp;</td>
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
			<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CREATED_DATE %>" size="11" maxlength="10" style="width: 7em;"
			value="<%= formatDate( document.getCreatedDatetime() ) %>"></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/time ?></td>
			<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__CREATED_TIME %>" size="5" maxlength="5" style="width: 4em;"
			value="<%= formatTime( document.getCreatedDatetime() ) %>"></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/created_by ?>
			<%= formatUser(document.getCreator()) %>&nbsp;<input type="submit" class="imcmsFormBtnSmall" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__GO_TO_CREATOR_BROWSER %>" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/select_creator_button ?>"></td>
		</tr>
		</table></td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/changed ?></td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MODIFIED_DATE %>" size="11" maxlength="10" style="width: 7em;"
			value="<%= formatDate( document.getModifiedDatetime() ) %>"></td>
			<td class="imcmsAdmText">&nbsp;<? install/htdocs/sv/jsp/docadmin/document_information.jsp/time ?></td>
			<td><input type="text" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__MODIFIED_TIME %>" size="5" maxlength="5" style="width: 4em;"
			value="<%= formatTime( document.getModifiedDatetime() ) %>"></td>
		</tr>
		</table></td>
	</tr><%
	} %>
	<tr>
		<td colspan="2">#gui_hr( "cccccc" )</td>
	</tr>
	<tr>
		<td class="imcmsAdmText"><? install/htdocs/sv/jsp/docadmin/document_information.jsp/42 ?></td>
		<td class="imcmsAdmText">
		<% UserDomainObject publisher = document.getPublisher() ; %>
		<%=
		null == publisher
		? "<? install/htdocs/sv/jsp/docadmin/document_information.jsp/no_publisher ?>"
		: formatUser(publisher)
		%>&nbsp;<input type="submit" class="imcmsFormBtnSmall" name="<%= EditDocumentInformationPageFlow.REQUEST_PARAMETER__GO_TO_PUBLISHER_BROWSER %>" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/select_publisher_button ?>"></td>
	</tr>
	<tr>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="96" height="1"></td>
		<td><img src="$contextPath/imcms/$language/images/admin/1x1.gif" width="556" height="1"></td>
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
		<script language="javascript">
		if (hasGetElementById) {
			document.writeln('<td><input type="button" id="advanced_button2" class="imcmsFormBtn" value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/advanced_button ?>" onClick="toggleAdvanced()"></td>') ;
			document.writeln('<td>&nbsp;&nbsp;</td>') ;
		}
		</script>
		<td><input type="SUBMIT" class="imcmsFormBtn" name="<%= HttpPageFlow.REQUEST_PARAMETER__OK_BUTTON %>"
		value=" <? install/htdocs/sv/jsp/docadmin/document_information.jsp/2004 ?> "></td>
		<td>&nbsp;</td>
		<td><input type="SUBMIT" class="imcmsFormBtn" name="<%= HttpPageFlow.REQUEST_PARAMETER__CANCEL_BUTTON %>"
		value="<? install/htdocs/sv/jsp/docadmin/document_information.jsp/2006 ?>"></td>
	</tr>
	</table></td>
</tr>
<tr>
	<td>#gui_hr( "blue" )</td>
</tr>
</form>
</table>
#gui_bottom()
#gui_outer_end()
</vel:velocity>
<%
if (!adminButtonsHidden) {
	String adminButtons = service.getAdminButtons( user, document) ;
	if (!"".equals( adminButtons )) {
		%><center><%= adminButtons %></center><%
	}
}
%>
<script language="javascript">
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
	mozScrollbarFix() ;
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

// Fix for Mozilla

function mozScrollbarFix() {
	if (isGecko) {
		var el = document.getElementById("theBody") ;
		var db = document.body ;
		el.style.marginRight = (db.scrollHeight == db.clientHeight) ? "16px" : "0" ;
	}
}
mozScrollbarFix() ;
// -->
</script>

</body>
</html>

